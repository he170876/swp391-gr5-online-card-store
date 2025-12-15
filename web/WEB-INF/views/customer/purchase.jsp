<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác nhận mua hàng - Online Card Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/home">Online Card Store</a>
        </div>
    </nav>

    <div class="container mt-4">
        <h2>Xác nhận mua hàng</h2>

        <c:if test="${not empty product}">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text">${product.description}</p>
                    <table class="table">
                        <tr>
                            <td>Giá gốc:</td>
                            <td><fmt:formatNumber value="${originalPrice}" type="number" /> VNĐ</td>
                        </tr>
                        <tr>
                            <td>Giảm giá:</td>
                            <td>${discountPercent}%</td>
                        </tr>
                        <tr class="table-primary">
                            <td><strong>Thành tiền:</strong></td>
                            <td><strong><fmt:formatNumber value="${finalPrice}" type="number" /> VNĐ</strong></td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="card">
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/customer/purchase">
                        <input type="hidden" name="productId" value="${product.id}">
                        <div class="mb-3">
                            <label for="receiverEmail" class="form-label">Email nhận thẻ (mã thẻ sẽ được gửi đến email này)</label>
                            <input type="email" class="form-control" id="receiverEmail" name="receiverEmail" 
                                   value="${receiverEmail}" required>
                        </div>
                        <button type="submit" class="btn btn-primary btn-lg">Xác nhận mua</button>
                        <a href="${pageContext.request.contextPath}/customer/product-detail?id=${product.id}" 
                           class="btn btn-secondary">Hủy</a>
                    </form>
                </div>
            </div>
        </c:if>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>


