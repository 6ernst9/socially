package com.robert.ernst.account.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name="session")
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    private Long id;
    @NonNull
    private Long userId;
    @NonNull
    private String deviceId;
    @NonNull
    private String ipAddress;
    @NonNull
    private String siteId;

    public Session(@NonNull Long userId, @NonNull String deviceId, @NonNull String ipAddress, @NonNull String siteId) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.siteId = siteId;
    }
}
