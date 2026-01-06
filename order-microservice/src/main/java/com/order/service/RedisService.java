package com.order.service;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

	public RedisService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void set(String key, String value, Duration ttl) {
		try {
			redisTemplate.opsForValue().set(key, value, ttl);
			logger.info("Set key: {} with value: {} and TTL: {}", key, value, ttl);
		} catch (Exception e) {
			logger.error("Error setting key {}: {}", key, e.getMessage());
			throw e;
		}
	}

	public String get(String key) {
		try {
			String value = redisTemplate.opsForValue().get(key);
			logger.debug("Got key: {} value: {}", key, value);
			return value;
		} catch (Exception e) {
			logger.error("Error getting key {}: {}", key, e.getMessage());
			return null;
		}
	}

	public boolean hasKey(String key) {
		try {
			Boolean exists = redisTemplate.hasKey(key);
			logger.debug("Has key {}: {}", key, exists);
			return Boolean.TRUE.equals(exists);
		} catch (Exception e) {
			logger.error("Error checking key {}: {}", key, e.getMessage());
			return false;
		}
	}

	public Long increment(String key) {
		try {
			Long newVal = redisTemplate.opsForValue().increment(key);
			logger.info("Incremented key: {} to: {}", key, newVal);
			// Verify by getting back
			String after = get(key);
			logger.info("Verified after increment - key: {} value: {}", key, after);
			return newVal;
		} catch (Exception e) {
			logger.error("Error incrementing key {}: {}", key, e.getMessage());
			throw e;
		}
	}
}
