<%-- 
    Document   : admin-roles
    Created on : Dec 10, 2025, 10:35:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="fw-bold mb-4">Quản lý vai trò</h2>

<div class="row">
    <div class="col-md-8">
        <div class="card p-3 mb-4">
            <h5 class="mb-3">Tất cả vai trò</h5>
            <table class="table table-hover align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên</th>
                    <th>Mô tả</th>
                    <th class="text-center">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="role" items="${roles}">
                    <tr>
                        <td>${role.id}</td>
                        <td><strong>${role.name}</strong></td>
                        <td>${role.description}</td>
                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/admin/roles?edit=${role.id}" class="btn btn-sm btn-primary">
                                <i class="fa fa-edit"></i> Sửa
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/roles/delete?id=${role.id}" 
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Bạn có chắc chắn muốn xóa vai trò này?')">
                                <i class="fa fa-trash"></i> Xóa
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card p-3">
            <h5 class="mb-3">${editRole != null ? 'Sửa vai trò' : 'Tạo vai trò mới'}</h5>
            
            <c:if test="${editRole != null}">
                <form method="POST" action="${pageContext.request.contextPath}/admin/roles/update">
                    <input type="hidden" name="id" value="${editRole.id}">
                    <div class="mb-3">
                        <label class="form-label">Tên</label>
                        <input type="text" class="form-control" name="name" value="${editRole.name}" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mô tả</label>
                        <textarea class="form-control" name="description" rows="3">${editRole.description}</textarea>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fa fa-save"></i> Cập nhật vai trò
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-secondary w-100 mt-2">Hủy</a>
                </form>
            </c:if>
            
            <c:if test="${editRole == null}">
                <form method="POST" action="${pageContext.request.contextPath}/admin/roles/create">
                    <div class="mb-3">
                        <label class="form-label">Tên</label>
                        <input type="text" class="form-control" name="name" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mô tả</label>
                        <textarea class="form-control" name="description" rows="3"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fa fa-plus"></i> Tạo vai trò
                    </button>
                </form>
            </c:if>
        </div>
    </div>
</div>

