<%-- 
    Document   : admin-staff
    Created on : Dec 10, 2025, 10:30:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h2 class="fw-bold mb-4">Quản lý nhân viên</h2>

<div class="card p-3">
    <table class="table table-hover align-middle">
        <thead>
        <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Họ tên</th>
            <th>Số điện thoại</th>
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th>Ngày tạo</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="staff" items="${staffList}">
            <tr>
                <td>${staff.id}</td>
                <td>${staff.email}</td>
                <td>${staff.fullName}</td>
                <td>${staff.phone}</td>
                <td>${staff.address}</td>
                <td>
                    <span class="badge ${staff.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                        ${staff.status}
                    </span>
                </td>
                <td>${staff.createdAtFormatted}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

