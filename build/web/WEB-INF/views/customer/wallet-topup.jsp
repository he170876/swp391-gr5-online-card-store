<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nạp tiền - Online Card Store</title>
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
        <h2>Nạp tiền vào ví</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <c:if test="${not empty transaction}">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Thông tin thanh toán</h5>
                    <p><strong>Mã tham chiếu:</strong> ${transaction.referenceCode}</p>
                    <p><strong>Số tiền:</strong> <fmt:formatNumber value="${transaction.amount}" type="number" /> VNĐ</p>
                    <p><strong>Trạng thái:</strong> 
                        <span class="badge ${transaction.status == 'SUCCESS' ? 'bg-success' : 'bg-warning'}">${transaction.status}</span>
                    </p>
                    <c:if test="${not empty transaction.qrUrl}">
                        <p><strong>QR Code:</strong></p>
                        <img src="${transaction.qrUrl}" alt="QR Code" class="img-fluid" style="max-width: 300px;">
                    </c:if>
                    <c:if test="${transaction.status == 'PENDING'}">
                        <form method="post" action="${pageContext.request.contextPath}/customer/wallet-topup" class="mt-3">
                            <input type="hidden" name="action" value="confirm">
                            <input type="hidden" name="referenceCode" value="${transaction.referenceCode}">
                            <button type="submit" class="btn btn-success">Tôi đã thanh toán</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:if>

        <c:if test="${empty transaction}">
            <div class="card">
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/customer/wallet-topup">
                        <input type="hidden" name="action" value="create">
                        <div class="mb-3">
                            <label for="amount" class="form-label">Số tiền nạp (VNĐ)</label>
                            <input type="number" class="form-control" id="amount" name="amount" 
                                   min="10000" max="5000000" step="1000" required>
                            <small class="form-text text-muted">Số tiền tối thiểu: 10,000 VNĐ. Tối đa: 5,000,000 VNĐ</small>
                        </div>
                        <button type="submit" class="btn btn-primary">Tạo yêu cầu nạp tiền</button>
                    </form>
                </div>
            </div>
        </c:if>

        <div class="mt-3">
            <a href="${pageContext.request.contextPath}/customer/wallet" class="btn btn-secondary">Quay lại ví</a>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


