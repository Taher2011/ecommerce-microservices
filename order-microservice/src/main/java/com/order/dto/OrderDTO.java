package com.order.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderDTO {
	private Long id;
	private String customerName;
	private Double amount;
	private String s3FileUrl;
}
