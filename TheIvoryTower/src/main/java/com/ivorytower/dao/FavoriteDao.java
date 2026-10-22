package com.ivorytower.dao;

public interface FavoriteDao {
    boolean add(long userId, long poemId);
    boolean remove(long userId, long poemId);
    boolean exists(long userId, long poemId);
}