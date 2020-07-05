package com.learnkafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {
	private Integer libraryEventId;
	
	private LIBRARY_EVENT_TYPE libraryEventType;
	
	private Book book;
}
