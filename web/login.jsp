<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
        <title>Đăng nhập - Online Card Store</title>
        <link rel="shortcut icon" type="image/x-icon" href="img/smalllogo.jpg">
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
                                <h3>Đăng nhập</h3>
                                <h4>Vui lòng đăng nhập vào tài khoản của bạn</h4>
                            </div>

                            <c:if test="${not empty param.success}">
                                <div class="alert alert-success" role="alert">${param.success}</div>
                            </c:if>

                            <c:if test="${not empty param.error}">
                                <div class="alert alert-danger" role="alert">${param.error}</div>
                            </c:if>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger" role="alert">${error}</div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/login" method="post" class="form-login-wrapper">
                                <c:if test="${not empty _csrf}">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                </c:if>

                                <div class="form-login">
                                    <label for="email">Email</label>
                                    <div class="form-addons">
                                        <input id="email" name="email" type="text" placeholder="Nhập địa chỉ email" value="${param.email}" required>
                                        <img src="${pageContext.request.contextPath}/assets/img/icons/mail.svg" alt="img">
                                    </div>
                                </div>

                                <div class="form-login">
                                    <label for="password">Mật khẩu</label>
                                    <div class="pass-group">
                                        <input id="password" name="password" value="${password}" type="password" class="pass-input" placeholder="Nhập mật khẩu" required>
                                        <span class="fas toggle-password fa-eye-slash" role="button" aria-label="Toggle password visibility"></span>
                                    </div>
                                </div>

                                <div class="form-login">
                                    <div class="alreadyuser">
                                        <h4><a href="${pageContext.request.contextPath}/forgetpassword.html" class="hover-a">Quên mật khẩu?</a></h4>
                                    </div>
                                </div>

                                <div class="form-login">
                                    <button type="submit" class="btn btn-login">Đăng nhập</button>
                                </div>
                            </form>

                            <div class="signinform text-center">
                                <h4>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register" class="hover-a">Đăng ký</a></h4>
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
