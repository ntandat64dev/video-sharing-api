package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.CategoryDto;
import com.example.videosharingapi.mapper.CategoryMapper;
import com.example.videosharingapi.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        var response = categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
        return ResponseEntity.ok(response);
    }
}
