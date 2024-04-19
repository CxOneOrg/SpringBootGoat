package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Post;

@Repository("postRepository")
public interface PostRepository extends JpaRepository<Post, Long> {
	Post findById(long id);
}
