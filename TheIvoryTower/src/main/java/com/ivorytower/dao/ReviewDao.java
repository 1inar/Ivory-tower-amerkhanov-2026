package com.ivorytower.dao;

import com.ivorytower.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    long create(Review review);
    List<Review> findByPoem(long poemId);
    Optional<Review> findById(long id);
    boolean existsByPoemAndCritic(long poemId, long criticId);
    boolean delete(long id);
    Double avgScore(long poemId);
}