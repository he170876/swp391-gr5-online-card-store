<%-- 
    Document   : homepage
    Created on : Dec 10, 2025, 1:48:50 AM
    Author     : hades
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Home - Online Card Store</title>
    </head>
    <body>

<jsp:include page="/guest/includes/header.jsp">
    <jsp:param name="title" value="Online Card Store"/>
</jsp:include>

<div class="container my-4">
    <div class="p-5 mb-4 bg-light rounded-3 hero">
        <div class="container-fluid py-4">
            <h1 class="display-6 fw-bold">Mua mã thẻ nhanh chóng & an toàn</h1>
            <p class="col-md-8 fs-6 text-muted">
                Giao mã tức thì – Thanh toán tiện lợi – Hỗ trợ 24/7
            </p>
            <div class="d-flex flex-wrap gap-2">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">Đăng nhập để mua</a>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary btn-lg">Xem sản phẩm</a>
            </div>
        </div>
    </div>

    <div class="d-flex align-items-center justify-content-between mb-3">
        <h4 class="mb-0">Sản phẩm nổi bật</h4>
        <a href="${pageContext.request.contextPath}/products" class="text-decoration-none">Xem tất cả</a>
    </div>
    <div class="row g-4">
        <c:forEach var="product" items="${featuredProducts}">
            <div class="col-12 col-md-6 col-lg-4">
                <div class="card h-100 shadow-sm border-0">
                    <img src="<c:out value='${product.imageUrl}'/>" class="card-img-top" alt="${product.name}">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="text-muted mb-1">${product.providerName}</p>
                        <h6 class="text-danger fw-bold"><fmt:formatNumber value="${product.finalPrice}" type="currency"/></h6>
                        <div class="mt-auto">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary w-100">Đăng nhập để mua</a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/guest/includes/footer.jsp"/>

    </body>
</html>

