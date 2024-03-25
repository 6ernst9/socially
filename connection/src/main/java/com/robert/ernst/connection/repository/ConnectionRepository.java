package com.robert.ernst.connection.repository;

import com.robert.ernst.connection.dto.Connection;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ConnectionRepository extends ReactiveCrudRepository<Connection, Long> {
}
