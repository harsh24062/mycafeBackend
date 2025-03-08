package com.mycafe.mycafe_backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtils {
     
    private CafeUtils(){

    }

    public static ResponseEntity<String> getResponseEntitty(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<>("{\"message\":\""+responseMessage+"\"}",httpStatus);
    } 


}
