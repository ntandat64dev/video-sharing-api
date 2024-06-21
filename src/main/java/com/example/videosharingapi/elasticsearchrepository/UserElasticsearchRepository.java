package com.example.videosharingapi.elasticsearchrepository;

import com.example.videosharingapi.document.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserElasticsearchRepository extends ElasticsearchRepository<User, String> {
}
