package com.learnkafka.kafkalibraryeventsconsumer.repo;

import org.springframework.data.repository.CrudRepository;

import com.learnkafka.kafkalibraryeventsconsumer.entity.LibraryEvent;

public interface LibraryEventsRepo extends CrudRepository<LibraryEvent, Integer>  {

}
