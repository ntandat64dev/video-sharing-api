package com.example.videosharingapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class TestUtil {
    private @Autowired ObjectMapper objectMapper;

    public <T> void toDto(MvcResult mvcResult, AtomicReference<T> reference, Class<T> clazz) throws Exception {
        reference.set(objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz));
    }
}
