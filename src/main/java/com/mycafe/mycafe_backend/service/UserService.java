package com.mycafe.mycafe_backend.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.model.User;
import com.mycafe.mycafe_backend.repository.UserRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    public ResponseEntity<String> signUp(Map<String,String> requestMap) {

       log.info("Inside signup {}",requestMap);

       try{
       if(validateSignUpMap(requestMap)){
         Optional<User> userOptional=userRepo.findByEmail(requestMap.get("email"));

          if(userOptional.isPresent()){
            return CafeUtils.getResponseEntitty("Email already exits", HttpStatus.BAD_REQUEST); 
          }else{
            userRepo.save(getUserFromMap(requestMap)); 
            return CafeUtils.getResponseEntitty("Successfully Registered", HttpStatus.OK);
          }
       }
       return CafeUtils.getResponseEntitty(CafeConstant.INVALID_DATA,HttpStatus.BAD_REQUEST);
     }catch(Exception e){
        e.printStackTrace();
     }

     return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String,String> requestMap){

        if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && 
           requestMap.containsKey("email") && requestMap.containsKey("password")){
           return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String,String> requestMap){
        User user=new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
      
        return user;
    }

}
