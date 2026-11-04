<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Главная" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<form class="search" method="get" action="${ctx}/">
    <input name="q" placeholder="Поиск по названию, тексту или автору" value="<c:out value='${q}'/>">
    <button type="submit">Найти</button>
</form>

<%-- Цитатник башни --%>
<c:if test="${not empty quotes}">
    <h2>Цитатник башни</h2>
    <c:forEach items="${quotes}" var="quote">
        <blockquote>
            «<c:out value="${quote.lineText}"/>»
            <div class="muted">
                <a href="${ctx}/poet?id=${quote.authorId}"><c:out value="${quote.authorName}"/></a>,
                <a href="${ctx}/poem?id=${quote.poemId}"><c:out value="${quote.poemTitle}"/></a>
                · подчёркиваний: ${quote.count}
            </div>
        </blockquote>
    </c:forEach>
</c:if>

<c:if test="${not empty tag}"><p>Тег: #<c:out value="${tag}"/> · <a href="${ctx}/">сбросить</a></p></c:if>
<c:if test="${empty poems}"><p class="muted">Здесь пока пусто.</p></c:if>

<c:forEach items="${poems}" var="p">
    <article>
        <h2><a href="${ctx}/poem?id=${p.id}"><c:out value="${p.title}"/></a></h2>
        <p class="muted">
            <a href="${ctx}/poet?id=${p.authorId}"><c:out value="${p.authorName}"/></a>
            · ${p.createdAtFormatted} · ♥ ${p.likeCount}
        </p>
        <p><c:out value="${p.excerpt}"/></p>
    </article>
</c:forEach>

<p>
    <c:if test="${hasPrev}"><a href="${ctx}/?page=${page - 1}">← Назад</a></c:if>
    <c:if test="${hasNext}"><a href="${ctx}/?page=${page + 1}">Дальше →</a></c:if>
</p>

<%@ include file="fragments/footer.jspf" %>