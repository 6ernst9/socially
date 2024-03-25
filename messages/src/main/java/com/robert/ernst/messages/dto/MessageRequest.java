package com.robert.ernst.messages.dto;

public record MessageRequest (Long senderId, Long receiverId, String content){
}
