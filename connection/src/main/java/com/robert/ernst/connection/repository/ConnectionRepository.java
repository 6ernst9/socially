package com.robert.ernst.connection.repository;

import com.robert.ernst.connection.dto.Connection;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ConnectionRepository extends ReactiveMongoRepository<Connection, Long> {
}
