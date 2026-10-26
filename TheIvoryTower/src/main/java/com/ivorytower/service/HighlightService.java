package com.ivorytower.service;

import com.ivorytower.dao.HighlightDao;
import com.ivorytower.dao.PoemDao;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.exception.ServiceException;
import com.ivorytower.model.LineStat;
import com.ivorytower.model.Poem;
import com.ivorytower.model.Quote;

import java.util.List;

public class HighlightService {
    private static final int MAX_PER_POEM = 5;

    private final HighlightDao highlightDao;
    private final PoemDao poemDao;
    private final RatingService ratingService;

    public HighlightService(HighlightDao highlightDao, PoemDao poemDao, RatingService ratingService) {
        this.highlightDao = highlightDao;
        this.poemDao = poemDao;
        this.ratingService = ratingService;
    }

    public List<LineStat> toggle(long userId, long poemId, int lineNo) {
        Poem poem = poemDao.findById(poemId)
                .orElseThrow(() -> new NotFoundException("Стих не найден"));
        if (poem.getAuthorId() == userId) {
            throw new ServiceException("Нельзя подчёркивать строки в своём стихе");
        }
        List<String> lines = poem.getLines();
        if (lineNo < 1 || lineNo > lines.size()) {
            throw new ServiceException("Такой строки нет");
        }
        if (lines.get(lineNo - 1).isBlank()) {
            throw new ServiceException("Пустую строку подчёркивать нельзя");
        }

        if (!highlightDao.remove(userId, poemId, lineNo)) {
            if (highlightDao.countByUserAndPoem(userId, poemId) >= MAX_PER_POEM) {
                throw new ServiceException("В одном стихе можно подчеркнуть не более "
                        + MAX_PER_POEM + " строк");
            }
            highlightDao.add(userId, poemId, lineNo);
            ratingService.promoteIfEligible(poem.getAuthorId());
        }
        return highlightDao.statsByPoem(poemId, userId);
    }

    public List<LineStat> stats(long poemId, Long viewerId) {
        return highlightDao.statsByPoem(poemId, viewerId);
    }

    public List<Quote> quoteOfTower(int limit) {
        return highlightDao.topQuotes(limit);
    }
}