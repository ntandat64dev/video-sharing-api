package com.example.videosharingapi.dto;

import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.entity.Category;
import lombok.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public final class CategoryDto {

    @IdExists(entity = Category.class)
    @NonNull
    private String id;

    private String category;
}
