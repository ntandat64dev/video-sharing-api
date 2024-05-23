package com.example.videosharingapi.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-key}")
    private String firebaseServiceAccountKey;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        var is = new ClassPathResource(firebaseServiceAccountKey).getInputStream();
        var options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
