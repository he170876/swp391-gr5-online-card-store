<%-- 
    Document   : admin-config
    Created on : Dec 10, 2025, 10:45:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="fw-bold mb-4">Cấu hình hệ thống</h2>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fa fa-check-circle"></i> ${successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fa fa-exclamation-circle"></i> ${errorMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="card p-4">
    <h5 class="fw-bold mb-3">Thiết lập hệ thống</h5>
    
    <form method="POST" action="${pageContext.request.contextPath}/admin/config">
        <div class="mb-3">
            <label for="systemName" class="form-label">Tên hệ thống</label>
            <input type="text" 
                   class="form-control" 
                   id="systemName" 
                   name="systemName" 
                   value="${config.systemName != null ? config.systemName : 'OCS - Online Card Store'}" 
                   required>
            <small class="form-text text-muted">Tên hiển thị của hệ thống</small>
        </div>
        
        <div class="mb-3">
            <label for="maintenanceMode" class="form-label">Chế độ bảo trì</label>
            <select class="form-select" id="maintenanceMode" name="maintenanceMode">
                <option value="false" ${config.maintenanceMode == false || config.maintenanceMode == null ? 'selected' : ''}>Tắt</option>
                <option value="true" ${config.maintenanceMode == true ? 'selected' : ''}>Bật</option>
            </select>
            <small class="form-text text-muted">Khi bật, chỉ admin mới có thể truy cập hệ thống</small>
        </div>
        
        <div class="mb-3">
            <label for="currency" class="form-label">Tiền tệ mặc định</label>
            <input type="text" 
                   class="form-control" 
                   id="currency" 
                   name="currency" 
                   value="${config.currency != null ? config.currency : 'VND'}" 
                   maxlength="10"
                   required>
            <small class="form-text text-muted">Mã tiền tệ (VD: VND, USD, EUR)</small>
        </div>
        
        <div class="mb-3">
            <label for="maxLoginAttempts" class="form-label">Số lần đăng nhập tối đa</label>
            <input type="number" 
                   class="form-control" 
                   id="maxLoginAttempts" 
                   name="maxLoginAttempts" 
                   value="${config.maxLoginAttempts != null ? config.maxLoginAttempts : 5}" 
                   min="1" 
                   max="10"
                   required>
            <small class="form-text text-muted">Số lần đăng nhập sai tối đa trước khi khóa tài khoản</small>
        </div>
        
        <div class="mb-3">
            <label for="emailSupport" class="form-label">Email hỗ trợ</label>
            <input type="email" 
                   class="form-control" 
                   id="emailSupport" 
                   name="emailSupport" 
                   value="${config.emailSupport != null ? config.emailSupport : 'support@ocs.com'}">
            <small class="form-text text-muted">Email liên hệ hỗ trợ khách hàng</small>
        </div>
        
        <div class="mb-3">
            <label for="phoneSupport" class="form-label">Số điện thoại hỗ trợ</label>
            <input type="text" 
                   class="form-control" 
                   id="phoneSupport" 
                   name="phoneSupport" 
                   value="${config.phoneSupport != null ? config.phoneSupport : '1900-xxxx'}"
                   pattern="[0-9-+() ]+">
            <small class="form-text text-muted">Số điện thoại hỗ trợ khách hàng</small>
        </div>
        
        <div class="mb-3">
            <label for="pageSize" class="form-label">Số bản ghi mỗi trang</label>
            <input type="number" 
                   class="form-control" 
                   id="pageSize" 
                   name="pageSize" 
                   value="${config.pageSize != null ? config.pageSize : 20}" 
                   min="10" 
                   max="100"
                   required>
            <small class="form-text text-muted">Số lượng bản ghi hiển thị mỗi trang trong danh sách</small>
        </div>
        
        <button type="submit" class="btn btn-primary">
            <i class="fa fa-save"></i> Lưu cấu hình
        </button>
        <a href="${pageContext.request.contextPath}/admin/config" class="btn btn-secondary">
            <i class="fa fa-undo"></i> Hủy
        </a>
    </form>
</div>

<div class="card p-4 mt-4">
    <h5 class="fw-bold mb-3">Thông tin cơ sở dữ liệu</h5>
    <table class="table">
        <tr>
            <th>Tên cơ sở dữ liệu</th>
            <td>OCS</td>
        </tr>
        <tr>
            <th>Loại cơ sở dữ liệu</th>
            <td>Microsoft SQL Server</td>
        </tr>
        <tr>
            <th>Trạng thái</th>
            <td><span class="badge bg-success">Đã kết nối</span></td>
        </tr>
    </table>
</div>

