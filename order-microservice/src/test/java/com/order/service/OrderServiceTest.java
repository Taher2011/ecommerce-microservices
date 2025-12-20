package com.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.order.amazon.S3.service.S3Service;
import com.order.dto.OrderDTO;
import com.order.entity.Order;
import com.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private S3Service s3Service;

	@Mock
	private MultipartFile multipartFile;

	@InjectMocks
	private OrderService orderService;

	// -------------------------
	// CREATE ORDER - POSITIVE
	// -------------------------
	@Test
	void createOrder_success() throws IOException {

		when(s3Service.uploadFile(multipartFile)).thenReturn("https://s3.amazonaws.com/test-file.jpg");

		Order savedOrder = new Order();
		savedOrder.setId(1L);
		savedOrder.setCustomerName("Taher");
		savedOrder.setAmount(500.0);
		savedOrder.setS3FileUrl("https://s3.amazonaws.com/test-file.jpg");

		when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

		OrderDTO result = orderService.createOrder("Taher", 500.0, multipartFile);

		assertNotNull(result);
		assertEquals("Taher", result.getCustomerName());
		assertEquals(500.0, result.getAmount());
		assertNotNull(result.getS3FileUrl());

		verify(orderRepository, times(1)).save(any(Order.class));
	}

	// -------------------------
	// CREATE ORDER - EXCEPTION
	// -------------------------
	@Test
	void createOrder_whenS3Fails_shouldThrowException() throws IOException {

		when(s3Service.uploadFile(multipartFile)).thenThrow(new IOException("S3 upload failed"));

		assertThrows(IOException.class, () -> {
			orderService.createOrder("Taher", 200.0, multipartFile);
		});
	}

	// -------------------------
	// GET ALL ORDERS
	// -------------------------
	@Test
	void getAllOrders_success() {

		Order order = new Order();
		order.setId(1L);
		order.setCustomerName("Ali");
		order.setAmount(300.0);

		when(orderRepository.findAll()).thenReturn(List.of(order));

		List<OrderDTO> orders = orderService.getAllOrders();

		assertEquals(1, orders.size());
		assertEquals("Ali", orders.get(0).getCustomerName());
	}

	// -------------------------
	// GET ORDER BY ID - FOUND
	// -------------------------
	@Test
	void getOrderById_found() {

		Order order = new Order();
		order.setId(1L);
		order.setCustomerName("User");

		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		OrderDTO dto = orderService.getOrderById(1L);

		assertNotNull(dto);
		assertEquals("User", dto.getCustomerName());
	}

	// -------------------------
	// GET ORDER BY ID - NOT FOUND
	// -------------------------
	@Test
	void getOrderById_notFound() {

		when(orderRepository.findById(1L)).thenReturn(Optional.empty());

		OrderDTO dto = orderService.getOrderById(1L);

		assertNull(dto);
	}

	// -------------------------
	// DELETE ORDER - SUCCESS
	// -------------------------
	@Test
	void deleteOrder_success() {

		when(orderRepository.existsById(1L)).thenReturn(true);

		boolean result = orderService.deleteOrder(1L);

		assertTrue(result);
		verify(orderRepository).deleteById(1L);
	}

	// -------------------------
	// DELETE ORDER - NOT FOUND
	// -------------------------
	@Test
	void deleteOrder_notFound() {

		when(orderRepository.existsById(1L)).thenReturn(false);

		boolean result = orderService.deleteOrder(1L);

		assertFalse(result);
		verify(orderRepository, never()).deleteById(anyLong());
	}

	// -------------------------
	// DOWNLOAD URL - POSITIVE
	// -------------------------
	@Test
	void getDownloadUrl_success() {

		Order order = new Order();
		order.setS3FileUrl("https://bucket.s3.amazonaws.com/file.jpg");

		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		when(s3Service.generatePresignedUrl(anyString())).thenReturn("https://presigned-url");

		String url = orderService.getDownloadUrl(1L);

		assertEquals("https://presigned-url", url);
	}

	// -------------------------
	// DOWNLOAD URL - EXCEPTION
	// -------------------------
	@Test
	void getDownloadUrl_orderNotFound_shouldThrowException() {

		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(RuntimeException.class, () -> {
			orderService.getDownloadUrl(1L);
		});
	}
}
