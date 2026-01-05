package com.order.util;

import java.security.Key;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenValidator {

	private static final String SECRET = "my-super-secret-key-my-super-secret-key";

	private Key getSignKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	public String extractUsername(String token) {
		return extractClaim(token, claims -> claims.getSubject());
	}

	public List<SimpleGrantedAuthority> extractRoles(String token) {
		List<String> roles = extractClaim(token, claims -> claims.get("roles", List.class));
		return roles.stream().map(role -> new SimpleGrantedAuthority(role)).toList();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public boolean isTokenValid(String token, UserDetails userDetails, int currentVersion) {
		return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token)
				&& extractTokenVersion(token) == currentVersion;
	}

	public Integer extractTokenVersion(String token) {
		return extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));
	}

	public String extractTokenType(String token) {
		return extractClaim(token, claims -> claims.get("type", String.class));
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, claims -> claims.getExpiration()).before(new java.util.Date());
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
		return claimsResolver.apply(claims);
	}
}
