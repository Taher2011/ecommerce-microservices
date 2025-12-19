package com.order.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.order.amazon.S3.service.S3Service;
import com.order.dto.OrderDTO;
import com.order.entity.Order;
import com.order.repository.OrderRepository;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final S3Service s3Service;

	public OrderService(OrderRepository orderRepository, S3Service s3Service) {
		this.orderRepository = orderRepository;
		this.s3Service = s3Service;
	}

	public OrderDTO createOrder(String customerName, Double amount, MultipartFile file) throws IOException {
		String s3Url = s3Service.uploadFile(file);

		Order order = new Order();
		order.setCustomerName(customerName);
		order.setAmount(amount);
		order.setS3FileUrl(s3Url);

		Order savedOrder = orderRepository.save(order);
		return mapToDTO(savedOrder);
	}

	// READ: Get all
	public List<OrderDTO> getAllOrders() {
		return orderRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	// READ: Get by ID
	public OrderDTO getOrderById(Long id) {
		return orderRepository.findById(id).map(this::mapToDTO).orElse(null);
	}

	// UPDATE: Full update (replace), new file upload if provided
	public OrderDTO updateOrder(Long id, String customerName, Double amount, MultipartFile file) throws IOException {
		Order existingOrder = orderRepository.findById(id).orElse(null);
		if (existingOrder == null) {
			return null; // Not found
		}

		// Update fields
		existingOrder.setCustomerName(customerName);
		existingOrder.setAmount(amount);

		if (file != null && !file.isEmpty()) {
			String newS3Url = s3Service.uploadFile(file);
			existingOrder.setS3FileUrl(newS3Url);
		}

		Order updatedOrder = orderRepository.save(existingOrder);
		return mapToDTO(updatedOrder);
	}

	public boolean deleteOrder(Long id) {
		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public String getDownloadUrl(Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		return s3Service.generatePresignedUrl(order.getS3FileUrl());
	}

	// Helper: Entity to DTO mapper
	private OrderDTO mapToDTO(Order order) {
		OrderDTO dto = new OrderDTO();
		dto.setId(order.getId());
		dto.setCustomerName(order.getCustomerName());
		dto.setAmount(order.getAmount());
		dto.setS3FileUrl(order.getS3FileUrl());
		return dto;
	}
}