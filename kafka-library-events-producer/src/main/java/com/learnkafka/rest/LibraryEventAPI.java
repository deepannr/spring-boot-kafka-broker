package com.learnkafka.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LIBRARY_EVENT_TYPE;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventsProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/kafka-producer")
@Slf4j
public class LibraryEventAPI {
	
	@Autowired
	private LibraryEventsProducer producer;

	@PostMapping("/v1/asynchronous")
	public ResponseEntity<LibraryEvent> sendLibraryEventAsync(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V1 Async");
		libraryEvent.setLibraryEventType(LIBRARY_EVENT_TYPE.NEW);
		producer.sendLibraryEventAsync(libraryEvent);
		log.info("After Sending Message V1 Async");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	
	@PostMapping("/v2/synchronous")
	public ResponseEntity<LibraryEvent> sendLibraryEventSync(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V1 Sync");
		libraryEvent.setLibraryEventType(LIBRARY_EVENT_TYPE.NEW);
		producer.sendLibraryEventSync(libraryEvent);
		log.info("After Sending Message V1 Sync");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	@PostMapping("/v3/asynchronous")
	public ResponseEntity<LibraryEvent> sendLibraryEventAsync2(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V3 Async");
		libraryEvent.setLibraryEventType(LIBRARY_EVENT_TYPE.NEW);
		producer.sendLibraryEventAsync2(libraryEvent);
		log.info("After Sending Message V3 Async");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	@PutMapping("/v3/asynchronous")
	public ResponseEntity<LibraryEvent> updateLibraryEventAsync2(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		log.info("Before Sending Message V3 Async");
		if (libraryEvent.getLibraryEventId() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(libraryEvent);
		}
		libraryEvent.setLibraryEventType(LIBRARY_EVENT_TYPE.UPDATE);
		producer.sendLibraryEventAsync2(libraryEvent);
		log.info("After Sending Message V3 Async");
		return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
	}
}
