<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title><c:out value="${param.title != null ? param.title : 'Online Card Store'}"/></title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <style>
            body { background-color: #f8f9fa; }
            .navbar-brand span { font-weight: 700; }
            .card-img-top { object-fit: cover; height: 180px; }
            .hero { background: linear-gradient(120deg, #e8f0ff, #f7fbff); }
        </style>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
            <div class="container">
                <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/">
                    <img src="${pageContext.request.contextPath}/img/smalllogo.jpg" alt="OCS" width="40" class="me-2 rounded">
                    <span>Online Card Store</span>
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#guestNav" aria-controls="guestNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="guestNav">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Trang chủ</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/products">Sản phẩm</a></li>
                    </ul>
                    <div class="d-flex gap-2">
                        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                    </div>
                </div>
            </div>
        </nav>

