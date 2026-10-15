package com.ivorytower.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Review {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private Long id;
    private Long poemId;
    private Long criticId;
    private int score;
    private String text;
    private LocalDateTime createdAt;

    private String criticName;

    public Review() {}

    public String getCreatedAtFormatted() {
        return createdAt == null ? "" : createdAt.format(FMT);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPoemId() {
        return poemId;
    }

    public void setPoemId(Long poemId) {
        this.poemId = poemId;
    }

    public Long getCriticId() {
        return criticId;
    }

    public void setCriticId(Long criticId) {
        this.criticId = criticId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCriticName() {
        return criticName;
    }

    public void setCriticName(String criticName) {
        this.criticName = criticName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return score == review.score && Objects.equals(id, review.id) && Objects.equals(poemId, review.poemId) && Objects.equals(criticId, review.criticId) && Objects.equals(text, review.text) && Objects.equals(createdAt, review.createdAt) && Objects.equals(criticName, review.criticName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}