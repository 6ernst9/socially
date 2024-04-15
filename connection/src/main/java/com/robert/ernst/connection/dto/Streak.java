package com.robert.ernst.connection.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Streak {
    private Integer streak;
    private Long id;
    private LocalDate lastDay;
}
