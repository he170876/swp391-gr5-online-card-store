<%-- 
    Document   : staff-category-detail
    Description: Category detail page for staff
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Chi tiết danh mục</h4>
        <h6>Xem thông tin chi tiết danh mục</h6>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/staff/category/edit?id=${category.id}" class="btn btn-primary me-2">
            <i class="fa fa-edit me-2"></i>Sửa
        </a>
        <a href="${pageContext.request.contextPath}/staff/category" class="btn btn-outline-secondary">
            <i class="fa fa-arrow-left me-2"></i>Quay lại
        </a>
    </div>
</div>

<div class="row">
    <!-- Category Information -->
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <div style="width: 100px; height: 100px; background: #1e3a5f; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px;">
                    <i class="fa fa-folder fa-3x text-white"></i>
                </div>
                
                <h4 class="mb-1">${category.name}</h4>
                <p class="text-muted mb-3">Mã DM: #${category.id}</p>
                
                <c:choose>
                    <c:when test="${category.status == 'ACTIVE'}">
                        <span class="badge bg-success fs-6">Hoạt động</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-danger fs-6">Ngừng hoạt động</span>
                    </c:otherwise>
                </c:choose>
                
                <hr class="my-3">
                
                <div class="text-start">
                    <h6 class="text-muted mb-2">Mô tả</h6>
                    <c:choose>
                        <c:when test="${not empty category.description}">
                            <p class="mb-0">${category.description}</p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0"><em>Chưa cập nhật</em></p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Statistics Card -->
        <div class="card mt-3">
            <div class="card-header">
                <h5 class="mb-0"><i class="fa fa-chart-bar me-2"></i>Thống kê</h5>
            </div>
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span>Tổng sản phẩm</span>
                    <span class="badge bg-primary fs-6">${category.productCount}</span>
                </div>
                <div class="d-flex justify-content-between align-items-center">
                    <span>Sản phẩm hoạt động</span>
                    <span class="badge bg-success">${products.size()}</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Products List -->
    <div class="col-md-8">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="fa fa-box me-2"></i>Danh sách sản phẩm</h5>
                <span class="badge bg-primary">${products.size()} sản phẩm</span>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty products}">
                        <div class="text-center py-4">
                            <i class="fa fa-inbox fa-3x text-muted mb-3 d-block"></i>
                            <p class="text-muted mb-0">Danh mục này chưa có sản phẩm nào</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Tên sản phẩm</th>
                                        <th>Nhà cung cấp</th>
                                        <th>Giá bán</th>
                                        <th>Số lượng</th>
                                        <th>Trạng thái</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${products}" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td>
                                                <strong>${product.name}</strong>
                                                <c:if test="${product.discountPercent > 0}">
                                                    <span class="badge bg-danger ms-1">-${product.discountPercent}%</span>
                                                </c:if>
                                            </td>
                                            <td>${product.providerName}</td>
                                            <td>
                                                <fmt:formatNumber value="${product.sellPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                            </td>
                                            <td>
                                                <span class="badge ${product.quantity > 0 ? 'bg-success' : 'bg-secondary'}">
                                                    ${product.quantity}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.status == 'ACTIVE'}">
                                                        <span class="badge bg-success">Hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Ngừng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/staff/product/detail?id=${product.id}" 
                                                   class="btn btn-sm btn-outline-info" title="Xem chi tiết">
                                                    <i class="fa fa-eye"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
