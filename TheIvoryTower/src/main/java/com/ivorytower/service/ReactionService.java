package com.ivorytower.service;

import com.ivorytower.dao.FavoriteDao;
import com.ivorytower.dao.LikeDao;
import com.ivorytower.dao.PoemDao;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.exception.ServiceException;
import com.ivorytower.model.Poem;

import java.util.List;

public class ReactionService {
    private final LikeDao likeDao;
    private final FavoriteDao favoriteDao;
    private final PoemDao poemDao;
    private final RatingService ratingService;

    public ReactionService(LikeDao likeDao, FavoriteDao favoriteDao,
                           PoemDao poemDao, RatingService ratingService) {
        this.likeDao = likeDao;
        this.favoriteDao = favoriteDao;
        this.poemDao = poemDao;
        this.ratingService = ratingService;
    }

    public ToggleResult toggleLike(long userId, long poemId) {
        long authorId = authorOf(poemId);
        if (authorId == userId) {
            throw new ServiceException("Нельзя ставить лайк своему стиху");
        }
        boolean active;
        if (likeDao.remove(userId, poemId)) {
            active = false;
        } else {
            likeDao.add(userId, poemId);
            active = true;
        }
        if (active) {
            ratingService.promoteIfEligible(authorId);
        }
        return new ToggleResult(active, likeDao.countByPoem(poemId));
    }

    public ToggleResult toggleFavorite(long userId, long poemId) {
        authorOf(poemId);
        boolean active;
        if (favoriteDao.remove(userId, poemId)) {
            active = false;
        } else {
            favoriteDao.add(userId, poemId);
            active = true;
        }
        return new ToggleResult(active, null);
    }

    public List<Poem> favorites(long userId) {
        List<Poem> poems = poemDao.findFavorites(userId);
        poems.forEach(p -> p.setFavoritedByMe(true));
        return poems;
    }

    private long authorOf(long poemId) {
        return poemDao.authorOf(poemId)
                .orElseThrow(() -> new NotFoundException("Стих не найден"));
    }
}