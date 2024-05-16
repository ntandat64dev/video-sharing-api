package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.CategoryDto;
import com.example.videosharingapi.entity.Category;
import com.example.videosharingapi.repository.CategoryRepository;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@Setter(onMethod_ = @Autowired)
public abstract class CategoryMapper {
    private CategoryRepository categoryRepository;

    public abstract CategoryDto toCategoryDto(Category category);

    public Category toCategory(CategoryDto categoryDto) {
        return categoryRepository.getReferenceById(categoryDto.getId());
    }
}
