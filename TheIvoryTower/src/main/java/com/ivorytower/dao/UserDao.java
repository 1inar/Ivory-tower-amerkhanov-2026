package com.ivorytower.dao;

import com.ivorytower.model.Role;
import com.ivorytower.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    long create(User user);
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void updateRole(long id, Role role);
    void updateBio(long id, String bio);
    List<User> findAll(int limit, int offset);
    int count();
    void delete(long id);
    void updateCriticBlocked(long id, boolean blocked);
}