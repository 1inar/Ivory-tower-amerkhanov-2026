package com.ivorytower.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role = Role.POET;
    private boolean emailVerified;
    private boolean criticBlocked;
    private String bio;
    private LocalDateTime createdAt;

    public User() {}

    public boolean isCritic()   { return role == Role.CRITIC; }
    public boolean isAdmin()    { return role == Role.ADMIN; }
    public boolean isReviewer() { return role.canReview(); }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isCriticBlocked() {
        return criticBlocked;
    }

    public void setCriticBlocked(boolean criticBlocked) {
        this.criticBlocked = criticBlocked;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}