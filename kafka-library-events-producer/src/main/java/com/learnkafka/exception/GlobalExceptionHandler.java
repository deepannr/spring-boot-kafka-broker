package com.learnkafka.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleRequestBody(MethodArgumentNotValidException ex) {
		List<FieldError> errorList = ex.getBindingResult().getFieldErrors();
		log.error("Field Error List: {}", errorList);
		
		String fieldErrors = errorList.stream()
			.map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
			.sorted()
			.collect(Collectors.joining(", "));
		log.error("fieldErrors : {}", fieldErrors);
		
		
		return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
	}
}
