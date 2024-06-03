package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.dto.response.SearchResponse;
import com.example.videosharingapi.enums.SearchSort;
import com.example.videosharingapi.enums.SearchType;
import com.example.videosharingapi.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@Validated
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<PageResponse<SearchResponse>> searchQuery(
            @RequestParam("q") String query,
            @RequestParam(value = "s_sort", required = false, defaultValue = "RELEVANCE") SearchSort searchSort,
            @RequestParam(value = "s_type", required = false, defaultValue = "ALL") SearchType searchType,
            @PageableDefault Pageable pageable
    ) {
        var response = searchService.search(query, pageable, searchSort, searchType);
        return ResponseEntity.ok(response);
    }
}
