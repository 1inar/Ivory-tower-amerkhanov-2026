package com.ivorytower.model;

import java.time.LocalDateTime;

public class EmailToken {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiresAt;

    public EmailToken() {}

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }


}