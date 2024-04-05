package com.example.videosharingapi.config.runner;

import com.example.videosharingapi.model.entity.Visibility;
import com.example.videosharingapi.repository.VisibilityRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * An {@link ApplicationRunner} used to initialize {@link Visibility} level in database. Used in {@code prod} profile.
 *
 * <p> In {@code dev}, database can be initialized by {@link InsertTestDataRunner}.
 *
 * @see Visibility
 */
@Component
@Profile("prod")
public class InsertDefaultVisibilityRunner implements ApplicationRunner {
    private final VisibilityRepository visibilityRepository;

    public InsertDefaultVisibilityRunner(VisibilityRepository visibilityRepository) {
        this.visibilityRepository = visibilityRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (visibilityRepository.findByLevel(Visibility.VisibilityLevel.PUBLIC) == null) {
            visibilityRepository.save(new Visibility(Visibility.VisibilityLevel.PUBLIC));
        }
        if (visibilityRepository.findByLevel(Visibility.VisibilityLevel.PRIVATE) == null) {
            visibilityRepository.save(new Visibility(Visibility.VisibilityLevel.PRIVATE));
        }
    }
}