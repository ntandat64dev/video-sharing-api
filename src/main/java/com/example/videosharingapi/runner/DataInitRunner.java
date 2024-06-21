package com.example.videosharingapi.runner;

import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class DataInitRunner implements ApplicationRunner {
    private final PrivacyRepository privacyRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final CommentRepository commentRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final FollowRepository followRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final VideoRepository videoRepository;
    private final HashtagRepository hashtagRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationObjectRepository notificationObjectRepository;
    private final NotificationRepository notificationRepository;

    private final PlaylistService playlistService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var privatePrivacy = privacyRepository.saveAndFlush(new Privacy(Privacy.Status.PUBLIC));
        var publicPrivacy = privacyRepository.saveAndFlush(new Privacy(Privacy.Status.PRIVATE));

        var roleAdmin = new Role("ADMIN");
        var roleUser = new Role("USER");
        roleRepository.saveAndFlush(roleAdmin);
        roleRepository.saveAndFlush(roleUser);

        // Create admin.
        var admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("00000000"))
                .roles(List.of(roleAdmin, roleUser))
                .publishedAt(LocalDateTime.now())
                .build();
        var adminThumbnail = new Thumbnail();
        adminThumbnail.setType(Thumbnail.Type.DEFAULT);
        adminThumbnail.setUrl("https://via.placeholder.com/100x100.png/0394fc/fff?text=Admin");
        adminThumbnail.setWidth(100);
        adminThumbnail.setHeight(100);
        admin.setThumbnails(List.of(adminThumbnail));
        userRepository.saveAndFlush(admin);
        playlistService.createDefaultPlaylistsForUser(admin);

        // Create users.
        var usernames = List.of(
                "Fireship",
                "Vox",
                "TED",
                "Standford",
                "MDI Studio",
                "NeetCode",
                "Code with CJ",
                "BBC News",
                "The Thao 247"
        );

        var imageUrls = List.of(
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_1.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_2.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_3.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_4.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_5.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_6.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_7.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_8.jpg",
                "https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/avatar_9.jpg"
        );

        var users = new User[9];
        for (int i = 1; i < 10; i++) {
            var user = User.builder()
                    .username(usernames.get(i - 1))
                    .password(passwordEncoder.encode(new String(new char[8])
                            .replace("\0", String.valueOf(i % 9))))
                    .publishedAt(LocalDateTime.now())
                    .roles(List.of(roleUser))
                    .build();
            var defaultThumbnail = Thumbnail.builder()
                    .type(Thumbnail.Type.DEFAULT)
                    .url(imageUrls.get(i - 1))
                    .width(100)
                    .height(100)
                    .build();

            user.setThumbnails(List.of(defaultThumbnail));
            userRepository.save(user);
            playlistService.createDefaultPlaylistsForUser(user);

            users[i - 1] = user;
        }

        // Add followers.
        for (var user : users) {
            for (var follower : users) {
                if (Objects.equals(user.getId(), follower.getId())) continue;
                if (new Random().nextInt(10) < 3) {
                    var follow = new Follow();
                    follow.setUser(user);
                    follow.setFollower(follower);
                    follow.setPublishedAt(LocalDateTime.now());
                    followRepository.saveAndFlush(follow);

                    var notificationObject = NotificationObject.builder()
                            .objectId(follow.getId())
                            .objectType(NotificationObject.ObjectType.FOLLOW)
                            .actionType(2)
                            .message(follower.getUsername() + " has followed you")
                            .publishedAt(LocalDateTime.now())
                            .build();
                    notificationObjectRepository.saveAndFlush(notificationObject);

                    var isSeen = new Random().nextBoolean();
                    var isRead = isSeen && new Random().nextBoolean();
                    var notification = Notification.builder()
                            .notificationObject(notificationObject)
                            .actor(follower)
                            .recipient(user)
                            .isSeen(isSeen)
                            .isRead(isRead)
                            .build();
                    notificationRepository.saveAndFlush(notification);
                }
            }
        }

        var hashtags = new ArrayList<>(Stream.of("Music", "Sport", "Gaming", "Style", "Programming Language",
                        "Youtuber", "Viral", "Memes", "Rap", "Art", "Trending", "Food", "Design", "Happy", "Love",
                        "Fashion", "Travel", "Podcasts", "Beauty", "Vacation", "Cooking", "Life", "Motivation",
                        "Study", "School", "Illustration", "Photography", "Book", "Film", "Football", "Comedy", "News",
                        "Entertainment", "Tiktok", "Youtube", "Facebook", "Instagram", "Live", "Tricks", "Remix",
                        "University", "Work", "Company", "Finance", "Workout", "Technology", "Social", "Vlog", "Job",
                        "Interview", "Environment", "War", "Ukraine", "Education", "Business")
                .map(Hashtag::new)
                .toList());
        hashtagRepository.saveAllAndFlush(hashtags);

        String[] locations = { "New York City, USA", "Tokyo, Japan", "London, UK", "Paris, France",
                "Rio de Janeiro, Brazil", "Moscow, Russia", "Dubai, UAE", "Rome, Italy", "Toronto, Canada",
                "Seoul, South Korea", "Berlin, Germany", "Cape Town, South Africa", "Mexico City, Mexico",
                "Hong Kong, China", "Mumbai, India", "Bangkok, Thailand", "Amsterdam, Netherlands", "Istanbul, Turkey",
                "Barcelona, Spain", "Buenos Aires, Argentina", "Singapore", "Vienna, Austria", "Dublin, Ireland",
                "Stockholm, Sweden", "Zurich, Switzerland", "Prague, Czech Republic", "Oslo, Norway" };

        var categories = new ArrayList<>(Stream.of("Autos & Vehicles", "Comedy", "Education", "Entertainment",
                        "Film & Animation", "Gaming", "Howto & Style", "Music", "News & Politics",
                        "Nonprofits & Activism", "People & Blogs", "Pets & Animals", "Science & Technology", "Sports",
                        "Travel & Events")
                .map(Category::new)
                .toList());
        categoryRepository.saveAllAndFlush(categories);

        var sampleVideos = List.of(
                Video.builder()
                        .title("15 Years Writing C++ - Advice for new programmers")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/15+Years+Writing+C%2B%2B+-+Advice+for+new+programmers.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/15+Years+Writing+C%2B%2B+-+Advice+for+new+programmers.mp4")
                        .durationSec(243)
                        .description("""
                                I'm a video game programmer and I've been using C++ as a programming language for 15 years. Of course in my time as a programmer I've branched out to other languages, but I'd say C++ has been the focus for me across my hobby and professional projects.""")
                        .build(),
                Video.builder()
                        .title("Dzeko & Torres - 2014 In 10 Minutes [62 Songs] (EDM)")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Dzeko+%26+Torres+-+2014+In+10+Minutes+%5B62+Songs%5D+(EDM).png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Dzeko+%26+Torres+-+2014+In+10+Minutes+%5B62+Songs%5D+(EDM).mp4")
                        .durationSec(615)
                        .description("""
                                •Proximity - Your favorite music you haven't heard yet.
                                » Facebook: https://bit.ly/FBProximity
                                » Twitter: https://bit.ly/ProximityTwitter
                                \n
                                Known for the end of the year mashups, Dzeko & Torres kills it with their 2014 mix. 62 songs in 10 minutes was tough but they did an insane job. Hope you all enjoy! :)
                                \n
                                •Dzeko & Torres:
                                  / dzekoandtorres
                                  / dzekoandtorres
                                  / dzekoandtorres
                                \n
                                Picture by: https://benadase.deviantart.com/
                                \n
                                If you'd like to submit a picture, you can send it here:
                                https://pandoric.com/contact
                                \n
                                Use the new template if you would like to submit a picture! :)
                                \n
                                \n
                                Timestamp!:
                                1. 0:00 Porter Robinson – Lionhearted (Arty Remix)
                                2. 0:00 Tritonal & Paris Blohm ft. Sterling Fox – Colors (Piano)
                                3. 0:10 Zhu – Faded (Acapella)
                                4. 0:46 Vicetone – United We Dance (Original Mix)
                                5. 0:52 Duke Dumont – Need U (Acapella)
                                6. 1:03 Michael Brun – Zenith (Original Mix)
                                7. 1:17 Vicetone – Low Down (Original Mix)
                                8. 1:23 MakJ & M35 – Go (Showtek Edit)
                                9. 1:24 Kryder & Tom Staar – Big Momma's House (Original Mix)
                                10. 1:35 David Guetta & Showtek ft. Vassy – Bad (Acapella)
                                """)
                        .build(),
                Video.builder()
                        .title("Mewone! - Escape (Original Mix)")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Mewone!+-+Escape+(Original+Mix)_4.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Mewone!+-+Escape+(Original+Mix)_4.mp4")
                        .durationSec(267)
                        .description("""
                                Escape
                                ●Yandex Music - https://vk.cc/cxDCqe
                                ●Spotify - https://vk.cc/cxDCsz
                                ●Apple Music - https://vk.cc/cxDCvi
                                \n
                                ●Soundcloud:  soundcloud.com/mewoneofficial
                                \n
                                 / mewoneofficial \s
                                ●Facebook - \s
                                \n
                                 / roman.moore..  .
                                ●VK
                                Roman Moore aka MewOne! - https://vk.com/mr_moore
                                Group - http://vk.com/mrmoore
                                \n
                                ●Video maker:\s
                                StrangeMan -  @strangemanedits
                                """)
                        .build(),
                Video.builder()
                        .title("Now in Android: 107 - KotlinConf, Android Studio updates, I-O recaps, AndroidX updates, and more!")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Now+in+Android-+107+-+KotlinConf%2C+Android+Studio+updates%2C+I-O+recaps%2C+AndroidX+updates%2C+and+more!.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Now+in+Android-+107+-+KotlinConf%2C+Android+Studio+updates%2C+I-O+recaps%2C+AndroidX+updates%2C+and+more!.mp4")
                        .durationSec(245)
                        .description("""
                                Welcome to Now in Android, your ongoing guide to what's new and notable in the world of Android development. In this episode we’ll cover Google @ KotlinConf, Android Studio updates, I/O recaps, AndroidX updates, and more!
                                \n
                                Chapters:
                                0:00 - Introduction
                                0:17 - Announcements
                                1:04 - Blog
                                2:19 - Videos
                                3:02 - AndroidX releases
                                3:36 - Conclusion
                                \n
                                Resources:
                                For links to these items, check out Now in Android #107 on Medium → https://goo.gle/3KI8qZO
                                \n
                                Now in Android podcast → https://goo.gle/podcast-nia
                                Now in Android articles → https://goo.gle/articles-nia
                                \n
                                Watch more Now in Android → https://goo.gle/now-in-android
                                Subscribe to Android Developers → https://goo.gle/AndroidDevs
                                \n
                                #Featured #AndroidDevelopers #NowInAndroid
                                \n
                                \n
                                \n
                                Speaker: Dan Galpin
                                Products Mentioned: Android""")
                        .build(),
                Video.builder()
                        .title("The #1 way to strengthen your mind is to use your body - Wendy Suzuki")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/The++1+way+to+strengthen+your+mind+is+to+use+your+body+-+Wendy+Suzuki.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/The+%231+way+to+strengthen+your+mind+is+to+use+your+body+-+Wendy+Suzuki.mp4")
                        .durationSec(365)
                        .description("""
                                Exercise gives your brain a “bubble bath of neurochemicals,” says Wendy Suzuki, a professor of neural science.\s
                                \n
                                Subscribe to Big Think on YouTube ►    / @bigthink \s
                                Up next, Forensic accountant explains why fraud thrives on Wall Street
                                 ►    • Forensic accountant explains why frau... \s
                                \n
                                Exercise can have surprisingly transformative impacts on the brain, according neuroscientist Wendy Suzuki. It has the power not only to boost mood and focus due to the increase in neurotransmitters like dopamine, serotonin, and noradrenaline, but also contributes to long-term brain health. Exercise stimulates the growth of new brain cells, particularly in the hippocampus, improving long-term memory and increasing its volume. Suzuki notes that you don’t have to become a marathon runner to obtain these benefits — even just 10 minutes of walking per day can have noticeable benefits. It just takes a bit of willpower and experimentation.\s
                                \n
                                0:00 My exercise epiphany
                                1:35 What is “runner’s high”?
                                2:40 The hippocampus & prefrontal cortex
                                3:32 Neuroplasticity: It’s never too late to move your body
                                \n
                                Read the video transcript ► https://bigthink.com/series/the-big-t...
                                \n
                                ----------------------------------------------------------------------------------\s
                                \n
                                About Wendy Suzuki:
                                Dr. Wendy A. Suzuki is a Professor of Neural Science and Psychology in the Center for Neural Science at New York University. She received her undergraduate degree in Physiology and Human Anatomy at the University of California, Berkeley in 1987, studying with Prof. Marion C. Diamond, a leader in the field of brain plasticity. She went on to earn her Ph.D. in Neuroscience from U.C. San Diego in 1993 and completed a post-doctoral fellowship at the National Institutes of Health before accepting her faculty position at New York University in 1998. Dr. Suzuki is author of the book Healthy Brain, Happy Life: A Personal Program to Activate Your Brain and Do Everything Better.
                                \n
                                ----------------------------------------------------------------------------------\s
                                \n
                                Read more of our stories on exercise:\s
                                The most damaging exercise myth
                                ► https://bigthink.com/health/most-dama...
                                No pain, no gain? Science debunks yet another exercise myth
                                ► https://bigthink.com/the-learning-cur...
                                In the West, yoga is exercise. In the East, it is something much bigger
                                ► https://bigthink.com/thinking/yoga-hi...
                                \n
                                ----------------------------------------------------------------------------------\s
                                \n
                                About Big Think | Smarter Faster™
                                ► Big Think\s
                                Our mission is to make you smarter, faster. Watch interviews with the world’s biggest thinkers on science, philosophy, business, and more. \s
                                ► Big Think+
                                Looking to ignite a learning culture at your company? Prepare your workforce for the future with educational courses from the world’s biggest thinkers. Trusted by Ford, Marriot, Bank of America, and many more. Learn how Big Think+ can empower your people today: https://bigthink.com/plus/?utm_source...
                                \n
                                ----------------------------------------------------------------------------------\s
                                \n
                                Want more Big Think?
                                ► Daily editorial features: https://bigthink.com/?utm_source=yout...
                                ► Get the best of Big Think right to your inbox: https://bigthink.com/subscribe/?utm_s...
                                ► Facebook: https://bigth.ink/facebook/
                                ► Instagram: https://bigth.ink/Instagram/
                                ► Twitter: https://bigth.ink/twitter/""")
                        .build(),
                Video.builder()
                        .title("Big Buck Bunny")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/SampleVideo_360x240_30mb.jpg")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Big+Buck+Bunny.mp4")
                        .durationSec(368)
                        .description("""
                                Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.
                                \n
                                Licensed under the Creative Commons Attribution license.
                                \n
                                https://www.bigbuckbunny.org""")
                        .build(),
                Video.builder()
                        .title("Why did my side-hustle fail How to validate business ideas")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Why+did+my+side-hustle+fail+How+to+validate+business+ideas.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Why+did+my+side-hustle+fail+How+to+validate+business+ideas.mp4")
                        .durationSec(505)
                        .description("""
                                 Last week I launched a side-hustle business with voice cloning AI, but things are not going well. Learn why most business ideas fail and develop techniques to improve your idea validation process.  \s
                                \n
                                 #tech #business #startups\s
                                 \n
                                 💬 Chat with Me on Discord
                                 \n
                                   / discord \s
                                 \n
                                 🔗 Resources
                                 \n
                                 The Art of the Side Hustle    • How to get rich as a solo software de... \s
                                 AI startup ideas    • 5 ideas for your own AI grift with Ch... \s
                                 My Voice Cloning Service https://vocalize.cloud
                                 Twitter report of business launch   / 1659930105127505920 \s
                                 \n
                                 🔥 Get More Content - Upgrade to PRO
                                 \n
                                 Upgrade at https://fireship.io/pro
                                 Use code YT25 for 25% off PRO access\s
                                 \n
                                 🎨 My Editor Settings
                                 \n
                                 - Atom One Dark\s
                                 - vscode-icons
                                 - Fira Code Font
                                 \n
                                 🔖 Topics Covered
                                 \n
                                 - Advice for solo businesses run by programmers
                                 - Building in public
                                 - Community building and viral marketing techniques
                                 - Top reasons businesses fail
                                 - Tips for launching a tech startup
                                 - How to validate business ideas
                                 - Running a software development side-hustle
                                 - Charles Bukowski Motivation""")
                        .build(),
                Video.builder()
                        .title("Mirzapur Season 3 - Official Teaser - Pankaj Tripathi, Ali Fazal, Shweta Tripathi, Rasika Dugal")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Mirzapur+Season+3+-+Official+Teaser+-+Pankaj+Tripathi%2C+Ali+Fazal%2C+Shweta+Tripathi%2C+Rasika+Dugal.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Mirzapur+Season+3+-+Official+Teaser+-+Pankaj+Tripathi%2C+Ali+Fazal%2C+Shweta+Tripathi%2C+Rasika+Dugal.mp4")
                        .durationSec(109)
                        .description("""
                                 Jungle mein bhaukaal machne wala hai!🔥
                                 #MirzapurOnPrime, July 5
                                                               \s
                                 About Prime Video: Prime Video is a premium streaming service that offers Prime members a collection of award-winning Amazon Original series, thousands of movies & TV shows—all with the ease of finding what they love to watch in one place. Prime Video is just one of the many benefits of a Prime membership, available for just ₹1499/ year.\s
                                 Included with Prime Video: Thousands of acclaimed TV shows & movies across languages & geographies, including Indian films such as Shershaah, Soorarai Pottru, Sardar Udham, Gehraiyaan, Jai Bhim, Jalsa, Shakuntala Devi, Sherni, Narappa, Sarpatta Parambarai, Kuruthi, Joji, Malik, and HOME, along with Indian-produced Amazon Original series like Farzi, Jubilee, Dahaad, The Family Man, Mirzapur, Made in Heaven, Four More Shots Please!, Mumbai Diaries 26/11, Suzhal – The Vortex, Modern Love, Paatal Lok, Bandish Bandits, Guilty Minds, Cinema Marte Dum Tak, and Amazon Original movies like Maja Ma & Ammu. Also included are popular global Amazon Originals like Citadel, The Lord of The Rings: The Rings of Power, Reacher, Tom Clancy's Jack Ryan, The Boys, Hunters, Fleabag, The Marvelous Mrs. Maisel, & many more, available for unlimited streaming as part of a Prime membership. Prime Video includes content across Hindi, Marathi, Gujarati, Tamil, Telugu, Kannada, Malayalam, Punjabi, & Bengali.\s
                                 Prime Video Mobile Edition: Consumers can also enjoy Prime Video’s exclusive content library with Prime Video Mobile Edition at ₹599 per year. This single-user, mobile-only annual video plan offers everyone access to high-quality entertainment exclusively on their mobile devices. Users can sign-up for this plan via the Prime Video app (on Android) or website.\s
                                 Instant Access: Prime Members can watch anywhere, anytime on the Prime Video app for smart TVs, mobile devices, Fire TV, Fire TV stick, Fire tablets, Apple TV, & multiple gaming devices. Prime Video is also available to consumers through Airtel and Vodafone pre-paid & post-paid subscription plans. In the Prime Video app, Prime members can download episodes on their mobile devices & tablets & watch anywhere offline at no additional cost.
                                 Enhanced experiences: Make the most of every viewing with 4K Ultra HD- & High Dynamic Range (HDR)-compatible content. Go behind the scenes of your favourite movies & TV shows with exclusive X-Ray access, powered by IMDb. Save it for later with select mobile downloads for offline viewing.\s
                                 Video Entertainment Marketplace: In addition to a Prime Video subscription, customers can also purchase add-on subscriptions to other streaming services, as well as, get rental access to movies on Prime Video. Prime Video Channels: Prime Video Channels offers friction-free & convenient access to a wide range of premium content from multiple video streaming services all available at a single destination – Prime Video website & apps. Prime Members can buy add-on subscriptions & enjoy a hassle-free entertainment experience, simplified discovery, frictionless payments, & more. Rent: Consumers can enjoy even more movies from new releases to classic favourites, available to rent – no Prime membership required. View titles available by visiting primevideo.com/storeVideo. The rental destination can be accessed via the STORE tab on primevideo.com & the Prime Video app on Android smart phones, smart-TVs, connected STBs, & Fire TV stick.
                                                              \s
                                 #mirzapur #primevideoindia
                                \s""")
                        .build(),
                Video.builder()
                        .title("Alan Walker, Ina Wroldsen – Barcelona (Official Music Video)")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Alan+Walker%2C+Ina+Wroldsen+%E2%80%93+Barcelona+(Official+Music+Video)-(480p).png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Alan+Walker%2C+Ina+Wroldsen+%E2%80%93+Barcelona+(Official+Music+Video)-(480p).mp4")
                        .durationSec(223)
                        .description("""
                                 This summer we’re hosting the first-ever Walkerworld Creator Games, a series of challenges that will help you share your creations with the world. In the end, 4 creators will each win $10,000. To join, start creating and tag your work with #WCG24.
                                                             \s
                                 In that regard, we’re unveiling a whole new area of the Walkerworld map: the Walkerworld Sculpture Garden. Catch a first glimpse of it in this video for ‘Barcelona’. See you at the opening ceremony on June 22nd.
                                                            \s
                                 Listen to ‘Barcelona’ here: https://alanwalker.lnk.to/Barcelona
                                 Remember to subscribe to the channel and turn on 🔔
                                                            \s
                                 -Alan
                                                            \s
                                 ////////////////\s
                                                            \s
                                 Lyrics:
                                                              \s
                                 [Intro]
                                 La-la-la-la
                                 La-la-la-la
                                 La-la-la-la
                                                             \s
                                 [Verse 1]
                                 I found my feet again, and now I need to teach them how to run
                                 I found the beat I lost like you left in the chorus of a song
                                 (La-la-la-la, la-la-la-la)
                                 I left the anger I was feeling at a bar in Barcelona
                                 Another brick, another wall
                                 And any day now I'll be okay, I won't miss you anymore
                                                            \s
                                 [Chorus]
                                 La-la-la-la
                                 I'm gonna dance until I dance you off my mind
                                 La-la-la-la-la-la, la-la-la-la
                                 I'm gonna sing until I sing you out of sight
                                 La-la-la-la-la-la, la-la-la-la
                                 I'm gonna walk until I walk you out the door
                                 La-la-la-la-la-la, la-la-la-la
                                 And any day now I'll be okay in the night
                                 Won't miss you anymore
                                                        \s
                                 [Drop]
                                 (Barcelona)
                                 (Barcelona)
                                             \s
                                 [Verse 2]
                                 Spoke to the people on the beaches where we used to sit alone
                                 La-la-la-la
                                 Swam in the deep end of the ocean and I threw away my phone
                                 La-la-la-la, la-la-la-la
                                 I kissed a girl who don't resemble you at all in Barcelona
                                 Another step, another shore
                                 And any day now I'll be okay, I won't miss you anymore
                                                             \s
                                 [Chorus]
                                 La-la-la-la
                                 I'm gonna dance until I dance you off my mind
                                 La-la-la-la-la-la, la-la-la-la
                                 I'm gonna sing until I sing you out of sight
                                 La-la-la-la-la-la, la-la-la-la
                                 I'm gonna walk until I walk you out the door
                                 La-la-la-la-la-la, la-la-la-la
                                 And any day now I'll be okay in the night
                                 Won't miss you anymore
                                                            \s
                                 [Drop]
                                 (Barcelona)
                                 (Barcelona)
                                                            \s
                                 [Bridge]
                                 I left the love you gave to me out on the streets of Barcelona
                                 Another step, another try
                                 And any day now, I'm gonna dance you off my mind
                                                            \s
                                 [Drop]
                                 (Barcelona)
                                 (Barcelona)
                                                             \s
                                 [Bridge]
                                 I left the love you gave to me out on the streets of Barcelona
                                 Another step, another try
                                 And any day now, I'm gonna dance you off my mind
                                                            \s
                                 [Chorus]
                                 La-la-la-la
                                 I'm gonna dance until I dance you off my mind
                                 La-la-la-la-la-la, la-la-la-la
                                 I'm gonna sing until I sing you out of sight
                                 La-la-la-la-la-la, la-la-la-la
                                 I'm gonna walk until I walk you out the door
                                 La-la-la-la-la-la, la-la-la-la
                                 And any day now I'll be okay in the night
                                 Won't miss you anymore
                                                            \s
                                 ////////////////\s
                                                             \s
                                 Credits:\s
                                 A Kreatell Creation\s
                                                             \s
                                 Director: Kristian Berg
                                 Produced by Eirik Heldal // Apparat.studio
                                 Key assets: Wirat Johannessen
                                 Animation by: NORTH Studio
                                 Head of CG: Finn Christian Skimmeland
                                 Video Sound design: Thomas "Tomtom" Haugland
                                                              \s
                                 ////////////////\s
                                                            \s
                                 Connect with me 📲
                                 👉Join the W41K3R5: https://w41k3r.com/
                                 👉Instagram:   / alanwalkermusic  \s
                                 👉TikTok:   / alanwalkermusic  \s
                                 👉Facebook:   / alanwalkermusic  \s
                                 👉Twitter:   / iamalanwalker  \s
                                 👉Snapchat:   / alanwalkermusic  \s
                                 👉Discord:   / discord \s
                                                               \s
                                 👕 Official Merchandise: https://store.alanwalker.com/
                                                             \s
                                 🎵Listen & Follow Walker Radio here: https://AlanWalker.lnk.to/WalkerRadio     \s
                                                             \s
                                 #AlanWalker #WalkersJoin #Walkerworld #barcelona
                                \s""")
                        .build(),
                Video.builder()
                        .title("SƠN TÙNG M-TP - ĐỪNG LÀM TRÁI TIM ANH ĐAU - OFFICIAL MUSIC VIDEO")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/S%C6%A0N+T%C3%99NG+M-TP+_+%C4%90%E1%BB%AANG+L%C3%80M+TR%C3%81I+TIM+ANH+%C4%90AU+_+OFFICIAL+MUSIC+VIDEO-(480p).png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/S%C6%A0N+T%C3%99NG+M-TP+_+%C4%90%E1%BB%AANG+L%C3%80M+TR%C3%81I+TIM+ANH+%C4%90AU+_+OFFICIAL+MUSIC+VIDEO-(480p).mp4")
                        .durationSec(325)
                        .description("""
                                 Hãy cùng thưởng thức ca khúc ĐỪNG LÀM TRÁI TIM ANH ĐAU ngay tại đây nhé: 👉🏻 👉🏻 👉🏻  https://vivienm.lnk.to/DLTTAD 💍❤️‍🩹🧩
                                 \s
                                 #DLTTAD #SonTungMTP #DungLamTraiTimAnhDau\s
                                 \s
                                 🚫🤲🏻♥️🙆🏻‍♂️😢
                                                              \s
                                 ▶ More information about Sơn Tùng M-TP:\s
                                   / mtp.fan \s
                                   / sontungmtp \s
                                    / sontungmtp \s
                                   / tiger050794  \s
                                   / sontungmtp777 \s
                                 @Spotify: https://spoti.fi/2HPWs20
                                 @Itunes: https://apple.co/2rlSl3w
                                                              \s
                                 ▶More information about M-TP Talent:
                                   / mtptalent \s
                                   / mtptalent \s
                                   / mtptalent \s
                                                             \s
                                 ▶ More about M-TP ENTERTAINMENT
                                   / mtptown \s
                                 https://mtpentertainment.com\s
                                   / mtpent_official \s
                                   / mtpent_official \s
                                                               \s
                                 ▶ CLICK TO SUBSCRIBE:  http://popsww.com/sontungmtp
                                 #sontungmtp #sontung #mtp #mtpentertainment
                                \s""")
                        .build(),
                Video.builder()
                        .title("React in 100 Seconds")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/React+in+100+Seconds.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/React+in+100+Seconds.mp4")
                        .durationSec(127)
                        .description("""
                                React is a little JavaScript library with a big influence over the webdev world. Learn the basics of React in 100 Seconds https://fireship.io/tags/react/

                                How I make these Videos    • How I Make Videos for Programmers (on... \s

                                #react #webdev #100SecondsOfCode

                                Install the quiz app 🤓

                                iOS https://itunes.apple.com/us/app/fires...
                                Android https://play.google.com/store/apps/de...

                                Upgrade to Fireship PRO at https://fireship.io/pro
                                Use code lORhwXd2 for 25% off your first payment.\s

                                My VS Code Theme

                                - Atom One Dark\s
                                - vscode-icons
                                - Fira Code Font
                                """)
                        .build(),
                Video.builder()
                        .title("How to keep your house cool in the summer without AC")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/How+to+keep+your+house+cool+in+the+summer+without+AC-(720p).png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/How+to+keep+your+house+cool+in+the+summer+without+AC-(720p).mp4")
                        .durationSec(115)
                        .description("""
                                Beat the heat with tips on how to keep your house cool without air conditioning.

                                To read more: http://cbc.ca/1.4778478

                                »»» Subscribe to CBC News to watch more videos: http://bit.ly/1RreYWS

                                Connect with CBC News Online:

                                For breaking news, video, audio and in-depth coverage: http://bit.ly/1Z0m6iX
                                Find CBC News on Facebook: http://bit.ly/1WjG36m
                                Follow CBC News on Twitter: http://bit.ly/1sA5P9H
                                For breaking news on Twitter: http://bit.ly/1WjDyks
                                Follow CBC News on Instagram: http://bit.ly/1Z0iE7O

                                Download the CBC News app for iOS: http://apple.co/25mpsUz
                                Download the CBC News app for Android: http://bit.ly/1XxuozZ

                                »»»»»»»»»»»»»»»»»»
                                For more than 75 years, CBC News has been the source Canadians turn to, to keep them informed about their communities, their country and their world. Through regional and national programming on multiple platforms, including CBC Television, CBC News Network, CBC Radio, CBCNews.ca, mobile and on-demand, CBC News and its internationally recognized team of award-winning journalists deliver the breaking stories, the issues, the analyses and the personalities that matter to Canadians.
                                """)
                        .build(),
                Video.builder()
                        .title("JavaScript for the Haters")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/JavaScript+for+the+Haters.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/JavaScript+for+the+Haters.mp4")
                        .durationSec(169)
                        .description("""
                                Why does everybody hate JavaScript so much? A complete roast of JS that highlights the strongest criticisms against the world's most popular programming language.\s
                                \n
                                #javascript #roast #comedy\s
                                \n
                                🔥 Black Friday Discount
                                \n
                                https://fireship.io/pro
                                \n
                                🔥 Use code BLACKFIRE at checkout for 40% off\s
                                \n
                                💬 Chat with Me on Discord
                                \n
                                  / discord \s
                                \n
                                🔗 Resources
                                \n
                                - Full JavaScript Course https://fireship.io/courses/js
                                - JavaScript in 100 Seconds    • JavaScript in 100 Seconds \s
                                - React for the Haters    • React for the Haters in 100 Seconds \s
                                \n
                                🎨 My Editor Settings
                                \n
                                - Atom One Dark\s
                                - vscode-icons
                                - Fira Code Font
                                \n
                                🔖 Topics Covered
                                \n
                                - JS Roast
                                - Funny JavaScript features
                                - Worst aspects of JavaScript
                                - JavaScript pitfalls
                                - JavaScript drama
                                """)
                        .build(),
                Video.builder()
                        .title("We Are Making a VIDEO GAME")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/We+Are+Making+a+VIDEO+GAME-(720p60).png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/We+Are+Making+a+VIDEO+GAME-(720p60).mp4")
                        .durationSec(60)
                        .description("""
                                The universe is a fascinating yet unexplored place – it's about time to change that!  🚀
                                Join the Star Birds on their journey across space, mine asteroids, and discover new technologies to advance even further!
                                And who knows? Maybe you'll discover something that's truly out of this world... 🌚
                                Star Birds is coming to your Steam library in 12,025!
                                \n
                                Wishlist now:  https://kgs.link/StarBirds
                                \n
                                \n
                                OUR CHANNELS
                                ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
                                German:        https://kgs.link/youtubeDE
                                Spanish:        https://kgs.link/youtubeES
                                French:          https://kgs.link/youtubeFR
                                Portuguese:  https://kgs.link/youtubePT
                                Arabic:           https://kgs.link/youtubeAR
                                Hindi:             https://kgs.link/youtubeHI
                                Japanese:     https://kgs.link/youtubeJA
                                Korean:          https://kgs.link/youtubeKO
                                \n
                                \n
                                HOW CAN YOU SUPPORT US?
                                ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
                                This is how we make our living and it would be a pleasure if you support us!
                                \n
                                Get Products designed with ❤ https://shop-us.kurzgesagt.org \s
                                Join the Patreon Bird Army 🐧  https://kgs.link/patreon \s
                                \n
                                \n
                                DISCUSSIONS & SOCIAL MEDIA
                                ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
                                TikTok:           https://kgs.link/tiktok
                                Reddit:            https://kgs.link/reddit
                                Instagram:     https://kgs.link/instagram
                                Twitter:           https://kgs.link/twitter
                                Facebook:      https://kgs.link/facebook
                                Discord:          https://kgs.link/discord
                                Newsletter:    https://kgs.link/newsletter


                                OUR VOICE
                                ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
                                The Kurzgesagt voice is from\s
                                Steve Taylor:  https://kgs.link/youtube-voice


                                OUR MUSIC ♬♪
                                ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
                                700+ minutes of Kurzgesagt Soundtracks by Epic Mountain:

                                Spotify:            https://kgs.link/music-spotify
                                Soundcloud:   https://kgs.link/music-soundcloud
                                Bandcamp:     https://kgs.link/music-bandcamp
                                Youtube:          https://kgs.link/music-youtube
                                Facebook:       https://kgs.link/music-facebook

                                If you want to help us caption this video, please send subtitles to subtitle@kurzgesagt.org
                                You can find info on what subtitle files work on YouTube here:
                                https://support.google.com/youtube/an...
                                Thank you!
                                """)
                        .build(),
                Video.builder()
                        .title("The Simpsons- Homer has a Crayon in his Brain")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/The-Simpsons-Homer-has-a-Crayon-in-his-Brain-_480p_.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/The+Simpsons-+Homer+has+a+Crayon+in+his+Brain-(480p).mp4")
                        .durationSec(195)
                        .description("The Simpsons- Homer has a Crayon in his Brain\n")
                        .build(),
                Video.builder()
                        .title("Family Guy - This is why Sweden, Chris, never Finland")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Family-Guy-This-is-why-Sweden_-Chris_-never-Finland.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Family+Guy+-+This+is+why+Sweden%2C+Chris%2C+never+Finland.mp4")
                        .durationSec(59)
                        .description("Season 19, episode 14: The Marrying Kind.")
                        .build(),
                Video.builder()
                        .title("DNA Study - Better Coffee - BBC News Review")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/DNA-Study-Better-Coffee-BBC-News-Review-_480p_.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/DNA+Study-+Better+Coffee-+BBC+News+Review-(480p).mp4")
                        .durationSec(361)
                        .description("""
                                Thanks for watching News Review. This is the last episode in its current format. From next week, we will be publishing our new ‘Learning English from the News’ audio podcast right here on our YouTube channel. Don’t worry, you’ll still be learning vocabulary from the latest news headlines with your favourite presenters! We hope you like the new programme.
                                \n
                                Researchers in Italy have created a complete genetic map of Arabica coffee. They say this will allow us to create new flavours of the world’s most popular drink. It could also lead to coffee plants that can cope with climate change.
                                \n
                                Key words and phrases:\s
                                \n
                                shines a light - draws attention to something
                                🔎 The documentary aims to shine a light on challenges facing endangered species.\s
                                🔎 The artist hopes to shine a light on the beauty of everyday moments.\s
                                \n
                                buzz - an atmosphere of excitement
                                🔎 The new smartphone generated a lot of buzz in the tech community.\s
                                🔎 There’s a lot of buzz around the restaurant’s new and innovative menu.\s
                                \n
                                brew - a cup of coffee or tea
                                🔎 The smell of the morning brew filled the kitchen.
                                🔎 Will you make me a brew? I take my tea with milk and no sugar.
                                \n
                                (Images via Getty Images)
                                \n
                                ✔️ 0:00 - Introduction\s
                                ✔️ 0:26 - Story\s
                                ✔️ 1:12 - Headline 1
                                ✔️ 2:57 - Headline 2
                                ✔️ 4:18 - Headline 3
                                ✔️ 5:34 - Language summary
                                \n
                                More episodes of News Review 👉 https://bit.ly/2K0Fdf0\s
                                \n
                                More popular videos to help you improve your English:\s
                                ⭐Could caffeine cut obesity? BBC News Review 👉    • Could caffeine cut obesity?: BBC News... \s
                                ⭐ The smell of coffee - 6 Minute English 👉    • The smell of coffee - 6 Minute English \s
                                ⭐ Exam Skills: 4 tips for listening exams 👉    • Exam Skills: 4 tips for listening exams \s
                                \n
                                🤩🤩🤩 SUBSCRIBE to our YouTube channel for more English videos and podcast English to help you improve your English 👉 https://tinyurl.com/ps3hplv
                                \n
                                ✔️ Visit our website 👉 https://www.bbc.co.uk/learningenglish
                                ✔️ Follow us on Instagram 👉   / bbclearningenglish \s
                                ✔️ Find us on Facebook 👉   / bbclearningenglish.multimedia \s
                                ✔️ Join us on TikTok 👉   / bbclearningenglish \s
                                \n
                                We like receiving and reading your comments - please use English when you comment 😊
                                #learnenglish #newsenglish #coffee
                                """)
                        .build(),
                Video.builder()
                        .title("Are you following your dreams")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Are-you-following-your-dreams.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Are+you+following+your+dreams.mp4")
                        .durationSec(379)
                        .description("""
                                In this programme, Neil and Beth talk about dreams. You'll hear from two people who dared to follow their dreams and are happy to have done so. You'll also learn some related English vocabulary along the way.
                                \n
                                This week's question
                                In 2012, Australian nurse, Bronnie Ware, wrote her bestselling book, The Top Five Regrets of the Dying, after interviewing terminally ill patients about their life regrets. So, what do you think their top regret was?
                                \n
                                a)    I wish I hadn’t worked so hard.
                                b)    I wish I had followed my dreams.
                                c)    I wish I’d made more money.
                                \n
                                Listen to the programme to find out the answer.\s
                                \n
                                Vocabulary
                                ✔️ utopia - perfect, ideal society where everyone is happy and gets along with each other
                                ✔️ struggle with (something) - find it difficult to accept or even think about (something)
                                ✔️ outlandish - strange, unusual and difficult to like
                                ✔️ conquer - control something by force\s
                                ✔️ humble - not proud or arrogant
                                ✔️ a grain of sand - small and insignificant, yet at the same time important, part of a whole
                                \n
                                [Cover: Getty Images]
                                You can download audio and text here 👉https://www.bbc.co.uk/learningenglish...
                                \n
                                😊 How to talk about regrets 👉     • Regret +  ing or + to - English In A ... \s
                                \n
                                More 6 Minute English episodes:
                                😊 Do emojis make language better? 👉    • Do emojis make language better? - 6 M... \s
                                😊 People who can taste words 👉    • People who can taste words - 6 Minute... \s
                                😊 Finding your way in space 👉    • Finding your way in space - 6 Minute ... \s
                                😊 Britain's love affair with coffee 👉    • Britain's love affair with coffee - 6... \s
                                😊 Intimacy on screen 👉    • Intimacy on screen - 6 Minute English \s
                                😊 Personalised diets 👉    • Personalised diets - 6 Minute English \s
                                😊 How green is nuclear energy? 👉    • How green is nuclear energy? - 6 Minu... \s
                                😊 Why we forget the things we learn 👉    • Why we forget the things we learn - 6... \s
                                \n
                                🤩🤩🤩 SUBSCRIBE to our YouTube channel for more English videos and podcast English to help you improve your English 👉 http://tinyurl.com/ps3hplv
                                \n
                                ✔️ Visit our website 👉 https://www.bbc.co.uk/learningenglish
                                ✔️ Follow us on Instagram 👉   / bbclearningenglish \s
                                ✔️ Find us on Facebook 👉   / bbclearningenglish.multimedia \s
                                ✔️ Join us on TikTok 👉   / bbclearningenglish \s
                                \n
                                We like receiving and reading your comments - please use English when you comment 😊
                                #learnenglish #followyourdreams #englishvocabulary #bbclearningenglish
                                """)
                        .build(),
                Video.builder()
                        .title("Why India's diaspora is so powerful")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Why-India_s-diaspora-is-so-powerful-_480p_.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Why+India's+diaspora+is+so+powerful-(480p).mp4")
                        .durationSec(227)
                        .description("""
                                India has the largest diaspora in the world. But that isn't the only reason why Indian migrants are so influential—in business, science and diplomacy.
                                \n
                                00:00 - Why India’s diaspora matters
                                00:28 - Size
                                01:20 - Power
                                02:11 - Diplomacy
                                \n
                                Sign up to The Economist’s daily newsletter: https://econ.st/3QAawvI
                                \n
                                India’s diaspora is bigger and more influential than any in history: https://econ.st/3J6sp3V\s
                                \n
                                Indian firms are flocking to the United Arab Emirates: https://econ.st/3CrkZV2\s
                                \n
                                India’s future will be shaped by its expats, says Gaurav Dalmia: https://econ.st/3X2qvqG
                                """)
                        .build(),
                Video.builder()
                        .title("Luke Combs - All I Ever Do Is Leave (Official Music Video)")
                        .location("https://vs-bucket-thumbnails.s3.ap-southeast-1.amazonaws.com/Luke-Combs-All-I-Ever-Do-Is-Leave-_Official-Music-Video_-_480p_.png")
                        .videoUrl("https://vs-bucket-videos.s3.ap-southeast-1.amazonaws.com/Luke+Combs+-+All+I+Ever+Do+Is+Leave+(Official+Music+Video)-(480p).mp4")
                        .durationSec(202)
                        .description("""
                                Listen to “All I Ever Do Is Leave” from Luke’s new album, “Fathers & Sons”: https://LC.lnk.to/FathersandSons\s
                                \n
                                Follow Luke:\s
                                Instagram: https://LC.lnk.to/profileYT/instagram\s
                                Facebook: https://LC.lnk.to/profileYT/facebook\s
                                Twitter: https://LC.lnk.to/profileYT/twitter\s
                                TikTok: https://LC.lnk.to/profileYT/tiktok\s
                                Twitch: https://LC.lnk.to/profileYT/twitch\s
                                Subscribe to his channel: https://LC.lnk.to/profileYT/youtube\s
                                Website/Tour Dates/Bootleggers Fan Club: https://LC.lnk.to/profileYT/officialsite\s
                                \n
                                Lyrics:
                                \s
                                Worn out stitches on a baseball
                                Up against the wall
                                Me and myself trying to play catch
                                ‘Cause Daddy ain’t home yet
                                I was Batman and Robin and the Joker, too
                                It was Gotham City lonesome in the living room
                                And Mama’d say, “You pray
                                ‘Cause your Daddy’s gonna be late”
                                \s
                                But the lights stayed on somehow
                                And love filled up our little house
                                Yeah life ain’t always what it seems
                                ‘Cause I thought all he ever did was leave
                                \s
                                But somebody played tooth fairy
                                Left the cash, took out the trash
                                Made Mama feel pretty
                                Put a dent in the whiskey
                                There were tip toe prayers and midnight kisses
                                I love you’s and little boy wishes for Saturday
                                When we could play all day
                                \s
                                But the lights stayed on somehow
                                And love filled up our little house
                                Yeah life ain’t always what it seems
                                ‘Cause I thought all he ever did was leave
                                \s
                                Worn out stitches on a baseball
                                Up against the wall
                                Him and himself trying to play catch
                                ‘Cause I ain’t home yet
                                \s
                                But the lights stay on somehow
                                And love fills up our little house
                                Yeah life ain’t always what it seems
                                I hope he don’t think
                                I hope he don’t think
                                All I ever do is leave
                                All I ever do is leave
                                \n
                                https://vevo.ly/Tqy6RJ
                                """)
                        .build()
        );

        var videos = new Video[49];
        for (int i = 1; i < 50; i++) {
            var sampleVideo = sampleVideos.get(new Random().nextInt(sampleVideos.size()));

            var isMadeForKids = new Random().nextInt() % 3 == 0;

            var defaultThumbnail = new Thumbnail();
            defaultThumbnail.setType(Thumbnail.Type.DEFAULT);
            defaultThumbnail.setUrl(sampleVideo.getLocation());
            defaultThumbnail.setWidth(720);
            defaultThumbnail.setHeight(450);

            var mediumThumbnail = new Thumbnail();
            mediumThumbnail.setType(Thumbnail.Type.MEDIUM);
            mediumThumbnail.setUrl(sampleVideo.getLocation());
            mediumThumbnail.setWidth(1280);
            mediumThumbnail.setHeight(720);

            var video = Video.builder()
                    .title(sampleVideo.getTitle())
                    .description(sampleVideo.getDescription())
                    .thumbnails(List.of(defaultThumbnail, mediumThumbnail))
                    .videoUrl(sampleVideo.getVideoUrl())
                    .durationSec(sampleVideo.getDurationSec())
                    .publishedAt(LocalDateTime.of(2024, new Random().nextInt(1, 5),
                            i % 30 != 0 ? i % 30 : 1, i % 24, 0, 0))
                    .privacy(new Random().nextInt() % 3 == 0 ? privatePrivacy : publicPrivacy)
                    .commentAllowed(i % 3 != 0)
                    .madeForKids(isMadeForKids)
                    .ageRestricted(!isMadeForKids && new Random().nextInt() % 3 == 0)
                    .category(categories.get(new Random().nextInt(categories.size())))
                    .location(new Random().nextInt() % 3 == 0 ?
                            locations[new Random().nextInt(locations.length)] :
                            null)
                    .user(users[new Random().nextInt(9)])
                    .build();
            video.setVideoStatistic(new VideoStatistic());

            Collections.shuffle(hashtags);
            int start = new Random().nextInt(0, hashtags.size() - 5),
                    end = start + new Random().nextInt(5);
            video.setHashtags(hashtags.subList(start, end));
            videoRepository.saveAndFlush(video);
            videos[i - 1] = video;

            var notificationObject = NotificationObject.builder()
                    .objectId(video.getId())
                    .objectType(NotificationObject.ObjectType.VIDEO)
                    .actionType(2)
                    .message(video.getUser().getUsername() + " uploaded: " + video.getTitle())
                    .publishedAt(LocalDateTime.now())
                    .build();
            notificationObjectRepository.saveAndFlush(notificationObject);

            var recipients = followRepository
                    .findAllByUserId(video.getUser().getId(), Pageable.unpaged())
                    .map(Follow::getFollower);

            var notifications = new ArrayList<Notification>();
            for (var recipient : recipients) {
                var isSeen = new Random().nextBoolean();
                var isRead = isSeen && new Random().nextBoolean();
                var notification = Notification.builder()
                        .notificationObject(notificationObject)
                        .actor(video.getUser())
                        .recipient(recipient)
                        .isSeen(isSeen)
                        .isRead(isRead)
                        .build();
                notifications.add(notification);
            }
            notificationRepository.saveAllAndFlush(notifications);

            for (int j = 0; j < new Random().nextInt(20); j++) {
                var user = users[new Random().nextInt(users.length)];
                for (int k = 0; k < new Random().nextInt(3); k++) {
                    var viewHistory = new ViewHistory();
                    viewHistory.setUser(user);
                    viewHistory.setVideo(video);
                    viewHistory.setPublishedAt(LocalDateTime.now());
                    viewHistory.setViewedDurationSec(new Random().nextInt(0,
                            video.getDurationSec() + 1));
                    viewHistoryRepository.saveAndFlush(viewHistory);
                }
            }

            var comments = List.of(
                    "Em rất thích các video về thực động vật kể cả sinh vật , anh làm về các video đó đi",
                    "Probably 50% of that building is all used to storeVideo memes",
                    "At the very moment you're watching this video , your data is being stored in there",
                    "2:18 First layer of security: moats with alligators",
                    "I need to delete my google history in that building",
                    "Bạn hát bài hát. Từ tiền tuyển tôi về hay quá. Tôi chúc bạn luôn mạnh khỏe gặp nhiều may mắn trong cuộc sống. Và có nhiều người nghe nhạc của bạn nhé",
                    "Bạn hát bài nào cũng hay"
            );

            for (int j = 0; j < new Random().nextInt(0, 15); j++) {
                var user = users[new Random().nextInt(users.length)];
                var comment = new Comment();
                comment.setUser(user);
                comment.setVideo(video);
                comment.setText(comments.get(new Random().nextInt(comments.size())));
                comment.setPublishedAt(LocalDateTime.now());
                commentRepository.saveAndFlush(comment);

                if (new Random().nextInt(20) == 0) {
                    for (int k = 0; k < new Random().nextInt(5); k++) {
                        var replyUser = users[new Random().nextInt(users.length)];
                        var replyComment = new Comment();
                        replyComment.setUser(replyUser);
                        replyComment.setVideo(video);
                        replyComment.setText(comments.get(new Random().nextInt(comments.size())));
                        replyComment.setPublishedAt(LocalDateTime.now());
                        replyComment.setParent(comment);
                        commentRepository.saveAndFlush(replyComment);
                    }
                }

                if (new Random().nextInt(10) == 0) {
                    for (int k = 0; k < new Random().nextInt(10); k++) {
                        var commentUser = users[new Random().nextInt(users.length)];
                        var commentRating = new CommentRating();
                        commentRating.setComment(comment);
                        commentRating.setUser(commentUser);
                        commentRating.setRating(new Random().nextBoolean() ?
                                CommentRating.Rating.LIKE :
                                CommentRating.Rating.DISLIKE);
                        commentRating.setPublishedAt(LocalDateTime.now());
                        commentRatingRepository.saveAndFlush(commentRating);
                    }
                }
            }
        }

        for (var user : users) {
            for (var video : videos) {
                if (new Random().nextInt(10) < 3) {
                    var videoRating = new VideoRating();
                    videoRating.setUser(user);
                    videoRating.setVideo(video);
                    videoRating.setPublishedAt(LocalDateTime.now());
                    videoRating.setRating(new Random().nextInt(10) > 2 ?
                            VideoRating.Rating.LIKE :
                            VideoRating.Rating.DISLIKE);
                    videoRatingRepository.saveAndFlush(videoRating);

                    if (videoRating.getRating() == VideoRating.Rating.LIKE) {
                        var playlist = playlistRepository.findLikedVideosPlaylistByUserId(user.getId());
                        var playlistItem = new PlaylistItem();
                        playlistItem.setPlaylist(playlist);
                        playlistItem.setVideo(video);
                        playlistItem.setPriority(playlistItemRepository.getMaxPriorityByPlaylistId(playlist.getId()));
                        playlistItemRepository.saveAndFlush(playlistItem);
                    }
                }
            }
        }

        var playlistName = List.of(
                "Music",
                "My Video",
                "Pop Music",
                "Danger Moments"
        );

        var videoList = new ArrayList<>(Arrays.stream(videos).toList());
        for (var user : users) {
            for (int i = 0; i < new Random().nextInt(4); i++) {
                var playlist = new Playlist();
                playlist.setUser(user);
                playlist.setTitle(playlistName.get(new Random().nextInt(playlistName.size())));
                playlist.setPublishedAt(LocalDateTime.now());
                playlist.setDefaultType(null);
                playlist.setPrivacy(new Random().nextInt() % 3 == 0 ? privatePrivacy : publicPrivacy);
                playlistRepository.saveAndFlush(playlist);

                Collections.shuffle(videoList);
                var randomVideos = videoList.subList(0, new Random().nextInt(5));
                for (var video : randomVideos) {
                    var playlistVideo = new PlaylistItem();
                    playlistVideo.setPlaylist(playlist);
                    playlistVideo.setVideo(video);
                    playlistVideo.setPriority(playlistItemRepository.getMaxPriorityByPlaylistId(playlist.getId()));
                    playlistItemRepository.saveAndFlush(playlistVideo);
                }
            }
        }

        log.info("Completed data initialization");
    }
}
