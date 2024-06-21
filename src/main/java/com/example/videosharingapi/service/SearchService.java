package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.dto.response.SearchResponse;
import com.example.videosharingapi.enums.SearchSort;
import com.example.videosharingapi.enums.SearchType;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    PageResponse<SearchResponse> search(String keyword, Pageable pageable,
                                        SearchSort searchSort, SearchType searchType);
}
