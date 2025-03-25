package com.mycafe.mycafe_backend.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycafe.mycafe_backend.constant.CafeConstant;
import com.mycafe.mycafe_backend.model.Otp;
import com.mycafe.mycafe_backend.model.User;
import com.mycafe.mycafe_backend.repository.OtpRepo;
import com.mycafe.mycafe_backend.repository.UserRepo;
import com.mycafe.mycafe_backend.utils.CafeUtils;

@Service
public class OtpService {
       
    @Autowired
    private OtpRepo otpRepo;

    @Autowired
    private UserRepo userRepo;

    private BCryptPasswordEncoder encoder(){
      return new BCryptPasswordEncoder(12);
    }

    // Generate a 6-Digit OTP
    public String generateOtp(){
      Random random=new Random();
      int otp=random.nextInt(100000,1000000);
      return String.valueOf(otp);
    }

    // Save OTP in database
    public void saveOtp(String email, String otp){
        Optional<Otp> optional=otpRepo.findByEmail(email);

        if(optional.isPresent()){
           //update the OTP in email
           otpRepo.updateOtp(email, otp,LocalDateTime.now().plusMinutes(5));
           
        }else{
            // create new field of OTP
            Otp newOtpEntry=new Otp();
            newOtpEntry.setEmail(email);
            newOtpEntry.setOtp(otp);
            newOtpEntry.setExpiration(LocalDateTime.now().plusMinutes(5)); // otp expire in 5 Min.
            otpRepo.save(newOtpEntry);
        }
    }
    
    
    //validate OTP
    public boolean validateOtp(String email, String otp){
        Optional<Otp> optional=otpRepo.findByEmail(email);

        if(optional.isEmpty())return false;

        Otp otpDetail=optional.get();

        if(!otpDetail.getExpiration().isBefore(LocalDateTime.now()) && otpDetail.getOtp().equals(otp)){
            return true;
        }
        return false;
    }

    public ResponseEntity<String> changePassword(Map<String,String> requestMap) {
        try {
          String email=requestMap.get("email");
          String otp=requestMap.get("otp");
          Optional<User> optional=userRepo.findByEmail(email);
          if(validateOtp(email, otp) && optional.isPresent()){
            User user=optional.get();

            user.setPassword(encoder().encode(requestMap.get("newPassword")));
            userRepo.save(user);
            
            return  CafeUtils.getResponseEntitty("Password Updated Successfully",HttpStatus.OK);
            
          }else{
            return CafeUtils.getResponseEntitty("Bad Credentials, Try Again!!",HttpStatus.UNAUTHORIZED);
          }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
      return  CafeUtils.getResponseEntitty(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
