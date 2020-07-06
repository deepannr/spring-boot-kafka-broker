package com.learnkafka.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {
	@NotNull
	@Positive
	private Integer libraryEventId;
	
	private LIBRARY_EVENT_TYPE libraryEventType;
	
	@NotNull
	@Valid
	private Book book;
}
