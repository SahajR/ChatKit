package com.stfalcon.chatkit.sample.common.data.fixtures;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

/*
 * Created by Anton Bevza on 1/13/17.
 */
abstract class FixturesData {

    static SecureRandom rnd = new SecureRandom();

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add("http://i.imgur.com/pv1tBmT.png");
            add("http://i.imgur.com/R3Jm1CL.png");
            add("http://i.imgur.com/ROz4Jgh.png");
            add("http://i.imgur.com/Qn9UesZ.png");
        }
    };

    static final ArrayList<String> groupChatImages = new ArrayList<String>() {
        {
            add("http://i.imgur.com/hRShCT3.png");
            add("http://i.imgur.com/zgTUcL3.png");
            add("http://i.imgur.com/mRqh5w1.png");
        }
    };

    static final ArrayList<String> groupChatTitles = new ArrayList<String>() {
        {
            add("Samuel, Michelle");
            add("Jordan, Jordan, Zoe");
            add("Julia, Angel, Kyle, Jordan");
        }
    };

    static final ArrayList<String> names = new ArrayList<String>() {
        {
            add("Samuel Reynolds");
            add("Kyle Hardman");
            add("Zoe Milton");
            add("Angel Ogden");
            add("Zoe Milton");
            add("Angelina Mackenzie");
            add("Kyle Oswald");
            add("Abigail Stevenson");
            add("Julia Goldman");
            add("Jordan Gill");
            add("Michelle Macey");
        }
    };

    static final ArrayList<String> messages = new ArrayList<String>() {
        {
            add("Hello! <https://google.com> and <https://google.com|Google> ha! *bold* _italic_ ~strike~ and again <https://google.com|Google> and ... <https://www.onet.pl|Onet>");
            add("This is my phone number - +1 (234) 567-89-01 <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("Here is my e-mail - myemail@example.com <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("Hey! Check out this awesome link! www.github.com <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("Hello! No problem. I can today at 2 pm. And after we can go to the office. <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("At first, for some time, I was not able to answer him one word <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("At length one of them called out in a clear, polite, smooth dialect, not unlike in sound to the Italian <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("By the bye, Bob, said Hopkins <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("He made his passenger captain of one, with four of the men; and himself, his mate, and five more, went in the other; and they contrived their business very well, for they came up to the ship about midnight. <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("So saying he unbuckled his baldric with the bugle <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
            add("Just then her head struck against the roof of the hall: in fact she was now more than nine feet high, and she at once took up the little golden key and hurried off to the garden door. <https://google.com> and <https://google.com|Google> *bold* _italic_ ~strike~");
        }
    };

    static final ArrayList<String> images = new ArrayList<String>() {
        {
            add("https://habrastorage.org/getpro/habr/post_images/e4b/067/b17/e4b067b17a3e414083f7420351db272b.jpg");
            add("http://www.designboom.com/wp-content/uploads/2015/11/stefano-boeri-architetti-vertical-forest-residential-tower-lausanne-switzerland-designboom-01.jpg");
        }
    };

    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    static String getRandomAvatar() {
        return avatars.get(rnd.nextInt(avatars.size()));
    }

    static String getRandomGroupChatImage() {
        return groupChatImages.get(rnd.nextInt(groupChatImages.size()));
    }

    static String getRandomGroupChatTitle() {
        return groupChatTitles.get(rnd.nextInt(groupChatTitles.size()));
    }

    static String getRandomName() {
        return names.get(rnd.nextInt(names.size()));
    }

    static String getRandomMessage() {
        return messages.get(rnd.nextInt(messages.size()));
    }

    static String getRandomImage() {
        return images.get(rnd.nextInt(images.size()));
    }

    static boolean getRandomBoolean() {
        return rnd.nextBoolean();
    }
}
