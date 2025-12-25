package com.order.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
public class CloudWatchMetricsConfig {

	@Bean
	public CloudWatchAsyncClient cloudWatchAsyncClient() {
		return CloudWatchAsyncClient.builder().region(Region.US_EAST_1).build();
	}
}
