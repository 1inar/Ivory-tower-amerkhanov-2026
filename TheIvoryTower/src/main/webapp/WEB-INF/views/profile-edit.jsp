<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Мой профиль" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>О себе</h1>
<c:if test="${not empty error}"><p class="flash error"><c:out value="${error}"/></p></c:if>

<form method="post" action="${ctx}/profile/edit">
    <input type="hidden" name="_csrf" value="${csrf}">
    <input type="hidden" name="_form" value="${formToken}">
    <label>Несколько слов о себе
        <textarea name="bio" rows="6"><c:out value="${bio}"/></textarea>
    </label>
    <button type="submit">Сохранить</button>
</form>

<%@ include file="fragments/footer.jspf" %>