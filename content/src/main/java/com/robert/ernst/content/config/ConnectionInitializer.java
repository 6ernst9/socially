package com.robert.ernst.content.config;

import com.robert.ernst.content.dto.Content;
import com.robert.ernst.content.repository.ContentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ConnectionInitializer {
    private final ContentRepository repository;

    @PostConstruct
    void initConnections(){
        var connection1 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 1L, List.of(2L, 3L, 4L));
        var connection2 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 2L, List.of(1L, 3L, 4L));
        var connection3 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 3L, List.of(1L, 2L, 4L));
        var connection4 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 4L, List.of(1L, 2L, 3L));
        var connection5 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 4L, List.of(1L, 3L));
        var connection6 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 3L, List.of(1L, 2L));
        var connection7 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 2L, List.of(1L, 3L));
        var connection8 = new Content("snap", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc  ", 1L, List.of(2L, 3L));

        var story1 = new Content("story", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fin5.jpg?alt=media&token=9fb6c0fe-dfae-4d80-ac26-7e38f935ab2c", 3L, List.of(1L, 3L, 4L));
        var story2 = new Content("story", "https://firebasestorage.googleapis.com/v0/b/isproject-6332b.appspot.com/o/profile%2Fp2.jpg?alt=media&token=51e07e31-2977-4010-a4f8-793d554d87cc", 2L, List.of(1L, 2L, 4L));
        var contents = List.of(connection1, connection2, connection3, connection4, connection5, connection6, connection7, connection8, story1, story2);

        repository.deleteAll().subscribe();
        repository.saveAll(contents).subscribe();
    }

}
