package com.ss.lms.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ss.lms.dao.UserDao;
import com.ss.lms.entity.User;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	UserDao userDao;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		/*
		 * Here we are using dummy data, you need to load user data from database or
		 * other third party application
		 */
		Optional<User> user = userDao.findByUsername(username);

		UserBuilder builder = null;
		if (user.isPresent()) {
			builder = org.springframework.security.core.userdetails.User.withUsername(username);
			builder.password(new BCryptPasswordEncoder().encode(user.get().getPassword()));
			builder.roles(user.get().getPassword());
		} else {
			throw new UsernameNotFoundException("User not found.");
		}

		return builder.build();
	}
	
	public User saveUser(User user) {
		return userDao.save(user);
	}

////Username, password, roles, permissions
//	private User findUserbyUsername(String username) {
//		if (username.equalsIgnoreCase("admin")) {
//			return new User(username, "admin123", "ADMIN");
//		}
//		return null;
//	}
}
