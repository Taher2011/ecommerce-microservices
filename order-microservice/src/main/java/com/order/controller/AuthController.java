package com.order.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.dto.AuthResponse;
import com.order.dto.LoginRequest;
import com.order.service.TokenVersionService;
import com.order.util.JwtTokenGenerator;
import com.order.util.JwtTokenValidator;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenGenerator jwtTokenGenerator;
	private final UserDetailsService userDetailsService;
	private final TokenVersionService tokenVersionService;
	private final JwtTokenValidator jwtTokenValidator; // Already injected

	public AuthController(AuthenticationManager authenticationManager, JwtTokenGenerator jwtTokenGenerator,
			UserDetailsService userDetailsService, TokenVersionService tokenVersionService,
			JwtTokenValidator jwtTokenValidator) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtTokenGenerator = jwtTokenGenerator;
		this.userDetailsService = userDetailsService;
		this.tokenVersionService = tokenVersionService;
		this.jwtTokenValidator = jwtTokenValidator;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		tokenVersionService.initIfAbsent(userDetails.getUsername());
		String accessToken = jwtTokenGenerator.generateToken(userDetails, "ACCESS");
		String refreshToken = jwtTokenGenerator.generateToken(userDetails, "REFRESH");
		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.body(new AuthResponse(accessToken, refreshToken));
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader) {
		String refreshToken = authHeader.substring(7);
		String username = jwtTokenValidator.extractUsername(refreshToken); // Use injected
		UserDetails user = userDetailsService.loadUserByUsername(username);
		int currentVersion = tokenVersionService.getVersion(username);
		if (!"REFRESH".equals(jwtTokenValidator.extractTokenType(refreshToken)) // Use injected
				|| !jwtTokenValidator.isTokenValid(refreshToken, user, currentVersion)) { // Use injected
			throw new RuntimeException("Invalid refresh token");
		}
		String newAccessToken = jwtTokenGenerator.generateToken(user, "ACCESS");
		return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(newAccessToken, refreshToken));
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			String username = jwtTokenValidator.extractUsername(token);
			if (username != null) {
				tokenVersionService.increment(username); // This should now log everything
			}
		}
		return ResponseEntity.ok("Logged out successfully");
	}
}
