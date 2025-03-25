package com.mycafe.mycafe_backend.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {

   private final String seceretKey="Hf8iW5u4fz+1K3Vw1R7a9qZOl6Rlny3c/UN2H7aXhDg=";
   private SecretKey SECERT_KEY=Keys.hmacShaKeyFor(Base64.getDecoder().decode(seceretKey));


   //NOTE-> here in String username , we will use email as it will be unique for all users

   public String generateToken(String username, String role){
       Map<String,Object> claims=new HashMap<>();
       claims.put("role", role);
       return createToken(claims, username);
   }

   private String createToken(Map<String,Object> claims,String subject){
      return Jwts.builder()
                 .claims(claims)
                 .subject(subject)
                 .header().add("typ","JWT")
                 .and()
                 .issuedAt(new Date(System.currentTimeMillis()))
                 .expiration(new Date(System.currentTimeMillis()+1000*60*60))  // 1 hour
                 .signWith(SECERT_KEY)
                 .compact(); 
    }


   public String extractUserName(String token){
     return extractClaim(token, Claims::getSubject);
   }
   
   public <T> T extractClaim(String token,Function<Claims,T> claimsResolver){
      final Claims claims=extractAllClaims(token);
      return claimsResolver.apply(claims);
   }

   public Claims extractAllClaims(String token){
      return  Jwts
                 .parser()
                 .verifyWith(SECERT_KEY)
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();
   }

   public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
