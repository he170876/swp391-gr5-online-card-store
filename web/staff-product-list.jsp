<%-- 
    Document   : staff-product-list
    Description: Product list page for staff
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Danh sách sản phẩm</h4>
        <h6>Quản lý tất cả sản phẩm trong hệ thống</h6>
    </div>
    <a href="${pageContext.request.contextPath}/staff/product/add" class="btn btn-primary">
        <i class="fa fa-plus me-2"></i>Thêm sản phẩm
    </a>
</div>

<!-- Filter Card -->
<div class="filter-card">
    <form action="${pageContext.request.contextPath}/staff/product" method="get">
        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label">Tìm kiếm</label>
                <input type="text" class="form-control" name="keyword" placeholder="Nhập tên sản phẩm..." 
                       value="${searchDTO.keyword}">
            </div>
            <div class="col-md-2">
                <label class="form-label">Danh mục</label>
                <select class="form-select select2" name="categoryId">
                    <option value="">Tất cả</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${searchDTO.categoryId == cat.id ? 'selected' : ''}>
                            ${cat.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label">Nhà cung cấp</label>
                <select class="form-select select2" name="providerId">
                    <option value="">Tất cả</option>
                    <c:forEach var="prov" items="${providers}">
                        <option value="${prov.id}" ${searchDTO.providerId == prov.id ? 'selected' : ''}>
                            ${prov.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label">Trạng thái</label>
                <select class="form-select" name="status">
                    <option value="">Tất cả</option>
                    <option value="ACTIVE" ${searchDTO.status == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                    <option value="INACTIVE" ${searchDTO.status == 'INACTIVE' ? 'selected' : ''}>Ngừng hoạt động</option>
                </select>
            </div>
            <div class="col-md-3 d-flex align-items-end">
                <button type="submit" class="btn btn-primary me-2">
                    <i class="fa fa-search me-1"></i> Tìm kiếm
                </button>
                <a href="${pageContext.request.contextPath}/staff/product" class="btn btn-outline-secondary">
                    <i class="fa fa-refresh me-1"></i> Đặt lại
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Product Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover" id="productTable">
                <thead>
                    <tr>
                        <th width="5%">#</th>
                        <th width="10%">Ảnh</th>
                        <th width="20%">Tên sản phẩm</th>
                        <th width="12%">Danh mục</th>
                        <th width="12%">Nhà cung cấp</th>
                        <th width="12%">Giá bán</th>
                        <th width="8%">Số lượng</th>
                        <th width="10%">Trạng thái</th>
                        <th width="11%">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty products}">
                            <tr>
                                <td colspan="9" class="text-center py-4">
                                    <i class="fa fa-inbox fa-3x text-muted mb-3 d-block"></i>
                                    Không tìm thấy sản phẩm nào
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="product" items="${products}" varStatus="loop">
                                <tr>
                                    <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty product.imageUrl}">
                                                <img src="${product.imageUrl}" alt="${product.name}" 
                                                     style="width: 50px; height: 50px; object-fit: cover; border-radius: 5px;">
                                            </c:when>
                                            <c:otherwise>
                                                <div style="width: 50px; height: 50px; background: #e9ecef; border-radius: 5px; display: flex; align-items: center; justify-content: center;">
                                                    <i class="fa fa-image text-muted"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <strong>${product.name}</strong>
                                        <c:if test="${product.discountPercent > 0}">
                                            <span class="badge bg-danger ms-1">-${product.discountPercent}%</span>
                                        </c:if>
                                    </td>
                                    <td>${product.categoryName}</td>
                                    <td>${product.providerName}</td>
                                    <td>
                                        <fmt:formatNumber value="${product.sellPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                        <c:if test="${product.discountPercent > 0}">
                                            <br>
                                            <small class="text-success">
                                                <fmt:formatNumber value="${product.finalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                            </small>
                                        </c:if>
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
                                                <span class="badge bg-danger">Ngừng hoạt động</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/staff/product/detail?id=${product.id}" 
                                               class="btn btn-sm btn-outline-info action-btn" title="Xem chi tiết">
                                                <i class="fa fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/staff/product/edit?id=${product.id}" 
                                               class="btn btn-sm btn-outline-primary action-btn" title="Sửa">
                                                <i class="fa fa-edit"></i>
                                            </a>
                                            <button type="button" class="btn btn-sm btn-outline-danger action-btn" 
                                                    title="Xóa" onclick="confirmDelete(${product.id}, '${product.name}')">
                                                <i class="fa fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Page navigation" class="mt-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted">
                        Hiển thị ${(currentPage - 1) * 10 + 1} - ${currentPage * 10 > totalCount ? totalCount : currentPage * 10} / ${totalCount} sản phẩm
                    </div>
                    <ul class="pagination mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/product?page=${currentPage - 1}&keyword=${searchDTO.keyword}&categoryId=${searchDTO.categoryId}&providerId=${searchDTO.providerId}&status=${searchDTO.status}">
                                <i class="fa fa-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:if test="${i <= 5 || i > totalPages - 2 || (i >= currentPage - 1 && i <= currentPage + 1)}">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/staff/product?page=${i}&keyword=${searchDTO.keyword}&categoryId=${searchDTO.categoryId}&providerId=${searchDTO.providerId}&status=${searchDTO.status}">${i}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/product?page=${currentPage + 1}&keyword=${searchDTO.keyword}&categoryId=${searchDTO.categoryId}&providerId=${searchDTO.providerId}&status=${searchDTO.status}">
                                <i class="fa fa-chevron-right"></i>
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>
        </c:if>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">Xác nhận xóa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Bạn có chắc chắn muốn xóa sản phẩm <strong id="deleteProductName"></strong>?
                <p class="text-muted mt-2 mb-0"><small>Sản phẩm sẽ được chuyển sang trạng thái không hoạt động.</small></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form id="deleteForm" action="${pageContext.request.contextPath}/staff/product/delete" method="post" style="display: inline;">
                    <input type="hidden" name="id" id="deleteProductId">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(id, name) {
    document.getElementById('deleteProductId').value = id;
    document.getElementById('deleteProductName').textContent = name;
    var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    deleteModal.show();
}
</script>
