<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/customer/includes/header.jsp">
    <jsp:param name="title" value="Online Card Store"/>
</jsp:include>

<jsp:include page="/customer/includes/navbar.jsp"/>

<div class="container my-4">
    <div class="p-5 mb-4 bg-light rounded-3 hero">
        <div class="container-fluid py-4">
            <h1 class="display-6 fw-bold">Mua mã thẻ siêu tốc</h1>
            <p class="col-md-8 fs-6 text-muted">
                Giao mã ngay • Thanh toán an toàn • Mã thẻ tức thì
            </p>
            <a href="${pageContext.request.contextPath}/customer/products"
               class="btn btn-primary btn-lg">
                Xem sản phẩm
            </a>
        </div>
    </div>

    <div class="d-flex align-items-center justify-content-between mb-3">
        <h4 class="mb-0">Sản phẩm nổi bật</h4>
        <a href="${pageContext.request.contextPath}/customer/products" class="text-decoration-none">Xem tất cả</a>
    </div>
    <div class="row g-4">
        <c:forEach var="product" items="${featuredProducts}">
            <div class="col-12 col-md-6 col-lg-4">
                <div class="card h-100 shadow-sm border-0">
                    <img src="<c:out value='${product.imageUrl}'/>" class="card-img-top" alt="${product.name}">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="text-muted mb-1">
                            ${product.providerName} • ${product.categoryName}
                        </p>
                        <h6 class="text-danger fw-bold">
                            <fmt:formatNumber value="${product.finalPrice}" type="currency"/>
                        </h6>
                        <div class="mt-auto">
                            <a href="${pageContext.request.contextPath}/customer/product/detail?id=${product.id}"
                               class="btn btn-primary w-100">
                                Mua ngay
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <div class="my-5">
        <h4 class="text-center mb-4">Vì sao chọn OCS?</h4>
        <div class="row g-4 text-center">
            <div class="col-12 col-md-4">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <i class="fa-solid fa-bolt fa-2x text-primary mb-3"></i>
                        <h6 class="fw-bold">Giao ngay</h6>
                        <p class="text-muted small mb-0">Nhận mã thẻ trong vài giây sau khi thanh toán.</p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-md-4">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <i class="fa-solid fa-shield-halved fa-2x text-success mb-3"></i>
                        <h6 class="fw-bold">Thanh toán an toàn</h6>
                        <p class="text-muted small mb-0">Ví điện tử bảo mật, giao dịch minh bạch.</p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-md-4">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <i class="fa-solid fa-headset fa-2x text-info mb-3"></i>
                        <h6 class="fw-bold">Hỗ trợ thân thiện</h6>
                        <p class="text-muted small mb-0">Luôn sẵn sàng hỗ trợ bạn 24/7.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/customer/includes/footer.jsp"/>

