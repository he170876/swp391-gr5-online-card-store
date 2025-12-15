<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn hàng của tôi - Online Card Store</title>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h2>Đơn hàng của tôi</h2>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Mã đơn</th>
                    <th>Ngày đặt</th>
                    <th>Email nhận</th>
                    <th>Giá</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>#${order.id}</td>
                        <td>${order.createdAt}</td>
                        <td>${order.receiverEmail}</td>
                        <td><fmt:formatNumber value="${order.finalPrice}" type="number" /> VNĐ</td>
                        <td>
                            <span class="badge 
                                ${order.status == 'PAID' || order.status == 'COMPLETED' ? 'bg-success' : 
                                  order.status == 'PENDING' ? 'bg-warning' : 
                                  order.status == 'CANCELED' ? 'bg-danger' : 'bg-secondary'}">
                                ${order.status}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/customer/order-detail?id=${order.id}" 
                               class="btn btn-sm btn-primary">Xem chi tiết</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty orders}">
            <div class="alert alert-info">Bạn chưa có đơn hàng nào.</div>
        </c:if>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


