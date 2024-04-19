package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Post;
import com.example.repository.PostRepository;

@Service("postService")
public class PostServiceImpl implements PostService{

	@Autowired
	private PostRepository postRepository;
	
    /*@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;*/
	
	@Override
	public List<Post> findAll() {
		return postRepository.findAll();
	}

	@Override
	public void savePost(Post post) {
		postRepository.save(post);
	}
	
	@Override
	public Post findPostById(long id) {
		return postRepository.findById(id);
	}
	
	@Override
	public void deletePost(Long postId) {
		postRepository.delete(postId);
		
	}
	
	@Override
	public void saveAll(List<Post> posts) {
		postRepository.save(posts);
	}

}
