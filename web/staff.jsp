<%-- 
    Document   : staff
    Created on : Dec 10, 2025, 2:34:34 AM
    Author     : hades
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - OCS Staff</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>

    <!-- DataTables -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">

    <!-- Select2 -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css" rel="stylesheet" />

    <!-- Custom Staff CSS -->
    <style>
        body {
            background: #f5f6fa;
            font-family: 'Inter', sans-serif;
        }

        .sidebar {
            width: 260px;
            height: 100vh;
            position: fixed;
            background: #1e3a5f;
            color: white;
            padding-top: 30px;
            overflow-y: auto;
        }

        .sidebar .logo {
            font-size: 22px;
            padding: 0 20px 20px;
            font-weight: bold;
            text-align: center;
            color: #fff;
        }

        .sidebar .menu a {
            display: block;
            padding: 14px 20px;
            color: #c7d4e3;
            text-decoration: none;
            font-size: 15px;
            transition: all 0.2s;
        }

        .sidebar .menu a:hover,
        .sidebar .menu a.active {
            background: #2d4a6f;
            color: #fff;
        }

        .sidebar .menu a i {
            margin-right: 10px;
            width: 20px;
            text-align: center;
        }

        .main-content {
            margin-left: 260px;
            padding: 30px;
            min-height: 100vh;
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
            background: #1e3a5f;
            border-color: #1e3a5f;
        }

        .btn-primary:hover {
            background: #163050;
            border-color: #163050;
        }

        .page-header {
            margin-bottom: 25px;
        }

        .page-header h4 {
            font-weight: 600;
            margin-bottom: 5px;
        }

        .page-header h6 {
            color: #888;
            font-weight: 400;
        }

        .badge-active {
            background: #28a745;
            color: white;
        }

        .badge-inactive {
            background: #dc3545;
            color: white;
        }

        .user-info {
            padding: 15px 20px;
            border-top: 1px solid rgba(255,255,255,0.1);
            margin-top: 20px;
        }

        .user-info .user-name {
            font-size: 14px;
            font-weight: 500;
        }

        .user-info .user-role {
            font-size: 12px;
            color: #a0b4c8;
        }

        .action-btn {
            padding: 5px 10px;
            font-size: 13px;
        }

        /* Filter card styling */
        .filter-card {
            background: #fff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }
    </style>
</head>

<body>

<!-- Sidebar -->
<div class="sidebar">
    <div class="logo">
        <i class="fa fa-store"></i> OCS Staff
    </div>

    <div class="menu">
        <a href="${pageContext.request.contextPath}/staff/dashboard" class="${active=='dashboard'?'active':''}">
            <i class="fa fa-chart-line"></i> Bảng điều khiển
        </a>

        <a href="${pageContext.request.contextPath}/staff/product" class="${active=='product'?'active':''}">
            <i class="fa fa-box"></i> Sản phẩm
        </a>

        <a href="${pageContext.request.contextPath}/staff/category" class="${active=='category'?'active':''}">
            <i class="fa fa-tags"></i> Danh mục
        </a>

        <a href="${pageContext.request.contextPath}/staff/provider" class="${active=='provider'?'active':''}">
            <i class="fa fa-truck"></i> Nhà cung cấp
        </a>

        <a href="${pageContext.request.contextPath}/staff/order" class="${active=='order'?'active':''}">
            <i class="fa fa-shopping-cart"></i> Đơn hàng
        </a>

        <a href="${pageContext.request.contextPath}/staff/card" class="${active=='card'?'active':''}">
            <i class="fa fa-credit-card"></i> Thẻ
        </a>

        <a href="${pageContext.request.contextPath}/logout">
            <i class="fa fa-right-from-bracket"></i> Đăng xuất
        </a>
    </div>

    <c:if test="${not empty sessionScope.user}">
        <div class="user-info">
            <div class="user-name">${sessionScope.user.fullName}</div>
            <div class="user-role">Nhân viên</div>
        </div>
    </c:if>
</div>

<!-- Main content -->
<div class="main-content">
    <!-- Alert messages -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fa fa-check-circle me-2"></i>${sessionScope.successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa fa-exclamation-circle me-2"></i>${sessionScope.errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fa fa-exclamation-circle me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Content page include -->
    <c:if test="${not empty contentPage}">
        <jsp:include page="${contentPage}" />
    </c:if>

    <c:if test="${empty contentPage}">
        <div class="page-header">
            <h4>Chào mừng</h4>
            <h6>Trang quản lý nhân viên</h6>
        </div>
        <div class="card">
            <div class="card-body">
                <p>Vui lòng chọn một chức năng từ menu bên trái.</p>
            </div>
        </div>
    </c:if>
</div>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- DataTables -->
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>

<!-- Select2 -->
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

<script>
    $(document).ready(function() {
        // Initialize Select2
        $('.select2').select2({
            theme: 'bootstrap-5'
        });
    });
</script>

</body>
</html>
