package com.ivorytower.service;

import com.ivorytower.dao.UserDao;
import com.ivorytower.exception.ServiceException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.User;
import com.ivorytower.util.PasswordUtil;
import com.ivorytower.util.Validator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
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
        userDao.create(user);
    }

    public User login(String email, String password) {
        String e = email == null ? "" : email.trim().toLowerCase();
        User user = userDao.findByEmail(e).orElse(null);
        if (user == null || password == null || !PasswordUtil.check(password, user.getPasswordHash())) {
            throw new ServiceException("Неверный email или пароль");
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
}