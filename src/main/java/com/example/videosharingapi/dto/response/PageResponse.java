package com.example.videosharingapi.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {

    private int pageNumber;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private List<T> items;

    public PageResponse(Page<T> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.items = page.getContent();
    }
}
