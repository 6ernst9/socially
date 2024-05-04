package com.robert.ernst.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Streak {
    private Integer streak;
    private Long id;
    private LocalDate lastDay;
}
