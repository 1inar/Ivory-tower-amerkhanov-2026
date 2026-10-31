<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${poet.username}" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1><c:out value="${poet.username}"/></h1>
<p class="muted">
    <c:choose>
        <c:when test="${poet.admin}">Администратор</c:when>
        <c:when test="${poet.critic}">Критик</c:when>
        <c:otherwise>Поэт</c:otherwise>
    </c:choose>
    · место в рейтинге: ${rating.position}
    · очки: ${rating.totalScore}
</p>
<c:if test="${not empty poet.bio}"><p><c:out value="${poet.bio}"/></p></c:if>

<h2>Стихи (${rating.poemCount})</h2>
<c:if test="${empty poems}"><p class="muted">Пока ничего не опубликовано.</p></c:if>
<c:forEach items="${poems}" var="p">
    <article>
        <h2><a href="${ctx}/poem?id=${p.id}"><c:out value="${p.title}"/></a></h2>
        <p class="muted">${p.createdAtFormatted} · ♥ ${p.likeCount}</p>
        <p><c:out value="${p.excerpt}"/></p>
    </article>
</c:forEach>

<%@ include file="fragments/footer.jspf" %>