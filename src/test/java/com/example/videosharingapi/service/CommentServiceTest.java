package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
public class CommentServiceTest {

    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired CommentService commentService;

    private final UUID userId = UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f");
    private final UUID videoId = UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978");

    private CommentDto commentDto;

    @BeforeEach
    public void setUp() {
        commentDto = new CommentDto();
        commentDto.setSnippet(CommentDto.Snippet.builder()
                .videoId(videoId)
                .authorId(userId)
                .text("Great video!")
                .build());
    }

    @Test
    public void givenVideoId_whenGetCommentsByVideoId_thenReturnSuccessful() {
        var commentDtoList = commentService.getCommentsByVideoId(videoId);
        assertThat(commentDtoList).hasSize(2);
        assertThat(commentDtoList.stream().map(CommentDto::getId)).contains(
                UUID.fromString("6c3239d6-5b33-461a-88dd-1e2150fb0324"),
                UUID.fromString("6b342e72-e278-482a-b1e8-c66a142b40ca")
        );
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenReturnSuccessful() {
        var response = commentService.postComment(commentDto);
        assertThat(response.getSnippet().getVideoId())
                .isEqualTo(UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978"));
        assertThat(response.getSnippet().getAuthorId())
                .isEqualTo(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"));
        assertThat(response.getSnippet().getText()).isEqualTo("Great video!");
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenAssertVideoIsSaved() {
        var response = commentService.postComment(commentDto);
        var comment = commentRepository.findById(response.getId());
        assertThat(comment).isNotNull();
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenVideoStatisticIsUpdated() {
        var videoStat = videoStatisticRepository.findById(videoId).orElseThrow();
        commentService.postComment(commentDto);
        assertThat(videoStat.getCommentCount()).isEqualTo(3);
    }

    @Test
    public void givenVideoId_whenGetTopLevelComment_thenReturnSuccessful() {
        var commentDto = commentService.getTopLevelComment(videoId);
        assertThat(commentDto.getId()).isNotNull();
    }
}
