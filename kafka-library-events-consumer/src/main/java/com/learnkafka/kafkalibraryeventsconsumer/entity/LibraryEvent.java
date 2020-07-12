package com.learnkafka.kafkalibraryeventsconsumer.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class LibraryEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer libraryEventId;
	
	@Enumerated(EnumType.STRING)
	private LIBRARY_EVENT_TYPE libraryEventType;
	
	@NotNull
	@Valid
	@OneToOne(mappedBy = "libraryEvent", cascade = CascadeType.ALL)
	@ToString.Exclude
	private Book book;

	@Override
	public String toString() {
		return "LibraryEvent [libraryEventId=" + libraryEventId + ", libraryEventType=" + libraryEventType + ", book="
				+ book + "]";
	}
}
