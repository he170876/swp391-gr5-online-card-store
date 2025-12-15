<%-- 
    Document   : admin
    Created on : Dec 10, 2025, 2:34:06 AM
    Author     : hades
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>${pageTitle}</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>

    <!-- Custom Admin CSS -->
    <style>
        body {
            background: #f5f6fa;
            font-family: 'Inter', sans-serif;
        }

        .sidebar {
            width: 260px;
            height: 100vh;
            position: fixed;
            background: #1e1e2d;
            color: white;
            padding-top: 30px;
        }

        .sidebar .logo {
            font-size: 22px;
            padding: 0 20px 20px;
            font-weight: bold;
            text-align: center;
        }

        .sidebar .menu a {
            display: block;
            padding: 14px 20px;
            color: #c7c7d9;
            text-decoration: none;
            font-size: 15px;
        }

        .sidebar .menu a:hover,
        .sidebar .menu a.active {
            background: #2d2d42;
            color: #fff;
        }

        .main-content {
            margin-left: 260px;
            padding: 30px;
        }

        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.06);
        }

        .table thead {
            background: #e8e8ef;
        }

        .btn-primary {
            background: #4c57ff;
            border-color: #4c57ff;
        }

        .btn-primary:hover {
            background: #3a45d0;
        }
    </style>
</head>

<body>

<!-- Sidebar -->
<div class="sidebar">
    <div class="logo">OCS Admin</div>

    <div class="menu">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="${active=='dashboard'?'active':''}">
            <i class="fa fa-chart-line"></i> Bảng điều khiển
        </a>

        <a href="${pageContext.request.contextPath}/admin/customer" class="${active=='customers'?'active':''}">
            <i class="fa fa-users"></i> Khách hàng
        </a>

        <a href="${pageContext.request.contextPath}/admin/staff" class="${active=='staff'?'active':''}">
            <i class="fa fa-user-tie"></i> Nhân viên
        </a>

        <a href="${pageContext.request.contextPath}/admin/roles" class="${active=='roles'?'active':''}">
            <i class="fa fa-shield-halved"></i> Vai trò
        </a>

        <a href="${pageContext.request.contextPath}/admin/reports" class="${active=='reports'?'active':''}">
            <i class="fa fa-chart-pie"></i> Báo cáo
        </a>

        <a href="${pageContext.request.contextPath}/admin/config" class="${active=='config'?'active':''}">
            <i class="fa fa-gear"></i> Cấu hình hệ thống
        </a>

        <a href="${pageContext.request.contextPath}/logout">
            <i class="fa fa-right-from-bracket"></i> Đăng xuất
        </a>
    </div>
</div>

<!-- Main content -->
<div class="main-content">
    <jsp:include page="${contentPage}" />
</div>

</body>
</html>
