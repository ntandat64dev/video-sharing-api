package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.ThumbnailMapper;
import com.example.videosharingapi.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Profile("prod")
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Client s3;
    private final ThumbnailMapper thumbnailMapper;

    @Value("${aws.s3.bucket-videos}")
    private String videosBucket;

    @Value("${aws.s3.bucket-thumbnails}")
    private String thumbnailBucket;

    @Override
    @Transactional
    public void storeVideo(MultipartFile file, MultipartFile thumbnailFile, VideoDto videoDto) {
        var videoUrl = storeVideoFile(file);
        if (videoUrl == null) throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);
        videoDto.getSnippet().setVideoUrl(videoUrl);

        var thumbnailUrl = storeThumbnailImage(thumbnailFile);
        if (thumbnailUrl == null) throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl(thumbnailUrl);
        thumbnail.setWidth(100);
        thumbnail.setHeight(100);

        videoDto.getSnippet().setThumbnails(thumbnailMapper.toMap(List.of(thumbnail)));
    }

    @Override
    @Transactional
    public String storeVideoFile(MultipartFile videoFile) {
        try {
            var videoKey = videoFile.getOriginalFilename() + UUID.randomUUID();
            var putVideoRequest = PutObjectRequest.builder()
                    .bucket(videosBucket)
                    .key(videoKey)
                    .contentType("video/*")
                    .build();
            var videoRequestBody = RequestBody.fromInputStream(videoFile.getInputStream(), videoFile.getSize());
            s3.putObject(putVideoRequest, videoRequestBody);
            // If no exception was thrown, then it means it was successful.
            return s3.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(videosBucket).key(videoKey).build())
                    .toString();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public String storeThumbnailImage(MultipartFile imageFile) {
        try {
            var thumbnailKey = imageFile.getOriginalFilename() + UUID.randomUUID();
            var putThumbnailRequest = PutObjectRequest.builder()
                    .bucket(thumbnailBucket)
                    .key(thumbnailKey)
                    .contentType("images/*")
                    .build();
            var thumbnailRequestBody = RequestBody
                    .fromInputStream(imageFile.getInputStream(), imageFile.getSize());
            s3.putObject(putThumbnailRequest, thumbnailRequestBody);
            return s3.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(thumbnailBucket).key(thumbnailKey).build())
                    .toString();
        } catch (IOException e) {
            return null;
        }
    }
}
