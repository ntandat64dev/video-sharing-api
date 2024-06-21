package com.example.videosharingapi.elasticsearchrepository;

import com.example.videosharingapi.document.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoElasticsearchRepository extends ElasticsearchRepository<Video, String> {
}
