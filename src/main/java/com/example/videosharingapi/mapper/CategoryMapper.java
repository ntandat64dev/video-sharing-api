package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.CategoryDto;
import com.example.videosharingapi.model.entity.Category;
import com.example.videosharingapi.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public abstract class CategoryMapper {

    private @Autowired CategoryRepository categoryRepository;

    public abstract CategoryDto toCategoryDto(Category category);

    public Category toCategory(CategoryDto categoryDto) {
        return categoryRepository.getReferenceById(categoryDto.getId());
    }
}
