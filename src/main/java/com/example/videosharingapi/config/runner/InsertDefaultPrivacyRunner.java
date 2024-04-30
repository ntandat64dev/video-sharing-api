package com.example.videosharingapi.config.runner;

import com.example.videosharingapi.entity.Privacy;
import com.example.videosharingapi.repository.PrivacyRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * An {@link ApplicationRunner} used to initialize {@link Privacy} level in database. Used in {@code prod} profile.
 *
 * <p> In {@code dev}, database can be initialized by {@link InsertTestDataRunner}.
 *
 * @see Privacy
 */
@Component
@Profile("prod")
public class InsertDefaultPrivacyRunner implements ApplicationRunner {
    private final PrivacyRepository privacyRepository;

    public InsertDefaultPrivacyRunner(PrivacyRepository privacyRepository) {
        this.privacyRepository = privacyRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (privacyRepository.findByStatus(Privacy.Status.PUBLIC) == null) {
            privacyRepository.save(new Privacy(Privacy.Status.PUBLIC));
        }
        if (privacyRepository.findByStatus(Privacy.Status.PRIVATE) == null) {
            privacyRepository.save(new Privacy(Privacy.Status.PRIVATE));
        }
    }
}