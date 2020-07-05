package com.learnkafka.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventsProducer;

@RestController
@RequestMapping("/kafka-producer")
public class LibraryEventAPI {
	private static final Logger log = LogManager.getLogger(LibraryEventAPI.class);
	
	@Autowired
	private LibraryEventsProducer producer;

	@PostMapping("/v1/asynchronous")
	public ResponseEntity<LibraryEvent> sendLibraryEventAsync(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V1 Async");
		producer.sendLibraryEventAsync(libraryEvent);
		log.info("After Sending Message V1 Async");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	
	@PostMapping("/v2/synchronous")
	public ResponseEntity<LibraryEvent> sendLibraryEventSync(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V1 Sync");
		producer.sendLibraryEventSync(libraryEvent);
		log.info("After Sending Message V1 Sync");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	@PostMapping("/v3/asynchronous")
	public ResponseEntity<LibraryEvent> sendLibraryEventAsync2(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V1 Async");
		producer.sendLibraryEventAsync2(libraryEvent);
		log.info("After Sending Message V1 Async");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
}
