package com.mycafe.mycafe_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.jwt.JwtFilter;
import com.mycafe.mycafe_backend.model.Category;
import com.mycafe.mycafe_backend.model.Product;
import com.mycafe.mycafe_backend.repository.ProductRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;
import com.mycafe.mycafe_backend.wrapper.ProductWrapper;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private JwtFilter jwtFilter;

    public ResponseEntity<String> addProduct(Map<String,String> requestMap) {
       try {
            if(jwtFilter.isAdmin()){
                // pass true if want to validate that map contains id 
               if(validateRequestMap(requestMap,false)){
                // pass true if want to add Product id in map
                productRepo.save(getProductFromRequestMap(requestMap,false));
                return CafeUtils.getResponseEntitty("Product Added Successfully",HttpStatus.OK);
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


    private boolean validateRequestMap(Map<String,String> requestMap, boolean validateId){
      if(requestMap.containsKey("name") && requestMap.containsKey("price")
         && requestMap.containsKey("categoryId") && requestMap.containsKey("description")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
      }
      return false;
    }


    private Product getProductFromRequestMap(Map<String,String> requestMap,boolean isAdd){
        
        Category category =new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product=new Product();

        if(isAdd){
           product.setId(Integer.parseInt(requestMap.get("id")));
        }
        product.setCategory(category);
        product.setStatus("true");
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        
        return product;
    }


    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
      try {
        return new ResponseEntity<>(productRepo.getAllProduct(),HttpStatus.OK);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<String> updateProduct(Map<String,String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
              if(validateRequestMap(requestMap,true)){
                Optional<Product> optional=productRepo.findById(Integer.parseInt(requestMap.get("id")));
                if(optional.isPresent()){
                  Product product=getProductFromRequestMap(requestMap,true);
                  product.setStatus(requestMap.get("status"));
                  productRepo.save(product);
                  return CafeUtils.getResponseEntitty("Product updated successfully",HttpStatus.OK);
                }else{
                    return CafeUtils.getResponseEntitty("Product Id doesn't exist",HttpStatus.BAD_REQUEST);
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


    public ResponseEntity<String> deleteProductById(int id) {
      try {
          if(jwtFilter.isAdmin()){
            Optional<Product> optional=productRepo.findById(id);
            if(optional.isPresent()){
             productRepo.deleteById(id);
             return new ResponseEntity<>("Product Deleted Successfully",HttpStatus.OK);
            }else{
              return new ResponseEntity<>("Product Id doesn't exist",HttpStatus.BAD_REQUEST);
            }
          }else{
            return CafeUtils.getResponseEntitty(CafeConstant.INVALID_DATA,HttpStatus.UNAUTHORIZED);
          }
      } catch (Exception e) {
        e.printStackTrace();
      } 
      return CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<String> updateStatus(Map<String,String> requestMap) {
      try {
         if(jwtFilter.isAdmin()){
           Optional<Product> optional=productRepo.findById(Integer.parseInt(requestMap.get("id")));
           if(optional.isPresent()){
            Product product=optional.get();
            product.setStatus(requestMap.get("status"));
            productRepo.save(product);
            return CafeUtils.getResponseEntitty("Status updated Successfully",HttpStatus.OK); 
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


    public ResponseEntity<List<ProductWrapper>> getByCategory(int id) {
      try {
        return new ResponseEntity<>(productRepo.getByCategory(id),HttpStatus.OK);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<ProductWrapper> getProductById(int id) {
      try {
          return new ResponseEntity<>(productRepo.getProductById(id),HttpStatus.OK); 
      } catch (Exception e) {
       e.printStackTrace();
      }
      return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
