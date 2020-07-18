package com.learnkafka.kafkalibraryeventsconsumer.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableKafka
@Slf4j
public class LibraryEventsConsumerConfig {
	
	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object, Object> consumerFactory) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConcurrency(3);
		configurer.configure(factory, consumerFactory);

		// Error Handler
		factory.setErrorHandler((exception, data) -> log.info("Exception Occured: {}, Data: {}", exception, data));

		// Retry Policy
		factory.setRetryTemplate(retryTemplate());
		
		// Set Recover Callback
		factory.setRecoveryCallback(context ->  {
			if (context.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
				log.error("Recoverable: {}", context.getLastThrowable().getMessage());
				
				// This prints list of attribute names. Here we need record attribute.
				Arrays.asList(context.attributeNames()).forEach(attributeName -> {
					log.info("Attribute Name: {}, Attribute Value: {}", attributeName, context.getAttribute(attributeName));
				});
				
				@SuppressWarnings("unchecked")
				ConsumerRecord<Integer, String> consumerRecord = (ConsumerRecord<Integer, String>) context.getAttribute("record");
				
				log.info("Key: {}, Value :{}", consumerRecord.key(), consumerRecord.value());
				kafkaTemplate.sendDefault(consumerRecord.key(), consumerRecord.value());
			} else {
				log.error("Not recoverable: {}", context.getLastThrowable().getMessage());
				throw new RuntimeException(context.getLastThrowable().getMessage());
			}
			return null;
		});
		return factory;
	}

	/**
	 * Here will have 3 maximum attempts and before performing each attempt will
	 * wait for 2 seconds
	 * 
	 * @return
	 */
	private RetryTemplate retryTemplate() {
		// If IllegalArgumentException is thrown, it will not be retried and
		// RecoverableDataAccessException will retry.
		// If traverseCauses is true, the exception causes will be traversed until
		// a match is found. Third Argument for retryPolicy
		Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
		retryableExceptions.put(IllegalArgumentException.class, false);
		retryableExceptions.put(RecoverableDataAccessException.class, true);
		
		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(2000); // Wait for 2 seconds for every retry.
		RetryTemplate retryTemplate = new RetryTemplate();
		RetryPolicy retryPolicy = new SimpleRetryPolicy(3, retryableExceptions, true); // Max attempts of 3.
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
		return retryTemplate;
	}
}
