package com.mycafe.mycafe_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.jwt.JwtFilter;
import com.mycafe.mycafe_backend.model.Category;
import com.mycafe.mycafe_backend.repository.CategoryRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private JwtFilter jwtFilter;

    public ResponseEntity<String> addNewCategory(Map<String,String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                // we keep validateId false if we don't want to check key in map 
                // we keep validateId true if we want to check key in map
               if(validateCategoryMap(requestMap,false)){
                // have to understand iSAdd logic here ********
                categoryRepo.save(getCategoryFromMap(requestMap, false));
                return CafeUtils.getResponseEntitty("Category Added Successfully",HttpStatus.OK);
               }else{
                return CafeUtils.getResponseEntitty(CafeConstant.INVALID_DATA,HttpStatus.BAD_REQUEST);
               }

            }else{
                return CafeUtils.getResponseEntitty(CafeConstant.UNAUTHORIZED_REQUEST,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // we keep validateId false if we dont want to check key(Id) in map 
    // we keep validateId true if we want to check key(Id) in map
    private boolean validateCategoryMap(Map<String,String> requestMap, boolean validateId){
       if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
       }
       return false;
    }

   // we keep isAdd false if we don't want to add key(Id) in category object in map 
    // we keep isAdd true if we want to  add key(Id) in category object in map
    private Category getCategoryFromMap(Map<String,String> requestMap, boolean isAdd){
      Category category=new Category();
      if(isAdd){
        category.setId(Integer.parseInt(requestMap.get("id")));
      }
      category.setName(requestMap.get("name"));
     return category;
    }


    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if(!Strings.isNullOrEmpty(filterValue) &&  filterValue.equalsIgnoreCase("true")){
                return new ResponseEntity<>(categoryRepo.getAllCategory(),HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryRepo.findAll(),HttpStatus.OK);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<String> updateCategory(Map<String,String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                  Optional<Category> optional=categoryRepo.findById(Integer.parseInt(requestMap.get("id")));
                  if(optional.isPresent()){
                    categoryRepo.save(getCategoryFromMap(requestMap,true));
                    return CafeUtils.getResponseEntitty("Category updated Successfully",HttpStatus.OK);
                  }else{
                    return new ResponseEntity<>("Category Id doesn't exist!!!",HttpStatus.BAD_REQUEST);
                  }
                }else{
                    return CafeUtils.getResponseEntitty(CafeConstant.INVALID_DATA,HttpStatus.BAD_REQUEST);
                }

            }else{
                return CafeUtils.getResponseEntitty(CafeConstant.UNAUTHORIZED_REQUEST,HttpStatus.UNAUTHORIZED);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
