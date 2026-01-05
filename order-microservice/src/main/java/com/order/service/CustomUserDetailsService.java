package com.order.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.order.util.PasswordGenerator;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final PasswordGenerator passwordGenerator;

	public CustomUserDetailsService(PasswordGenerator passwordGenerator) {
		this.passwordGenerator = passwordGenerator;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		if (!"taher".equals(username)) {
			throw new UsernameNotFoundException("User not found");
		}
		return User.builder().username(username).password(passwordGenerator.getEncode()).roles("ADMIN").build();
	}
}
