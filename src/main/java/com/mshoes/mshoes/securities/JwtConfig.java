package com.mshoes.mshoes.securities;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.mshoes.mshoes.models.User;
import com.mshoes.mshoes.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtConfig {

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app.jwt-expiration-milliseconds}")
	private long jwtExpirationMillis;

	@Autowired
	private UserRepository userRepository;

	public String getJwtSecret() {
		return jwtSecret;
	}

	public long getJwtExpirationMillis() {
		return jwtExpirationMillis;
	}

	// generate token
	public String generateToken(Authentication authentication) {
		String username = authentication.getName();
		// Lấy user với username tương ứng
		User user = userRepository.findByUsername(username);

		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtExpirationMillis);

		SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());

		return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(expireDate)
				.claim("userId", user.getId()).signWith(secretKey).compact();
	}

	// get username from the token
	public String getUsernameFromJWT(String token) {
		SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());

		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	// validate JWT token
	public boolean validateToken(String token) {
		try {
			SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException ex) {
			return false;
		}

	}
}
