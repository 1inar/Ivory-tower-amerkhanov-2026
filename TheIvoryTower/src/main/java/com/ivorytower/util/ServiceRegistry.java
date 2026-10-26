package com.ivorytower.util;

import com.ivorytower.service.*;

public class ServiceRegistry {
    private final AuthService authService;
    private final PoemService poemService;
    private final ReactionService reactionService;
    private final HighlightService highlightService;
    private final ReviewService reviewService;
    private final RatingService ratingService;
    private final AdminService adminService;

    public ServiceRegistry(AuthService authService, PoemService poemService,
                           ReactionService reactionService, HighlightService highlightService,
                           ReviewService reviewService, RatingService ratingService,
                           AdminService adminService) {
        this.authService = authService;
        this.poemService = poemService;
        this.reactionService = reactionService;
        this.highlightService = highlightService;
        this.reviewService = reviewService;
        this.ratingService = ratingService;
        this.adminService = adminService;
    }

    public AuthService getAuthService()           { return authService; }
    public PoemService getPoemService()           { return poemService; }
    public ReactionService getReactionService()   { return reactionService; }
    public HighlightService getHighlightService() { return highlightService; }
    public ReviewService getReviewService()       { return reviewService; }
    public RatingService getRatingService()       { return ratingService; }
    public AdminService getAdminService()         { return adminService; }
}