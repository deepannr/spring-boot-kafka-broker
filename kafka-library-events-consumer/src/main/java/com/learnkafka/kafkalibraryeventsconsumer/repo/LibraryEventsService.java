package com.learnkafka.kafkalibraryeventsconsumer.repo;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.kafkalibraryeventsconsumer.entity.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LibraryEventsService {
	@Autowired
	private LibraryEventsRepo repo;

	@Autowired
	private ObjectMapper objectMapper;

	public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord)
			throws JsonMappingException, JsonProcessingException {
		LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
		log.info("LibraryEvent : {}", libraryEvent);

		switch (libraryEvent.getLibraryEventType()) {
		case NEW:
			saveLibraryEvent(libraryEvent);
			break;

		case UPDATE:
			validateLibraryEvent(libraryEvent);
			saveLibraryEvent(libraryEvent);
			break;

		default:
			break;
		}
	}

	private void validateLibraryEvent(LibraryEvent libraryEvent) {
		if (libraryEvent.getLibraryEventId() == null) {
			throw new IllegalArgumentException("Library Event Id is missing");
		}

		Optional<LibraryEvent> optionalLibrary = repo.findById(libraryEvent.getLibraryEventId());

		if (optionalLibrary == null) {
			throw new IllegalArgumentException("Library Event is missing");
		}

		log.info("Library Event Present: {}", optionalLibrary.get());
	}

	private void saveLibraryEvent(LibraryEvent libraryEvent) {
		libraryEvent.getBook().setLibraryEvent(libraryEvent);
		repo.save(libraryEvent);
		log.info("Saved to Library Events: {}", libraryEvent);
	}
}
