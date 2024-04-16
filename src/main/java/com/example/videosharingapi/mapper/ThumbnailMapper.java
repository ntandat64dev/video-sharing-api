package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.payload.ThumbnailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ThumbnailMapper {

    ThumbnailDto toThumbnailDto(Thumbnail thumbnail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    Thumbnail toThumbnail(ThumbnailDto thumbnailDto);

    default Map<Thumbnail.Type, ThumbnailDto> toMap(List<Thumbnail> value) {
        return value.stream().collect(Collectors.toMap(Thumbnail::getType, this::toThumbnailDto));
    }

    default List<Thumbnail> fromMap(Map<Thumbnail.Type, ThumbnailDto> value) {
        var result = new ArrayList<Thumbnail>();
        value.forEach((type, thumbnailDto) -> {
            var thumbnail = toThumbnail(thumbnailDto);
            thumbnail.setType(type);
            result.add(thumbnail);
        });
        return result;
    }
}
