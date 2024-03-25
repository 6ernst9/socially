package com.robert.ernst.account.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name="accounts")
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private Long id;
    @NonNull
    private String profilePhoto;
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String fullName;
    @NonNull
    private String email;
    @NonNull
    private String phoneNumber;
    @NonNull
    private Boolean isAdmin;

    public Account(@NonNull String username,
                   @NonNull String email,
                   @NonNull String password,
                   @NonNull String fullName,
                   @NonNull String phoneNumber){
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.isAdmin = false;
        this.profilePhoto = "";
    }
}
