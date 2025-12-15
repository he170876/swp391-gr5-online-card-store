<%-- 
    Document   : admin-config
    Created on : Dec 10, 2025, 10:45:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>

<h2 class="fw-bold mb-4">Cấu hình hệ thống</h2>

<div class="card p-4">
    <h5 class="fw-bold mb-3">Thiết lập hệ thống</h5>
    
    <form method="POST" action="${pageContext.request.contextPath}/admin/config">
        <div class="mb-3">
            <label class="form-label">Tên hệ thống</label>
            <input type="text" class="form-control" name="systemName" value="OCS - Online Card Store" readonly>
        </div>
        
        <div class="mb-3">
            <label class="form-label">Chế độ bảo trì</label>
            <select class="form-select" name="maintenanceMode">
                <option value="false" selected>Tắt</option>
                <option value="true">Bật</option>
            </select>
        </div>
        
        <div class="mb-3">
            <label class="form-label">Tiền tệ mặc định</label>
            <input type="text" class="form-control" name="currency" value="VND" readonly>
        </div>
        
        <div class="mb-3">
            <label class="form-label">Số lần đăng nhập tối đa</label>
            <input type="number" class="form-control" name="maxLoginAttempts" value="5" min="1" max="10">
        </div>
        
        <button type="submit" class="btn btn-primary">
            <i class="fa fa-save"></i> Lưu cấu hình
        </button>
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

