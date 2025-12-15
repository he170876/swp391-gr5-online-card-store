<%-- 
    Document   : admin-change-password
    Created on : Dec 10, 2025
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="fw-bold mb-4">
    <i class="fa fa-key"></i> Đổi mật khẩu
</h2>

<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card p-4">
            <div class="card-body">
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
                
                <form method="POST" action="${pageContext.request.contextPath}/admin/change-password" id="changePasswordForm">
                    <div class="mb-3">
                        <label for="oldPassword" class="form-label">
                            <i class="fa fa-lock"></i> Mật khẩu hiện tại
                        </label>
                        <input type="password" 
                               class="form-control" 
                               id="oldPassword" 
                               name="oldPassword" 
                               required
                               autocomplete="current-password"
                               placeholder="Nhập mật khẩu hiện tại">
                    </div>
                    
                    <div class="mb-3">
                        <label for="newPassword" class="form-label">
                            <i class="fa fa-key"></i> Mật khẩu mới
                        </label>
                        <input type="password" 
                               class="form-control" 
                               id="newPassword" 
                               name="newPassword" 
                               minlength="6"
                               required
                               autocomplete="new-password"
                               placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)">
                        <small class="form-text text-muted">
                            <i class="fa fa-info-circle"></i> Mật khẩu phải có ít nhất 6 ký tự
                        </small>
                    </div>
                    
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">
                            <i class="fa fa-check-circle"></i> Xác nhận mật khẩu mới
                        </label>
                        <input type="password" 
                               class="form-control" 
                               id="confirmPassword" 
                               name="confirmPassword" 
                               minlength="6"
                               required
                               autocomplete="new-password"
                               placeholder="Nhập lại mật khẩu mới">
                    </div>
                    
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
                            <i class="fa fa-times"></i> Hủy
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fa fa-save"></i> Đổi mật khẩu
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
        <div class="card mt-3 p-3">
            <div class="card-body">
                <h6 class="fw-bold mb-2">
                    <i class="fa fa-shield-alt"></i> Lưu ý bảo mật:
                </h6>
                <ul class="mb-0 small text-muted">
                    <li>Mật khẩu phải có ít nhất 6 ký tự</li>
                    <li>Nên sử dụng kết hợp chữ hoa, chữ thường, số và ký tự đặc biệt</li>
                    <li>Không chia sẻ mật khẩu với người khác</li>
                    <li>Đổi mật khẩu định kỳ để tăng cường bảo mật</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<script>
    // Validate password match
    document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
        const oldPassword = document.getElementById('oldPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (oldPassword.trim() === '') {
            e.preventDefault();
            alert('Vui lòng nhập mật khẩu hiện tại!');
            return false;
        }
        
        if (newPassword.length < 6) {
            e.preventDefault();
            alert('Mật khẩu mới phải có ít nhất 6 ký tự!');
            return false;
        }
        
        if (newPassword !== confirmPassword) {
            e.preventDefault();
            alert('Mật khẩu xác nhận không khớp!');
            return false;
        }
        
        if (oldPassword === newPassword) {
            e.preventDefault();
            alert('Mật khẩu mới phải khác mật khẩu hiện tại!');
            return false;
        }
    });
</script>

