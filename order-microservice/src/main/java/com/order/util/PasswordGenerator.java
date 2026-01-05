package com.order.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Component
@Getter
public class PasswordGenerator {

	private String encode;

	@PostConstruct
	public void generate() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		encode = encoder.encode("order-12345");
	}
}
