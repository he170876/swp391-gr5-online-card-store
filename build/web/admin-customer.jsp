<%-- 
    Document   : admin-customer
    Created on : Dec 10, 2025, 10:25:02 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="fw-bold mb-4">Quản lý khách hàng</h2>

<div class="card p-3">
<table class="table table-hover align-middle">
    <thead>
    <tr>
        <th>ID</th>
        <th>Email</th>
        <th>Tên</th>
        <th>Trạng thái</th>
        <th>Ví</th>
        <th class="text-center">Thao tác</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="c" items="${customers}">
        <tr>
            <td>${c.id}</td>
            <td>${c.email}</td>
            <td>${c.fullName}</td>
            <td>
                <span class="badge ${c.active?'bg-success':'bg-danger'}">
                    ${c.active ? 'Hoạt động' : 'Bị khóa'}
                </span>
            </td>
            <td>${c.walletBalance} VND</td>
            <td class="text-center">
                <c:if test="${c.active}">
                    <a href="${pageContext.request.contextPath}/admin/customer/block?id=${c.id}"
                       class="btn btn-danger btn-sm">
                       <i class="fa fa-ban"></i> Khóa
                    </a>
                </c:if>

                <c:if test="${!c.active}">
                    <a href="${pageContext.request.contextPath}/admin/customer/unblock?id=${c.id}"
                       class="btn btn-success btn-sm">
                       <i class="fa fa-check"></i> Mở khóa
                    </a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>
