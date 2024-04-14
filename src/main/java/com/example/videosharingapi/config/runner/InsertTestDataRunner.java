package com.example.videosharingapi.config.runner;

import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * An {@link ApplicationRunner} used to initialize database for testing purpose.
 */
@Profile("init")
@Component
public class InsertTestDataRunner implements ApplicationRunner {

    private @Autowired ChannelRepository channelRepository;
    private @Autowired CommentRatingRepository commentRatingRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired PlaylistRepository playlistRepository;
    private @Autowired PlaylistVideoRepository playlistVideoRepository;
    private @Autowired SubscriptionRepository subscriptionRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired VideoRatingRepository videoRatingRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired HashtagRepository hashtagRepository;
    private @Autowired ViewHistoryRepository viewHistoryRepository;
    private @Autowired VisibilityRepository visibilityRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        visibilityRepository.saveAndFlush(new Visibility(Visibility.VisibilityLevel.PUBLIC));
        visibilityRepository.saveAndFlush(new Visibility(Visibility.VisibilityLevel.PRIVATE));

        var users = new User[9];
        for (int i = 1; i < 10; i++) {
            var user = new User("user%s@gmail.com".formatted(i),
                    new String(new char[8]).replace("\0", String.valueOf(i % 9)));
            userRepository.saveAndFlush(user);
            users[i - 1] = user;

            var channel = new Channel();
            channel.setTitle(user.getEmail());
            channel.setPublishedAt(LocalDateTime.now());

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

            channel.setThumbnails(List.of(defaultThumbnail, mediumThumbnail));
            channel.setUser(user);
            channelRepository.saveAndFlush(channel);
        }

        var hashtags = new ArrayList<>(Stream.of("Music", "Sport", "Gaming", "Style", "Programming Language",
                        "Youtuber", "Viral", "Memes", "Rap", "Art", "Trending", "Food", "Design", "Happy", "Love",
                        "Fashion", "Travel", "Podcasts", "Beauty", "Vacation", "Cooking", "Life", "Motivation",
                        "Study", "School", "Illustration", "Photography", "Book", "Film", "Football", "Comedy", "News",
                        "Entertainment", "Tiktok", "Youtube", "Facebook", "Instagram", "Live", "Tricks", "Remix",
                        "University", "Work", "Company", "Finance", "Workout", "Technology", "Social", "Vlog", "Job",
                        "Interview", "Environment", "War", "Ukraine", "Education", "Business", "Influence", "Success",
                        "Pets", "Adventure", "Electric", "Pop", "Minecraft", "League of Legends", "Funny videos",
                        "Learning", "Family", "Knowledge", "EDM", "Mindset", "Productivity", "BBC", "Weather",
                        "Climate change", "Hunger", "UEFA Champion League", "Game shows", "Lo-fi", "Technology trends",
                        "Java", "Sitcoms", "TED", "How to", "Anime", "Action", "Communicate", "Millionaire",
                        "Electric Car", "Furniture", "Cave", "Beach", "Tennis", "Space", "Science", "Experiment",
                        "Disaster", "COVID-19", "Pandemic")
                .map(Hashtag::new)
                .toList());
        hashtagRepository.saveAll(hashtags);

        String[] locations = {
                "New York City, USA", "Tokyo, Japan", "London, UK", "Paris, France", "Sydney, Australia",
                "Rio de Janeiro, Brazil", "Moscow, Russia", "Dubai, UAE", "Rome, Italy", "Toronto, Canada",
                "Seoul, South Korea", "Berlin, Germany", "Cape Town, South Africa", "Mexico City, Mexico",
                "Hong Kong, China", "Mumbai, India", "Bangkok, Thailand", "Amsterdam, Netherlands", "Istanbul, Turkey",
                "Barcelona, Spain", "Buenos Aires, Argentina", "Singapore", "Vienna, Austria", "Dublin, Ireland",
                "Stockholm, Sweden", "Zurich, Switzerland", "Prague, Czech Republic", "Oslo, Norway",
                "Warsaw, Poland", "Athens, Greece", "Kuala Lumpur, Malaysia", "Cairo, Egypt", "Helsinki, Finland",
                "Brussels, Belgium", "Lisbon, Portugal", "Edinburgh, Scotland", "Auckland, New Zealand",
                "Shanghai, China", "Seville, Spain", "Venice, Italy", "Florence, Italy", "Marrakech, Morocco",
                "Rio de Janeiro, Brazil", "Jakarta, Indonesia", "Kyoto, Japan", "Santorini, Greece",
                "St. Petersburg, Russia", "Copenhagen, Denmark", "Jerusalem, Israel", "Cape Town, South Africa",
                "Vancouver, Canada", "Havana, Cuba", "Salzburg, Austria", "Dubrovnik, Croatia", "Reykjavik, Iceland",
                "Budapest, Hungary", "Krakow, Poland", "Santiago, Chile", "Hanoi, Vietnam",
                "Kruger National Park, South Africa", "Phuket, Thailand", "Maui, Hawaii",
                "San Francisco, USA", "Los Angeles, USA", "Chicago, USA", "Miami, USA", "Las Vegas, USA",
                "Washington D.C., USA", "Boston, USA", "Seattle, USA", "Dallas, USA", "Austin, USA", "Denver, USA",
                "Portland, USA", "New Orleans, USA", "Philadelphia, USA", "San Diego, USA", "Nashville, USA",
                "Atlanta, USA", "Houston, USA", "Phoenix, USA", "Orlando, USA", "Honolulu, USA", "San Antonio, USA",
                "Charlotte, USA", "Indianapolis, USA", "Detroit, USA", "Minneapolis, USA", "Pittsburgh, USA",
                "Cleveland, USA", "Columbus, USA", "Kansas City, USA", "St. Louis, USA", "Tampa, USA",
                "Baltimore, USA", "Milwaukee, USA", "Raleigh, USA", "Norfolk, USA", "Salt Lake City, USA",
                "Memphis, USA", "Louisville, USA", "Richmond, USA", "Oklahoma City, USA", "Albuquerque, USA" };

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
                    .user(users[new Random().nextInt(9)])
                    .build();
            video.setVideoSpec(new VideoSpec());

            Collections.shuffle(hashtags);
            int start = new Random().nextInt(0, hashtags.size() - 5), end = start + new Random().nextInt(5);
            video.setHashtags(hashtags.subList(start, end));
            videoRepository.saveAndFlush(video);
        }

        var channels = channelRepository.findAll();
        for (int i = 1; i < 10; i++) {
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

        var videos = videoRepository.findAll();
        for (var video : videos) {
            for (int i = 0; i < new Random().nextInt(0, 50); i++) {
                var user = users[new Random().nextInt(users.length)];
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
                var user = users[new Random().nextInt(users.length)];
                var videoRating = new VideoRating();
                videoRating.setUser(user);
                videoRating.setVideo(video);
                videoRating.setRatedAt(LocalDateTime.now());
                videoRating.setRating(new Random().nextInt(10) > 2 ? VideoRating.Rating.LIKE : VideoRating.Rating.DISLIKE);
                videoRatingRepository.saveAndFlush(videoRating);
            }

            for (int i = 0; i < new Random().nextInt(0, 30); i++) {
                var user = users[new Random().nextInt(users.length)];
                var comment = new Comment();
                comment.setUser(user);
                comment.setVideo(video);
                comment.setContent("Good video");
                comment.setCommentedAt(LocalDateTime.now());
                comment.setIsReply(false);
                commentRepository.saveAndFlush(comment);

                if (new Random().nextInt(50) == 0) {
                    for (int j = 0; j < new Random().nextInt(10); j++) {
                        var replyUser = users[new Random().nextInt(users.length)];
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
                                var commentUser = users[new Random().nextInt(users.length)];
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
                        var commentUser = users[new Random().nextInt(users.length)];
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
                byte index = 0;
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
