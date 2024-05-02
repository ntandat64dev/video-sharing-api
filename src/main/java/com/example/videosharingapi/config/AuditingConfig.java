package com.example.videosharingapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Value("${spring.jpa.auditor}")
    public String auditor;

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(auditor);
    }
}
