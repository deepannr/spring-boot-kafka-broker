package com.learnkafka.kafkalibraryeventsconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class LibraryEventsConsumer {
	private static final Logger log = LogManager.getLogger(LibraryEventsConsumer.class);
	
	@KafkaListener(topics = {"library-events"})
	public void fetchMessage(ConsumerRecord<Integer, String> consumerRecord) {
		log.info("Customer Record: {}", consumerRecord);
	}
}
