package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.AbstractElasticsearchContainer;
import com.example.videosharingapi.common.TestSql;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: Create Testcontainers for Firebase
// TODO: Test documents CRUD

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class SearchControllerTest extends AbstractElasticsearchContainer {

    private @Autowired MockMvc mockMvc;

    @Test
    public void giveSearchQueryAndParams_whenSearch_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "1")
                        .param("s_type", "ALL")
                        .param("s_sort", "VIEW_COUNT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].snippet.id").value("37b32dc2"));

        mockMvc.perform(get("/api/v1/search")
                        .param("q", "1")
                        .param("s_type", "USER")
                        .param("s_sort", "VIEW_COUNT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        mockMvc.perform(get("/api/v1/search")
                        .param("q", "Watch Later")
                        .param("s_type", "ALL")
                        .param("s_sort", "VIEW_COUNT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        mockMvc.perform(get("/api/v1/search")
                        .param("q", "video")
                        .param("s_type", "ALL")
                        .param("s_sort", "UPLOAD_DATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.items[0].snippet.id").value("e65707b4"))
                .andExpect(jsonPath("$.items[1].snippet.id").value("f7d9b74b"))
                .andExpect(jsonPath("$.items[2].snippet.id").value("37b32dc2"));

        mockMvc.perform(get("/api/v1/search")
                        .param("q", "my")
                        .param("s_type", "ALL")
                        .param("s_sort", "VIEW_COUNT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].snippet.id").value("d8659362"));
    }
}
