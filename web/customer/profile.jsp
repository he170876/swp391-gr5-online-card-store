<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/customer/includes/header.jsp">
    <jsp:param name="title" value="Profile"/>
</jsp:include>

<jsp:include page="/customer/includes/navbar.jsp"/>

<div class="container my-4">
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <h4 class="mb-4">Hồ sơ</h4>
    <div class="row g-4">
        <div class="col-12 col-lg-6">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <form method="post">
                        <div class="mb-3">
                            <label class="form-label">Họ và tên</label>
                            <input type="text" name="fullName" value="${userProfile.fullName}" class="form-control" required>
                            <c:if test="${not empty errorFullName}"><div class="text-danger small mt-1">${errorFullName}</div></c:if>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" value="${userProfile.email}" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Số điện thoại</label>
                            <input type="text" name="phone" value="${userProfile.phone}" class="form-control">
                            <c:if test="${not empty errorPhone}"><div class="text-danger small mt-1">${errorPhone}</div></c:if>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Địa chỉ</label>
                            <input type="text" name="address" value="${userProfile.address}" class="form-control">
                            <c:if test="${not empty errorAddress}"><div class="text-danger small mt-1">${errorAddress}</div></c:if>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Trạng thái</label><br>
                            <span class="badge ${userProfile.status == 'ACTIVE' ? 'bg-success' : 'bg-warning text-dark'}">${userProfile.status}</span>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Cập nhật thông tin</button>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-12 col-lg-6">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h6 class="text-muted">Số dư ví</h6>
                    <h3 class="fw-bold text-success">
                        <fmt:formatNumber value="${userProfile.walletBalance}" type="currency"/>
                    </h3>
                    <p class="text-muted small mb-0">Sử dụng ví để mua mã thẻ nhanh chóng.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/customer/includes/footer.jsp"/>

