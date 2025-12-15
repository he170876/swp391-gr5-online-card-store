<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm - Online Card Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/home">Online Card Store</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/home">Trang chủ</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/wallet">Ví của tôi</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/orders">Đơn hàng</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty product}">
            <div class="row">
                <div class="col-md-8">
                    <h2>${product.name}</h2>
                    <p class="text-muted">${product.description}</p>
                    
                    <div class="mb-3">
                        <h4 class="text-danger">
                            <c:set var="finalPrice" value="${product.sellPrice * (1 - product.discountPercent / 100)}" />
                            <fmt:formatNumber value="${finalPrice}" type="number" /> VNĐ
                            <c:if test="${product.discountPercent > 0}">
                                <span class="text-muted text-decoration-line-through ms-2">
                                    <fmt:formatNumber value="${product.sellPrice}" type="number" /> VNĐ
                                </span>
                                <span class="badge bg-success">Giảm ${product.discountPercent}%</span>
                            </c:if>
                        </h4>
                    </div>

                    <div class="mb-3">
                        <p><strong>Số lượng còn lại:</strong> ${availableStock} sản phẩm</p>
                    </div>

                    <c:if test="${availableStock > 0}">
                        <a href="${pageContext.request.contextPath}/customer/purchase?productId=${product.id}" 
                           class="btn btn-primary btn-lg">Mua ngay</a>
                    </c:if>
                    <c:if test="${availableStock <= 0}">
                        <button class="btn btn-secondary btn-lg" disabled>Hết hàng</button>
                    </c:if>
                </div>
            </div>
        </c:if>

        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/customer/home" class="btn btn-secondary">Quay lại</a>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


