<%-- 
    Document   : staff-product-edit
    Description: Edit product page for staff
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4>Sửa sản phẩm</h4>
        <h6>Cập nhật thông tin sản phẩm</h6>
    </div>
    <a href="${pageContext.request.contextPath}/staff/product" class="btn btn-outline-secondary">
        <i class="fa fa-arrow-left me-2"></i>Quay lại
    </a>
</div>

<!-- Form Card -->
<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/staff/product/edit" method="post" id="productForm">
            <input type="hidden" name="id" value="${formDTO.id}">
            
            <!-- Basic Information -->
            <h5 class="mb-3"><i class="fa fa-info-circle me-2"></i>Thông tin cơ bản</h5>
            <div class="row mb-4">
                <div class="col-md-6">
                    <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                    <input type="text" class="form-control ${not empty errors.name ? 'is-invalid' : ''}" 
                           name="name" value="${formDTO.name}" placeholder="Nhập tên sản phẩm" maxlength="100" required>
                    <c:if test="${not empty errors.name}">
                        <div class="invalid-feedback">${errors.name}</div>
                    </c:if>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Danh mục <span class="text-danger">*</span></label>
                    <select class="form-select select2 ${not empty errors.categoryId ? 'is-invalid' : ''}" 
                            name="categoryId" required>
                        <option value="">Chọn danh mục</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}" ${formDTO.categoryId == cat.id ? 'selected' : ''}>
                                ${cat.name}
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty errors.categoryId}">
                        <div class="invalid-feedback d-block">${errors.categoryId}</div>
                    </c:if>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Nhà cung cấp <span class="text-danger">*</span></label>
                    <select class="form-select select2 ${not empty errors.providerId ? 'is-invalid' : ''}" 
                            name="providerId" required>
                        <option value="">Chọn nhà cung cấp</option>
                        <c:forEach var="prov" items="${providers}">
                            <option value="${prov.id}" ${formDTO.providerId == prov.id ? 'selected' : ''}>
                                ${prov.name}
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty errors.providerId}">
                        <div class="invalid-feedback d-block">${errors.providerId}</div>
                    </c:if>
                </div>
            </div>

            <div class="row mb-4">
                <div class="col-md-12">
                    <label class="form-label">Mô tả</label>
                    <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" 
                              name="description" rows="3" placeholder="Nhập mô tả sản phẩm" maxlength="255">${formDTO.description}</textarea>
                    <c:if test="${not empty errors.description}">
                        <div class="invalid-feedback">${errors.description}</div>
                    </c:if>
                    <div class="form-text">Tối đa 255 ký tự</div>
                </div>
            </div>

            <div class="row mb-4">
                <div class="col-md-12">
                    <label class="form-label">URL Ảnh</label>
                    <input type="url" class="form-control ${not empty errors.imageUrl ? 'is-invalid' : ''}" 
                           name="imageUrl" value="${formDTO.imageUrl}" placeholder="https://example.com/image.jpg">
                    <c:if test="${not empty errors.imageUrl}">
                        <div class="invalid-feedback">${errors.imageUrl}</div>
                    </c:if>
                    <div class="form-text">Nhập URL ảnh bắt đầu bằng http:// hoặc https://</div>
                </div>
            </div>

            <!-- Preview Image -->
            <c:if test="${not empty formDTO.imageUrl}">
                <div class="row mb-4">
                    <div class="col-md-12">
                        <label class="form-label">Xem trước ảnh</label>
                        <div>
                            <img src="${formDTO.imageUrl}" alt="Preview" style="max-width: 200px; max-height: 200px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd;">
                        </div>
                    </div>
                </div>
            </c:if>

            <hr class="my-4">

            <!-- Pricing Information -->
            <h5 class="mb-3"><i class="fa fa-tags me-2"></i>Thông tin giá</h5>
            <div class="row mb-4">
                <div class="col-md-4">
                    <label class="form-label">Giá gốc (VNĐ) <span class="text-danger">*</span></label>
                    <input type="number" class="form-control ${not empty errors.costPrice ? 'is-invalid' : ''}" 
                           name="costPrice" value="${formDTO.costPrice}" placeholder="0" min="0" step="1000" required>
                    <c:if test="${not empty errors.costPrice}">
                        <div class="invalid-feedback">${errors.costPrice}</div>
                    </c:if>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                    <input type="number" class="form-control ${not empty errors.sellPrice ? 'is-invalid' : ''}" 
                           name="sellPrice" value="${formDTO.sellPrice}" placeholder="0" min="0" step="1000" required>
                    <c:if test="${not empty errors.sellPrice}">
                        <div class="invalid-feedback">${errors.sellPrice}</div>
                    </c:if>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Giảm giá (%)</label>
                    <input type="number" class="form-control ${not empty errors.discountPercent ? 'is-invalid' : ''}" 
                           name="discountPercent" value="${formDTO.discountPercent != null ? formDTO.discountPercent : 0}" 
                           placeholder="0" min="0" max="100" step="0.1">
                    <c:if test="${not empty errors.discountPercent}">
                        <div class="invalid-feedback">${errors.discountPercent}</div>
                    </c:if>
                </div>
            </div>

            <hr class="my-4">

            <!-- Status -->
            <h5 class="mb-3"><i class="fa fa-toggle-on me-2"></i>Trạng thái</h5>
            <div class="row mb-4">
                <div class="col-md-6">
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
                    <c:if test="${not empty errors.status}">
                        <div class="text-danger small mt-1">${errors.status}</div>
                    </c:if>
                </div>
            </div>

            <hr class="my-4">

            <!-- Submit Buttons -->
            <div class="d-flex justify-content-end gap-2">
                <a href="${pageContext.request.contextPath}/staff/product" class="btn btn-outline-secondary">
                    <i class="fa fa-times me-1"></i> Hủy
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="fa fa-save me-1"></i> Cập nhật sản phẩm
                </button>
            </div>
        </form>
    </div>
</div>

<script>
document.getElementById('productForm').addEventListener('submit', function(e) {
    var costPrice = parseFloat(document.querySelector('input[name="costPrice"]').value) || 0;
    var sellPrice = parseFloat(document.querySelector('input[name="sellPrice"]').value) || 0;
    
    if (sellPrice < costPrice) {
        e.preventDefault();
        alert('Giá bán phải lớn hơn hoặc bằng giá gốc!');
        return false;
    }
});
</script>
