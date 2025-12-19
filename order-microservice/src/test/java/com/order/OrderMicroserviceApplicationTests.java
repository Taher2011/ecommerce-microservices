package com.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.order.amazon.S3.service.S3Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ActiveProfiles("test")
@SpringBootTest
class OrderMicroserviceApplicationTests {

	@MockBean
	private S3Client s3Client;

	@MockBean
	private S3Presigner s3Presigner;

	@MockBean
	private S3Service s3Service;

	@Test
	void contextLoads() {
	}

}
