<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
    <title>Đăng ký - Online Card Store</title>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/img/smalllogo.jpg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/fontawesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
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
                        <h3>Đăng ký tài khoản</h3>
                        <h4>Tạo tài khoản mới để bắt đầu mua sắm</h4>
                    </div>
                    
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">${error}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/auth/register" method="post" class="form-login-wrapper">
                        <div class="form-login">
                            <label for="email">Email <span class="text-danger">*</span></label>
                            <div class="form-addons">
                                <input id="email" name="email" type="email" placeholder="Nhập địa chỉ email" 
                                       value="${email}" required>
                                <img src="${pageContext.request.contextPath}/assets/img/icons/mail.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="fullName">Họ và tên <span class="text-danger">*</span></label>
                            <div class="form-addons">
                                <input id="fullName" name="fullName" type="text" placeholder="Nhập họ và tên" 
                                       value="${fullName}" required>
                                <img src="${pageContext.request.contextPath}/assets/img/icons/user.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="phone">Số điện thoại</label>
                            <div class="form-addons">
                                <input id="phone" name="phone" type="tel" placeholder="Nhập số điện thoại" 
                                       value="${phone}">
                                <img src="${pageContext.request.contextPath}/assets/img/icons/phone.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="address">Địa chỉ</label>
                            <div class="form-addons">
                                <input id="address" name="address" type="text" placeholder="Nhập địa chỉ" 
                                       value="${address}">
                                <img src="${pageContext.request.contextPath}/assets/img/icons/map-pin.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="password">Mật khẩu <span class="text-danger">*</span></label>
                            <div class="pass-group">
                                <input id="password" name="password" type="password" class="pass-input" 
                                       placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)" required minlength="6">
                                <span class="fas toggle-password fa-eye-slash" role="button"></span>
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="confirmPassword">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                            <div class="pass-group">
                                <input id="confirmPassword" name="confirmPassword" type="password" class="pass-input" 
                                       placeholder="Nhập lại mật khẩu" required minlength="6">
                                <span class="fas toggle-password fa-eye-slash" role="button"></span>
                            </div>
                        </div>

                        <div class="form-login">
                            <button type="submit" class="btn btn-login">Đăng ký</button>
                        </div>
                    </form>

                    <div class="signinform text-center">
                        <h4>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login" class="hover-a">Đăng nhập</a></h4>
                    </div>
                </div>
            </div>

            <div class="login-img">
                <img src="${pageContext.request.contextPath}/assets/img/login.jpg" alt="img">
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/feather.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>


