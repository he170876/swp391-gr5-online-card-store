<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng - Online Card Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/home">Online Card Store</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/home">Trang chủ</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/orders">Đơn hàng</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <h2>Chi tiết đơn hàng #${order.id}</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty order}">
            <div class="row">
                <div class="col-md-8">
                    <div class="card mb-4">
                        <div class="card-body">
                            <h5 class="card-title">Thông tin đơn hàng</h5>
                            <table class="table">
                                <tr>
                                    <td>Mã đơn:</td>
                                    <td>#${order.id}</td>
                                </tr>
                                <tr>
                                    <td>Ngày đặt:</td>
                                    <td>${order.createdAt}</td>
                                </tr>
                                <tr>
                                    <td>Email nhận:</td>
                                    <td>${order.receiverEmail}</td>
                                </tr>
                                <tr>
                                    <td>Trạng thái:</td>
                                    <td>
                                        <span class="badge 
                                            ${order.status == 'PAID' || order.status == 'COMPLETED' ? 'bg-success' : 
                                              order.status == 'PENDING' ? 'bg-warning' : 
                                              order.status == 'CANCELED' ? 'bg-danger' : 'bg-secondary'}">
                                            ${order.status}
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <c:if test="${not empty product}">
                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="card-title">Sản phẩm</h5>
                                <p><strong>${product.name}</strong></p>
                                <p>${product.description}</p>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${not empty cardInfo}">
                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="card-title">Thông tin thẻ</h5>
                                <c:if test="${not empty cardInfo.serial}">
                                    <p><strong>Serial:</strong> ${cardInfo.serial}</p>
                                </c:if>
                                <c:if test="${not empty cardInfo.expiryDate}">
                                    <p><strong>Hạn sử dụng:</strong> ${cardInfo.expiryDate}</p>
                                </c:if>
                                <p><strong>Mã thẻ:</strong></p>
                                <div class="input-group mb-3">
                                    <input type="password" class="form-control" id="cardCode" 
                                           value="${cardInfo.code}" readonly>
                                    <button class="btn btn-outline-secondary" type="button" id="toggleCardCode">
                                        <i class="fas fa-eye"></i> Hiện
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Thông tin thanh toán</h5>
                            <table class="table">
                                <tr>
                                    <td>Giá gốc:</td>
                                    <td><fmt:formatNumber value="${order.originalPrice}" type="number" /> VNĐ</td>
                                </tr>
                                <tr>
                                    <td>Giảm giá:</td>
                                    <td>${order.discountPercent}%</td>
                                </tr>
                                <tr class="table-primary">
                                    <td><strong>Thành tiền:</strong></td>
                                    <td><strong><fmt:formatNumber value="${order.finalPrice}" type="number" /> VNĐ</strong></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-secondary">Quay lại danh sách đơn hàng</a>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            $('#toggleCardCode').click(function() {
                var input = $('#cardCode');
                var button = $(this);
                if (input.attr('type') === 'password') {
                    input.attr('type', 'text');
                    button.html('<i class="fas fa-eye-slash"></i> Ẩn');
                } else {
                    input.attr('type', 'password');
                    button.html('<i class="fas fa-eye"></i> Hiện');
                }
            });
        });
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


