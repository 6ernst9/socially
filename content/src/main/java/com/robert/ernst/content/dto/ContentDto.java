package com.robert.ernst.content.dto;

import java.util.List;

public record ContentDto(String url, String type, Long posterId, List<Long> receivers) {
}
