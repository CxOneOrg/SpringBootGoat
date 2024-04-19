package com.example.controller;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.example.model.Post;
import com.example.model.User;
import com.example.service.PostService;
import com.example.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;
	
	@PersistenceContext
	private EntityManager em;
	
	/*@Autowired
	private StorageService storageService;*/
	
	@RequestMapping(value="/user", method = RequestMethod.GET)
	public ModelAndView userProfile(@RequestParam("email") String userEmail){
		
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());	
		User userX = userService.findUserByEmail(userEmail);
		
		String query = "Select * from user where email = '" + userEmail+"'";
		List result = em.createNativeQuery(query).getResultList();
		
		User userY = result.size()>0 ? new User((Object[])  result.get(0)) : null;
		//System.out.println("########## " + results.size());
		
		modelAndView.addObject("userX", userY);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user/profile");
		return modelAndView;
	}
	
	@RequestMapping(value= "/user/upload", method = RequestMethod.POST) 
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/user/" + user.getId();
        }

        try {

            // Get the file and save it somewhere
        	String content = new String(file.getBytes(), "UTF-8");
        	System.out.println(content);
        	
        	
        	DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(content));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("post");
            if(nodes.getLength() == 0) throw new Exception("No posts to add");
            List <Post> postsToAdd = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
              Element element = (Element) nodes.item(i);

              Node child = element.getFirstChild();
              if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                Post p = new Post();
                p.setContent(cd.getData());
                postsToAdd.add(p);
              }
              
            }
            
            
            for(Post p : postsToAdd) {
            	p.setUser(user);
            	p.setCreationDate(LocalDateTime.now().toString());
            	Thread.sleep(1);
            }
            postService.saveAll(postsToAdd);
            
            
            redirectAttributes.addFlashAttribute("message","You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message","Some error occured while adding your XML posts. Please verify if your file follows the correct format!");
        }

        return "redirect:/user/" + user.getId();
    }
	
	@RequestMapping(value="/editUser/{id}", method = RequestMethod.POST)
	public ModelAndView editPost(@Valid User userX, BindingResult bindingResult, @PathVariable("id") int userId){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loggedUser = userService.findUserByEmail(auth.getName());
		//if(loggedUser.getId() == userId) {
			User user = userService.findUserById(userId);
			user.setEmail(userX.getEmail());
			user.setName(userX.getName());
			user.setLastName(userX.getLastName());
			user.setBio(userX.getBio());
			userService.saveUser(user);
			modelAndView.addObject("message", "User profile has been edited successfully");

			modelAndView.addObject("user", userService.findUserById(userId));
		//}
		//else {
		//	modelAndView.addObject("message", "You can't modify other user's profile!");
		//}
		
		modelAndView.setViewName("user/editUser");
		return modelAndView;
	}
	
	@RequestMapping(value="/editUser/{id}", method = RequestMethod.GET)
	public ModelAndView editPost(@PathVariable("id") int userId){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loggedUser = userService.findUserByEmail(auth.getName());
		User user = userService.findUserById(userId);
		//if(user.getId() == userId) {
			modelAndView.setViewName("user/editUser");
		//}
		//else {
		//	modelAndView.addObject("message", "You can't modify other user's profile!");
		//	modelAndView.setViewName("feed");
		//}
		modelAndView.addObject("user", user);
		
		return modelAndView;
	}
	
	
	
}
