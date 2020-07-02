package com.learnkafka.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnkafka.domain.LibraryEvent;

@RestController
@RequestMapping("/kafka-producer")
public class LibraryEventAPI {
	
	@PostMapping("/v1/libraryEvent")
	public ResponseEntity<LibraryEvent> addBook(@RequestBody LibraryEvent libraryEvent) {
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}

}
