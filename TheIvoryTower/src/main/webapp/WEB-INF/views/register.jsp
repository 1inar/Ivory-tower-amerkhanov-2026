<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Регистрация" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="auth">
    <h1>Регистрация</h1>
    <c:if test="${not empty error}"><p class="flash error"><c:out value="${error}"/></p></c:if>

    <form method="post" action="${ctx}/register">
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="_form" value="${formToken}">

        <label>Логин
            <input name="username" required minlength="3" maxlength="30" value="<c:out value='${old.username}'/>">
        </label>
        <c:if test="${not empty errors.username}"><span class="err"><c:out value="${errors.username}"/></span></c:if>

        <label>Email
            <input name="email" type="email" required value="<c:out value='${old.email}'/>">
        </label>
        <c:if test="${not empty errors.email}"><span class="err"><c:out value="${errors.email}"/></span></c:if>

        <label>Пароль
            <input name="password" type="password" required minlength="8">
        </label>
        <c:if test="${not empty errors.password}"><span class="err"><c:out value="${errors.password}"/></span></c:if>

        <label>Повторите пароль
            <input name="password2" type="password" required minlength="8">
        </label>
        <c:if test="${not empty errors.password2}"><span class="err"><c:out value="${errors.password2}"/></span></c:if>

        <button type="submit">Зарегистрироваться</button>
    </form>
    <p>Уже есть аккаунт? <a href="${ctx}/login">Войти</a></p>
</div>

<%@ include file="fragments/footer.jspf" %>