package com.mycafe.mycafe_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mycafe.mycafe_backend.model.Product;
import com.mycafe.mycafe_backend.wrapper.ProductWrapper;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer>{
  
    @Query("SELECT new com.mycafe.mycafe_backend.wrapper.ProductWrapper(p.id,p.name,p.description,p.price,p.status,p.category.id,p.category.name) FROM Product p")
    List<ProductWrapper> getAllProduct();

}
