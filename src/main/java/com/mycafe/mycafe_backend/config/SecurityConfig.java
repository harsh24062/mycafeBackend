package com.mycafe.mycafe_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mycafe.mycafe_backend.jwt.JwtFilter;
import com.mycafe.mycafe_backend.service.MyUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        
        return http
                  .csrf(customizer -> customizer.disable())
                  .authorizeHttpRequests(request -> request.requestMatchers("/user/login","/user/signup","/user/forgotPassword","/user/otp-changePassword").permitAll().anyRequest().authenticated())
                  .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                  .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager(); 
    }

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuth=new DaoAuthenticationProvider();
        daoAuth.setUserDetailsService(myUserDetailService);
        daoAuth.setPasswordEncoder(new BCryptPasswordEncoder(12)); 
        return daoAuth;
    }


}
