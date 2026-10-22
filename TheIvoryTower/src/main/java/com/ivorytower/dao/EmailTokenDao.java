package com.ivorytower.dao;

import com.ivorytower.model.EmailToken;

import java.util.Optional;

public interface EmailTokenDao {
    void save(EmailToken token);
    Optional<EmailToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteExpired();
}