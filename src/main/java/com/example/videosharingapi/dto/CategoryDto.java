package com.example.videosharingapi.dto;

import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.entity.Category;
import lombok.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public final class CategoryDto {

    @IdExistsConstraint(entity = Category.class)
    @NonNull
    private String id;

    private String category;
}
