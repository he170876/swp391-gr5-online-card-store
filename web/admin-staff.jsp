<%-- 
    Document   : admin-staff
    Created on : Dec 10, 2025, 10:30:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h2 class="fw-bold mb-4">Quản lý nhân viên</h2>

<!-- Search Form -->
<div class="card p-3 mb-3">
    <form method="get" action="${pageContext.request.contextPath}/admin/staff" class="row g-3">
        <div class="col-md-6">
            <label for="keyword" class="form-label">Tìm kiếm theo tên</label>
            <input type="text" 
                   class="form-control" 
                   id="keyword" 
                   name="keyword" 
                   placeholder="Nhập tên nhân viên..." 
                   value="${keyword}">
        </div>
        <div class="col-md-4">
            <label for="status" class="form-label">Trạng thái</label>
            <select class="form-select" id="status" name="status">
                <option value="ALL" ${statusFilter == 'ALL' || statusFilter == null || statusFilter == '' ? 'selected' : ''}>Tất cả</option>
                <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                <option value="LOCKED" ${statusFilter == 'LOCKED' ? 'selected' : ''}>Bị khóa</option>
                <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>Không hoạt động</option>
            </select>
        </div>
        <div class="col-md-2 d-flex align-items-end">
            <button type="submit" class="btn btn-primary w-100">
                <i class="fa fa-search"></i> Tìm kiếm
            </button>
        </div>
        <c:if test="${not empty keyword || (not empty statusFilter && statusFilter != 'ALL')}">
            <div class="col-12">
                <a href="${pageContext.request.contextPath}/admin/staff" class="btn btn-secondary btn-sm">
                    <i class="fa fa-times"></i> Xóa bộ lọc
                </a>
            </div>
        </c:if>
    </form>
</div>

<div class="card p-3">
    <table class="table table-hover align-middle">
        <thead>
        <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Họ tên</th>
            <th>Số điện thoại</th>
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th>Ngày tạo</th>
            <th class="text-center">Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty staffList}">
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        <i class="fa fa-info-circle"></i> Không tìm thấy nhân viên nào
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="staff" items="${staffList}">
                    <tr>
                        <td>${staff.id}</td>
                        <td>${staff.email}</td>
                        <td>${staff.fullName}</td>
                        <td>${staff.phone}</td>
                        <td>${staff.address}</td>
                        <td>
                            <span class="badge ${staff.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                                ${staff.status == 'ACTIVE' ? 'Hoạt động' : 'Bị khóa'}
                            </span>
                        </td>
                        <td>${staff.createdAtFormatted}</td>
                        <td class="text-center">
                            <c:if test="${staff.status == 'ACTIVE'}">
                                <a href="${pageContext.request.contextPath}/admin/staff/block?id=${staff.id}&page=${currentPage}&keyword=${keyword}&status=${statusFilter}"
                                   class="btn btn-danger btn-sm">
                                   <i class="fa fa-ban"></i> Khóa
                                </a>
                            </c:if>

                            <c:if test="${staff.status != 'ACTIVE'}">
                                <a href="${pageContext.request.contextPath}/admin/staff/unblock?id=${staff.id}&page=${currentPage}&keyword=${keyword}&status=${statusFilter}"
                                   class="btn btn-success btn-sm">
                                   <i class="fa fa-check"></i> Mở khóa
                                </a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

<!-- Pagination -->
<div class="d-flex justify-content-between align-items-center mt-3">
        <div class="text-muted">
            <c:choose>
                <c:when test="${totalRecords > 0}">
                    Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalRecords ? totalRecords : currentPage * pageSize} 
                    trong tổng số ${totalRecords} nhân viên
                </c:when>
                <c:otherwise>
                    Không có dữ liệu
                </c:otherwise>
            </c:choose>
        </div>
        <nav>
            <ul class="pagination mb-0">
                <!-- Previous button -->
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" 
                       href="${pageContext.request.contextPath}/admin/staff?page=${currentPage - 1}&pageSize=${pageSize}&keyword=${keyword}&status=${statusFilter}">
                        <i class="fa fa-chevron-left"></i> Trước
                    </a>
                </li>
                
                <!-- Page numbers -->
                <c:choose>
                    <c:when test="${totalPages > 0}">
                        <c:forEach var="i" begin="${currentPage > 3 ? currentPage - 2 : 1}" 
                                   end="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" 
                                   href="${pageContext.request.contextPath}/admin/staff?page=${i}&pageSize=${pageSize}&keyword=${keyword}&status=${statusFilter}">
                                    ${i}
                                </a>
                            </li>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <li class="page-item active">
                            <span class="page-link">1</span>
                        </li>
                    </c:otherwise>
                </c:choose>
                
                <!-- Next button -->
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" 
                       href="${pageContext.request.contextPath}/admin/staff?page=${currentPage + 1}&pageSize=${pageSize}&keyword=${keyword}&status=${statusFilter}">
                        Sau <i class="fa fa-chevron-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
        
        <!-- Page size selector -->
        <div class="d-flex align-items-center gap-2">
            <label class="text-muted small">Hiển thị:</label>
            <select class="form-select form-select-sm" style="width: auto;" 
                    onchange="window.location.href='${pageContext.request.contextPath}/admin/staff?page=1&pageSize=' + this.value + '&keyword=${keyword}&status=${statusFilter}'">
                <option value="5" ${pageSize == 5 ? 'selected' : ''}>5</option>
                <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                <option value="100" ${pageSize == 100 ? 'selected' : ''}>100</option>
            </select>
        </div>
    </div>

