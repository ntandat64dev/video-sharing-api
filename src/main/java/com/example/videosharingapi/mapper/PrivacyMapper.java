package com.example.videosharingapi.mapper;

import com.example.videosharingapi.entity.Privacy;
import com.example.videosharingapi.repository.PrivacyRepository;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@Setter(onMethod_ = @Autowired)
public abstract class PrivacyMapper {
    private PrivacyRepository privacyRepository;

    public Privacy fromStatus(String status) {
        var privacyStatus = Privacy.Status.valueOf(status.toUpperCase());
        return privacyRepository.findByStatus(privacyStatus);
    }
}
