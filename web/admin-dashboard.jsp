<%-- 
    Document   : admin-dashboard
    Created on : Dec 10, 2025, 10:22:12 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 class="mb-4 fw-bold">Bảng điều khiển</h2>

<div class="row g-4">
    <div class="col-lg-6 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-users fa-2x text-primary mb-2"></i>
            <h6>Tổng khách hàng</h6>
            <h3 class="fw-bold">${data.totalCustomers}</h3>
        </div>
    </div>

    <div class="col-lg-6 col-md-6">
        <div class="card p-3 text-center">
            <i class="fa fa-user-tie fa-2x text-info mb-2"></i>
            <h6>Tổng nhân viên</h6>
            <h3 class="fw-bold">${data.totalStaff}</h3>
        </div>
    </div>
</div>

<div class="card mt-4 p-4">
    <h5 class="fw-bold mb-3">
        <i class="fa fa-chart-bar"></i> Thống kê người dùng
    </h5>
    <canvas id="chartUsers"></canvas>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Biểu đồ cột so sánh số lượng khách hàng và nhân viên
    new Chart(document.getElementById('chartUsers'), {
        type: 'bar',
        data: {
            labels: ['Khách hàng', 'Nhân viên'],
            datasets: [{
                label: 'Số lượng người dùng',
                data: [${data.totalCustomers}, ${data.totalStaff}],
                backgroundColor: [
                    'rgba(76, 87, 255, 0.8)',
                    'rgba(23, 162, 184, 0.8)'
                ],
                borderColor: [
                    'rgba(76, 87, 255, 1)',
                    'rgba(23, 162, 184, 1)'
                ],
                borderWidth: 2,
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Số lượng: ' + context.parsed.y + ' người';
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        precision: 0
                    },
                    title: {
                        display: true,
                        text: 'Số lượng người dùng'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Loại người dùng'
                    }
                }
            }
        }
    });
</script>

