<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chủ - Online Card Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/home">Online Card Store</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/wallet">Ví của tôi</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/orders">Đơn hàng</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/auth/profile">Tài khoản</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-md-3">
                <h5>Danh mục</h5>
                <ul class="list-group">
                    <li class="list-group-item"><a href="${pageContext.request.contextPath}/customer/home">Tất cả</a></li>
                    <c:forEach var="category" items="${categories}">
                        <li class="list-group-item">
                            <a href="${pageContext.request.contextPath}/customer/home?categoryId=${category.id}">${category.name}</a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
            <div class="col-md-9">
                <form method="get" action="${pageContext.request.contextPath}/customer/home" class="mb-4">
                    <div class="input-group">
                        <input type="text" name="keyword" class="form-control" placeholder="Tìm kiếm sản phẩm..." value="${keyword}">
                        <button class="btn btn-primary" type="submit">Tìm kiếm</button>
                    </div>
                </form>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="row">
                    <c:forEach var="product" items="${products}">
                        <div class="col-md-4 mb-4">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">${product.name}</h5>
                                    <p class="card-text">${product.description}</p>
                                    <p class="text-danger fw-bold">
                                        <c:set var="finalPrice" value="${product.sellPrice * (1 - product.discountPercent / 100)}" />
                                        ${finalPrice} VNĐ
                                        <c:if test="${product.discountPercent > 0}">
                                            <span class="text-muted text-decoration-line-through ms-2">${product.sellPrice} VNĐ</span>
                                            <span class="badge bg-success">-${product.discountPercent}%</span>
                                        </c:if>
                                    </p>
                                    <p class="text-muted">Còn lại: ${product.quantity} sản phẩm</p>
                                    <a href="${pageContext.request.contextPath}/customer/product-detail?id=${product.id}" class="btn btn-primary">Xem chi tiết</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <c:if test="${empty products}">
                    <div class="alert alert-info">Không tìm thấy sản phẩm nào.</div>
                </c:if>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


