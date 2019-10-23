package com.ss.lms.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

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
	
	//We provide the data source for users using the below config method
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(jdbcudm.getDataSource()).passwordEncoder(passwordEncoder);
    }

    //We provide request authorization in the below method
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Below line will allow any authenticated user to access any resource within the system
        http.authorizeRequests()
    		.antMatchers("/user/**").hasAuthority("ADMIN")
        	.antMatchers("/lms/admin/**").hasAuthority("ADMIN")
        	.antMatchers("/lms/librarian/**").hasAnyAuthority("LIBRARIAN","ADMIN")
        	.antMatchers("/lms/borrower/**").hasAnyAuthority("BORROWER","LIBRARIAN","ADMIN")
        	.and().httpBasic().and().formLogin()
        	.and().csrf().disable();
    }
}
