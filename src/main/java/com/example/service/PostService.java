package com.example.service;

import java.util.List;

import com.example.model.Post;

public interface PostService {
	public void savePost(Post post);
	public List<Post> findAll();
	public Post findPostById(long id);
	public void deletePost(Long postId);
	public void saveAll(List<Post> posts);
}
