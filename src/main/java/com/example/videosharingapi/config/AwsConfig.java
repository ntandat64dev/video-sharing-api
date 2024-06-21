package com.example.videosharingapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${aws.credential.access-key-id}")
    private String accessKeyId;

    @Value("${aws.credential.access-key}")
    private String accessKey;

    @Bean
    public S3Client s3Client() {
        var credentials = AwsBasicCredentials.create(accessKeyId, accessKey);
        var credentialsProvider = StaticCredentialsProvider.create(credentials);
        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
