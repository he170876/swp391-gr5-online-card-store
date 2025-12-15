<%-- 
    Document   : homepage
    Created on : Dec 10, 2025, 1:48:50 AM
    Author     : hades
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Home - Online Card Store</title>
    </head>
    <body>

    <c:choose>
        <c:when test="${empty sessionScope.user}">
            <h2>Chào mừng</h2>
            <a href="login">Đăng nhập</a></br>
            <a href="register">Đăng ký</a>
        </c:when>

        <c:otherwise>
            <h2>Xin chào ${sessionScope.user.fullName} (Role: ${sessionScope.user.roleId})</h2>

            <form action="logout" method="get">
                <button type="submit">Đăng xuất</button>
            </form>
        </c:otherwise>
    </c:choose>

    </body>
</html>

