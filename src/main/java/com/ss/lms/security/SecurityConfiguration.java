package com.ss.lms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ss.lms.entity.User;
import com.ss.lms.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

//    //We provide the data source for users using the below config method
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().
//                withUser("admin").password(passwordEncoder().encode("password")).
//                roles("ADMIN").authorities("ACCESS_ADMIN","ACCESS_LIB","ACCESS_BORR")
//                .and().
//                withUser("librarian").password(passwordEncoder().encode("libpass")).
//                roles("LIBRARIAN").authorities("ACCESS_LIB","ACCESS_BORR")
//                .and().
//                withUser("borrower").password(passwordEncoder().encode("borrpass")).
//                roles("BORROWER").authorities("ACCESS_BORR");
//    }
//
//    //We provide request authorization in the below method
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //Below line will allow any authenticated user to access any resource within the system
//        http.authorizeRequests().antMatchers("/lms/global").permitAll()
//                .antMatchers("/lms/admin/**").hasAuthority("ACCESS_ADMIN")
//                .antMatchers("/lms/librarian/**").hasAnyAuthority("ACCESS_LIB")
//                .antMatchers("/lms/borrower/**").hasAnyAuthority("ACCESS_BORR")
//                .and().formLogin();
//
//    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().hasAnyRole("ADMIN", "LIBRARIAN", "BORROWER").and().formLogin().and()
				.logout().permitAll().logoutSuccessUrl("/login");
		
//				.and()
//	    .csrf().disable();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserService();
	};

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};

}
