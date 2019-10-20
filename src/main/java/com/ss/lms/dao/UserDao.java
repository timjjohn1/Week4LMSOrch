package com.ss.lms.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ss.lms.entity.User;

public interface UserDao extends CrudRepository<User, Long> {
	 
    Optional<User> findByUsername(String username);
}