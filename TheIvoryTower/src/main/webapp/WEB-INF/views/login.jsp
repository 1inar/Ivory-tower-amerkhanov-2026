<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Вход" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="auth">
    <h1>Вход</h1>
    <c:if test="${not empty error}"><p class="flash error"><c:out value="${error}"/></p></c:if>

    <form method="post" action="${ctx}/login">
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="_form" value="${formToken}">

        <label>Email
            <input name="email" type="email" required value="<c:out value='${old.email}'/>">
        </label>
        <label>Пароль
            <input name="password" type="password" required>
        </label>
        <button type="submit">Войти</button>
    </form>
    <p>Нет аккаунта? <a href="${ctx}/register">Зарегистрироваться</a></p>
</div>

<%@ include file="fragments/footer.jspf" %>