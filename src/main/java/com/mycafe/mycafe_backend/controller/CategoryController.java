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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.model.Category;
import com.mycafe.mycafe_backend.service.CategoryService;
import com.mycafe.mycafe_backend.utils.CafeUtils;

@RestController
@RequestMapping(path = "/category")
public class CategoryController {
     
   @Autowired
   private CategoryService categoryService;

    
   @PostMapping(path="/add")
   public ResponseEntity<String> addNewCategory(@RequestBody(required = true) Map<String,String> requestMap){
       try {
          return categoryService.addNewCategory(requestMap);
       } catch (Exception e) {
        e.printStackTrace();
       }
       return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
   }
    

   @GetMapping(path = "/get")
   public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue){
         try {
            return categoryService.getAllCategory(filterValue);
         } catch (Exception e) {
            e.printStackTrace();
         }
         return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
   }


   @PostMapping(path = "/update")
   public ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String,String> requestMap){
      try {
         return categoryService.updateCategory(requestMap);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
   }

}
