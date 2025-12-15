<%-- 
    Document   : forgot-password
    Created on : Dec 13, 2025, 6:31:14 PM
    Author     : hades
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
        <title>Forgot Password - Online Card Store</title>
        <link rel="shortcut icon" type="image/x-icon" href="img/smalllogo.jpg">
        <link rel="stylesheet" href="assets/css/bootstrap.min.css">
        <link rel="stylesheet" href="assets/plugins/fontawesome/css/fontawesome.min.css">
        <link rel="stylesheet" href="assets/plugins/fontawesome/css/all.min.css">
        <link rel="stylesheet" href="assets/css/style.css">
    </head>
    <body class="account-page">

        <div class="main-wrapper">
            <div class="account-content">
                <div class="login-wrapper">
                    <div class="login-content">
                        <div class="login-userset">

                            <!-- Logo -->
                            <div class="login-logo" style="display:flex;align-items:center;gap:10px;">
                                <img src="${pageContext.request.contextPath}/img/logo.jpg" alt="img" style="height:60px;">
                                <h2 style="margin:0;font-weight:600;">Online Card Store</h2>
                            </div>

                            <!-- Heading -->
                            <div class="login-userheading">
                                <h3>Forgot Password</h3>
                                <h4>Enter your email to receive OTP</h4>
                            </div>

                            <!-- Message -->
                            <c:if test="${not empty msg}">
                                <div class="alert alert-success">${msg}</div>
                            </c:if>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>

                            <!-- Form -->
                            <form action="${pageContext.request.contextPath}/forgotPassword" method="post">

                                <div class="form-login">
                                    <label>Email</label>
                                    <div class="form-addons">
                                        <input type="email"
                                               name="email"
                                               placeholder="Enter your registered email"
                                               value="<c:out value='${emailValue}'/>"
                                               required>
                                        <img src="assets/img/icons/mail.svg" alt="img">
                                    </div>
                                </div>

                                <div class="form-login">
                                    <button type="submit" class="btn btn-login">
                                        Send OTP
                                    </button>
                                </div>

                            </form>

                            <!-- Back to login -->
                            <div class="signinform text-center">
                                <h4>
                                    Remember your password?
                                    <a href="${pageContext.request.contextPath}/login" class="hover-a">
                                        Sign In
                                    </a>
                                </h4>
                            </div>

                        </div>
                    </div>

                    <div class="login-img">
                        <img src="assets/img/login.jpg" alt="img">
                    </div>
                </div>
            </div>
        </div>

        <script src="assets/js/jquery-3.6.0.min.js"></script>
        <script src="assets/js/feather.min.js"></script>
        <script src="assets/js/bootstrap.bundle.min.js"></script>
        <script src="assets/js/script.js"></script>
    </body>
</html>

