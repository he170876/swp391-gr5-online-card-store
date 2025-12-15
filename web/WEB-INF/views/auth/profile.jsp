<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
    <title>Thông tin cá nhân - Online Card Store</title>
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
                        <h3>Thông tin cá nhân</h3>
                        <h4>Cập nhật thông tin tài khoản của bạn</h4>
                    </div>
                    
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">${error}</div>
                    </c:if>
                    
                    <c:if test="${not empty success}">
                        <div class="alert alert-success" role="alert">${success}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/auth/profile" method="post" class="form-login-wrapper">
                        <div class="form-login">
                            <label for="email">Email</label>
                            <div class="form-addons">
                                <input id="email" type="email" value="${user.email}" disabled>
                                <img src="${pageContext.request.contextPath}/assets/img/icons/mail.svg" alt="img">
                            </div>
                            <small class="form-text text-muted">Email không thể thay đổi</small>
                        </div>

                        <div class="form-login">
                            <label for="fullName">Họ và tên <span class="text-danger">*</span></label>
                            <div class="form-addons">
                                <input id="fullName" name="fullName" type="text" placeholder="Nhập họ và tên" 
                                       value="${user.fullName}" required>
                                <img src="${pageContext.request.contextPath}/assets/img/icons/user.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="phone">Số điện thoại</label>
                            <div class="form-addons">
                                <input id="phone" name="phone" type="tel" placeholder="Nhập số điện thoại" 
                                       value="${user.phone}">
                                <img src="${pageContext.request.contextPath}/assets/img/icons/phone.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label for="address">Địa chỉ</label>
                            <div class="form-addons">
                                <input id="address" name="address" type="text" placeholder="Nhập địa chỉ" 
                                       value="${user.address}">
                                <img src="${pageContext.request.contextPath}/assets/img/icons/map-pin.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <label>Số dư ví</label>
                            <div class="form-addons">
                                <input type="text" value="${user.walletBalance} VNĐ" disabled>
                                <img src="${pageContext.request.contextPath}/assets/img/icons/wallet.svg" alt="img">
                            </div>
                        </div>

                        <div class="form-login">
                            <button type="submit" class="btn btn-login">Cập nhật thông tin</button>
                        </div>
                    </form>

                    <div class="signinform text-center">
                        <h4><a href="${pageContext.request.contextPath}/auth/change-password" class="hover-a">Đổi mật khẩu</a></h4>
                        <h4 style="margin-top: 10px;">
                            <c:choose>
                                <c:when test="${user.roleId == 1}">
                                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="hover-a">Quay lại trang quản trị</a>
                                </c:when>
                                <c:when test="${user.roleId == 2}">
                                    <a href="${pageContext.request.contextPath}/staff" class="hover-a">Quay lại trang nhân viên</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/customer/home" class="hover-a">Quay lại trang chủ</a>
                                </c:otherwise>
                            </c:choose>
                        </h4>
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


