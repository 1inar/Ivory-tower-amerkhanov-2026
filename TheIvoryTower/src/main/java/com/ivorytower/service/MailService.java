package com.ivorytower.service;

public interface MailService {
    void send(String to, String subject, String body);
}