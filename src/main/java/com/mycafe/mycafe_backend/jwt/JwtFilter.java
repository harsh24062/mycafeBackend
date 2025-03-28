package com.mycafe.mycafe_backend.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mycafe.mycafe_backend.service.MyUserDetailService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailService myUserDetailService;

    private Claims claims=null;
    private String username=null; // its going to be email

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       

       if(request.getServletPath().matches("/user/login|/user/signup|/user/forgotPassword|/user/otp-changePassword")){
           filterChain.doFilter(request, response);
       }

       else{
           String  authString=request.getHeader("Authorization");
          String token=null;
        
          if(authString!=null && authString.startsWith("Bearer ")){
              token = authString.substring(7);
              username = jwtUtil.extractUserName(token);
              claims = jwtUtil.extractAllClaims(token);
            }

           if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
             UserDetails userDetails=myUserDetailService.loadUserByUsername(username);

             if(jwtUtil.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
             }
           }

          filterChain.doFilter(request, response);
        } 
    }


    public boolean isAdmin(){
        return "admin".equalsIgnoreCase((String)claims.get("role"));
    }

    public boolean isUser(){
        return "user".equalsIgnoreCase((String)claims.get("role"));
    }

    public String currentUser(){
        return username;
    }

}
