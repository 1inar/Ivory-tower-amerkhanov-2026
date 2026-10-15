package com.ivorytower.model;

public class PoetRating {
    private Long userId;
    private String username;
    private int poemCount;
    private int likes;
    private int highlights;
    private Double avgReview;
    private int totalScore;
    private int position;
    public PoetRating() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoemCount() {
        return poemCount;
    }

    public void setPoemCount(int poemCount) {
        this.poemCount = poemCount;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getHighlights() {
        return highlights;
    }

    public void setHighlights(int highlights) {
        this.highlights = highlights;
    }

    public Double getAvgReview() {
        return avgReview;
    }

    public void setAvgReview(Double avgReview) {
        this.avgReview = avgReview;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}