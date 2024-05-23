package com.example.videosharingapi.common;

import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

@Component
public class TestUtil {

    public <T> T json(MvcResult result, String path) throws UnsupportedEncodingException {
        return JsonPath.read(result.getResponse().getContentAsString(), path);
    }
}
