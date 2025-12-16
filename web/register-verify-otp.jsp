<%-- 
    Document   : register-verify-otp
    Created on : Dec 12, 2025, 9:57:57 PM
    Author     : hades
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
        <title>Xác thực OTP - Online Card Store</title>

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

                            <div class="login-logo" style="display:flex;align-items:center;gap:10px;">
                                <img src="${pageContext.request.contextPath}/img/logo.jpg" alt="img" style="height:60px;">
                                <h2 style="margin:0;font-weight:600;">Online Card Store</h2>
                            </div>

                            <div class="login-userheading">
                                <h3>Xác Thực OTP</h3>
                                <h4>Mã OTP đã được gửi đến email:</h4>

                                <h5 style="color:#0d6efd;">
                                    <strong>${maskedEmail}</strong>
                                </h5>
                            </div>

                            <!-- Hiển thị msg -->
                            <c:if test="${not empty msgs}">
                                <div class="alert alert-success">${msgs}</div>
                            </c:if>
                            <c:if test="${not empty msg}">
                                <div class="alert alert-success">${msg}</div>
                            </c:if>
                            <c:if test="${not empty param.msg}">
                                <div class="alert alert-success">${param.msg}</div>
                            </c:if>

                            <!-- Hiển thị lỗi -->
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger" role="alert">${error}</div>
                            </c:if>
                            <c:if test="${not empty param.error}">
                                <div class="alert alert-danger" role="alert">${param.error}</div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/registerVerifyOTP" method="post">

                                <div class="form-login">
                                    <label>Nhập mã OTP</label>
                                    <div class="form-addons">
                                        <input type="text" name="otp" placeholder="Enter your OTP" required maxlength="6">
                                        <img src="assets/img/icons/mail.svg" alt="img">
                                    </div>
                                </div>

                                <div class="form-login">
                                    <button type="submit" class="btn btn-login">Xác nhận OTP</button>
                                </div>
                            </form>

                            <div class="form-login text-center">
                                <form action="${pageContext.request.contextPath}/resendRegisterOTP" method="post">
                                    <button class="btn btn-secondary" style="width:100%;">Gửi lại OTP</button>
                                </form>
                            </div>

                            <div class="signinform text-center">
                                <h4>Already a user? <a href="${pageContext.request.contextPath}/login" class="hover-a">Sign In</a></h4>
                            </div>

                            <!-- Back to Home -->
                            <div class="signinform text-center mt-3">
                                <h4>
                                    <a href="${pageContext.request.contextPath}/" class="hover-a">
                                        <i class="fas fa-arrow-left"></i> Quay về trang chủ
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

