package com.robert.ernst.messages.config;

import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.repository.MessageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MessagesConfig {
    @Bean
    CommandLineRunner commandLineRunner(MessageRepository messageRepository) {
        return args -> {
            var message1 = new Message(1L, 2L, "Hey there");
            var message2 = new Message(2L, 1L, "Heii");
            var message3 = new Message(1L, 2L, "How are you?");
            var message4 = new Message(2L, 1L, "good, you??");
            var message5 = new Message(1L, 2L, "same, nothing new");
            var message6 = new Message(2L, 1L, "do you want to go out sometimes?");
            var message7 = new Message(1L, 2L, "I would love that");
            var message8 = new Message(2L, 1L, "Tomorrow at 8pm?");
            var message9 = new Message(1L, 2L, "Sounds like a date");
            var message10 = new Message(2L, 1L, "excellent");
            var message11 = new Message(3L, 4L, "Hey there");
            var message12 = new Message(4L, 3L, "Heii");
            var message13 = new Message(3L, 4L, "How are you?");
            var message14 = new Message(4L, 3L, "good, you??");
            var message15 = new Message(3L, 4L, "same, nothing new");
            var message16 = new Message(4L, 3L, "do you want to go out sometimes?");
            var message17 = new Message(3L, 4L, "I would love that");
            var message18 = new Message(4L, 3L, "Tomorrow at 8pm?");
            var message19 = new Message(3L, 4L, "Sounds like a date");
            var message20 = new Message(4L, 3L, "excellent");

            messageRepository.saveAll(List.of(
                    message1,
                    message2,
                    message3,
                    message4,
                    message5,
                    message6,
                    message7,
                    message8,
                    message9,
                    message10,
                    message11,
                    message12,
                    message13,
                    message14,
                    message15,
                    message16,
                    message17,
                    message18,
                    message19,
                    message20
            ));
        };
    }
}
