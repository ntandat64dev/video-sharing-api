package com.example.videosharingapi.config.runner;

import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.repository.*;
import lombok.RequiredArgsConstructor;
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

/**
 * An {@link ApplicationRunner} used to initialize the database for testing purpose.
 */
@Profile("init")
@Component
@RequiredArgsConstructor
public class InsertTestDataRunner implements ApplicationRunner {

    private final CommentRatingRepository commentRatingRepository;
    private final CommentRepository commentRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final VideoRepository videoRepository;
    private final HashtagRepository hashtagRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final PrivacyRepository privacyRepository;
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;
    private final NotificationObjectRepository notificationObjectRepository;
    private final NotificationRepository notificationRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var publicPrivacy = privacyRepository.saveAndFlush(new Privacy(Privacy.Status.PUBLIC));
        var privatePrivacy = privacyRepository.saveAndFlush(new Privacy(Privacy.Status.PRIVATE));

        var roleAdmin = new Role("ADMIN");
        var roleUser = new Role("USER");
        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);

        var users = new User[9];
        for (int i = 1; i < 10; i++) {
            var user = User.builder()
                    .username("user%s".formatted(i))
                    .password(passwordEncoder.encode(new String(new char[8])
                            .replace("\0", String.valueOf(i % 9))))
                    .roles(i == 1
                            ? List.of(roleAdmin, roleUser)
                            : List.of(roleUser))
                    .publishedAt(LocalDateTime.now())
                    .build();

            var defaultThumbnail = new Thumbnail();
            defaultThumbnail.setType(Thumbnail.Type.DEFAULT);
            defaultThumbnail.setUrl("https://via.placeholder.com/100x100.png/d13328/fff?text=Avatar");
            defaultThumbnail.setWidth(100);
            defaultThumbnail.setHeight(100);

            var mediumThumbnail = new Thumbnail();
            mediumThumbnail.setType(Thumbnail.Type.MEDIUM);
            mediumThumbnail.setUrl("https://via.placeholder.com/200x200.png/d13328/fff?text=Avatar");
            mediumThumbnail.setWidth(200);
            mediumThumbnail.setHeight(200);

            user.setThumbnails(List.of(defaultThumbnail, mediumThumbnail));

            userRepository.saveAndFlush(user);
            users[i - 1] = user;
        }

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
                    notificationObjectRepository.save(notificationObject);

                    var isSeen = new Random().nextBoolean();
                    var isRead = isSeen && new Random().nextBoolean();
                    var notification = Notification.builder()
                            .notificationObject(notificationObject)
                            .actor(follower)
                            .recipient(user)
                            .isSeen(isSeen)
                            .isRead(isRead)
                            .build();
                    notificationRepository.save(notification);
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
        hashtagRepository.saveAll(hashtags);

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
        categoryRepository.saveAll(categories);

        var videos = new Video[49];
        for (int i = 1; i < 50; i++) {
            var isMadeForKids = new Random().nextInt() % 3 == 0;

            var defaultThumbnail = new Thumbnail();
            defaultThumbnail.setType(Thumbnail.Type.DEFAULT);
            defaultThumbnail.setUrl("https://dummyimage.com/720x450/fff/aaa");
            defaultThumbnail.setWidth(720);
            defaultThumbnail.setHeight(450);

            var mediumThumbnail = new Thumbnail();
            mediumThumbnail.setType(Thumbnail.Type.MEDIUM);
            mediumThumbnail.setUrl("https://dummyimage.com/1280x720/fff/aaa");
            mediumThumbnail.setWidth(1280);
            mediumThumbnail.setHeight(720);

            var video = Video.builder()
                    .title("Video %s".formatted(i))
                    .description("Video %s description".formatted(i))
                    .thumbnails(List.of(defaultThumbnail, mediumThumbnail))
                    .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
                    .durationSec(new Random().nextInt(540, 3600))
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
            notificationObjectRepository.save(notificationObject);

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
            notificationRepository.saveAll(notifications);

            for (int j = 0; j < new Random().nextInt(20); j++) {
                var user = users[new Random().nextInt(users.length)];
                for (int k = 0; k < new Random().nextInt(3); k++) {
                    var viewHistory = new ViewHistory();
                    viewHistory.setUser(user);
                    viewHistory.setVideo(video);
                    viewHistory.setPublishedAt(LocalDateTime.now());
                    viewHistory.setViewedDurationSec(new Random().nextInt(540,
                            video.getDurationSec() + 1));
                    viewHistoryRepository.saveAndFlush(viewHistory);
                }
            }

            for (int j = 0; j < new Random().nextInt(0, 15); j++) {
                var user = users[new Random().nextInt(users.length)];
                var comment = new Comment();
                comment.setUser(user);
                comment.setVideo(video);
                comment.setText("Good video");
                comment.setPublishedAt(LocalDateTime.now());
                commentRepository.saveAndFlush(comment);

                if (new Random().nextInt(20) == 0) {
                    for (int k = 0; k < new Random().nextInt(5); k++) {
                        var replyUser = users[new Random().nextInt(users.length)];
                        var replyComment = new Comment();
                        replyComment.setUser(replyUser);
                        replyComment.setVideo(video);
                        replyComment.setText("Reply for %s".formatted(comment.getId()));
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
                }
            }
        }

        var videoList = new ArrayList<>(Arrays.stream(videos).toList());
        for (var user : users) {
            for (int i = 0; i < new Random().nextInt(4); i++) {
                var playlist = new Playlist();
                playlist.setUser(user);
                playlist.setTitle("Playlist %c".formatted((char) (new Random().nextInt(95) + 32)));
                playlist.setPublishedAt(LocalDateTime.now());
                playlist.setIsUserCreate(true);
                playlist.setPrivacy(new Random().nextInt() % 3 == 0 ? privatePrivacy : publicPrivacy);
                playlistRepository.saveAndFlush(playlist);

                Collections.shuffle(videoList);
                var randomVideos = videoList.subList(0, new Random().nextInt(5));
                byte index = 0;
                for (var video : randomVideos) {
                    var playlistVideo = new PlaylistItem();
                    playlistVideo.setPlaylist(playlist);
                    playlistVideo.setVideo(video);
                    playlistVideo.setPriority(index++);
                    playlistItemRepository.saveAndFlush(playlistVideo);
                }
            }
        }
    }
}
