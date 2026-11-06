<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Админка" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Админка: пользователи</h1>

<table>
    <tr><th>Логин</th><th>Email</th><th>Роль</th><th></th></tr>
    <c:forEach items="${users}" var="u">
        <tr>
            <td><a href="${ctx}/poet?id=${u.id}"><c:out value="${u.username}"/></a></td>
            <td><c:out value="${u.email}"/></td>
            <c:choose>
                <c:when test="${u.id == sessionScope.currentUser.id}">
                    <td>${u.role}</td>
                    <td class="muted">это вы</td>
                </c:when>
                <c:otherwise>
                    <td>
                        <form class="row" method="post" action="${ctx}/admin">
                            <input type="hidden" name="_csrf" value="${csrf}">
                            <input type="hidden" name="action" value="role">
                            <input type="hidden" name="user" value="${u.id}">
                            <input type="hidden" name="page" value="${page}">
                            <select name="role">
                                <option value="POET"   ${u.role == 'POET'   ? 'selected' : ''}>POET</option>
                                <option value="CRITIC" ${u.role == 'CRITIC' ? 'selected' : ''}>CRITIC</option>
                                <option value="ADMIN"  ${u.role == 'ADMIN'  ? 'selected' : ''}>ADMIN</option>
                            </select>
                            <button type="submit">Сменить</button>
                        </form>
                    </td>
                    <td>
                        <form method="post" action="${ctx}/admin"
                              onsubmit="return confirm('Удалить пользователя вместе со всеми его стихами?')">
                            <input type="hidden" name="_csrf" value="${csrf}">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="user" value="${u.id}">
                            <input type="hidden" name="page" value="${page}">
                            <button type="submit">Удалить</button>
                        </form>
                    </td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
</table>

<c:if test="${pages > 1}">
    <p>
        <c:if test="${page > 1}"><a href="${ctx}/admin?page=${page - 1}">← Назад</a></c:if>
        Страница ${page} из ${pages}
        <c:if test="${page < pages}"><a href="${ctx}/admin?page=${page + 1}">Дальше →</a></c:if>
    </p>
</c:if>

<%@ include file="fragments/footer.jspf" %>