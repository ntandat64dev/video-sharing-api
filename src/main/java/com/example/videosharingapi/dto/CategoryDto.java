package com.example.videosharingapi.dto;

import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.model.entity.Category;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public final class CategoryDto {

    @IdExistsConstraint(entity = Category.class)
    @NonNull
    private UUID id;

    private String category;
}
