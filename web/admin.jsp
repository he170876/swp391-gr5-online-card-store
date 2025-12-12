<%-- 
    Document   : admin
    Created on : Dec 10, 2025, 2:34:06 AM
    Author     : hades
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Page</title>
    </head>
    <body>
        <h1>Admin Page</h1>
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <h2>Chào mừng</h2>
                <a href="login">Đăng nhập</a>
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
