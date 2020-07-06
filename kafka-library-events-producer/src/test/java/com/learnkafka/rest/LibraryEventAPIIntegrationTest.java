package com.learnkafka.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;


import com.learnkafka.domain.Book;
import com.learnkafka.domain.LIBRARY_EVENT_TYPE;
import com.learnkafka.domain.LibraryEvent;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events-test"}, partitions = 3)
@TestPropertySource(properties = { "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}" })
public class LibraryEventAPIIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	private Consumer<Integer, String> consumer;
	
	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@BeforeEach
	public void setup() {
		Map<String, Object> configs = new HashMap<>(
				KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<Integer, String>(configs, new IntegerDeserializer(),
				new StringDeserializer()).createConsumer();
		embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	
	@AfterEach
	public void tearDown() {
		consumer.close();
	}

	@Test
	@Timeout(5)
	public void testSendLibraryEventAsync() {
		//Given
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

		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<LibraryEvent> request = new HttpEntity<LibraryEvent>(libraryEvent, headers);

		//When
		ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/kafka-producer/v1/asynchronous",
				HttpMethod.POST, request, LibraryEvent.class);
		
		//Then
		LibraryEvent libraryEventResponse = responseEntity.getBody();
		
		//assertEquals(ResponseEntity.status(HttpStatus.CREATED), responseEntity.getStatusCode());
		assertEquals(libraryEvent.getLibraryEventId(), libraryEventResponse.getLibraryEventId());
		assertEquals(libraryEvent.getBook().getBookId(), libraryEventResponse.getBook().getBookId());
		
		System.out.println("libraryEventGetBookId: " + libraryEvent.getBook().getBookId());
		
//		ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events-test");
//		assertEquals(111, consumerRecord.key());
//		System.out.println("consumerRecordValue : " + consumerRecord.value());
	}
}
