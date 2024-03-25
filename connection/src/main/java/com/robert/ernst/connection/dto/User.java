package com.robert.ernst.connection.dto;

import java.time.LocalDateTime;

public record User(Long id, LocalDateTime sinceConnected) {
}
