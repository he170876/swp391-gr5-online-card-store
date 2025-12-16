<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
        <title>Register - Online Card Store</title>

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
                                <img src="${pageContext.request.contextPath}/img/logo.jpg" alt="logo" style="height:60px;">
                                <h2 style="margin:0;font-weight:600;">Online Card Store</h2>
                            </div>

                            <div class="login-userheading">
                                <h3>Tạo tài khoản</h3>
                                <h4>Tiếp tục trải nghiệm mua sắm của bạn</h4>
                            </div>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger" role="alert">${error}</div>
                            </c:if>

                            <c:if test="${not empty param.error}">
                                <div class="alert alert-danger" role="alert">${param.error}</div>
                            </c:if>

                            <c:if test="${not empty errors.general}">
                                <div class="alert alert-danger" role="alert">${errors.general}</div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/register"
                                  method="post"
                                  class="form-login">

                                <!-- HỌ VÀ TÊN -->
                                <div class="form-login">
                                    <label>Họ và tên</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="fullName"
                                               value="<c:out value='${fullNameValue}'/>"
                                               placeholder="Nhập họ và tên"
                                               required>
                                        <img src="assets/img/icons/users1.svg" alt="icon">
                                    </div>
                                    <c:if test="${not empty errors.fullName}">
                                        <div class="text-danger">${errors.fullName}</div>
                                    </c:if>
                                </div>

                                <!-- EMAIL -->
                                <div class="form-login">
                                    <label>Email</label>
                                    <div class="form-addons">
                                        <input type="email"
                                               name="email"
                                               placeholder="Nhập địa chỉ email"
                                               value="<c:out value='${emailValue}'/>"
                                               required>
                                        <img src="assets/img/icons/mail.svg" alt="icon">
                                    </div>
                                    <c:if test="${not empty errors.email}">
                                        <div class="text-danger">${errors.email}</div>
                                    </c:if>
                                </div>

                                <!-- MẬT KHẨU -->
                                <div class="form-login">
                                    <label>Mật khẩu</label>
                                    <div class="pass-group">
                                        <input type="password"
                                               name="password"
                                               class="pass-input"
                                               placeholder="Nhập mật khẩu"
                                               required>
                                        <span class="fas toggle-password fa-eye-slash"></span>
                                    </div>
                                    <c:if test="${not empty errors.password}">
                                        <div class="text-danger">${errors.password}</div>
                                    </c:if>
                                </div>

                                <!-- XÁC NHẬN MẬT KHẨU -->
                                <div class="form-login">
                                    <label>Xác nhận mật khẩu</label>
                                    <div class="pass-group">
                                        <input type="password"
                                               name="confirmPassword"
                                               class="pass-input"
                                               placeholder="Nhập lại mật khẩu"
                                               required>
                                    </div>
                                </div>

                                <!-- SỐ ĐIỆN THOẠI -->
                                <div class="form-login">
                                    <label>Số điện thoại</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="phone"
                                               placeholder="Nhập số điện thoại"
                                               value="<c:out value='${phoneValue}'/>">
                                    </div>
                                    <c:if test="${not empty errors.phone}">
                                        <div class="text-danger">${errors.phone}</div>
                                    </c:if>
                                </div>

                                <!-- ĐỊA CHỈ -->
                                <div class="form-login">
                                    <label>Địa chỉ</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="address"
                                               placeholder="Nhập địa chỉ"
                                               value="<c:out value='${addressValue}'/>">
                                    </div>
                                    <c:if test="${not empty errors.address}">
                                        <div class="text-danger">${errors.address}</div>
                                    </c:if>
                                </div>

                                <!-- SUBMIT -->
                                <div class="form-login">
                                    <button type="submit" class="btn btn-login">
                                        Đăng ký
                                    </button>
                                </div>

                            </form>

                            <div class="signinform text-center">
                                <h4>
                                    Đã có tài khoản?
                                    <a href="${pageContext.request.contextPath}/login" class="hover-a">
                                        Đăng nhập
                                    </a>
                                </h4>
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
