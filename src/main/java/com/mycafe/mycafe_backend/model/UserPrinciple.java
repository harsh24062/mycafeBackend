package com.mycafe.mycafe_backend.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrinciple implements UserDetails {

    private User user;

    public UserPrinciple(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
       return user.getPassword();
    }

    @Override
    public String getUsername() {
       return user.getEmail();
    }

}
