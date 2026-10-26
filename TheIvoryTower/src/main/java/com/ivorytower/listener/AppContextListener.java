package com.ivorytower.listener;

import com.ivorytower.dao.*;
import com.ivorytower.dao.jdbc.*;
import com.ivorytower.service.*;
import com.ivorytower.util.*;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;

@WebListener
public class AppContextListener implements ServletContextListener {
    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        try (InputStream in = AppContextListener.class.getResourceAsStream("/config.properties")) {
            AppConfig cfg = new AppConfig(in);
            dataSource = DataSourceFactory.create(cfg);

            UserDao userDao = new JdbcUserDao(dataSource);
            EmailTokenDao tokenDao = new JdbcEmailTokenDao(dataSource);
            PoemDao poemDao = new JdbcPoemDao(dataSource);
            TagDao tagDao = new JdbcTagDao(dataSource);
            LikeDao likeDao = new JdbcLikeDao(dataSource);
            FavoriteDao favoriteDao = new JdbcFavoriteDao(dataSource);
            ReviewDao reviewDao = new JdbcReviewDao(dataSource);
            HighlightDao highlightDao = new JdbcHighlightDao(dataSource);
            RatingDao ratingDao = new JdbcRatingDao(dataSource);

            MailService mailService = cfg.getBoolean("mail.dev")
                    ? new DevMailService()
                    : new SmtpMailService(cfg.get("mail.host"), cfg.getInt("mail.port"),
                    cfg.get("mail.user"), cfg.get("mail.password"),
                    cfg.get("mail.from"));

            RatingService ratingService = new RatingService(ratingDao, userDao, cfg);
            AuthService authService = new AuthService(userDao, tokenDao, mailService, cfg);
            PoemService poemService = new PoemService(poemDao, tagDao, likeDao, favoriteDao);
            ReactionService reactionService = new ReactionService(likeDao, favoriteDao, poemDao, ratingService);
            HighlightService highlightService = new HighlightService(highlightDao, poemDao, ratingService);
            ReviewService reviewService = new ReviewService(reviewDao, poemDao, ratingService);
            AdminService adminService = new AdminService(userDao);

            ctx.setAttribute(Attr.SERVICES, new ServiceRegistry(authService, poemService,
                    reactionService, highlightService, reviewService, ratingService, adminService));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось запустить приложение", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}