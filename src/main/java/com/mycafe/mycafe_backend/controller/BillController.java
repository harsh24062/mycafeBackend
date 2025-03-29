package com.mycafe.mycafe_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.service.BillService;
import com.mycafe.mycafe_backend.utils.CafeUtils;

@RestController
@RequestMapping(path = "/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping(path = "/generateReport")
    public ResponseEntity<String> generateReport(@RequestBody(required = true) Map<String,Object> requestMap){
       try {
        return billService.generateReport(requestMap);
       } catch (Exception e) {
       e.printStackTrace();
       }
       return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
