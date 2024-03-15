package com.example.videosharingapi;

import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class VideoSharingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoSharingApiApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner(UserRepository userRepository) {
        return args -> {
            var video1 = Video.builder()
                    .title("""
                            Big Buck Bunny and A Little Cute of Big Car with a Aggressive Man Big Buck Bunny and A Little Cute of\s
                            Big Car with a Aggressive Man""")
                    .description("""
                            Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself.\s
                            When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no\s
                            bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.

                            Licensed under the Creative Commons Attribution license
                            https://www.bigbuckbunny.org""")
                    .thumbnailUrl("https://i.easil.com/wp-content/uploads/20210901120345/Teal-White-Tech-Design-with-white-person-outline-youtube-thumbnail-2.jpg")
                    .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                    .build();

            var video2 = Video.builder()
                    .title("""
                            Elephant Dream""")
                    .description("""
                            The first Blender Open Movie from 2006.""")
                    .thumbnailUrl("https://i.easil.com/wp-content/uploads/20210901120345/Teal-White-Tech-Design-with-white-person-outline-youtube-thumbnail-2.jpg")
                    .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                    .build();

            var user1 = User.builder()
                    .email("user1@gmail.com")
                    .password("1111")
                    .photoUrl(null)
                    .channelName("user1@gmail.com")
                    .build();

            video1.setUser(user1);
            video2.setUser(user1);
            user1.setVideos(List.of(video1, video2));
            userRepository.save(user1);

            var video3 = Video.builder()
                    .title("""
                            For Bigger Blazes""")
                    .description("""
                            HBO GO now works with Chromecast -- the easiest way to enjoy online video on your TV.\s
                            For when you want to settle into your Iron Throne to watch the latest episodes. For $35.

                            Learn how to use Chromecast with HBO GO and more at google.com/chromecast.""")
                    .thumbnailUrl("https://i.easil.com/wp-content/uploads/20210901120345/Teal-White-Tech-Design-with-white-person-outline-youtube-thumbnail-2.jpg")
                    .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
                    .build();

            var video4 = Video.builder()
                    .title("""
                            For Bigger Escape""")
                    .description("""
                            Introducing Chromecast. The easiest way to enjoy online video and music on your TV—for when\s
                            Batman's escapes aren't quite big enough. For $35. Learn how to use Chromecast with Google Play\s
                            Movies and more at google.com/chromecast.""")
                    .thumbnailUrl("https://i.easil.com/wp-content/uploads/20210901120345/Teal-White-Tech-Design-with-white-person-outline-youtube-thumbnail-2.jpg")
                    .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
                    .build();

            var video5 = Video.builder()
                    .title("""
                            For Bigger Fun""")
                    .description("""
                            Introducing Chromecast. The easiest way to enjoy online video and music on your TV. For $35.  Find out more at google.com/chromecast.""")
                    .thumbnailUrl("https://i.easil.com/wp-content/uploads/20210901120345/Teal-White-Tech-Design-with-white-person-outline-youtube-thumbnail-2.jpg")
                    .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
                    .build();

            var user2 = User.builder()
                    .email("user2@gmail.com")
                    .password("2222")
                    .photoUrl(null)
                    .channelName("user2@gmail.com")
                    .build();

            video3.setUser(user2);
            video4.setUser(user2);
            video5.setUser(user2);
            user2.setVideos(List.of(video3, video4, video5));
            userRepository.save(user2);
        };
    }
}
