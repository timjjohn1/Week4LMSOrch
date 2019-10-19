package com.ss.lms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    //We provide the data source for users using the below config method
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
                withUser("admin").password(passwordEncoder().encode("password")).
                roles("ADMIN").authorities("ACCESS_ADMIN","ACCESS_LIB","ACCESS_BORR")
                .and().
                withUser("librarian").password(passwordEncoder().encode("libpass")).
                roles("LIBRARIAN").authorities("ACCESS_LIB","ACCESS_BORR")
                .and().
                withUser("borrower").password(passwordEncoder().encode("borrpass")).
                roles("BORROWER").authorities("ACCESS_BORR");
    }

    //We provide request authorization in the below method
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Below line will allow any authenticated user to access any resource within the system
        http.authorizeRequests().antMatchers("/api/global").permitAll()
                .antMatchers("/lms/admin/**").hasAuthority("ACCESS_ADMIN")
                .antMatchers("/lms/librarian/**").hasAnyAuthority("ACCESS_LIB")
                .antMatchers("/lms/borrower/**").hasAnyAuthority("ACCESS_BORR")
                .and().formLogin();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }
}
