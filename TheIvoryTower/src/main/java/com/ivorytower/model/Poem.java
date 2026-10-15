package com.ivorytower.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Poem {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private Long id;
    private Long authorId;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String authorName;
    private List<Tag> tags = new ArrayList<>();
    private int likeCount;
    private Double avgScore;
    private boolean likedByMe;
    private boolean favoritedByMe;

    public Poem() {}

    public List<String> getLines() {
        if (body == null || body.isBlank()) return List.of();
        return List.of(body.strip().split("\\R"));
    }

    public String getExcerpt() {
        return getLines().stream()
                .filter(s -> !s.isBlank())
                .limit(2)
                .reduce((a, b) -> a + " / " + b)
                .orElse("");
    }

    public boolean isEdited() {
        return updatedAt != null;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null ? "" : createdAt.format(FMT);
    }

    public boolean isFavoritedByMe() {
        return favoritedByMe;
    }

    public void setFavoritedByMe(boolean favoritedByMe) {
        this.favoritedByMe = favoritedByMe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Poem poem = (Poem) o;
        return likeCount == poem.likeCount && likedByMe == poem.likedByMe && favoritedByMe == poem.favoritedByMe && Objects.equals(id, poem.id) && Objects.equals(authorId, poem.authorId) && Objects.equals(title, poem.title) && Objects.equals(body, poem.body) && Objects.equals(createdAt, poem.createdAt) && Objects.equals(updatedAt, poem.updatedAt) && Objects.equals(authorName, poem.authorName) && Objects.equals(tags, poem.tags) && Objects.equals(avgScore, poem.avgScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}