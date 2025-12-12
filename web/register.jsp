<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0">
        <title>Đăng ký - Online Card Store</title>
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
                                <h3>Create an Account</h3>
                                <h4>Continue where you left off</h4>
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

                            <form action="${pageContext.request.contextPath}/register" method="post" class="form-login">

                                <div class="form-login">
                                    <label>Full Name</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="fullName"
                                               value="<c:out value='${fullNameValue}'/>"
                                               placeholder="Enter your full name"
                                               required>
                                        <img src="assets/img/icons/users1.svg" alt="img">
                                    </div>
                                    <c:if test="${not empty errors.fullName}">
                                        <div class="text-danger">${errors.fullName}</div>
                                    </c:if>
                                </div>

                                <div class="form-login">
                                    <label>Email</label>
                                    <div class="form-addons">
                                        <input type="email"
                                               name="email"
                                               placeholder="Enter your email address"
                                               value="<c:out value='${emailValue}'/>"
                                               required>
                                        <img src="assets/img/icons/mail.svg" alt="img">
                                    </div>
                                    <c:if test="${not empty errors.email}">
                                        <div class="text-danger">${errors.email}</div>
                                    </c:if>
                                </div>

                                <div class="form-login">
                                    <label>Password</label>
                                    <div class="pass-group">
                                        <input type="password" name="password" class="pass-input" placeholder="Enter your password" required>
                                        <span class="fas toggle-password fa-eye-slash"></span>
                                    </div>
                                    <c:if test="${not empty errors.password}">
                                        <div class="text-danger">${errors.password}</div>
                                    </c:if>
                                </div>

                                <div class="form-login">
                                    <label>Confirm Password</label>
                                    <div class="pass-group">
                                        <input type="password" name="confirmPassword" class="pass-input" placeholder="Re-enter your password" required>
                                    </div>
                                </div>

                                <div class="form-login">
                                    <label>Phone</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="phone"
                                               placeholder="Enter your phone number"
                                               value="<c:out value='${phoneValue}'/>">
                                    </div>
                                    <c:if test="${not empty errors.phone}">
                                        <div class="text-danger">${errors.phone}</div>
                                    </c:if>
                                </div>

                                <div class="form-login">
                                    <label>Address</label>
                                    <div class="form-addons">
                                        <input type="text"
                                               name="address"
                                               placeholder="Enter your address"
                                               value="<c:out value='${addressValue}'/>">
                                    </div>
                                    <c:if test="${not empty errors.address}">
                                        <div class="text-danger">${errors.address}</div>
                                    </c:if>
                                </div>

                                <div class="form-login">
                                    <button type="submit" class="btn btn-login">Sign Up</button>
                                </div>

                            </form>

                            <div class="signinform text-center">
                                <h4>Already a user? <a href="${pageContext.request.contextPath}/login" class="hover-a">Sign In</a></h4>
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
