package com.mycafe.mycafe_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycafe.mycafe_backend.model.Bill;

@Repository
public interface BillRepo extends JpaRepository<Bill,Integer> {
  
}
