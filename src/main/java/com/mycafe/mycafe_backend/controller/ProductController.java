package com.mycafe.mycafe_backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.service.ProductService;
import com.mycafe.mycafe_backend.utils.CafeUtils;
import com.mycafe.mycafe_backend.wrapper.ProductWrapper;

@RestController
@RequestMapping(path = "/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(path = "/add")
    public ResponseEntity<String> addProduct(@RequestBody Map<String,String> requestMap){
        try {
            return productService.addProduct(requestMap);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping(path = "/get")
    public ResponseEntity<List<ProductWrapper>> getAllProduct(){
        try {
            return productService.getAllProduct();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping(path = "/update")
    public ResponseEntity<String> updateProduct(@RequestBody Map<String,String> requestMap){
        try {
            return productService.updateProduct(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable(name = "id",required = true) int id){
        try {
            return productService.deleteProductById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping(path = "/updateStatus")
    public ResponseEntity<String> updateStatus(@RequestBody Map<String,String> requestMap){
      try {
          return productService.updateStatus(requestMap);         
        } catch (Exception e) {
        e.printStackTrace();
      }
      return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/getByCategory/{id}")
    public ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable(name="id", required = true) int id){
      try {
      return  productService.getByCategory(id);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/getById/{id}")
    public ResponseEntity<ProductWrapper> getById(@PathVariable(name="id",required = true)int id){
        try {
            return productService.getProductById(id);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
