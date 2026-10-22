package com.ivorytower.dao;

public interface LikeDao {
    boolean add(long userId, long poemId);
    boolean remove(long userId, long poemId);
    boolean exists(long userId, long poemId);
    int countByPoem(long poemId);
}