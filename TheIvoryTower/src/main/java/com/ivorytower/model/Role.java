package com.ivorytower.model;

public enum Role {
    POET,
    CRITIC,
    ADMIN;

    public boolean canReview() {
        return this == CRITIC || this == ADMIN;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

}