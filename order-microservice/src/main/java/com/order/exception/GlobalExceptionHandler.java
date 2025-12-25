package com.order.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.order.dto.ErrorResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	// Business Exception: Order Not Found (404)
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleOrderNotFound(OrderNotFoundException ex) {
		log.warn("Order not found: {}", ex.getMessage());
		ErrorResponseDTO error = new ErrorResponseDTO("ORDER_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	// Business Exception: File Upload Failed (400)
	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<ErrorResponseDTO> handleFileUpload(FileUploadException ex) {
		log.warn("File upload failed: {}", ex.getMessage());
		ErrorResponseDTO error = new ErrorResponseDTO("FILE_UPLOAD_FAILED", ex.getMessage(),
				HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// Handle Max Upload Size (400)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponseDTO> handleMaxSizeException(MaxUploadSizeExceededException ex) {
		log.warn("File size exceeded: {}", ex.getMessage());
		ErrorResponseDTO error = new ErrorResponseDTO("FILE_TOO_LARGE", "File size exceeds the maximum allowed limit",
				HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	// IO Exception (500 - Internal Server Error)
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponseDTO> handleIOException(IOException ex) {
		log.error("IO Exception occurred: {}", ex.getMessage(), ex);
		ErrorResponseDTO error = new ErrorResponseDTO("INTERNAL_SERVER_ERROR",
				"An error occurred while processing the file", HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// General Runtime Exception (500 - for business logic errors like in download
	// URL)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
		log.error("Runtime Exception occurred: {}", ex.getMessage(), ex);
		ErrorResponseDTO error = new ErrorResponseDTO("BUSINESS_LOGIC_ERROR", ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Catch-all for any other un-handled exceptions (500)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
		log.error("Unhandled Exception occurred: {}", ex.getMessage(), ex);
		ErrorResponseDTO error = new ErrorResponseDTO("INTERNAL_SERVER_ERROR",
				"An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}