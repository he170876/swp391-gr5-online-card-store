<%-- 
    Document   : staff-category-list
    Description: Category list page for staff
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Danh sách danh mục</h4>
        <h6>Quản lý tất cả danh mục trong hệ thống</h6>
    </div>
    <a href="${pageContext.request.contextPath}/staff/category/add" class="btn btn-primary">
        <i class="fa fa-plus me-2"></i>Thêm danh mục
    </a>
</div>

<!-- Filter Card -->
<div class="filter-card">
    <form action="${pageContext.request.contextPath}/staff/category" method="get">
        <div class="row g-3">
            <div class="col-md-4">
                <label class="form-label">Tìm kiếm</label>
                <input type="text" class="form-control" name="keyword" placeholder="Nhập tên hoặc mô tả..." 
                       value="${searchDTO.keyword}">
            </div>
            <div class="col-md-3">
                <label class="form-label">Trạng thái</label>
                <select class="form-select" name="status">
                    <option value="">Tất cả</option>
                    <option value="ACTIVE" ${searchDTO.status == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                    <option value="INACTIVE" ${searchDTO.status == 'INACTIVE' ? 'selected' : ''}>Ngừng hoạt động</option>
                </select>
            </div>
            <div class="col-md-5 d-flex align-items-end">
                <button type="submit" class="btn btn-primary me-2">
                    <i class="fa fa-search me-1"></i> Tìm kiếm
                </button>
                <a href="${pageContext.request.contextPath}/staff/category" class="btn btn-outline-secondary">
                    <i class="fa fa-refresh me-1"></i> Đặt lại
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Category Table -->
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover" id="categoryTable">
                <thead>
                    <tr>
                        <th width="5%">#</th>
                        <th width="25%">Tên danh mục</th>
                        <th width="35%">Mô tả</th>
                        <th width="12%">Số sản phẩm</th>
                        <th width="10%">Trạng thái</th>
                        <th width="13%">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty categories}">
                            <tr>
                                <td colspan="6" class="text-center py-4">
                                    <i class="fa fa-inbox fa-3x text-muted mb-3 d-block"></i>
                                    Không tìm thấy danh mục nào
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="category" items="${categories}" varStatus="loop">
                                <tr>
                                    <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                                    <td>
                                        <strong>${category.name}</strong>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty category.description}">
                                                ${category.description}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa cập nhật</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge ${category.productCount > 0 ? 'bg-primary' : 'bg-secondary'}">
                                            ${category.productCount} sản phẩm
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${category.status == 'ACTIVE'}">
                                                <span class="badge bg-success">Hoạt động</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Ngừng hoạt động</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/staff/category/detail?id=${category.id}" 
                                               class="btn btn-sm btn-outline-info action-btn" title="Xem chi tiết">
                                                <i class="fa fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/staff/category/edit?id=${category.id}" 
                                               class="btn btn-sm btn-outline-primary action-btn" title="Sửa">
                                                <i class="fa fa-edit"></i>
                                            </a>
                                            <button type="button" class="btn btn-sm btn-outline-danger action-btn" 
                                                    title="Xóa" onclick="confirmDelete(${category.id}, '${category.name}', ${category.productCount})">
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
                        Hiển thị ${(currentPage - 1) * 10 + 1} - ${currentPage * 10 > totalCount ? totalCount : currentPage * 10} / ${totalCount} danh mục
                    </div>
                    <ul class="pagination mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/category?page=${currentPage - 1}&keyword=${searchDTO.keyword}&status=${searchDTO.status}">
                                <i class="fa fa-chevron-left"></i>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:if test="${i <= 5 || i > totalPages - 2 || (i >= currentPage - 1 && i <= currentPage + 1)}">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/staff/category?page=${i}&keyword=${searchDTO.keyword}&status=${searchDTO.status}">${i}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/staff/category?page=${currentPage + 1}&keyword=${searchDTO.keyword}&status=${searchDTO.status}">
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
                <p>Bạn có chắc chắn muốn xóa danh mục <strong id="deleteCategoryName"></strong>?</p>
                <p class="text-muted mb-0"><small>Danh mục sẽ được chuyển sang trạng thái không hoạt động.</small></p>
                <div id="hasProductsWarning" class="alert alert-warning mt-3 mb-0" style="display: none;">
                    <i class="fa fa-exclamation-triangle me-2"></i>
                    Danh mục này đang có sản phẩm hoạt động. Bạn cần ngừng hoạt động hoặc chuyển sản phẩm trước khi xóa.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form id="deleteForm" action="${pageContext.request.contextPath}/staff/category/delete" method="post" style="display: inline;">
                    <input type="hidden" name="id" id="deleteCategoryId">
                    <button type="submit" id="deleteBtn" class="btn btn-danger">Xóa</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(id, name, productCount) {
    document.getElementById('deleteCategoryId').value = id;
    document.getElementById('deleteCategoryName').textContent = name;
    
    var warningDiv = document.getElementById('hasProductsWarning');
    var deleteBtn = document.getElementById('deleteBtn');
    
    if (productCount > 0) {
        warningDiv.style.display = 'block';
        deleteBtn.disabled = true;
    } else {
        warningDiv.style.display = 'none';
        deleteBtn.disabled = false;
    }
    
    var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    deleteModal.show();
}
</script>
