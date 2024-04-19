package com.example.controller;

import java.util.Collection;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.model.Post;
import com.example.model.User;
import com.example.service.PostService;
import com.example.service.UserService;

@Controller
public class FeedController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;
	
	@RequestMapping(value="/feed", method = RequestMethod.GET)
	public ModelAndView feed(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Collection<Post> posts = postService.findAll();
		posts = new TreeSet<>(posts); // Sort the posts
		for(Post p : posts)
			p.setCreationDate(p.getDateString());
		
		Post postForBinding = new Post();
		postForBinding.setUser(user);
		modelAndView.addObject("user", user);
		modelAndView.addObject("post", postForBinding);
		modelAndView.addObject("posts", posts);
		modelAndView.setViewName("feed");
		return modelAndView;
	}
	
	
}
