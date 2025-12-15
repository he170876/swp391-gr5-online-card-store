<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/customer/includes/header.jsp">
    <jsp:param name="title" value="Browse Products"/>
</jsp:include>

<jsp:include page="/customer/includes/navbar.jsp"/>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0">Sản phẩm</h4>
    </div>

    <form method="get" class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-12 col-lg-4">
                    <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="Tìm kiếm sản phẩm...">
                </div>
                <div class="col-6 col-lg-2">
                    <select name="categoryId" class="form-select">
                        <option value="">Danh mục</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.id}" ${c.id == categoryId ? 'selected' : ''}>${c.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-6 col-lg-2">
                    <select name="providerId" class="form-select">
                        <option value="">Nhà cung cấp</option>
                        <c:forEach var="p" items="${providers}">
                            <option value="${p.id}" ${p.id == providerId ? 'selected' : ''}>${p.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-6 col-lg-2">
                    <select name="priceRange" class="form-select">
                        <option value="">Khoảng giá</option>
                        <option value="UNDER_50" ${priceRange == 'UNDER_50' ? 'selected' : ''}>Dưới 50.000đ</option>
                        <option value="50_100" ${priceRange == '50_100' ? 'selected' : ''}>50.000đ – 100.000đ</option>
                        <option value="100_500" ${priceRange == '100_500' ? 'selected' : ''}>100.000đ – 500.000đ</option>
                        <option value="OVER_500" ${priceRange == 'OVER_500' ? 'selected' : ''}>Trên 500.000đ</option>
                    </select>
                </div>
                <div class="col-6 col-lg-2">
                    <select name="stockStatus" class="form-select">
                        <option value="">Tình trạng hàng</option>
                        <option value="IN" ${stockStatus == 'IN' ? 'selected' : ''}>Còn hàng</option>
                        <option value="OUT" ${stockStatus == 'OUT' ? 'selected' : ''}>Hết hàng</option>
                    </select>
                </div>
                <div class="col-6 col-lg-2 d-flex gap-2">
                    <button type="submit" class="btn btn-primary w-100">Lọc</button>
                    <a class="btn btn-outline-secondary w-100" href="${pageContext.request.contextPath}/customer/products">Xóa lọc</a>
                </div>
            </div>
        </div>
    </form>

    <c:if test="${empty products}">
        <div class="alert alert-light border">
            Không có dữ liệu phù hợp. Vui lòng thử lại với bộ lọc khác.
        </div>
    </c:if>

    <div class="row g-4">
        <c:forEach var="product" items="${products}">
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
                               class="btn btn-primary w-100 ${product.quantity <= 0 ? 'disabled' : ''}">
                                <c:choose>
                                    <c:when test="${product.quantity <= 0}">Hết hàng</c:when>
                                    <c:otherwise>Mua ngay</c:otherwise>
                                </c:choose>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <div class="d-flex justify-content-center align-items-center gap-2 mt-4">
        <c:set var="prevPage" value="${currentPage - 1}"/>
        <c:set var="nextPage" value="${currentPage + 1}"/>
        <a class="btn btn-outline-secondary ${currentPage == 1 ? 'disabled' : ''}"
           href="${pageContext.request.contextPath}/customer/products?page=${prevPage}&keyword=${keyword}&categoryId=${categoryId}&providerId=${providerId}&priceRange=${priceRange}&stockStatus=${stockStatus}">
            Trước
        </a>
        <span>Trang ${currentPage} / ${totalPages}</span>
        <a class="btn btn-outline-secondary ${currentPage == totalPages ? 'disabled' : ''}"
           href="${pageContext.request.contextPath}/customer/products?page=${nextPage}&keyword=${keyword}&categoryId=${categoryId}&providerId=${providerId}&priceRange=${priceRange}&stockStatus=${stockStatus}">
            Sau
        </a>
    </div>
</div>

<jsp:include page="/customer/includes/footer.jsp"/>

