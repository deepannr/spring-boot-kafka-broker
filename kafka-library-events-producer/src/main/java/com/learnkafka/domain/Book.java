package com.learnkafka.domain;

import javax.validation.constraints.NotBlank;
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
public class Book {
	@NotNull
	@Positive
	private Integer bookId;
	
	@NotBlank
	private String bookName;
	
	@NotBlank
	private String bookAuthor;

}
