package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.ThumbnailDto;
import com.example.videosharingapi.entity.Thumbnail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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

    @Named("getDefaultUrl")
    default String getDefaultThumbnailUrl(List<Thumbnail> thumbnails) {
        return thumbnails.stream()
                .filter(thumbnail -> thumbnail.getType() == Thumbnail.Type.DEFAULT)
                .findFirst()
                .orElseThrow()
                .getUrl();
    }

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
