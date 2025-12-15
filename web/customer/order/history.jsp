<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/customer/includes/header.jsp">
    <jsp:param name="title" value="Order History"/>
</jsp:include>

<jsp:include page="/customer/includes/navbar.jsp"/>

<div class="container my-4">
    <c:if test="${purchaseSuccess}">
        <div class="alert alert-success">
            Đặt hàng thành công! Mã thẻ của bạn hiển thị bên dưới.
        </div>
    </c:if>

    <h4 class="mb-3">Lịch sử đơn hàng</h4>

    <form method="get" class="row g-3 mb-3">
        <div class="col-12 col-md-6">
            <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="Tìm kiếm đơn hàng">
        </div>
        <div class="col-12 col-md-4">
            <select name="status" class="form-select">
                <option value="">Trạng thái</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
            </select>
        </div>
        <div class="col-6 col-md-2">
            <button class="btn btn-primary w-100" type="submit">Lọc</button>
        </div>
    </form>

    <div class="card shadow-sm border-0">
        <div class="card-body">
            <c:if test="${empty orders}">
                <div class="alert alert-light border mb-0">
                    Không có dữ liệu phù hợp. Vui lòng thử lại với bộ lọc khác.
                </div>
            </c:if>
            <c:if test="${not empty orders}">
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Sản phẩm</th>
                                <th>Giá</th>
                                <th>Trạng thái</th>
                                <th>Ngày mua</th>
                                <th>Mã thẻ</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${orders}">
                                <tr>
                                    <td>#${order.id}</td>
                                    <td>${order.productName}</td>
                                    <td><fmt:formatNumber value="${order.finalPrice}" type="currency"/></td>
                                    <td>
                                        <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : order.status == 'PENDING' ? 'bg-warning text-dark' : 'bg-secondary'}">
                                            ${order.status}
                                        </span>
                                    </td>
                                    <td>${order.createdAt}</td>
                                    <td>
                                        <c:if test="${order.status == 'COMPLETED'}">
                                            <span class="fw-semibold">${order.cardCode}</span>
                                        </c:if>
                                        <c:if test="${order.status != 'COMPLETED'}">
                                            <span class="text-muted">—</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-center align-items-center gap-2 mt-3">
                    <c:set var="prevPage" value="${currentPage - 1}"/>
                    <c:set var="nextPage" value="${currentPage + 1}"/>
                    <a class="btn btn-outline-secondary ${currentPage == 1 ? 'disabled' : ''}"
                       href="${pageContext.request.contextPath}/customer/orders?page=${prevPage}&keyword=${keyword}&status=${status}">
                        Trước
                    </a>
                    <span>Trang ${currentPage} / ${totalPages}</span>
                    <a class="btn btn-outline-secondary ${currentPage == totalPages ? 'disabled' : ''}"
                       href="${pageContext.request.contextPath}/customer/orders?page=${nextPage}&keyword=${keyword}&status=${status}">
                        Sau
                    </a>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/customer/includes/footer.jsp"/>

