package com.stfalcon.chatkit.messages.utils

import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.widget.TextView
import com.github.ajalt.timberkt.w
import com.stfalcon.chatkit.messages.MarkDown
import com.stfalcon.chatkit.utils.NonbreakingSpan
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk <grzegorz.pawelczuk@ftlearning.com>
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 * //13-2-2018 - Mikołaj Kowal - added support for nested expressions
 */


class MessageTextUtils {

    companion object {

        fun applyTextTransformations(view: TextView, rawText: String, @ColorInt linkColor: Int){
            val text  = EmojiTextUtils.transform( rawText )
            view.text = MessageTextUtils.transform( text, linkColor )
            view.movementMethod = LinkMovementMethod.getInstance()
        }

        private fun transform(text: String, @ColorInt linkColor: Int) : SpannableString{
            return MessageTextUtils.transform(text, getTextPatterns(text), linkColor)
        }

        fun getTextPatterns(text: String): MutableList<PatternDescriptor> {
            val list: MutableList<PatternDescriptor> = mutableListOf()
            val pattern = Pattern.compile("<(.*?)>|\\*(.*?)\\*|_(.*?)_|~(.*?)~")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                var group = matcher.group()
                var isLink = false
                var isBold = false
                var isItalic = false
                var isStroke = false
                var surrounding = MarkDown.NONE
                when {
                    group.startsWith("<") -> {
                        isLink = true
                        surrounding = MarkDown.LINK
                    }
                    group.startsWith("*") -> {
                        isBold = true
                        surrounding = MarkDown.BOLD
                    }
                    group.startsWith("_") -> {
                        isItalic = true
                        surrounding = MarkDown.ITALIC
                    }
                    group.startsWith("~") -> {
                        isStroke = true
                        surrounding = MarkDown.STROKE
                    }
                }
                group = group.substring(1, group.length - 1)
                if (findNextMarkDown(0, group) != -1) {
                    val reqList = getTextPatterns(group)
                    if (reqList.size > 0) {
                        reqList.forEach { unit: PatternDescriptor ->
                            when {
                                isBold -> unit.isBold = true
                                isItalic -> unit.isItalic = true
                                isStroke -> unit.isStroke = true
                                isLink -> unit.isLink = true
                            }
                            list.add(unit)
                        }
                    }
                }
                var url: PatternDescriptor? = null
                if (group.contains("|") && isLink) {
                    val split = group.split("|")
                    if (split.isNotEmpty()) {
                        url = PatternDescriptor(removeMarkDowns(split[0]), removeMarkDowns(split[1]), true, isBold, isItalic, isStroke, surrounding)
                    }
                } else {
                    url = PatternDescriptor(removeMarkDowns(group), null, isLink, isBold, isItalic, isStroke, surrounding)
                }
                url?.run {
                    list.add(this)
                }
            }
            return list
        }

        private fun findNextMarkDown(currentIndex: Int, text: String): Int {
            var closestIndex = Int.MAX_VALUE
            val pattern = "*~<>_"
            var thisIndex: Int
            pattern.forEach { tag ->
                thisIndex = text.indexOf(tag, currentIndex)
                if (thisIndex != -1 && thisIndex < closestIndex) {
                    closestIndex = thisIndex
                }
            }
            if (closestIndex == Int.MAX_VALUE) {
                return -1
            }
            return closestIndex
        }

        private fun removeMarkDowns(markDownText: String): String {
            val pattern = "*~<>_"
            var toReturn = String(markDownText.toCharArray())
            var thisSign: String
            for (i in 0 until pattern.length) {
                thisSign = pattern[i].toString()
                toReturn = toReturn.replace(thisSign, "")
            }
            return toReturn
        }

        private fun howManyLevelsIn(url: PatternDescriptor): Int {
            var i = -1
            if (url.isBold) {
                i += 1
            }
            if (url.isItalic) {
                i += 1
            }
            if (url.isLink) {
                i += 1
            }
            if (url.isStroke) {
                i += 1
            }
            return i
        }

        private fun calculateOffset(urls: MutableList<PatternDescriptor>): MutableList<PatternDescriptor> {
            var thisUrl: PatternDescriptor
            for (i in 0 until urls.size) {
                thisUrl = urls[i]
                if (thisUrl.isLink && thisUrl.surrounding != MarkDown.LINK) {
                    var j = 0
                    var nextUrl: PatternDescriptor
                    while (i + j < urls.size) {
                        nextUrl = urls[i + j]
                        if (nextUrl.surrounding == MarkDown.LINK) {
                            thisUrl.offset = nextUrl.content.length + 1
                            break
                        }
                        j++
                    }
                }
                urls[i] = thisUrl

            }
            return urls
        }

        fun transform(text: String, urls: MutableList<PatternDescriptor>, color: Int): SpannableString {
            val checkedUrls = calculateOffset(urls)
            val descriptors: MutableList<PatternSpanDescriptor> = mutableListOf()
            var urlText: String
            var textToCheck = text
            checkedUrls.forEach { url ->
                urlText = url.toTag()
                if (textToCheck.indexOf(urlText) != -1) {
                    val parts = textToCheck.split(urlText)
                    if (parts.isNotEmpty()) {
                        textToCheck = parts[0] + url.getLabelToDisplay() + parts.drop(1).joinToString(separator = urlText)
                        descriptors.add(PatternSpanDescriptor(
                                parts[0].length - howManyLevelsIn(url) - url.offset,
                                parts[0].length + url.getLabelToDisplay().length - howManyLevelsIn(url) - url.offset,
                                url.content,
                                url.label,
                                url.isLink,
                                url.isBold,
                                url.isItalic,
                                url.isStroke
                        )
                        )
                    }
                }
            }

            val spannableString = SpannableString(textToCheck)

            descriptors.forEach { content ->

                if (content.isLink) {
                    spannableString.setSpan(URLSpan(content.content), content.startIndex, content.endIndex, 0)
                    spannableString.setSpan(ForegroundColorSpan(color), content.startIndex, content.endIndex, 0)
                }
                if (content.isBold) {
                    spannableString.setSpan(StyleSpan(Typeface.BOLD), content.startIndex, content.endIndex, 0)
                }
                if (content.isItalic) {
                    spannableString.setSpan(StyleSpan(Typeface.ITALIC), content.startIndex, content.endIndex, 0)
                }
                if (content.isStroke) {
                    spannableString.setSpan(StrikethroughSpan(), content.startIndex, content.endIndex, 0)
                }
            }

            return transformCommandLike(spannableString)
        }

        private fun transformCommandLike(spannableString: SpannableString): SpannableString {
            val text = spannableString.toString()
            val pattern = Pattern.compile("(/(.*?)\\s)|(/(.*+))")

            val matcher = pattern.matcher(text)
            var group: String
            while (matcher.find()) {
                group = matcher.group()
                if (!group.startsWith("//")) { // its url
                    w { "GROUP $group" }
                    val indexOf = text.indexOf(group)
                    spannableString.setSpan(NonbreakingSpan(), indexOf, indexOf + group.length, 0)
                }
            }
            return spannableString
        }
    }

    data class PatternSpanDescriptor(val startIndex: Int, val endIndex: Int, val content: String, val label: String? = null, val isLink: Boolean = true, val isBold: Boolean = false, val isItalic: Boolean = false, val isStroke: Boolean = false)

    data class PatternDescriptor(var content: String, val label: String? = null, var isLink: Boolean = true, var isBold: Boolean = false, var isItalic: Boolean = false, var isStroke: Boolean = false, @MarkDown.MarkDowns var surrounding: Int = MarkDown.NONE, var beginIndex: Int = 0, var endIndex: Int = 0, var offset: Int = 0) {
        fun toTag(): String {
            if (content.contains("|")) {
                content = content.split("|")[1]
            }
            return when (surrounding) {
                MarkDown.LINK -> {
                    var swapData = "<$content"
                    label?.run {
                        swapData += "|$label"
                    }
                    swapData + ">"
                }
                MarkDown.BOLD -> "*$content*"
                MarkDown.ITALIC -> "_${content}_"
                MarkDown.STROKE -> "~$content~"
                else -> content
            }
        }

        fun getLabelToDisplay(): String {
            return label ?: content
        }
    }
}
