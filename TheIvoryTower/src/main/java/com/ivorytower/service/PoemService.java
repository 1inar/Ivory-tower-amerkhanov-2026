package com.ivorytower.service;

import com.ivorytower.dao.FavoriteDao;
import com.ivorytower.dao.LikeDao;
import com.ivorytower.dao.PoemDao;
import com.ivorytower.dao.TagDao;
import com.ivorytower.exception.AccessDeniedException;
import com.ivorytower.exception.NotFoundException;
import com.ivorytower.exception.ValidationException;
import com.ivorytower.model.Poem;
import com.ivorytower.model.Tag;
import com.ivorytower.model.User;
import com.ivorytower.util.Validator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PoemService {
    private final PoemDao poemDao;
    private final TagDao tagDao;
    private final LikeDao likeDao;
    private final FavoriteDao favoriteDao;

    public PoemService(PoemDao poemDao, TagDao tagDao, LikeDao likeDao, FavoriteDao favoriteDao) {
        this.poemDao = poemDao;
        this.tagDao = tagDao;
        this.likeDao = likeDao;
        this.favoriteDao = favoriteDao;
    }

    public long create(User author, String title, String body, String tagsCsv) {
        List<String> tags = parseTags(tagsCsv);
        validate(title, body, tags);

        Poem poem = new Poem();
        poem.setAuthorId(author.getId());
        poem.setTitle(title.trim());
        poem.setBody(normalizeBody(body));
        return poemDao.create(poem, tagIds(tags));
    }

    public Poem view(long id, User viewer) {
        Poem poem = poemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Стих не найден"));
        if (viewer != null) {
            poem.setLikedByMe(likeDao.exists(viewer.getId(), id));
            poem.setFavoritedByMe(favoriteDao.exists(viewer.getId(), id));
        }
        return poem;
    }

    public void update(User actor, long id, String title, String body, String tagsCsv) {
        Poem poem = poemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Стих не найден"));
        checkCanModify(actor, poem);

        List<String> tags = parseTags(tagsCsv);
        validate(title, body, tags);

        poem.setTitle(title.trim());
        poem.setBody(normalizeBody(body));
        poemDao.update(poem, tagIds(tags));
    }

    public void delete(User actor, long id) {
        Poem poem = poemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Стих не найден"));
        checkCanModify(actor, poem);
        poemDao.delete(id);
    }


    public List<Poem> latest(int page, int size) {
        return poemDao.findLatest(size, offset(page, size));
    }

    public int pageCount(int size) {
        return Math.max(1, (int) Math.ceil(poemDao.countAll() / (double) size));
    }

    public List<Poem> byAuthor(long authorId) {
        return poemDao.findByAuthor(authorId);
    }

    public List<Poem> byTag(String tag, int page, int size) {
        return poemDao.findByTag(tag.trim().toLowerCase(), size, offset(page, size));
    }

    public List<Poem> search(String q) {
        if (q == null || q.isBlank()) return List.of();
        return poemDao.search(q.trim());
    }

    public List<Tag> allTags() {
        return tagDao.findAll();
    }


    private void checkCanModify(User actor, Poem poem) {
        boolean owner = actor.getId().equals(poem.getAuthorId());
        if (!owner && !actor.isAdmin()) {
            throw new AccessDeniedException("Изменять стих может только его автор");
        }
    }

    private void validate(String title, String body, List<String> tags) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (!Validator.lengthBetween(title, 1, 120)) {
            errors.put("title", "Название: от 1 до 120 символов");
        }
        if (!Validator.lengthBetween(body, 10, 5000)) {
            errors.put("body", "Текст: от 10 до 5000 символов");
        }
        if (tags.size() > 5) {
            errors.put("tags", "Не более 5 тегов");
        } else {
            for (String t : tags) {
                if (t.length() < 2 || t.length() > 30) {
                    errors.put("tags", "Каждый тег: от 2 до 30 символов");
                    break;
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private static List<String> parseTags(String csv) {
        List<String> result = new ArrayList<>();
        if (csv == null) return result;
        for (String part : csv.split(",")) {
            String t = part.trim().toLowerCase();
            if (!t.isEmpty() && !result.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    private List<Integer> tagIds(List<String> names) {
        List<Integer> ids = new ArrayList<>();
        for (String name : names) {
            ids.add(tagDao.findOrCreate(name).getId());
        }
        return ids;
    }

    private static String normalizeBody(String body) {
        return body.replace("\r\n", "\n").replace("\r", "\n").strip();
    }

    private static int offset(int page, int size) {
        return (Math.max(page, 1) - 1) * size;
    }
}