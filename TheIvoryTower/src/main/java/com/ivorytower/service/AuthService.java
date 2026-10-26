package com.ivorytower.service;

import com.ivorytower.dao.EmailTokenDao;
import com.ivorytower.dao.UserDao;
import com.ivorytower.exception.ServiceException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.EmailToken;
import com.ivorytower.model.User;
import com.ivorytower.util.AppConfig;
import com.ivorytower.util.PasswordUtil;
import com.ivorytower.util.TokenUtil;
import com.ivorytower.util.Validator;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao;
    private final EmailTokenDao tokenDao;
    private final MailService mailService;
    private final AppConfig config;

    public AuthService(UserDao userDao, EmailTokenDao tokenDao, MailService mailService, AppConfig config) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.mailService = mailService;
        this.config = config;
    }

    public void register(String username, String email, String password, String password2) {
        username = username == null ? "" : username.trim();
        email = email == null ? "" : email.trim().toLowerCase();

        Map<String, String> errors = new LinkedHashMap<>();
        if (!Validator.isUsername(username)) {
            errors.put("username", "Логин: от 3 до 30 символов, буквы, цифры и _");
        } else if (userDao.existsByUsername(username)) {
            errors.put("username", "Этот логин уже занят");
        }
        if (!Validator.isEmail(email)) {
            errors.put("email", "Введите корректный email");
        } else if (userDao.existsByEmail(email)) {
            errors.put("email", "Этот email уже зарегистрирован");
        }
        if (!Validator.isStrongPassword(password)) {
            errors.put("password", "Пароль: от 8 символов, минимум одна буква и одна цифра");
        }
        if (password == null || !password.equals(password2)) {
            errors.put("password2", "Пароли не совпадают");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setId(userDao.create(user));
        sendConfirmation(user);
    }

    public void confirmEmail(String token) {
        EmailToken t = tokenDao.findByToken(token == null ? "" : token)
                .orElseThrow(() -> new ServiceException("Ссылка недействительна или уже использована"));
        if (t.isExpired()) {
            tokenDao.deleteByToken(token);
            throw new ServiceException("Срок действия ссылки истёк. Запросите письмо повторно.");
        }
        userDao.markEmailVerified(t.getUserId());
        tokenDao.deleteByToken(token);
    }

    public void resendConfirmation(String email) {
        String e = email == null ? "" : email.trim().toLowerCase();
        userDao.findByEmail(e)
                .filter(u -> !u.isEmailVerified())
                .ifPresent(this::sendConfirmation);
    }

    public User login(String email, String password) {
        String e = email == null ? "" : email.trim().toLowerCase();
        User user = userDao.findByEmail(e).orElse(null);
        if (user == null || password == null || !PasswordUtil.check(password, user.getPasswordHash())) {
            throw new ServiceException("Неверный email или пароль");
        }
        if (!user.isEmailVerified()) {
            throw new ServiceException("Подтвердите email: перейдите по ссылке из письма");
        }
        user.setPasswordHash(null);
        return user;
    }

    public Optional<User> findById(long id) {
        Optional<User> user = userDao.findById(id);
        user.ifPresent(u -> u.setPasswordHash(null));
        return user;
    }

    public void updateBio(User user, String bio) {
        String clean = bio == null ? "" : bio.trim();
        if (clean.length() > 500) {
            Map<String, String> errors = new LinkedHashMap<>();
            errors.put("bio", "О себе: не более 500 символов");
            throw new ValidationException(errors);
        }
        userDao.updateBio(user.getId(), clean);
        user.setBio(clean);
    }

    private void sendConfirmation(User user) {
        EmailToken t = new EmailToken();
        t.setUserId(user.getId());
        t.setToken(TokenUtil.randomToken());
        t.setExpiresAt(LocalDateTime.now().plusHours(config.getInt("token.ttl.hours")));
        tokenDao.save(t);

        String link = config.get("app.baseUrl") + "/verify?token=" + t.getToken();
        String body = "Здравствуйте, " + user.getUsername() + "!\n\n"
                + "Чтобы подтвердить почту и войти в Башню из слоновой кости, перейдите по ссылке:\n"
                + link + "\n\n"
                + "Ссылка действует " + config.getInt("token.ttl.hours") + " ч.";
        mailService.send(user.getEmail(), "Подтверждение регистрации — Ivory Tower", body);
    }
}