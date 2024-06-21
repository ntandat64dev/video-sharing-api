package com.example.videosharingapi.elasticsearchrepository;

import com.example.videosharingapi.document.Playlist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistElasticsearchRepository extends ElasticsearchRepository<Playlist, String> {
}
