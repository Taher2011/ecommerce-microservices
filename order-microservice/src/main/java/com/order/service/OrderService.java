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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final S3Service s3Service;

	public OrderService(OrderRepository orderRepository, S3Service s3Service) {
		this.orderRepository = orderRepository;
		this.s3Service = s3Service;
	}

	// CREATE
	public OrderDTO createOrder(String customerName, Double amount, MultipartFile file) throws IOException {
		log.info("Creating order for customer={}, amount={}", customerName, amount);
		if (file == null || file.isEmpty()) {
			log.warn("No file provided for customer={}", customerName);
		}

		String s3Url = s3Service.uploadFile(file);
		log.debug("File uploaded to S3. URL={}", s3Url);

		Order order = new Order();
		order.setCustomerName(customerName);
		order.setAmount(amount);
		order.setS3FileUrl(s3Url);

		Order savedOrder = orderRepository.save(order);
		log.info("Order created successfully. orderId={}", savedOrder.getId());
		return mapToDTO(savedOrder);
	}

	// READ: Get all
	public List<OrderDTO> getAllOrders() {
		log.info("Fetching all orders");
		List<OrderDTO> orders = orderRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());

		log.debug("Total orders fetched={}", orders.size());
		return orders;
	}

	// READ: Get by ID
	public OrderDTO getOrderById(Long id) {
		log.info("Fetching order by id={}", id);
		return orderRepository.findById(id).map(order -> {
			log.debug("Order found for id={}", id);
			return mapToDTO(order);
		}).orElseGet(() -> {
			log.warn("Order not found for id={}", id);
			return null;
		});
	}

	// UPDATE
	public OrderDTO updateOrder(Long id, String customerName, Double amount, MultipartFile file) throws IOException {
		log.info("Updating order id={}", id);
		Order existingOrder = orderRepository.findById(id).orElse(null);
		if (existingOrder == null) {
			log.warn("Cannot update. Order not found for id={}", id);
			return null;
		}
		existingOrder.setCustomerName(customerName);
		existingOrder.setAmount(amount);
		if (file != null && !file.isEmpty()) {
			log.debug("New file provided for order id={}", id);
			String newS3Url = s3Service.uploadFile(file);
			existingOrder.setS3FileUrl(newS3Url);
			log.debug("S3 URL updated for order id={}", id);
		}
		Order updatedOrder = orderRepository.save(existingOrder);
		log.info("Order updated successfully. orderId={}", id);
		return mapToDTO(updatedOrder);
	}

	// DELETE
	public boolean deleteOrder(Long id) {
		log.info("Deleting order id={}", id);
		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);
			log.info("Order deleted successfully. orderId={}", id);
			return true;
		}
		log.warn("Delete failed. Order not found for id={}", id);
		return false;
	}

	// DOWNLOAD URL
	public String getDownloadUrl(Long orderId) {
		log.info("Generating download URL for orderId={}", orderId);
		Order order = orderRepository.findById(orderId).orElseThrow(() -> {
			log.error("Order not found while generating download URL. orderId={}", orderId);
			return new RuntimeException("Order not found");
		});
		String url = s3Service.generatePresignedUrl(order.getS3FileUrl());
		log.debug("Presigned URL generated for orderId={}", orderId);
		return url;
	}

	// Helper mapper
	private OrderDTO mapToDTO(Order order) {
		log.debug("Mapping Order entity to DTO. orderId={}", order.getId());

		OrderDTO dto = new OrderDTO();
		dto.setId(order.getId());
		dto.setCustomerName(order.getCustomerName());
		dto.setAmount(order.getAmount());
		dto.setS3FileUrl(order.getS3FileUrl());
		return dto;
	}
}
