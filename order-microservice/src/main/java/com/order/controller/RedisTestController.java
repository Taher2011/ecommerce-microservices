//package com.order.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.order.redis.service.RedisService;
//
//@RestController
//@RequestMapping("/redis-test")
//public class RedisTestController {
//
//	@Autowired
//	private RedisService redisService;
//
//	@GetMapping("/set")
//	public String set() {
//		redisService.set("hello", "world");
//		return "OK";
//	}
//
//	@GetMapping("/get")
//	public String get() {
//		return redisService.get("hello");
//	}
//}
