package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.CommentService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    private final CommentMapper commentMapper;
    private final MessageSource messageSource;

    public CommentServiceImpl(
            CommentMapper commentMapper, CommentRepository commentRepository,
            UserRepository userRepository, VideoRepository videoRepository, MessageSource messageSource
    ) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.messageSource = messageSource;
    }

    @Override
    public List<CommentDto> getCommentsByVideoId(UUID videoId) {
        if (!videoRepository.existsById(videoId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.video.id.not-exist",
                        new Object[] { videoId }, LocaleContextHolder.getLocale()));
        return commentRepository.findByVideoId(videoId).stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto postComment(CommentDto commentDto) {
        checkUserIdAndVideoIdExistent(commentDto.getSnippet().getAuthorId(), commentDto.getSnippet().getVideoId());

        var comment = commentMapper.toComment(commentDto);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getTopLevelComment(UUID videoId) {
        // TODO: Implement
        return commentMapper.toCommentDto(commentRepository.findByVideoId(videoId).stream()
                .findFirst()
                .orElse(null));
    }

    private void checkUserIdAndVideoIdExistent(UUID userId, UUID videoId) throws ApplicationException {
        if (!userRepository.existsById(userId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.user.id.not-exist",
                        new Object[] { userId }, LocaleContextHolder.getLocale()));
        if (!videoRepository.existsById(videoId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.video.id.not-exist",
                        new Object[] { videoId }, LocaleContextHolder.getLocale()));
    }
}
