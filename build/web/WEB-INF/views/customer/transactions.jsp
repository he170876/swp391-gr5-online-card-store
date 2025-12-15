<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử giao dịch - Online Card Store</title>
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
        <h2>Lịch sử giao dịch</h2>

        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/customer/transactions" 
               class="btn ${empty filterType ? 'btn-primary' : 'btn-outline-primary'}">Tất cả</a>
            <a href="${pageContext.request.contextPath}/customer/transactions?type=TOPUP" 
               class="btn ${filterType == 'TOPUP' ? 'btn-primary' : 'btn-outline-primary'}">Nạp tiền</a>
            <a href="${pageContext.request.contextPath}/customer/transactions?type=PURCHASE" 
               class="btn ${filterType == 'PURCHASE' ? 'btn-primary' : 'btn-outline-primary'}">Mua hàng</a>
            <a href="${pageContext.request.contextPath}/customer/transactions?type=REFUND" 
               class="btn ${filterType == 'REFUND' ? 'btn-primary' : 'btn-outline-primary'}">Hoàn tiền</a>
        </div>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Thời gian</th>
                    <th>Loại</th>
                    <th>Số tiền</th>
                    <th>Số dư sau</th>
                    <th>Trạng thái</th>
                    <th>Mã tham chiếu</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="transaction" items="${transactions}">
                    <tr>
                        <td>${transaction.createdAt}</td>
                        <td>
                            <c:choose>
                                <c:when test="${transaction.type == 'TOPUP'}">Nạp tiền</c:when>
                                <c:when test="${transaction.type == 'PURCHASE'}">Mua hàng</c:when>
                                <c:when test="${transaction.type == 'REFUND'}">Hoàn tiền</c:when>
                                <c:otherwise>${transaction.type}</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="${transaction.amount >= 0 ? 'text-success' : 'text-danger'}">
                            <fmt:formatNumber value="${transaction.amount}" type="number" /> VNĐ
                        </td>
                        <td><fmt:formatNumber value="${transaction.balance}" type="number" /> VNĐ</td>
                        <td>
                            <span class="badge ${transaction.status == 'SUCCESS' ? 'bg-success' : transaction.status == 'PENDING' ? 'bg-warning' : 'bg-danger'}">
                                ${transaction.status}
                            </span>
                        </td>
                        <td>${transaction.referenceCode}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty transactions}">
            <div class="alert alert-info">Chưa có giao dịch nào.</div>
        </c:if>

        <div class="mt-3">
            <a href="${pageContext.request.contextPath}/customer/wallet" class="btn btn-secondary">Quay lại ví</a>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


