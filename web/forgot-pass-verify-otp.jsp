<%-- 
    Document   : forgot-pass-verify-otp
    Created on : Dec 16, 2025
    Author     : hades
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
        <title>Reset Password - Online Card Store</title>

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
                                <img src="${pageContext.request.contextPath}/img/logo.jpg" alt="logo" style="height:60px;">
                                <h2 style="margin:0;font-weight:600;">Online Card Store</h2>
                            </div>

                            <!-- Heading -->
                            <div class="login-userheading">
                                <h3>Đặt lại mật khẩu</h3>
                                <h4>Nhập mã OTP và mật khẩu mới</h4>
                            </div>

                            <!-- Message -->
                            <c:if test="${not empty msg}">
                                <div class="alert alert-success">${msg}</div>
                            </c:if>

                            <c:if test="${not empty param.msg}">
                                <div class="alert alert-success">${param.msg}</div>
                            </c:if>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>

                            <!-- Thông tin email & thời hạn OTP -->
                            <c:if test="${not empty displayEmail}">
                                <div class="text-center mb-3">
                                    <small class="text-muted d-block">
                                        Mã OTP đã được gửi đến email:
                                        <strong>
                                            <c:out value="${displayEmail}" />
                                        </strong>
                                    </small>

                                    <small class="text-warning">
                                        Mã OTP có hiệu lực trong <strong>5 phút</strong>
                                    </small>
                                </div>
                            </c:if>

                            <!-- Form -->
                            <form action="${pageContext.request.contextPath}/forgotPasswordOTP" method="post">

                                <!-- OTP -->
                                <div class="form-login">
                                    <label>Mã OTP</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="otp"
                                               placeholder="Nhập mã OTP"
                                               maxlength="6"
                                               required>
                                        <img src="assets/img/icons/key.svg" alt="icon">
                                    </div>
                                </div>

                                <!-- New password -->
                                <div class="form-login">
                                    <label>Mật khẩu mới</label>
                                    <div class="form-addons">
                                        <input type="password"
                                               name="newPassword"
                                               placeholder="Nhập mật khẩu mới"
                                               required>
                                        <img src="assets/img/icons/lock.svg" alt="icon">
                                    </div>
                                </div>

                                <!-- Confirm password -->
                                <div class="form-login">
                                    <label>Xác nhận mật khẩu</label>
                                    <div class="form-addons">
                                        <input type="password"
                                               name="confirmPassword"
                                               placeholder="Nhập lại mật khẩu mới"
                                               required>
                                        <img src="assets/img/icons/lock.svg" alt="icon">
                                    </div>
                                </div>

                                <!-- Submit -->
                                <div class="form-login">
                                    <button type="submit" class="btn btn-login">
                                        Đặt lại mật khẩu
                                    </button>
                                </div>
                            </form>

                            <!-- Resend OTP -->
                            <div class="text-center mt-3">
                                <form action="${pageContext.request.contextPath}/resendForgotPassOtp" method="post">
                                    <button type="submit" class="btn btn-link">
                                        Gửi lại mã OTP
                                    </button>
                                </form>
                            </div>

                            <!-- Back to login -->
                            <div class="signinform text-center">
                                <h4>
                                    Quay lại
                                    <a href="${pageContext.request.contextPath}/login" class="hover-a">
                                        Đăng nhập
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
