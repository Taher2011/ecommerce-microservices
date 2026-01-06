package com.order.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.order.service.TokenVersionService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenGenerator {

	private final TokenVersionService tokenVersionService;

	public JwtTokenGenerator(TokenVersionService tokenVersionService) {
		this.tokenVersionService = tokenVersionService;
	}

	private static final String SECRET = "my-super-secret-key-my-super-secret-key";

	private static final long ACCESS_EXPIRATION = 1000 * 60 * 10; // 10 minutes
	private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days

	private Key getSignKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	public String generateToken(UserDetails userDetails, String type) {
		Map<String, Object> claims = new HashMap<>();
		int version = tokenVersionService.getVersion(userDetails.getUsername());
		List<String> roles = userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).toList();
		claims.put("roles", roles);
		claims.put("tokenVersion", version);
		claims.put("type", type); // ACCESS or REFRESH
		long expirationTime = "ACCESS".equals(type) ? ACCESS_EXPIRATION : REFRESH_EXPIRATION;
		return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(getSignKey()).compact();
	}
}
