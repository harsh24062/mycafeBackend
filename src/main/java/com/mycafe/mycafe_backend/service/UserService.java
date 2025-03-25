package com.mycafe.mycafe_backend.service;

import java.util.ArrayList;
import java.util.List;
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
import com.mycafe.mycafe_backend.jwt.JwtFilter;
import com.mycafe.mycafe_backend.jwt.JwtUtil;
import com.mycafe.mycafe_backend.model.User;
import com.mycafe.mycafe_backend.repository.UserRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;
import com.mycafe.mycafe_backend.utils.EmailUtils;
import com.mycafe.mycafe_backend.wrapper.UserWrapper;

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
    private JwtFilter jwtFilter;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private OtpService otpService;

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
        user.setStatus("true");
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
      return CafeUtils.getResponseEntitty("Invalid Email or Password, Try Again!!!", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<UserWrapper>> getAllUser() {
      try {
        if(jwtFilter.isAdmin()){
          return new ResponseEntity<List<UserWrapper>>(userRepo.getAllUser(),HttpStatus.OK);
        }else{
          return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }

      } catch (Exception e) {
         e.printStackTrace();
      }
      
      return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<String> update(Map<String,String> requestMap) {
      try {

        if(jwtFilter.isAdmin()){
           
          Optional<User> optional=userRepo.findById(Integer.parseInt(requestMap.get("id")));
          
          if(optional.isPresent()){
            userRepo.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
            sendMailToAllAdmins(requestMap.get("status"),optional.get().getEmail(),userRepo.getAllAdmin());
            return CafeUtils.getResponseEntitty("Status Updated",HttpStatus.OK);
          }else{
            return CafeUtils.getResponseEntitty("User Id not Exist",HttpStatus.BAD_REQUEST);
          }

        }else{
          return CafeUtils.getResponseEntitty(CafeConstant.UNAUTHORIZED_REQUEST,HttpStatus.UNAUTHORIZED);
        }
        
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void sendMailToAllAdmins(String status, String email, List<String> allAdmins){
      allAdmins.remove(jwtFilter.currentUser());
      if(status!=null && status.equalsIgnoreCase("true")){
        emailUtils.sendSimpleMessage(jwtFilter.currentUser(), "Account Approved","User: \n"+email+"\n Approved by Admin:\n"+jwtFilter.currentUser(), allAdmins);
      }else{
        emailUtils.sendSimpleMessage(jwtFilter.currentUser(), "Account Disabled","User: \n"+email+"\n Disabled by Admin:\n"+jwtFilter.currentUser(), allAdmins);
      }
    }

    public ResponseEntity<String> checkToken() {
      return CafeUtils.getResponseEntitty("true",HttpStatus.OK);
    }


    // change Password Service
    public ResponseEntity<String> changePassword(Map<String,String> requestMap) {
       try {
          Optional<User> optional=userRepo.findByEmail(jwtFilter.currentUser());
          if(optional.isPresent()){
            User user=optional.get();
            if(encoder().matches(requestMap.get("oldPassword"),user.getPassword())){
              user.setPassword(encoder().encode(requestMap.get("newPassword")));
              userRepo.save(user);
              return  CafeUtils.getResponseEntitty("Password Updated Successfully",HttpStatus.OK);
            }else{
              return CafeUtils.getResponseEntitty("Incorrect Old Password",HttpStatus.BAD_REQUEST);
            }
          }else{
            return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
          }
       } catch (Exception e) {
        e.printStackTrace();
       }
       return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // forgot Password service
    public ResponseEntity<String> forgotPassword(Map<String,String> requestMap) {
      try {
        Optional<User> optional=userRepo.findByEmail(requestMap.get("email"));
        if(optional.isPresent()){
            
          //otp logic write here
          String otp=otpService.generateOtp();
          otpService.saveOtp(requestMap.get("email"), otp);

          // send email to emailId with otp
          emailUtils.forgotPassword(requestMap.get("email"),"mycafe OTP Verification to changePassword",otp);

          return CafeUtils.getResponseEntitty("OTP sent to Your Email Id",HttpStatus.OK);

        }else{
          return CafeUtils.getResponseEntitty("Email doesn't exist!!",HttpStatus.BAD_REQUEST);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
