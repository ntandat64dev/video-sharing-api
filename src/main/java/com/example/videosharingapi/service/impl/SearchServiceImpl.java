package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.document.Indexes;
import com.example.videosharingapi.document.Playlist;
import com.example.videosharingapi.document.User;
import com.example.videosharingapi.document.Video;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.dto.response.SearchResponse;
import com.example.videosharingapi.enums.SearchSort;
import com.example.videosharingapi.enums.SearchType;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.mapper.VideoMapper;
import com.example.videosharingapi.repository.PlaylistRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQueryBuilder;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    private final VideoMapper videoMapper;
    private final UserMapper userMapper;

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public PageResponse<SearchResponse> search(
            String keyword, Pageable pageable, SearchSort searchSort, SearchType searchType
    ) {
        var query = buildQuery(keyword, pageable, searchSort);
        var documentType = getDocumentType(searchType);
        var searchHits = searchDocument(query, documentType, searchType);

        List<String> videoIds = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        List<String> playlistIds = new ArrayList<>();

        for (var searchHit : searchHits.getSearchHits()) {
            if (searchHit.getIndex() == null) continue;
            switch (searchHit.getIndex()) {
                case Indexes.VIDEO:
                    videoIds.add(searchHit.getId());
                    break;
                case Indexes.USER:
                    userIds.add(searchHit.getId());
                    break;
                case Indexes.PLAYLIST: {
                    var playlist = (Playlist) searchHit.getContent();
                    if (playlist.isVisible()) {
                        playlistIds.add(playlist.getId());
                    }
                    break;
                }
            }
        }

        var searchResponses = new ArrayList<SearchResponse>();
        addVideoSearchResponses(searchResponses, videoIds);
        addUserSearchResponses(searchResponses, userIds);
        addPlaylistSearchResponses(searchResponses, playlistIds);

        long totalHits = searchHits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / pageable.getPageSize());

        return PageResponse.<SearchResponse>builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(totalPages)
                .totalElements(totalHits)
                .items(searchResponses)
                .build();
    }

    private <T> SearchHits<T> searchDocument(Query query, Class<T> clazz, SearchType searchType) {
        var indexCoordinate = switch (searchType) {
            case ALL -> IndexCoordinates.of(Indexes.VIDEO, Indexes.USER, Indexes.PLAYLIST);
            case VIDEO -> IndexCoordinates.of(Indexes.VIDEO);
            case USER -> IndexCoordinates.of(Indexes.USER);
            case PLAYLIST -> IndexCoordinates.of(Indexes.PLAYLIST);
        };
        return elasticsearchOperations.search(query, clazz, indexCoordinate);
    }

    private Query buildQuery(String keyword, Pageable pageable, SearchSort searchSort) {
        if (searchSort == SearchSort.RELEVANCE)
            return new StringQuery("{\"query_string\":{\"query\":\"%s\"}}".formatted(keyword), pageable);

        String property = null;
        String unmappedType = null;
        if (searchSort == SearchSort.UPLOAD_DATE) {
            property = "publishedDate";
            unmappedType = "date";
        } else if (searchSort == SearchSort.VIEW_COUNT) {
            property = "viewCount";
            unmappedType = "long";
        }

        var query = """
                {
                     "query": {
                         "bool": {
                             "must": [
                                 {
                                     "query_string": {
                                         "query": "%s"
                                     }
                                 }
                             ],
                             "filter": {
                                 "bool": {
                                     "should": [
                                         {
                                             "bool": {
                                                 "filter": {
                                                     "term": {
                                                         "_index": "video"
                                                     }
                                                 }
                                             }
                                         },
                                         {
                                             "bool": {
                                                 "filter": {
                                                     "term": {
                                                         "_index": "user"
                                                     }
                                                 }
                                             }
                                         },
                                         {
                                             "bool": {
                                                 "filter": [
                                                     {
                                                         "term": {
                                                             "_index": "playlist"
                                                         }
                                                     },
                                                     {
                                                         "term": {
                                                             "isVisible": true
                                                         }
                                                     }
                                                 ]
                                             }
                                         }
                                     ]
                                 }
                             }
                         }
                     },
                     "sort": [
                         {
                             "%s": {
                                 "order": "desc",
                                 "missing": "_last",
                                 "unmapped_type": "%s"
                             }
                         }
                     ]
                }
                """.formatted(keyword, property, unmappedType);

        return new SearchTemplateQueryBuilder()
                .withSource(query)
                .withPageable(pageable)
                .build();
    }

    private Class<?> getDocumentType(SearchType searchType) {
        return switch (searchType) {
            case VIDEO -> Video.class;
            case USER -> User.class;
            case PLAYLIST -> Playlist.class;
            case ALL -> Object.class;
        };
    }

    private void addVideoSearchResponses(List<SearchResponse> searchResponses, List<String> videoIds) {
        if (videoIds.isEmpty()) return;
        var videoIdsString = StringUtils.join(videoIds, ",");
        videoRepository.findAllByIdsAndKeepOrder(videoIds, videoIdsString).forEach(video -> {
            var videoDto = videoMapper.toVideoDto(video);
            var searchResponse = new SearchResponse(SearchResponse.SearchType.VIDEO, videoDto);
            searchResponses.add(searchResponse);
        });
    }

    private void addUserSearchResponses(List<SearchResponse> searchResponses, List<String> userId) {
        if (userId.isEmpty()) return;
        var userIdsString = StringUtils.join(userId, ",");
        userRepository.findAllByIdsAndKeepOrder(userId, userIdsString).forEach(user -> {
            var userDto = userMapper.toUserDto(user);
            var searchResponse = new SearchResponse(SearchResponse.SearchType.USER, userDto);
            searchResponses.add(searchResponse);
        });
    }

    private void addPlaylistSearchResponses(List<SearchResponse> searchResponses, List<String> playlistIds) {
        if (playlistIds.isEmpty()) return;
        var playlistIdsString = StringUtils.join(playlistIds, ",");
        playlistRepository.findAllByIdsAndKeepOrder(playlistIds, playlistIdsString).forEach(playlist -> {
            var searchResponse = new SearchResponse(SearchResponse.SearchType.PLAYLIST, playlist);
            searchResponses.add(searchResponse);
        });
    }

}
