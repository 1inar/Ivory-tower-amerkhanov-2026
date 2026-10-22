package com.ivorytower.dao;

import com.ivorytower.model.Poem;

import java.util.List;
import java.util.Optional;

public interface PoemDao {
    long create(Poem poem, List<Integer> tagIds);
    Optional<Poem> findById(long id);
    List<Poem> findLatest(int limit, int offset);
    List<Poem> findByAuthor(long authorId);
    List<Poem> findByTag(String tagName, int limit, int offset);
    List<Poem> search(String query);
    boolean update(Poem poem, List<Integer> tagIds);
    boolean delete(long id);
    int countAll();
    Optional<Long> authorOf(long poemId);
    List<Poem> findFavorites(long userId);
}