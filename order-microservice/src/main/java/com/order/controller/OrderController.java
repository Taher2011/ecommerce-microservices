package com.order.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.order.dto.OrderDTO;
import com.order.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	// CREATE
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<OrderDTO> createOrder(@RequestParam String customerName, @RequestParam Double amount,
			@RequestParam MultipartFile file) throws IOException {
		log.info("POST /api/orders called. customerName={}, amount={}", customerName, amount);
		OrderDTO orderDTO = orderService.createOrder(customerName, amount, file);
		log.info("Order created successfully. orderId={}", orderDTO.getId());
		return ResponseEntity.ok(orderDTO);
	}

	// READ: Get all orders
	@GetMapping
	public ResponseEntity<List<OrderDTO>> getAllOrders() {
		log.info("GET /api/orders called");
		List<OrderDTO> orders = orderService.getAllOrders();
		log.info("GET /api/orders success. totalOrders={}", orders.size());
		return ResponseEntity.ok(orders);
	}

	// READ: Get order by ID
	@GetMapping("/{id}")
	public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
		log.info("GET /api/orders/{} called", id);
		OrderDTO orderDTO = orderService.getOrderById(id);
		if (orderDTO == null) {
			log.warn("Order not found. orderId={}", id);
			return ResponseEntity.notFound().build();
		}
		log.info("GET /api/orders/{} success", id);
		return ResponseEntity.ok(orderDTO);
	}

	// UPDATE
	@PutMapping("/{id}")
	public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestParam String customerName,
			@RequestParam Double amount, @RequestParam(required = false) MultipartFile file) throws IOException {
		log.info("PUT /api/orders/{} called", id);
		OrderDTO updatedOrder = orderService.updateOrder(id, customerName, amount, file);
		if (updatedOrder == null) {
			log.warn("Update failed. Order not found. orderId={}", id);
			return ResponseEntity.notFound().build();
		}
		log.info("Order updated successfully. orderId={}", id);
		return ResponseEntity.ok(updatedOrder);
	}

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		log.info("DELETE /api/orders/{} called", id);
		boolean deleted = orderService.deleteOrder(id);
		if (!deleted) {
			log.warn("Delete failed. Order not found. orderId={}", id);
			return ResponseEntity.notFound().build();
		}
		log.info("Order deleted successfully. orderId={}", id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/download-url")
	public ResponseEntity<String> getPresignedDownloadUrl(@PathVariable Long id) {
		log.info("GET /{}/download-url called", id);
		String presignedUrl = orderService.getDownloadUrl(id);
		log.info("GET /{}/download-url success", id);
		return ResponseEntity.ok(presignedUrl);
	}
}