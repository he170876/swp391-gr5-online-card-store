<%-- 
    Document   : staff-provider-add
    Description: Add new provider page for staff
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Thêm nhà cung cấp mới</h4>
        <h6>Tạo nhà cung cấp mới trong hệ thống</h6>
    </div>
    <a href="${pageContext.request.contextPath}/staff/provider" class="btn btn-outline-secondary">
        <i class="fa fa-arrow-left me-2"></i>Quay lại
    </a>
</div>

<!-- Form Card -->
<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/staff/provider/add" method="post" id="providerForm">
            
            <!-- Basic Information -->
            <h5 class="mb-3"><i class="fa fa-info-circle me-2"></i>Thông tin nhà cung cấp</h5>
            <div class="row mb-4">
                <div class="col-md-6">
                    <label class="form-label">Tên nhà cung cấp <span class="text-danger">*</span></label>
                    <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" 
                           name="name" value="${formDTO.name}" placeholder="Nhập tên nhà cung cấp" maxlength="100" required>
                    <c:if test="${not empty errors.name}">
                        <div class="invalid-feedback">${errors.name}</div>
                    </c:if>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Trạng thái <span class="text-danger">*</span></label>
                    <div class="mt-2">
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="status" id="statusActive" 
                                   value="ACTIVE" ${formDTO.status != 'INACTIVE' ? 'checked' : ''}>
                            <label class="form-check-label" for="statusActive">
                                <span class="badge bg-success">Hoạt động</span>
                            </label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="status" id="statusInactive" 
                                   value="INACTIVE" ${formDTO.status == 'INACTIVE' ? 'checked' : ''}>
                            <label class="form-check-label" for="statusInactive">
                                <span class="badge bg-danger">Ngừng hoạt động</span>
                            </label>
                        </div>
                    </div>
                    <c:if test="${not empty errors.status}">
                        <div class="text-danger small mt-1">${errors.status}</div>
                    </c:if>
                </div>
            </div>

            <div class="row mb-4">
                <div class="col-md-12">
                    <label class="form-label">Thông tin liên hệ</label>
                    <textarea class="form-control ${not empty errors.contactInfo ? 'is-invalid' : ''}" 
                              name="contactInfo" rows="3" placeholder="Nhập thông tin liên hệ (địa chỉ, email, số điện thoại...)" maxlength="255">${formDTO.contactInfo}</textarea>
                    <c:if test="${not empty errors.contactInfo}">
                        <div class="invalid-feedback">${errors.contactInfo}</div>
                    </c:if>
                    <div class="form-text">Tối đa 255 ký tự</div>
                </div>
            </div>

            <hr class="my-4">

            <!-- Submit Buttons -->
            <div class="d-flex justify-content-end gap-2">
                <a href="${pageContext.request.contextPath}/staff/provider" class="btn btn-outline-secondary">
                    <i class="fa fa-times me-1"></i> Hủy
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="fa fa-save me-1"></i> Lưu nhà cung cấp
                </button>
            </div>
        </form>
    </div>
</div>
