package com.ivorytower.service;

import com.ivorytower.dao.UserDao;
import com.ivorytower.exception.AccessDeniedException;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.exception.ServiceException;
import com.ivorytower.model.Role;
import com.ivorytower.model.User;

import java.util.List;

public class AdminService {
    private final UserDao userDao;

    public AdminService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> users(User admin, int page, int size) {
        requireAdmin(admin);
        List<User> list = userDao.findAll(size, (Math.max(page, 1) - 1) * size);
        list.forEach(u -> u.setPasswordHash(null));
        return list;
    }

    public int pageCount(User admin, int size) {
        requireAdmin(admin);
        return Math.max(1, (int) Math.ceil(userDao.count() / (double) size));
    }

    public void setRole(User admin, long userId, Role role) {
        requireAdmin(admin);
        if (admin.getId() == userId) {
            throw new ServiceException("Нельзя менять роль самому себе");
        }
        User target = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (role == Role.POET && target.getRole() == Role.CRITIC) {
            userDao.updateCriticBlocked(userId, true);
        } else if (role == Role.CRITIC) {
            userDao.updateCriticBlocked(userId, false);
        }
        userDao.updateRole(userId, role);
    }

    public void deleteUser(User admin, long userId) {
        requireAdmin(admin);
        if (admin.getId() == userId) {
            throw new ServiceException("Нельзя удалить самого себя");
        }
        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userDao.delete(userId);
    }

    private void requireAdmin(User admin) {
        if (admin == null || !admin.isAdmin()) {
            throw new AccessDeniedException("Только для администратора");
        }
    }
}