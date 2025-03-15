package com.mycafe.mycafe_backend.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.jwt.JwtUtil;
import com.mycafe.mycafe_backend.model.User;
import com.mycafe.mycafe_backend.repository.UserRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private  MyUserDetailService myUserDetailService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

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
        log.error("{}",e);
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
        user.setPassword(encoder().encode(requestMap.get("password")));
        user.setStatus("false");
        user.setRole("user");
      
        return user;
    }

    private BCryptPasswordEncoder encoder(){
      return new BCryptPasswordEncoder(12);
    }

    public ResponseEntity<String> login(Map<String,String>requestMap) {
      log.info("Inside login");

      try {

        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password")));
        if(authentication.isAuthenticated()){
          if(myUserDetailService.getUserDetail().getStatus().equalsIgnoreCase("true")){
            return new ResponseEntity<String>("{\"token\":\""+ jwtUtil.generateToken(myUserDetailService.getUserDetail().getEmail(), myUserDetailService.getUserDetail().getRole())+"\"}",HttpStatus.OK);
          }
        
          else{
          return new ResponseEntity<String>(" Message: Wait for Admin Approval",HttpStatus.BAD_REQUEST);
          }
        }

      } catch (Exception e) {
        log.error("{}",e);
      }
      return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
    }

}
