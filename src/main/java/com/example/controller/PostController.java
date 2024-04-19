package com.example.controller;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.Post;
import com.example.model.User;
import com.example.service.PostService;
import com.example.service.UserService;

@Controller
public class PostController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;
	
	
	@RequestMapping(value="/addPost", method = RequestMethod.GET)
	public ModelAndView addPost(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		modelAndView.addObject("post", new Post());
		modelAndView.setViewName("post/createPost");
		return modelAndView;
	}
	
	@RequestMapping(value = "/addPost", method = RequestMethod.POST)
	public String createNewPost(@Valid Post post, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		post.setUser(user);
		post.setCreationDate(LocalDateTime.now().toString());
		postService.savePost(post);
		redirectAttributes.addFlashAttribute("message", "Post has been added successfully.");
		redirectAttributes.addFlashAttribute("user", user);
			
		
		return "redirect:/feed";
	}
	
	@RequestMapping(value="/editPost/{id}", method = RequestMethod.POST)
	public ModelAndView editPost(@Valid Post post, BindingResult bindingResult, @PathVariable("id") long postId){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Post savedPost = postService.findPostById(postId);
		if(user.getPosts().stream().filter(p->p.getId()==postId).count()==1) {
			post.setId(postId);
			post.setUser(user);
			post.setCreationDate(savedPost.getCreationDate());
			postService.savePost(post);
			modelAndView.addObject("message", "Post has been edited successfully");
		}
		else {
			modelAndView.addObject("message", "You can't modify a post that wasn't made by you");
		}
		modelAndView.addObject("user", user);
		modelAndView.setViewName("post/editPost");
		return modelAndView;
	}
	
	@RequestMapping(value="/editPost/{id}", method = RequestMethod.GET)
	public ModelAndView editPost(@PathVariable("id") long postId){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Post post = postService.findPostById(postId);
		modelAndView.addObject("user", user);
		if(user.getPosts().stream().filter(p->p.getId()==postId).count()==1) {
			modelAndView.addObject("post", post);
			
		}
		modelAndView.setViewName("post/editPost");
		return modelAndView;
	}
	
	@RequestMapping(value="/deletePost/{id}", method = RequestMethod.GET)
	public String deletePost(@PathVariable("id") long postId, RedirectAttributes redirectAttributes){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Post post = postService.findPostById(postId);
		System.out.println("queres apagar o post : " +post.getId());
		modelAndView.addObject("user", user);
		if(user.getPosts().stream().filter(p->p.getId()==postId).count()==1) {
			postService.deletePost(post.getId());
			redirectAttributes.addFlashAttribute("message", "Post has been successfully deleted");
		}
		else {
			redirectAttributes.addFlashAttribute("message", "You can't delete a post that wasn't made by you");
		}
		
		modelAndView.setViewName("feed");
		return "redirect:/feed";
	}
	

}
