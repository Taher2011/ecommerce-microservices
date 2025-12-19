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

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<OrderDTO> createOrder(@RequestParam String customerName, @RequestParam Double amount,
			@RequestParam MultipartFile file) throws IOException {
		OrderDTO orderDTO = orderService.createOrder(customerName, amount, file);
		return ResponseEntity.ok(orderDTO);
	}

	// READ: Get all orders
	@GetMapping
	public ResponseEntity<List<OrderDTO>> getAllOrders() {
		List<OrderDTO> orders = orderService.getAllOrders();
		return ResponseEntity.ok(orders);
	}

	// READ: Get order by ID
	@GetMapping("/{id}")
	public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
		OrderDTO orderDTO = orderService.getOrderById(id);
		return orderDTO != null ? ResponseEntity.ok(orderDTO) : ResponseEntity.notFound().build();
	}

	// UPDATE: Full update by ID (use PUT for replace)
	@PutMapping("/{id}")
	public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestParam String customerName,
			@RequestParam Double amount, @RequestParam(required = false) MultipartFile file) throws IOException {
		OrderDTO updatedOrder = orderService.updateOrder(id, customerName, amount, file);
		return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
	}

	// DELETE: Delete by ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		boolean deleted = orderService.deleteOrder(id);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/download-url")
	public ResponseEntity<String> getPresignedDownloadUrl(@PathVariable Long id) {
		String presignedUrl = orderService.getDownloadUrl(id);
		return ResponseEntity.ok(presignedUrl);
	}

}