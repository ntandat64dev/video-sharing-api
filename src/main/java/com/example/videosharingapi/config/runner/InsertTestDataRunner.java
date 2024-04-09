package com.example.videosharingapi.config.runner;

import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

/**
 * An {@link ApplicationRunner} used to initialize database for testing purpose.
 */
//@org.springframework.stereotype.Component
public class InsertTestDataRunner implements ApplicationRunner {

    private final ChannelRepository channelRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final CommentRepository commentRepository;
    private final HashtagRepository hashtagRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final VideoHashtagRepository videoHashtagRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final VideoRepository videoRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final VisibilityRepository visibilityRepository;

    public InsertTestDataRunner(ChannelRepository channelRepository, CommentRatingRepository commentRatingRepository,
                                CommentRepository commentRepository, HashtagRepository hashtagRepository, PlaylistRepository playlistRepository,
                                PlaylistVideoRepository playlistVideoRepository, SubscriptionRepository subscriptionRepository,
                                UserRepository userRepository, VideoHashtagRepository videoHashtagRepository,
                                VideoRatingRepository videoRatingRepository, VideoRepository videoRepository,
                                ViewHistoryRepository viewHistoryRepository, VisibilityRepository visibilityRepository) {
        this.channelRepository = channelRepository;
        this.commentRatingRepository = commentRatingRepository;
        this.commentRepository = commentRepository;
        this.hashtagRepository = hashtagRepository;
        this.playlistRepository = playlistRepository;
        this.playlistVideoRepository = playlistVideoRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.videoHashtagRepository = videoHashtagRepository;
        this.videoRatingRepository = videoRatingRepository;
        this.videoRepository = videoRepository;
        this.viewHistoryRepository = viewHistoryRepository;
        this.visibilityRepository = visibilityRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        visibilityRepository.saveAndFlush(new Visibility(Visibility.VisibilityLevel.PUBLIC));
        visibilityRepository.saveAndFlush(new Visibility(Visibility.VisibilityLevel.PRIVATE));

        for (var hashtag : new String[] { "Music", "Sport", "Gaming", "Style", "Programming Language", "Youtuber", "Viral", "Memes", "Rap", "Art",
                "Trending", "Food", "Design", "Happy", "Love", "Fashion", "Travel", "Podcasts", "Beauty", "Vacation", "Cooking", "Life",
                "Motivation", "Study", "School", "Illustration", "Photography", "Book", "Film", "Football", "Comedy", "News", "Entertainment",
                "Tiktok", "Youtube", "Facebook", "Instagram", "Live", "Tricks", "Remix", "University", "Work", "Company", "Finance", "Workout",
                "Technology", "Social", "Vlog", "Job", "Interview", "Environment", "War", "Ukraine", "Education", "Business", "Influence", "Success",
                "Pets", "Adventure", "Electric", "Pop", "Minecraft", "League of Legends", "Funny videos", "Learning", "Family", "Knowledge", "EDM",
                "Mindset", "Productivity", "BBC", "Weather", "Climate change", "Hunger", "UEFA Champion League", "Game shows", "Lo-fi",
                "Technology trends", "Java", "Sitcoms", "TED", "How to", "Anime", "Action", "Communicate", "Millionaire", "Electric Car",
                "Furniture", "Cave", "Beach", "Tennis", "Space", "Science", "Experiment", "Disaster", "COVID-19", "Pandemic" }
        ) {
            hashtagRepository.saveAndFlush(new Hashtag(hashtag));
        }

        for (int i = 1; i < 100; i++) {
            var user = new User("user%s@gmail.com".formatted(i), new String(new char[8]).replace("\0", String.valueOf(i % 9)));
            userRepository.saveAndFlush(user);

            var channel = new Channel();
            channel.setName(user.getEmail());
            channel.setJoinDate(LocalDateTime.now());
            channel.setPictureUrl("/default_avatar.png");
            channel.setUser(user);
            channelRepository.saveAndFlush(channel);
        }

        String[] locations = {
                "New York City, USA", "Tokyo, Japan", "London, UK", "Paris, France", "Sydney, Australia", "Rio de Janeiro, Brazil", "Moscow, Russia",
                "Dubai, UAE", "Rome, Italy", "Toronto, Canada", "Seoul, South Korea", "Berlin, Germany", "Cape Town, South Africa",
                "Mexico City, Mexico", "Hong Kong, China", "Mumbai, India", "Bangkok, Thailand", "Amsterdam, Netherlands", "Istanbul, Turkey",
                "Barcelona, Spain", "Buenos Aires, Argentina", "Singapore", "Vienna, Austria", "Dublin, Ireland", "Stockholm, Sweden",
                "Zurich, Switzerland", "Prague, Czech Republic", "Oslo, Norway", "Warsaw, Poland", "Athens, Greece", "Kuala Lumpur, Malaysia",
                "Cairo, Egypt", "Helsinki, Finland", "Brussels, Belgium", "Lisbon, Portugal", "Edinburgh, Scotland", "Auckland, New Zealand",
                "Shanghai, China", "Seville, Spain", "Venice, Italy", "Florence, Italy", "Marrakech, Morocco", "Rio de Janeiro, Brazil",
                "Jakarta, Indonesia", "Kyoto, Japan", "Santorini, Greece", "St. Petersburg, Russia", "Copenhagen, Denmark", "Jerusalem, Israel",
                "Cape Town, South Africa", "Vancouver, Canada", "Havana, Cuba", "Salzburg, Austria", "Dubrovnik, Croatia", "Reykjavik, Iceland",
                "Budapest, Hungary", "Krakow, Poland", "Santiago, Chile", "Hanoi, Vietnam", "Kruger National Park, South Africa", "Phuket, Thailand",
                "Maui, Hawaii", "San Francisco, USA", "Los Angeles, USA", "Chicago, USA", "Miami, USA", "Las Vegas, USA", "Washington D.C., USA",
                "Boston, USA", "Seattle, USA", "Dallas, USA", "Austin, USA", "Denver, USA", "Portland, USA", "New Orleans, USA", "Philadelphia, USA",
                "San Diego, USA", "Nashville, USA", "Atlanta, USA", "Houston, USA", "Phoenix, USA", "Orlando, USA", "Honolulu, USA",
                "San Antonio, USA", "Charlotte, USA", "Indianapolis, USA", "Detroit, USA", "Minneapolis, USA", "Pittsburgh, USA", "Cleveland, USA",
                "Columbus, USA", "Kansas City, USA", "St. Louis, USA", "Tampa, USA", "Baltimore, USA", "Milwaukee, USA", "Raleigh, USA",
                "Norfolk, USA", "Salt Lake City, USA", "Memphis, USA", "Louisville, USA", "Richmond, USA", "Oklahoma City, USA", "Albuquerque, USA" };

        for (int i = 1; i < 500; i++) {
            var isMadeForKids = new Random().nextInt() % 3 == 0;
            var video = Video.builder()
                    .title("Video %s".formatted(i))
                    .description("Video %s description".formatted(i))
                    .thumbnailUrl("/sample_video_thumbnail.jpg")
                    .videoUrl("/SampleVideo_1280x720_10mb.mp4")
                    .durationSec(new Random().nextInt(540, 3600))
                    .uploadDate(LocalDateTime.of(2024, new Random().nextInt(1, 5), i % 30 != 0 ? i % 30 : 1,
                            i % 24, 0, 0))
                    .visibility(
                            new Random().nextInt() % 3 == 0 ?
                                    visibilityRepository.findByLevel(Visibility.VisibilityLevel.PRIVATE) :
                                    visibilityRepository.findByLevel(Visibility.VisibilityLevel.PUBLIC)
                    )
                    .isCommentAllowed(i % 3 != 0)
                    .isMadeForKids(isMadeForKids)
                    .isAgeRestricted(!isMadeForKids && new Random().nextInt() % 3 == 0)
                    .location(new Random().nextInt() % 3 == 0 ? locations[new Random().nextInt(locations.length)] : null)
                    .user(userRepository.findByEmail("user%s@gmail.com".formatted(new Random().nextInt(1, 100))))
                    .build();
            video.setVideoSpec(new VideoSpec());
            videoRepository.saveAndFlush(video);

            var hashtags = hashtagRepository.findAll();
            var indexes = new int[4];
            for (int j = 0; j < new Random().nextInt(4); j++) {
                var index = -1;
                o:
                while (index == -1) {
                    var randomIndex = new Random().nextInt(hashtags.size());
                    for (var ix : indexes) {
                        if (ix == randomIndex) continue o;
                    }
                    indexes[j] = index = randomIndex;
                }

                var hashtag = hashtags.get(index);
                var videoHashtag = new VideoHashtag();
                videoHashtag.setVideo(video);
                videoHashtag.setHashtag(hashtag);
                videoHashtagRepository.saveAndFlush(videoHashtag);
            }
        }

        var channels = channelRepository.findAll();
        for (int i = 1; i < 100; i++) {
            var user = userRepository.findByEmail("user%s@gmail.com".formatted(i));
            for (int j = 0; j < new Random().nextInt(10); j++) {
                var channel = channels.get(new Random().nextInt(channels.size()));
                if (!channel.getUser().getId().equals(user.getId())) {
                    var subscription = new Subscription();
                    subscription.setUser(user);
                    subscription.setChannel(channel);
                    subscription.setSubscribedAt(LocalDateTime.now());
                    subscriptionRepository.saveAndFlush(subscription);
                }
            }
        }

        var users = userRepository.findAll();
        var videos = videoRepository.findAll();
        for (var video : videos) {
            for (int i = 0; i < new Random().nextInt(0, 50); i++) {
                var user = users.get(new Random().nextInt(users.size()));
                for (int j = 0; j < new Random().nextInt(3); j++) {
                    var viewHistory = new ViewHistory();
                    viewHistory.setUser(user);
                    viewHistory.setVideo(video);
                    viewHistory.setViewedAt(LocalDateTime.now());
                    viewHistory.setViewedDuration(new Random().nextInt(540, video.getDurationSec() + 1));
                    viewHistoryRepository.saveAndFlush(viewHistory);
                }
            }

            for (int i = 0; i < new Random().nextInt(0, 20); i++) {
                var user = users.get(new Random().nextInt(users.size()));
                var videoRating = new VideoRating();
                videoRating.setUser(user);
                videoRating.setVideo(video);
                videoRating.setRatedAt(LocalDateTime.now());
                videoRating.setRating(new Random().nextInt(10) > 2 ? VideoRating.Rating.LIKE : VideoRating.Rating.DISLIKE);
                videoRatingRepository.saveAndFlush(videoRating);
            }

            for (int i = 0; i < new Random().nextInt(0, 30); i++) {
                var user = users.get(new Random().nextInt(users.size()));
                var comment = new Comment();
                comment.setUser(user);
                comment.setVideo(video);
                comment.setContent("Good video");
                comment.setCommentedAt(LocalDateTime.now());
                comment.setIsReply(false);
                commentRepository.saveAndFlush(comment);

                if (new Random().nextInt(50) == 0) {
                    for (int j = 0; j < new Random().nextInt(10); j++) {
                        var replyUser = users.get(new Random().nextInt(users.size()));
                        var replyComment = new Comment();
                        replyComment.setUser(replyUser);
                        replyComment.setVideo(video);
                        replyComment.setContent("Reply for %s".formatted(comment.getId()));
                        replyComment.setCommentedAt(LocalDateTime.now());
                        replyComment.setParent(comment);
                        replyComment.setIsReply(true);
                        commentRepository.saveAndFlush(replyComment);

                        if (new Random().nextInt(80) == 0) {
                            for (int k = 0; k < new Random().nextInt(10); k++) {
                                var commentUser = users.get(new Random().nextInt(users.size()));
                                var replyCommentRating = new CommentRating();
                                replyCommentRating.setComment(replyComment);
                                replyCommentRating.setUser(commentUser);
                                replyCommentRating.setRating(new Random().nextBoolean() ? CommentRating.Rating.LIKE : CommentRating.Rating.DISLIKE);
                                replyCommentRating.setRatedAt(LocalDateTime.now());
                                commentRatingRepository.saveAndFlush(replyCommentRating);
                            }
                        }
                    }
                }

                if (new Random().nextInt(40) == 0) {
                    for (int j = 0; j < new Random().nextInt(30); j++) {
                        var commentUser = users.get(new Random().nextInt(users.size()));
                        var commentRating = new CommentRating();
                        commentRating.setComment(comment);
                        commentRating.setUser(commentUser);
                        commentRating.setRating(new Random().nextBoolean() ? CommentRating.Rating.LIKE : CommentRating.Rating.DISLIKE);
                        commentRating.setRatedAt(LocalDateTime.now());
                        commentRatingRepository.saveAndFlush(commentRating);
                    }
                }
            }
        }

        for (var user : users) {
            for (int i = 0; i < new Random().nextInt(4); i++) {
                var playlist = new Playlist();
                playlist.setUser(user);
                playlist.setTitle("Playlist %s".formatted(i));
                playlist.setUpdatedAt(LocalDateTime.now());
                playlist.setIsUserCreate(true);
                playlist.setVisibility(
                        new Random().nextInt() % 3 == 0 ?
                                visibilityRepository.findByLevel(Visibility.VisibilityLevel.PRIVATE) :
                                visibilityRepository.findByLevel(Visibility.VisibilityLevel.PUBLIC)
                );
                playlistRepository.saveAndFlush(playlist);

                Collections.shuffle(videos);
                var randomVideos = videos.subList(0, new Random().nextInt(5));
                int index = 0;
                for (var video : randomVideos) {
                    var playlistVideo = new PlaylistVideo();
                    playlistVideo.setPlaylist(playlist);
                    playlistVideo.setVideo(video);
                    playlistVideo.setPriority(index++);
                    playlistVideoRepository.saveAndFlush(playlistVideo);
                }
            }
        }
    }
}
