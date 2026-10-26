package com.ivorytower.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevMailService implements MailService {
    private static final Logger log = LoggerFactory.getLogger(DevMailService.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("\n===== ПИСЬМО (режим разработки) =====\nКому: {}\nТема: {}\n\n{}\n=====================================",
                to, subject, body);
    }
}