<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Избранное" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Избранное</h1>
<c:if test="${empty poems}"><p class="muted">Вы пока ничего не добавили в избранное.</p></c:if>
<c:forEach items="${poems}" var="p">
    <article>
        <h2><a href="${ctx}/poem?id=${p.id}"><c:out value="${p.title}"/></a></h2>
        <p class="muted">
            <a href="${ctx}/poet?id=${p.authorId}"><c:out value="${p.authorName}"/></a>
            · ♥ ${p.likeCount}
        </p>
        <p><c:out value="${p.excerpt}"/></p>
    </article>
</c:forEach>

<%@ include file="fragments/footer.jspf" %>