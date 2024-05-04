package com.robert.ernst.account.repository;

import com.robert.ernst.account.dto.Session;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends ReactiveCrudRepository<Session, Long> {
}
