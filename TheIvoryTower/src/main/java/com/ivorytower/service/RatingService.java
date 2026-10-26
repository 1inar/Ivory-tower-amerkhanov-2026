package com.ivorytower.service;

import com.ivorytower.dao.RatingDao;
import com.ivorytower.dao.UserDao;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.model.PoetRating;
import com.ivorytower.model.Role;
import com.ivorytower.model.User;
import com.ivorytower.util.AppConfig;

import java.util.List;

public class RatingService {
    private final RatingDao ratingDao;
    private final UserDao userDao;
    private final int threshold;
    private final int minPoems;

    public RatingService(RatingDao ratingDao, UserDao userDao, AppConfig config) {
        this.ratingDao = ratingDao;
        this.userDao = userDao;
        this.threshold = config.getInt("critic.threshold");
        this.minPoems = config.getInt("critic.minPoems");
    }

    public List<PoetRating> top(int limit) {
        return ratingDao.top(limit);
    }

    public PoetRating of(long userId) {
        PoetRating rating = ratingDao.forUser(userId)
                .orElseThrow(() -> new NotFoundException("Поэт не найден"));
        if (rating.getPoemCount() > 0) {
            int position = 1;
            for (PoetRating r : ratingDao.top(1000)) {
                if (r.getUserId() == userId) {
                    position = r.getPosition();
                    break;
                }
            }
            rating.setPosition(position);
        }
        return rating;
    }

    public boolean promoteIfEligible(long userId) {
        User user = userDao.findById(userId).orElse(null);
        if (user == null || user.getRole() != Role.POET || user.isCriticBlocked()) {
            return false;
        }
        PoetRating rating = ratingDao.forUser(userId).orElse(null);
        if (rating == null) {
            return false;
        }
        if (rating.getTotalScore() >= threshold && rating.getPoemCount() >= minPoems) {
            userDao.updateRole(userId, Role.CRITIC);
            return true;
        }
        return false;
    }
}