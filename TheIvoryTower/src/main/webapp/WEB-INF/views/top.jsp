<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Рейтинг поэтов" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Рейтинг поэтов</h1>
<p class="muted">Очки: лайки ×1 + подчёркнутые строки ×2 + средняя оценка критиков ×5.</p>

<c:if test="${empty rating}"><p class="muted">Пока никого нет.</p></c:if>
<c:if test="${not empty rating}">
    <table>
        <tr><th>#</th><th>Поэт</th><th>Стихов</th><th>Лайков</th><th>Подчёркиваний</th><th>Очки</th></tr>
        <c:forEach items="${rating}" var="r">
            <tr>
                <td>${r.position}</td>
                <td><a href="${ctx}/poet?id=${r.userId}"><c:out value="${r.username}"/></a></td>
                <td>${r.poemCount}</td>
                <td>${r.likes}</td>
                <td>${r.highlights}</td>
                <td><strong>${r.totalScore}</strong></td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<%@ include file="fragments/footer.jspf" %>