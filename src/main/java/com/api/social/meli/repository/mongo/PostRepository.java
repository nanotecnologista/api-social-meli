package com.api.social.meli.repository.mongo;

import com.api.social.meli.model.mongo.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}