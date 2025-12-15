<%-- 
    Document   : staff-product-detail
    Description: Product detail page for staff
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Chi tiết sản phẩm</h4>
        <h6>Xem thông tin chi tiết sản phẩm</h6>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/staff/product/edit?id=${product.id}" class="btn btn-primary me-2">
            <i class="fa fa-edit me-2"></i>Sửa
        </a>
        <a href="${pageContext.request.contextPath}/staff/product" class="btn btn-outline-secondary">
            <i class="fa fa-arrow-left me-2"></i>Quay lại
        </a>
    </div>
</div>

<div class="row">
    <!-- Product Image -->
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <c:choose>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="${product.imageUrl}" alt="${product.name}" 
                             style="max-width: 100%; max-height: 300px; object-fit: contain; border-radius: 10px;">
                    </c:when>
                    <c:otherwise>
                        <div style="height: 250px; background: #f0f0f0; border-radius: 10px; display: flex; align-items: center; justify-content: center;">
                            <i class="fa fa-image fa-5x text-muted"></i>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <h4 class="mt-3">${product.name}</h4>
                <p class="text-muted">${product.description}</p>
                
                <c:choose>
                    <c:when test="${product.status == 'ACTIVE'}">
                        <span class="badge bg-success fs-6">Hoạt động</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-danger fs-6">Ngừng hoạt động</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Product Information -->
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fa fa-info-circle me-2"></i>Thông tin sản phẩm</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="text-muted small">Mã sản phẩm</label>
                        <p class="mb-0 fw-bold">#${product.id}</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="text-muted small">Tên sản phẩm</label>
                        <p class="mb-0 fw-bold">${product.name}</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="text-muted small">Danh mục</label>
                        <p class="mb-0">
                            <span class="badge bg-primary">${product.categoryName}</span>
                        </p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="text-muted small">Nhà cung cấp</label>
                        <p class="mb-0">
                            <span class="badge bg-info">${product.providerName}</span>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Pricing Card -->
        <div class="card mt-3">
            <div class="card-header">
                <h5 class="mb-0"><i class="fa fa-tags me-2"></i>Thông tin giá</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label class="text-muted small">Giá gốc</label>
                        <p class="mb-0 fw-bold">
                            <fmt:formatNumber value="${product.costPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                        </p>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="text-muted small">Giá bán</label>
                        <p class="mb-0 fw-bold text-primary">
                            <fmt:formatNumber value="${product.sellPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                        </p>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="text-muted small">Giảm giá</label>
                        <p class="mb-0">
                            <c:choose>
                                <c:when test="${product.discountPercent > 0}">
                                    <span class="badge bg-danger">${product.discountPercent}%</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Không giảm giá</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <c:if test="${product.discountPercent > 0}">
                        <div class="col-md-4 mb-3">
                            <label class="text-muted small">Giá sau giảm</label>
                            <p class="mb-0 fw-bold text-success">
                                <fmt:formatNumber value="${product.finalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                            </p>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="text-muted small">Tiết kiệm</label>
                            <p class="mb-0 text-danger">
                                <fmt:formatNumber value="${product.sellPrice - product.finalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                            </p>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Inventory Card -->
        <div class="card mt-3">
            <div class="card-header">
                <h5 class="mb-0"><i class="fa fa-warehouse me-2"></i>Thông tin tồn kho</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="text-muted small">Số lượng tồn kho</label>
                        <p class="mb-0">
                            <span class="badge ${product.quantity > 0 ? 'bg-success' : 'bg-secondary'} fs-5">
                                ${product.quantity}
                            </span>
                        </p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="text-muted small">Trạng thái kho</label>
                        <p class="mb-0">
                            <c:choose>
                                <c:when test="${product.quantity > 10}">
                                    <span class="text-success"><i class="fa fa-check-circle me-1"></i>Còn hàng</span>
                                </c:when>
                                <c:when test="${product.quantity > 0}">
                                    <span class="text-warning"><i class="fa fa-exclamation-triangle me-1"></i>Sắp hết hàng</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-danger"><i class="fa fa-times-circle me-1"></i>Hết hàng</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
