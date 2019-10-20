package com.ss.lms.security;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	@Autowired
	DataSource dataSource;
	
	@Autowired
	JdbcUserDetailsManager jdbcudm;
	
	@Autowired
	PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		jdbcUserDetailsManager.setDataSource(dataSource);
		return jdbcUserDetailsManager;
	}
	
	//We provide the data source for users using the below config method
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(jdbcudm.getDataSource()).passwordEncoder(passwordEncoder());
    }

    //We provide request authorization in the below method
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Below line will allow any authenticated user to access any resource within the system
        http.authorizeRequests()
        	.antMatchers("/lms/admin**").hasAnyRole("ACCESS_ADMIN")
        	.antMatchers("/lms/librarian**").hasAnyRole("ACCESS_LIB")
        	.antMatchers("/lms/borrower**").hasAnyRole("ACCESS_BORR")
        	.antMatchers("/user**").permitAll()
        	.and().formLogin();
    }
	
	@GetMapping(value = "/user/{username}", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<UserDetails> readUserByName(@PathVariable String username)
	{
		UserDetails result = jdbcudm.loadUserByUsername(username);
		return new ResponseEntity<UserDetails>(result,HttpStatus.OK);
	}
	
	// TODO http status codes? idk man- how does literally any of this work- may the spring docs have mercy on my soul
	@RequestMapping("/user/admin/username/{userName}/password/{password}")
	public void test(@PathVariable("userName") String userName, @PathVariable("password") String password)
	{
		// Create the authorities for the user, this makes some sense, but where do i define what an authroity means??
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ACCESS_ADMIN"));
		
		// Instantiate the user, is the user entity defined by spring? Do i need to make a dao???? what??????????????
		UserDetails user = new User(userName, passwordEncoder().encode(password), authorities);
		
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
