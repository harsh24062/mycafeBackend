package com.mycafe.mycafe_backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.service.OtpService;
import com.mycafe.mycafe_backend.service.UserService;
import com.mycafe.mycafe_backend.utils.CafeUtils;
import com.mycafe.mycafe_backend.wrapper.UserWrapper;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;
       
    // signup  --- add new entry in database for new user
    @PostMapping(path = "/signup")
    public ResponseEntity<String>  signUp(@RequestBody(required = true) Map<String,String> requestMap){
        try {
            return userService.signUp(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);       
    }

    // login -- it will aslo return a jwt token
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody Map<String,String> requestMap){
        try {
          return userService.login(requestMap); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
    }

    // provide list of ueer to only admin
    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>> getAllUser(){
        try {
            return  userService.getAllUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // used to update status 
    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody(required = true) Map<String,String> requestMap){
        try {
            return userService.update(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR); 
    }


    @GetMapping(path= "/checkToken")
    public ResponseEntity<String> checkToken(){
        try {
           return  userService.checkToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody Map<String,String> requestMap){
        try {
            return userService.changePassword(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    } 


    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String,String> requestMap){
       try {
         return userService.forgotPassword(requestMap);
       } catch (Exception e) {
        e.printStackTrace();
       }
       return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping(path = "otp-changePassword")
    public ResponseEntity<String> otpChangePassword(@RequestBody Map<String,String> requestMap){
        try {
          return  otpService.changePassword(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
