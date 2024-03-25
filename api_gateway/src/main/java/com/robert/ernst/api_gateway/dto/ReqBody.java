package com.robert.ernst.api_gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ReqBody(String path, @JsonIgnore Object body) {
}
