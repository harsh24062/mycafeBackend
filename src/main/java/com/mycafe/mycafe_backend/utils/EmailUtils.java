package com.mycafe.mycafe_backend.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailUtils {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
       SimpleMailMessage message=new SimpleMailMessage();
       message.setFrom("charshu26@gmail.com");
       message.setTo(to);
       message.setSubject(subject);
       message.setText(text);
       if(list!=null && list.size()>0)message.setCc(getCcArray(list));
       javaMailSender.send(message);
    }

    private String[] getCcArray(List<String> list){
        String cc[]=new String[list.size()];
        for(int i=0;i<list.size();i++){
            cc[i]=list.get(i);
        }
        return cc;
    }


    public void forgotPassword(String to,String subject,String otp) throws MessagingException{

        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message,true);
      
        helper.setFrom("charshu26@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg="<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to + " <br><b>OTP: </b> " + otp + "<br><a href=\"http://localhost:8080/user/otp-changePassword\">Click here to change Password</a></p>";
        message.setContent(htmlMsg,"text/html");

        javaMailSender.send(message);
     }


   

}
