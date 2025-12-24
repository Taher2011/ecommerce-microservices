package com.order.amazon.S3.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Service
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	// UPLOAD FILE
	public String uploadFile(MultipartFile file) throws IOException {
		log.info("Uploading file to S3. originalFilename={}, size={} bytes", file.getOriginalFilename(),
				file.getSize());
		String key = "orders/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		log.debug("Generated S3 key={}", key);
		try {
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key)
					.contentType(file.getContentType()).serverSideEncryption(ServerSideEncryption.AES256).build();
			s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
			String s3Url = "https://" + bucketName + ".s3.amazonaws.com/" + key;
			log.info("File uploaded successfully to S3. key={}, bucket={}", key, bucketName);
			return s3Url;
		} catch (Exception ex) {
			log.error("Failed to upload file to S3. bucket={}, key={}", bucketName, key, ex);
			throw ex;
		}
	}

	// PRESIGNED URL
	public String generatePresignedUrl(String s3Url) {
		log.info("Generating presigned URL for S3 object");
		String key = s3Url.substring(s3Url.indexOf(".com/") + 5);
		log.debug("Extracted S3 key from URL={}", key);
		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(5)).getObjectRequest(getObjectRequest).build();
			String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
			log.info("Presigned URL generated successfully. key={}, expiry=5 minutes", key);
			return presignedUrl;
		} catch (Exception ex) {
			log.error("Failed to generate presigned URL. bucket={}, key={}", bucketName, key, ex);
			throw ex;
		}
	}
}
