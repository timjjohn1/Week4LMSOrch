package com.ss.lms.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/**")
public class UserController
{
	@Autowired
	JdbcUserDetailsManager jdbcudm;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping(value = "why/{username}", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<UserDetails> readUserByName(@PathVariable String username)
	{
		UserDetails result = jdbcudm.loadUserByUsername(username);
		return new ResponseEntity<UserDetails>(result,HttpStatus.OK);
	}
	
	// TODO http status codes? idk man- how does literally any of this work- may the spring docs have mercy on my soul
	@RequestMapping("admin/username/{userName}/password/{password}")
	public void test(@PathVariable("userName") String userName, @PathVariable("password") String password)
	{
		// Create the authorities for the user, this makes some sense, but where do i define what an authroity means??
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		
		// Instantiate the user, is the user entity defined by spring? Do i need to make a dao???? what??????????????
		UserDetails user = new User(userName, passwordEncoder.encode(password), authorities);
		
		// Save the user to the db, where is the schema made??? does it do it for you????
		jdbcudm.createUser(user);
		
		// create an authentication token and add the authentication token to the security context (????)
																// why is this null?, what is a "principal"!?!?!?
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
		// where is the security conext? Do i need to implement anything? why are there no comprehensive tutorials on this?
		// isnt spring over a decade old? why is it so hard to find info on this stuff???????
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
