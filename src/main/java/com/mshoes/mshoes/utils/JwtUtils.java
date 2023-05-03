package com.mshoes.mshoes.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final String JWT_SECRET = "jLYCne6ll1inJXAzA5JrBBScizdGMWhmMEtLuk1iFEEOkfH9HSzcMrISnNs+JNZJjcThv2f6UhO4ERaG7XGNYA==";

    SecretKey secretKey = new SecretKeySpec(JWT_SECRET.getBytes(), SignatureAlgorithm.HS512.getJcaName());

    public  Claims getClaimsFromToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    public  String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getTokenLoginFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) { // Tên của cookie lưu token là "token"
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
