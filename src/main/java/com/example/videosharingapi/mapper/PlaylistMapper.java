package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.ThumbnailDto;
import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.repository.PlaylistItemRepository;
import com.example.videosharingapi.util.MessageUtil;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;

@Mapper(componentModel = "spring",
        uses = { UserMapper.class, PrivacyMapper.class, ThumbnailMapper.class })
@Setter(onMethod_ = @Autowired)
public abstract class PlaylistMapper {
    private PlaylistItemRepository playlistItemRepository;
    private PrivacyMapper privacyMapper;
    private ThumbnailMapper thumbnailMapper;

    @Mapping(target = ".", source = "snippet")
    @Mapping(target = ".", source = "status")
    @Mapping(target = "user", source = "snippet.userId")
    @Mapping(target = "publishedAt", source = "snippet.publishedAt", defaultExpression = "java(LocalDateTime.now())")
    @Mapping(target = "defaultType", expression = "java(null)")
    public abstract Playlist toPlaylist(PlaylistDto playlistDto);

    @Mapping(target = "snippet", expression = "java(mapSnippet(playlist))")
    @Mapping(target = "status", expression = "java(mapStatus(playlist))")
    @Mapping(target = "contentDetails", expression = "java(mapContentDetails(playlist))")
    public abstract PlaylistDto toPlaylistDto(Playlist playlist);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "thumbnails", expression = "java(findThumbnails(playlist))")
    @Mapping(target = "title", expression = "java(mapTitle(playlist))")
    @Mapping(target = "userImageUrl", source = "playlist.user.thumbnails", qualifiedByName = "getDefaultUrl")
    protected abstract PlaylistDto.Snippet mapSnippet(Playlist playlist);

    @Mapping(target = "privacy", source = "privacy.status")
    @Mapping(target = "isDefaultPlaylist", expression = "java(playlist.getDefaultType() != null)")
    protected abstract PlaylistDto.Status mapStatus(Playlist playlist);

    @Mapping(target = "itemCount", expression = "java(getItemCount(playlist))")
    protected abstract PlaylistDto.ContentDetails mapContentDetails(Playlist playlist);

    protected String mapTitle(Playlist playlist) {
        if (playlist.getDefaultType() == null) return playlist.getTitle();

        if (playlist.getDefaultType() == 0) {
            return MessageUtil.decode("message.playlist.watch-later");
        } else {
            return MessageUtil.decode("message.playlist.like-videos");
        }
    }

    protected Map<Thumbnail.Type, ThumbnailDto> findThumbnails(Playlist playlist) {
        var item = playlistItemRepository.findTopByPlaylistIdOrderByPriorityAsc(playlist.getId());
        if (item == null) return Collections.emptyMap();
        return thumbnailMapper.toMap(item.getVideo().getThumbnails());
    }

    protected long getItemCount(Playlist playlist) {
        return playlistItemRepository.countAllByPlaylistId(playlist.getId());
    }

    public void updatePlaylist(Playlist playlist, PlaylistDto playlistDto) {
        playlist.setTitle(playlistDto.getSnippet().getTitle());
        playlist.setDescription(playlistDto.getSnippet().getDescription());
        playlist.setPrivacy(privacyMapper.fromStatus(playlistDto.getStatus().getPrivacy()));
    }
}
