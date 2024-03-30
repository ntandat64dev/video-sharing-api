package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.VideoService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final VideoSpecRepository videoSpecRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final VideoTagRepository videoTagRepository;
    private final VisibilityRepository visibilityRepository;

    private final MessageSource messageSource;

    public VideoServiceImpl(VideoRepository videoRepository, VideoSpecRepository videoSpecRepository, UserRepository userRepository,
                            TagRepository tagRepository, VideoTagRepository videoTagRepository, VisibilityRepository visibilityRepository,
                            MessageSource messageSource) {
        this.videoRepository = videoRepository;
        this.videoSpecRepository = videoSpecRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.videoTagRepository = videoTagRepository;
        this.visibilityRepository = visibilityRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll()
                .stream().map(video -> new VideoDto(video.getId(), video.getTitle(), video.getDescription(), video.getThumbnailUrl(),
                        video.getVideoUrl(), video.getDurationSec(), video.getUploadDate(), video.getVideoTags().stream().map(videoTag ->
                        videoTag.getTag().getTag()).collect(Collectors.toSet()),
                        video.getVisibility().getLevel().toString().toLowerCase(Locale.US), video.getUser().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public VideoDto save(VideoDto videoDto) {
        if (!userRepository.existsById(videoDto.getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { videoDto.getUserId() }, LocaleContextHolder.getLocale()));

        var visibilityLevel = Visibility.VisibilityLevel.valueOf(videoDto.getVisibility().toUpperCase());
        var visibility = visibilityRepository.findByLevel(visibilityLevel);
        var video = Video.builder()
                .title(videoDto.getTitle())
                .description(videoDto.getDescription())
                .thumbnailUrl(videoDto.getThumbnailUrl())
                .videoUrl(videoDto.getVideoUrl())
                .durationSec(videoDto.getDurationSec())
                .uploadDate(LocalDateTime.now())
                .visibility(visibility)
                .user(User.builder().id(videoDto.getUserId()).build())
                .build();
        var savedVideo = videoRepository.save(video);

        var videoSpec = new VideoSpec();
        videoSpec.setVideo(video);
        videoSpecRepository.save(videoSpec);

        if (videoDto.getTags() != null) {
            var videoTags = new ArrayList<VideoTag>();
            videoDto.getTags().forEach(tagString -> {
                var tag = new Tag();
                tag.setTag(tagString);
                var savedTag = tagRepository.saveIfNotExist(tag);
                var videoTag = new VideoTag();
                videoTag.setTag(savedTag);
                videoTag.setVideo(video);
                videoTags.add(videoTag);
            });
            videoTagRepository.saveAll(videoTags);
        }

        videoDto.setId(savedVideo.getId());
        videoDto.setUploadDate(savedVideo.getUploadDate());
        return videoDto;
    }
}
