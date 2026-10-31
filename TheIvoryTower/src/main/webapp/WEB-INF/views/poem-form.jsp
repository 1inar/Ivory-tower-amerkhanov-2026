<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${heading}" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1><c:out value="${heading}"/></h1>

<form method="post" action="${ctx}${formAction}">
    <input type="hidden" name="_csrf" value="${csrf}">
    <input type="hidden" name="_form" value="${formToken}">

    <label>Название
        <input name="title" required maxlength="120" value="<c:out value='${old.title}'/>">
    </label>
    <c:if test="${not empty errors.title}"><span class="err"><c:out value="${errors.title}"/></span></c:if>

    <label>Текст (каждая строка стиха с новой строки)
        <textarea name="body" rows="14" required minlength="10" maxlength="5000"><c:out value="${old.body}"/></textarea>
    </label>
    <c:if test="${not empty errors.body}"><span class="err"><c:out value="${errors.body}"/></span></c:if>

    <label>Теги через запятую (не больше 5)
        <input name="tags" placeholder="осень, лирика" value="<c:out value='${old.tags}'/>">
    </label>
    <c:if test="${not empty errors.tags}"><span class="err"><c:out value="${errors.tags}"/></span></c:if>

    <button type="submit">Сохранить</button>
</form>

<%@ include file="fragments/footer.jspf" %>