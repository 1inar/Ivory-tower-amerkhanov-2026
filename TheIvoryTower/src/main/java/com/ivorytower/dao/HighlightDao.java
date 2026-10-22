package com.ivorytower.dao;

import com.ivorytower.model.LineStat;
import com.ivorytower.model.Quote;

import java.util.List;

public interface HighlightDao {
    boolean add(long userId, long poemId, int lineNo);
    boolean remove(long userId, long poemId, int lineNo);
    int countByUserAndPoem(long userId, long poemId);
    List<LineStat> statsByPoem(long poemId, Long currentUserId);
    List<Quote> topQuotes(int limit);
}