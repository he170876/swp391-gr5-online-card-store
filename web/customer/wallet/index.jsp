<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/customer/includes/header.jsp">
    <jsp:param name="title" value="My Wallet"/>
</jsp:include>

<jsp:include page="/customer/includes/navbar.jsp"/>

<div class="container my-4">
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <div class="row g-4">
        <div class="col-12 col-lg-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body text-center">
                    <h6 class="text-muted">Số dư ví</h6>
                    <h2 class="fw-bold text-success">
                        <fmt:formatNumber value="${walletBalance}" type="currency"/>
                    </h2>
                    <button class="btn btn-success mt-3" data-bs-toggle="collapse" data-bs-target="#topupForm">Nạp tiền</button>
                </div>
            </div>

            <div class="collapse show" id="topupForm">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h6 class="fw-bold mb-3">Nạp tiền qua VietQR</h6>
                        <form action="${pageContext.request.contextPath}/customer/wallet/topup" method="post">
                            <div class="mb-3">
                                <label class="form-label">Số tiền</label>
                                <input type="number" class="form-control" name="amount" min="10000" step="1000" placeholder="Nhập số tiền" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Tạo mã VietQR</button>
                        </form>
                        <div class="mt-3 small text-muted">
                            <div>Ngân hàng: VietinBank</div>
                            <div>Số tài khoản: 109874971099</div>
                            <div>Chủ tài khoản: ONLINE CARD STORE</div>
                            <div>Nội dung: NAPVI OCS {USER_ID}</div>
                            <div class="mt-2">Vui lòng quét mã VietQR bằng ứng dụng ngân hàng để nạp tiền vào ví. Nội dung chuyển khoản phải đúng để hệ thống xử lý chính xác.</div>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty newTopup}">
                <div class="card shadow-sm mt-4">
                    <div class="card-body text-center">
                        <h6 class="fw-bold mb-2">Quét mã để nạp</h6>
                        <img src="${newTopup.qrUrl}" alt="VietQR" class="img-fluid rounded mb-2">
                        <p class="small text-muted mb-0">Nội dung: ${newTopup.referenceCode}</p>
                        <p class="small text-muted">Trạng thái: ${newTopup.status}</p>
                    </div>
                </div>
            </c:if>
        </div>

        <div class="col-12 col-lg-8">
            <h5 class="mb-3">Lịch sử nạp tiền</h5>

            <form method="get" class="row g-3 mb-3">
                <div class="col-12 col-md-6">
                    <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="Tìm kiếm theo mã giao dịch...">
                </div>
                <div class="col-12 col-md-4">
                    <select name="status" class="form-select">
                        <option value="">Trạng thái giao dịch</option>
                        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                        <option value="SUCCESS" ${status == 'SUCCESS' ? 'selected' : ''}>SUCCESS</option>
                        <option value="FAILED" ${status == 'FAILED' ? 'selected' : ''}>FAILED</option>
                    </select>
                </div>
                <div class="col-12 col-md-4">
                    <select name="type" class="form-select">
                        <option value="">Loại giao dịch</option>
                        <option value="IN" ${type == 'IN' ? 'selected' : ''}>Tiền vào</option>
                        <option value="OUT" ${type == 'OUT' ? 'selected' : ''}>Tiền ra</option>
                    </select>
                </div>
                <div class="col-6 col-md-2 d-flex gap-2">
                    <button class="btn btn-primary w-100" type="submit">Lọc</button>
                </div>
            </form>

            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <c:if test="${empty transactions}">
                        <div class="alert alert-light border mb-0">
                            Không có dữ liệu phù hợp. Vui lòng thử lại với bộ lọc khác.
                        </div>
                    </c:if>
                    <c:if test="${not empty transactions}">
                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Mã giao dịch</th>
                                        <th>Số tiền</th>
                                        <th>Trạng thái</th>
                                        <th>Ngày tạo</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="tx" items="${transactions}">
                                        <tr>
                                            <td>${tx.referenceCode}</td>
                                            <td><fmt:formatNumber value="${tx.amount}" type="currency"/></td>
                                            <td>
                                                <span class="badge ${tx.status == 'SUCCESS' ? 'bg-success' : tx.status == 'PENDING' ? 'bg-warning text-dark' : 'bg-danger'}">
                                                    ${tx.status}
                                                </span>
                                            </td>
                                            <td>${tx.createdAt}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="d-flex justify-content-center align-items-center gap-2 mt-3">
                            <c:set var="prevPage" value="${currentPage - 1}"/>
                            <c:set var="nextPage" value="${currentPage + 1}"/>
                            <a class="btn btn-outline-secondary ${currentPage == 1 ? 'disabled' : ''}"
                               href="${pageContext.request.contextPath}/customer/wallet?page=${prevPage}&status=${status}&keyword=${keyword}">
                                Trước
                            </a>
                            <span>Trang ${currentPage} / ${totalPages}</span>
                            <a class="btn btn-outline-secondary ${currentPage == totalPages ? 'disabled' : ''}"
                               href="${pageContext.request.contextPath}/customer/wallet?page=${nextPage}&status=${status}&keyword=${keyword}">
                                Sau
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/customer/includes/footer.jsp"/>

