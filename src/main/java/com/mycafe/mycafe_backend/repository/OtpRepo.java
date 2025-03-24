package com.mycafe.mycafe_backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mycafe.mycafe_backend.model.Otp;

import jakarta.transaction.Transactional;

@Repository
public interface OtpRepo extends JpaRepository<Otp,Long> {
     
    Optional<Otp> findByEmail(String email); 

    @Modifying
    @Transactional
    @Query("UPDATE Otp u SET u.otp=:otp, u.expiration=:expiration WHERE u.email=:email")
    void updateOtp(@Param("email") String email,@Param("otp") String otp,@Param("expiration") LocalDateTime expiration);

}
