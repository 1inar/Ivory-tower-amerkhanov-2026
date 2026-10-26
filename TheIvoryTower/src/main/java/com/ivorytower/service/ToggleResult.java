package com.ivorytower.service;

public class ToggleResult {
    private final boolean active;
    private final Integer count;

    public ToggleResult(boolean active, Integer count) {
        this.active = active;
        this.count = count;
    }

    public boolean isActive() { return active; }
    public Integer getCount() { return count; }
}