package com.robert.ernst.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Connection {
    @Id
    private Long id;
    @NonNull
    private List<User> friends;
    @NonNull
    private List<Long> blocked;
    public Connection(@NonNull Long id){
        this.id = id;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
    }
}
