package com.learnkafka.producer;

import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.isA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
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
	public void testSendLibraryEventAsync2Failure()
			throws JsonProcessingException, InterruptedException, ExecutionException {
		
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
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSendLibraryEventAsync2Success()
			throws JsonProcessingException, InterruptedException, ExecutionException {
		
		String topic = "library-events";
		
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
		SettableListenableFuture future = new SettableListenableFuture();
		
		String jsonRecord = objectMapper.writeValueAsString(libraryEvent);
		
		ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(topic, libraryEvent.getLibraryEventId(), jsonRecord);
		
		RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topic, 1), 1l, 1l, System.currentTimeMillis(), 1l, 1, 1);
		
		SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);
		
		future.set(sendResult);
		
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventsProducer.sendLibraryEventAsync2(libraryEvent);
		
		SendResult<Integer, String> sendResultAssert = listenableFuture.get();
		
		sendResultAssert.getRecordMetadata().partition();
		
		assertEquals(1, sendResultAssert.getRecordMetadata().partition());
	}
}
