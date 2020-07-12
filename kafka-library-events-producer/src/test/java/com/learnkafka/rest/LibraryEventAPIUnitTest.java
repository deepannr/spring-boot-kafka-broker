package com.learnkafka.rest;

import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LIBRARY_EVENT_TYPE;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventsProducer;
import com.learnkafka.rest.LibraryEventsAPI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LibraryEventsAPI.class)
@AutoConfigureMockMvc
public class LibraryEventAPIUnitTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private LibraryEventsProducer libraryEventsProducer;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void testSendLibraryEventAsync() throws Exception {
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
		
		String jsonValue = objectMapper.writeValueAsString(libraryEvent);
		
		when(libraryEventsProducer.sendLibraryEventAsync(isA(LibraryEvent.class))).thenReturn(null);
		
		
		//When
		mockMvc.perform(
				post("/kafka-producer/v1/asynchronous")
					.content(jsonValue)
					.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
		
		//Then
	}
	
	@Test
	public void testSendLibraryEventAsyncBadRequest() throws Exception {
		//Given
		Book book = Book
					.builder()
					.bookId(123)
					.bookName(null)
					.bookAuthor("Dee")
					.build();

		LibraryEvent libraryEvent = LibraryEvent
						.builder()
						.libraryEventId(111)
						.libraryEventType(LIBRARY_EVENT_TYPE.NEW)
						.book(book)
						.build();
		
		String jsonValue = objectMapper.writeValueAsString(libraryEvent);
		
		when(libraryEventsProducer.sendLibraryEventAsync(isA(LibraryEvent.class))).thenReturn(null);
		
		
		//When
		mockMvc.perform(
				post("/kafka-producer/v1/asynchronous")
					.content(jsonValue)
					.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError());
		
		//Then
	}

}
