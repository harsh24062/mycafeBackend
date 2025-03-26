package com.mycafe.mycafe_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mycafe.mycafe_backend.model.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {

   @Query("SELECT c FROM Category c WHERE c.id IN(SELECT p.category.id FROM Product p WHERE p.status='true')")
   List<Category> getAllCategory();

}
