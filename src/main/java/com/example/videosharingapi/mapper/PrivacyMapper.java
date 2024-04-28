package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.Privacy;
import com.example.videosharingapi.repository.PrivacyRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public abstract class PrivacyMapper {

    private @Autowired PrivacyRepository privacyRepository;

    public Privacy fromStatus(String status) {
        var privacyStatus = Privacy.Status.valueOf(status.toUpperCase());
        return privacyRepository.findByStatus(privacyStatus);
    }
}
