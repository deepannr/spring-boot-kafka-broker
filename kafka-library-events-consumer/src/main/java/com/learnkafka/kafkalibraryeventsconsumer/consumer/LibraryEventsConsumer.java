package com.learnkafka.kafkalibraryeventsconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.learnkafka.kafkalibraryeventsconsumer.repo.LibraryEventsService;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class LibraryEventsConsumer {
	
	@Autowired
	private LibraryEventsService libraryEventsService;
	
	@KafkaListener(topics = {"library-events"}, groupId = "temp-id")
	public void fetchMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonMappingException, JsonProcessingException {
		log.info("Customer Record: {}", consumerRecord);
		log.info("consumerRecord Value: {}", consumerRecord.value());
		libraryEventsService.processLibraryEvent(consumerRecord);
	}
}
