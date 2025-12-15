<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/customer/home">
            <img src="${pageContext.request.contextPath}/img/smalllogo.jpg" alt="OCS" width="40" class="me-2 rounded">
            <span>Online Card Store</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#customerNav" aria-controls="customerNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="customerNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/customer/home">Trang chủ</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/customer/products">Sản phẩm</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/customer/wallet">Ví tiền</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/customer/orders">Đơn hàng</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/customer/profile">Hồ sơ</a></li>
            </ul>
            <div class="d-flex align-items-center gap-3">
                <c:if test="${not empty sessionScope.user}">
                    <div class="text-end">
                        <div class="small text-muted">Số dư ví</div>
                        <div class="fw-semibold text-success">
                            <fmt:formatNumber value="${sessionScope.user.walletBalance}" type="currency"/>
                        </div>
                    </div>
                </c:if>
                <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>
    </div>
</nav>

