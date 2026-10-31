<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${poem.title}" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1><c:out value="${poem.title}"/></h1>
<p class="muted">
    <a href="${ctx}/poet?id=${poem.authorId}"><c:out value="${poem.authorName}"/></a>
    · ${poem.createdAtFormatted}<c:if test="${poem.edited}"> · изменено</c:if>
    <c:if test="${poem.avgScore != null}">
        · оценка критиков <fmt:formatNumber value="${poem.avgScore}" maxFractionDigits="1"/>
    </c:if>
</p>

<div class="poem-body" id="poem" data-id="${poem.id}" data-ctx="${ctx}" data-can="${canReact}">
    <c:forEach items="${poem.lines}" var="line" varStatus="s">
        <c:set var="st" value="${stats[s.count]}"/>
        <div class="line${st.mine ? ' mine' : ''}" data-line="${s.count}">
            <span class="text"><c:out value="${line}"/></span>
            <small class="cnt"><c:if test="${st != null}">${st.count}</c:if></small>
        </div>
    </c:forEach>
</div>

<c:choose>
    <c:when test="${canReact}">
        <p class="muted">Нажмите на строку, чтобы подчеркнуть её (не больше 5 строк в стихе). Самые подчёркнутые попадут в «Цитатник башни».</p>
    </c:when>
    <c:when test="${empty sessionScope.currentUser}">
        <p class="muted"><a href="${ctx}/login">Войдите</a>, чтобы ставить лайки и подчёркивать строки.</p>
    </c:when>
</c:choose>

<p>
    <c:forEach items="${poem.tags}" var="t">
        <a href="${ctx}/?tag=${t.name}">#<c:out value="${t.name}"/></a>
    </c:forEach>
</p>

<c:if test="${not empty sessionScope.currentUser}">
    <p class="actions">
        <c:if test="${canReact}">
            <button type="button" id="like-btn" class="toggle ${poem.likedByMe ? 'on' : ''}">
                ♥ <span id="like-count">${poem.likeCount}</span>
            </button>
        </c:if>
        <button type="button" id="fav-btn" class="toggle ${poem.favoritedByMe ? 'on' : ''}">★ В избранное</button>
    </p>
</c:if>
<c:if test="${empty sessionScope.currentUser}">
    <p class="muted">♥ ${poem.likeCount}</p>
</c:if>

<c:if test="${not empty sessionScope.currentUser
              and (sessionScope.currentUser.id == poem.authorId or sessionScope.currentUser.admin)}">
    <div class="actions">
        <a href="${ctx}/poem/edit?id=${poem.id}">Редактировать</a>
        <form method="post" action="${ctx}/poem/delete?id=${poem.id}"
              onsubmit="return confirm('Удалить стих?')">
            <input type="hidden" name="_csrf" value="${csrf}">
            <button type="submit">Удалить</button>
        </form>
    </div>
</c:if>

<h2>Рецензии критиков</h2>
<c:if test="${empty reviews}"><p class="muted">Рецензий пока нет.</p></c:if>
<c:forEach items="${reviews}" var="r">
    <div class="review">
        <p class="muted">
            <c:out value="${r.criticName}"/> · оценка ${r.score}/10 · ${r.createdAtFormatted}
        </p>
        <p><c:out value="${r.text}"/></p>
        <c:if test="${sessionScope.currentUser.id == r.criticId}">
            <form method="post" action="${ctx}/review/delete?id=${r.id}&poem=${poem.id}"
                  onsubmit="return confirm('Удалить рецензию?')">
                <input type="hidden" name="_csrf" value="${csrf}">
                <button type="submit">Удалить рецензию</button>
            </form>
        </c:if>
    </div>
</c:forEach>

<c:if test="${canReview}">
    <h2>Ваша рецензия</h2>
    <form method="post" action="${ctx}/review/add">
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="poem" value="${poem.id}">
        <label>Оценка (от 1 до 10)
            <input type="number" name="score" min="1" max="10" required>
        </label>
        <label>Текст рецензии
            <textarea name="text" rows="5" required minlength="20" maxlength="2000"></textarea>
        </label>
        <button type="submit">Опубликовать</button>
    </form>
</c:if>

<script src="${ctx}/static/js/poem.js"></script>
<%@ include file="fragments/footer.jspf" %>