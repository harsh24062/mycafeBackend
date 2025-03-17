package com.mycafe.mycafe_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mycafe.mycafe_backend.model.User;
import com.mycafe.mycafe_backend.wrapper.UserWrapper;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u WHERE u.email=:email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT new com.mycafe.mycafe_backend.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) FROM User u WHERE u.role='user'")
    List<UserWrapper> getAllUser();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status=:status WHERE u.id=:id")
    void updateStatus(@Param("status") String status,@Param("id") int id);

    @Query("SELECT u.email FROM User u WHERE u.role='admin'")
    List<String> getAllAdmin();

}
