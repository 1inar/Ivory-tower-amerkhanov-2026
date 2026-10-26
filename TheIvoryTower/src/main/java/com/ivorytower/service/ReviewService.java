package com.ivorytower.service;

import com.ivorytower.dao.PoemDao;
import com.ivorytower.dao.ReviewDao;
import com.ivorytower.exception.AccessDeniedException;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.exception.ServiceException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.Review;
import com.ivorytower.model.User;
import com.ivorytower.util.Validator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReviewService {
    private final ReviewDao reviewDao;
    private final PoemDao poemDao;
    private final RatingService ratingService;

    public ReviewService(ReviewDao reviewDao, PoemDao poemDao, RatingService ratingService) {
        this.reviewDao = reviewDao;
        this.poemDao = poemDao;
        this.ratingService = ratingService;
    }

    public void add(User critic, long poemId, int score, String text) {
        if (!critic.isReviewer()) {
            throw new AccessDeniedException("Рецензии могут писать только критики");
        }
        long authorId = poemDao.authorOf(poemId)
                .orElseThrow(() -> new NotFoundException("Стих не найден"));
        if (authorId == critic.getId()) {
            throw new ServiceException("Нельзя рецензировать свой стих");
        }
        if (reviewDao.existsByPoemAndCritic(poemId, critic.getId())) {
            throw new ServiceException("Вы уже написали рецензию на этот стих");
        }

        Map<String, String> errors = new LinkedHashMap<>();
        if (score < 1 || score > 10) {
            errors.put("score", "Оценка: от 1 до 10");
        }
        if (!Validator.lengthBetween(text, 20, 2000)) {
            errors.put("text", "Текст рецензии: от 20 до 2000 символов");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Review review = new Review();
        review.setPoemId(poemId);
        review.setCriticId(critic.getId());
        review.setScore(score);
        review.setText(text.trim());
        reviewDao.create(review);
        ratingService.promoteIfEligible(authorId);
    }

    public void delete(User actor, long reviewId) {
        Review review = reviewDao.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Рецензия не найдена"));
        boolean mine = review.getCriticId().equals(actor.getId());
        if (!mine && !actor.isAdmin()) {
            throw new AccessDeniedException("Удалить рецензию может её автор или админ");
        }
        reviewDao.delete(reviewId);
    }

    public List<Review> forPoem(long poemId) {
        return reviewDao.findByPoem(poemId);
    }
}