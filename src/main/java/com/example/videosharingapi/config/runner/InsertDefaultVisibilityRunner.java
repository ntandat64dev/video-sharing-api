package com.example.videosharingapi.config.runner;

import com.example.videosharingapi.model.entity.Visibility;
import com.example.videosharingapi.repository.VisibilityRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InsertDefaultVisibilityRunner implements ApplicationRunner {
    private final VisibilityRepository visibilityRepository;

    public InsertDefaultVisibilityRunner(VisibilityRepository visibilityRepository) {
        this.visibilityRepository = visibilityRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (visibilityRepository.findByLevel(Visibility.VisibilityLevel.PUBLIC) == null) {
            var visibilityPublic = new Visibility();
            visibilityPublic.setLevel(Visibility.VisibilityLevel.PUBLIC);
            visibilityRepository.save(visibilityPublic);
        }
        if (visibilityRepository.findByLevel(Visibility.VisibilityLevel.PRIVATE) == null) {
            var visibilityPrivate = new Visibility();
            visibilityPrivate.setLevel(Visibility.VisibilityLevel.PRIVATE);
            visibilityRepository.save(visibilityPrivate);
        }
    }
}

/*
 git commit -m "Add more information to DTO objects and update services" -m "- Update UserDto, VideoDto and create ChannelDto
> - Create FakeStorageService for testing and replace StorageServiceImpl as 'dev' profile
> - Make StorageServiceImpl as 'prod' profile
> - Add config.runner.InsertDefaultVisibilityRunner runner to create default visibility levels (PRIVATE and PUBLIC) on initializing database
> - Create resources/static folder for serving default resources

 */
