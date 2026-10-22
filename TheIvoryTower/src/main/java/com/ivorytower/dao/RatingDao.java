package com.ivorytower.dao;

import com.ivorytower.model.PoetRating;

import java.util.List;
import java.util.Optional;

public interface RatingDao {
    List<PoetRating> top(int limit);
    Optional<PoetRating> forUser(long userId);
    int poemCount(long userId);
}