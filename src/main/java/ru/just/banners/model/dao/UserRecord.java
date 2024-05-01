package ru.just.banners.model.dao;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserRecord {
    @Column(name = "USER_ID")
    private final Long userId;
    @Column(name = "TOKEN")
    private final String token;
    @Column(name = "IS_ADMIN")
    private final Boolean isAdmin;
}
