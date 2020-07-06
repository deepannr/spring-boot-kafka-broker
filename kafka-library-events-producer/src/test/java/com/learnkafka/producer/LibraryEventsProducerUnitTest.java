package com.learnkafka.producer;

import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.isA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LIBRARY_EVENT_TYPE;
import com.learnkafka.domain.LibraryEvent;

@ExtendWith(MockitoExtension.class)
public class LibraryEventsProducerUnitTest {
	@Mock
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	@InjectMocks
	private LibraryEventsProducer libraryEventsProducer;
	
	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSendLibraryEventAsync2Failure() throws JsonProcessingException, InterruptedException, ExecutionException {
		
		Book book = Book
				.builder()
				.bookId(123)
				.bookName("Kafka")
				.bookAuthor("Dee")
				.build();

		LibraryEvent libraryEvent = LibraryEvent
						.builder()
						.libraryEventId(111)
						.libraryEventType(LIBRARY_EVENT_TYPE.NEW)
						.book(book)
						.build();
		
		@SuppressWarnings("rawtypes")
		SettableListenableFuture listenableFuture = new SettableListenableFuture();
		listenableFuture.setException(new RuntimeException("Throwing Exception"));
		
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(listenableFuture);
		
		assertThrows(Exception.class, () -> libraryEventsProducer.sendLibraryEventAsync2(libraryEvent).get());
	}
	
}
