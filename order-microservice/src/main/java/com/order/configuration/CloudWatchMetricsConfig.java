package com.order.configuration;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
public class CloudWatchMetricsConfig {

	@Bean
	public CloudWatchAsyncClient cloudWatchAsyncClient() {
		return CloudWatchAsyncClient.builder().region(Region.US_EAST_1).build();
	}

	@Bean
	public CloudWatchConfig cloudWatchConfig() {
		return new CloudWatchConfig() {
			@Override
			public String get(String key) {
				return null;
			}

			@Override
			public String namespace() {
				return "OrderServiceApp";
			}

			@Override
			public Duration step() {
				return Duration.ofMinutes(1);
			}
		};
	}

	@Bean
	public CloudWatchMeterRegistry cloudWatchMeterRegistry(CloudWatchConfig config, CloudWatchAsyncClient client) {
		return new CloudWatchMeterRegistry(config, Clock.SYSTEM, client);
	}
}
