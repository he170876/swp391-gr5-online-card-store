<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ thống đang bảo trì - Online Card Store</title>
        <link rel="shortcut icon" type="image/x-icon" href="img/smalllogo.jpg">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/fontawesome.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome/css/all.min.css">
        <style>
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            .maintenance-container {
                background: white;
                border-radius: 20px;
                padding: 60px 40px;
                text-align: center;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                max-width: 600px;
                width: 90%;
            }
            .maintenance-icon {
                font-size: 80px;
                color: #667eea;
                margin-bottom: 30px;
            }
            .maintenance-title {
                font-size: 32px;
                font-weight: bold;
                color: #333;
                margin-bottom: 20px;
            }
            .maintenance-message {
                font-size: 18px;
                color: #666;
                margin-bottom: 30px;
                line-height: 1.6;
            }
            .admin-login-btn {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                padding: 12px 30px;
                border-radius: 25px;
                font-size: 16px;
                font-weight: 600;
                text-decoration: none;
                display: inline-block;
                transition: transform 0.3s;
            }
            .admin-login-btn:hover {
                transform: translateY(-2px);
                color: white;
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }
        </style>
    </head>
    <body>
        <div class="maintenance-container">
            <div class="maintenance-icon">
                <i class="fas fa-tools"></i>
            </div>
            <h1 class="maintenance-title">Hệ thống đang bảo trì</h1>
            <p class="maintenance-message">
                Xin lỗi vì sự bất tiện này. Hệ thống đang được bảo trì để cải thiện chất lượng dịch vụ.
                <br><br>
                Chúng tôi sẽ sớm quay trở lại. Vui lòng quay lại sau.
            </p>
            <a href="${pageContext.request.contextPath}/login" class="admin-login-btn">
                <i class="fas fa-sign-in-alt"></i> Đăng nhập (Admin)
            </a>
        </div>
    </body>
</html>

