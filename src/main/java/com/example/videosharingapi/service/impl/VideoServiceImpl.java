package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.mapper.VideoVideoDtoMapper;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.model.entity.VideoHashtag;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.HashtagRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoHashtagRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.VideoService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final VideoHashtagRepository videoHashtagRepository;

    private final MessageSource messageSource;
    private final VideoVideoDtoMapper videoVideoDtoMapper;

    public VideoServiceImpl(VideoRepository videoRepository, UserRepository userRepository,
                            HashtagRepository hashtagRepository, VideoHashtagRepository videoHashtagRepository,
                            MessageSource messageSource, VideoVideoDtoMapper videoVideoDtoMapper) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.hashtagRepository = hashtagRepository;
        this.videoHashtagRepository = videoHashtagRepository;
        this.messageSource = messageSource;
        this.videoVideoDtoMapper = videoVideoDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream()
                .map(videoVideoDtoMapper::videoToVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getRecommendVideos(UUID userId) {
        // TODO: Get actual recommend videos
        return videoRepository.findAllByUserId(userId).stream()
                .map(videoVideoDtoMapper::videoToVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    public VideoDto save(VideoDto videoDto) {
        if (!userRepository.existsById(videoDto.getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { videoDto.getUserId() }, LocaleContextHolder.getLocale()));

        var video = videoRepository.save(videoVideoDtoMapper.videoDtoToVideo(videoDto));
        saveHashtag(videoDto.getHashtags(), video);
        videoDto.setId(video.getId());
        videoDto.setUploadDate(video.getUploadDate());
        return videoDto;
    }

    private void saveHashtag(Set<String> hashtags, Video video) {
        if (hashtags != null) {
            var videoTags = new ArrayList<VideoHashtag>();
            hashtags.forEach(tagString -> {
                var hashtag = new Hashtag();
                hashtag.setTag(tagString);
                var savedHashtag = hashtagRepository.saveIfNotExist(hashtag);
                var videoHashtag = new VideoHashtag();
                videoHashtag.setHashtag(savedHashtag);
                videoHashtag.setVideo(video);
                videoTags.add(videoHashtag);
            });
            videoHashtagRepository.saveAll(videoTags);
        }
    }
}
