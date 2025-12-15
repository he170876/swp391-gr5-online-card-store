<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/customer/includes/header.jsp">
    <jsp:param name="title" value="${product.name}"/>
</jsp:include>

<jsp:include page="/customer/includes/navbar.jsp"/>

<div class="container my-4">
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">${param.error}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="row g-4">
        <div class="col-12 col-lg-6">
            <div class="card shadow-sm border-0 h-100">
                <img src="<c:out value='${product.imageUrl}'/>" class="card-img-top" alt="${product.name}">
            </div>
        </div>
        <div class="col-12 col-lg-6">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-body d-flex flex-column">
                    <p class="text-muted mb-1">${product.providerName} • ${product.categoryName}</p>
                    <h3 class="fw-bold">${product.name}</h3>
                    <p class="text-muted">${product.description}</p>
                    <h4 class="text-danger fw-bold mb-3">
                        <fmt:formatNumber value="${product.finalPrice}" type="currency"/>
                    </h4>
                    <div class="mb-2">
                        <span class="badge bg-light text-dark">Giảm ${product.discountPercent}%</span>
                        <span class="badge ${availableCount > 0 ? 'bg-success' : 'bg-secondary'}">
                            <c:choose>
                                <c:when test="${availableCount > 0}">${availableCount} mã có sẵn</c:when>
                                <c:otherwise>Hết hàng</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="mb-2">
                        <small class="text-muted">Số dư ví:</small>
                        <div class="fw-semibold text-success">
                            <fmt:formatNumber value="${walletBalance}" type="currency"/>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/customer/purchase" method="post" class="mt-auto">
                        <input type="hidden" name="productId" value="${product.id}">
                        <div class="mb-3">
                            <label class="form-label">Nhận mã qua email</label>
                            <input type="email" class="form-control" name="receiverEmail" value="${sessionScope.user.email}" required>
                        </div>
                        <c:set var="cannotBuy" value="${availableCount <= 0 || walletBalance lt product.finalPrice}"/>
                        <button type="submit" class="btn btn-primary w-100" ${cannotBuy ? 'disabled' : ''}>
                            <c:choose>
                                <c:when test="${availableCount <= 0}">Hết hàng</c:when>
                                <c:when test="${walletBalance lt product.finalPrice}">Số dư không đủ</c:when>
                                <c:otherwise>Mua ngay</c:otherwise>
                            </c:choose>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/customer/includes/footer.jsp"/>

