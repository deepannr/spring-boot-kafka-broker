package com.learnkafka.producer;

import org.springframework.kafka.support.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class LibraryEventsProducer {
	private static final Logger log = LogManager.getLogger(LibraryEventsProducer.class);
	
	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public void sendLibraryEventAsync(LibraryEvent libraryEvent) throws JsonProcessingException {
		Integer key = libraryEvent.getLibraryEventId();
		String value = objectMapper.writeValueAsString(libraryEvent.getBook()); 
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);
		
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}

			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}
		});
	}
	
	public void sendLibraryEventSync(LibraryEvent libraryEvent) throws JsonProcessingException {
		Integer key = libraryEvent.getLibraryEventId();
		String value = objectMapper.writeValueAsString(libraryEvent.getBook()); 
		
		try {
			SendResult<Integer, String> result = kafkaTemplate.sendDefault(key, value).get();
			handleSuccess(key, value, result);
		} catch (InterruptedException | ExecutionException e) {
			handleFailure(key, value, e);
		}
	}
	
	
	public void sendLibraryEventAsync2(LibraryEvent libraryEvent) throws JsonProcessingException {
		Integer key = libraryEvent.getLibraryEventId();
		String value = objectMapper.writeValueAsString(libraryEvent.getBook()); 
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(buildProducerRecord("library-events", key, value));
		
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}

			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}
		});
	}
	
	private ProducerRecord<Integer, String> buildProducerRecord(String topic, Integer key, String value) {
		List<Header> recordHeaders = Arrays.asList(new RecordHeader("event-source", "Postman".getBytes()), new RecordHeader("event-os", "Windows".getBytes()));
		return new ProducerRecord<Integer, String>(topic, null, key, value, recordHeaders);
	}
	
	private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		log.info("Message Sent Successfully with \n Key: {}, \n Value : {}, \n Partition: {}", key, value,
				result.getRecordMetadata().partition());
	}
	
	private void handleFailure(Integer key, String value, Throwable ex) {
		log.error("Error in sending Message \n Key: {}, \n Value : {}, \n Exception: {}", key, value,
				ex.fillInStackTrace());
		try {
			throw ex;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			log.error("Error in Throwable: {}" , e.fillInStackTrace());
		}
	}
}
