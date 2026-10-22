package com.ivorytower.dao;

import com.ivorytower.model.Tag;

import java.util.List;

public interface TagDao {
    Tag findOrCreate(String name);
    List<Tag> findAll();
    List<Tag> findByPoem(long poemId);
}