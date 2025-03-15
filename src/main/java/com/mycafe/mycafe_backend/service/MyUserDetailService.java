package com.mycafe.mycafe_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycafe.mycafe_backend.model.User;
import com.mycafe.mycafe_backend.model.UserPrinciple;
import com.mycafe.mycafe_backend.repository.UserRepo;

@Service
public class MyUserDetailService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;

    private User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional=userRepo.findByEmail(username);

        if(userOptional.isPresent()) {
            userDetail=userOptional.get();
            return new UserPrinciple(userDetail);}

        else throw new UsernameNotFoundException("User not Found!!!");
    }

    public User getUserDetail(){
        return userDetail;
    }

}
