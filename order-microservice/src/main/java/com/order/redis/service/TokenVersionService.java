package com.order.redis.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenVersionService {

	private static final String PREFIX = "token_version:";
	private static final Duration TTL = Duration.ofDays(7);

	@Autowired
	private RedisService redisService; // Switch to this for centralized ops

	// Get current token version
	public int getVersion(String username) {
		String key = PREFIX + username;
		String val = redisService.get(key);
		return val == null ? 0 : Integer.parseInt(val);
	}

	// Initialize key if absent
	public void initIfAbsent(String username) {
		String key = PREFIX + username;
		if (!redisService.hasKey(key)) {
			redisService.set(key, "0", TTL);
		}
	}

	// Increment version on logout
	public void increment(String username) {
		String key = PREFIX + username;
		Long val = redisService.increment(key); // Now with logging & verify
		System.out.println("REDIS VERSION = " + val); // Keep your print
	}
}
