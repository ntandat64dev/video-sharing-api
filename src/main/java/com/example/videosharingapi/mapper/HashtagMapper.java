package com.example.videosharingapi.mapper;

import com.example.videosharingapi.entity.Hashtag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class HashtagMapper {

    String toString(Hashtag hashtag) {
        return hashtag.getTag();
    }
}
