package com.mycafe.mycafe_backend.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

}
