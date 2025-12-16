<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.title != null ? param.title : 'Online Card Store'}"/></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <style>
      body {
        background-color: #f8f9fa;
      }

      .navbar-brand span {
        font-weight: 700;
      }

      .card-img-top {
        object-fit: cover;
        height: 180px;
      }

      .hero {
        background: linear-gradient(120deg, #e8f0ff, #f7fbff);
      }

      /* Chat UI đẹp, to hơn */
      #chat-messages {
        display: flex;
        flex-direction: column;
        gap: 15px;
        padding: 20px;
        height: 500px; /* To hơn nhiều */
        overflow-y: auto;
        background: #f5f7fa;
      }

      .message {
        max-width: 85%;
        padding: 14px 18px;
        border-radius: 22px;
        line-height: 1.6;
        word-wrap: break-word;
        font-size: 1rem;
      }

      .user-message {
        align-self: flex-end;
        background: #0d6efd;
        color: white;
        border-bottom-right-radius: 6px;
      }

      .ai-message {
        align-self: flex-start;
        background: white;
        color: #212529;
        border: 1px solid #e9ecef;
        border-bottom-left-radius: 6px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
      }

      .ai-message img {
        max-width: 100%;
        border-radius: 10px;
        margin-top: 10px;
      }

      .modal-dialog {
        max-width: 600px; /* Modal rộng hơn */
      }

      .modal-content {
        border-radius: 16px;
        overflow: hidden;
      }

      .modal-header {
        background: linear-gradient(135deg, #4285f4, #34a853);
        color: white;
        border-bottom: none;
      }

      .modal-body {
        padding: 0;
      }

      .input-group {
        padding: 15px;
        background: white;
        border-top: 1px solid #dee2e6;
      }

      #chat-icon button {
        box-shadow: 0 6px 20px rgba(13, 110, 253, 0.3);
        transition: transform 0.2s;
      }

      #chat-icon button:hover {
        transform: scale(1.1);
      }
    </style>
  </head>

  <body>
    <div id="chat-icon" style="position: fixed; bottom: 20px; right: 20px; z-index: 1000;">
      <button class="btn btn-primary rounded-circle shadow-lg" style="width: 65px; height: 65px;" onclick="openChat()">
        <i class="fas fa-robot fa-2x"></i>
      </button>
    </div>

    <div class="modal fade" id="chatModal" tabindex="-1" aria-labelledby="chatModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="chatModalLabel"><i class="fas fa-robot me-2"></i>Chat với Gemini</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body p-0">
            <div id="chat-messages"></div>
            <div class="input-group">
              <input type="text" id="chat-input" class="form-control rounded-pill" placeholder="Nhập tin nhắn..." autocomplete="off">
              <button class="btn btn-success rounded-pill ms-2" onclick="sendMessage()">Gửi</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module">
      import { GoogleGenAI } from "https://cdn.jsdelivr.net/npm/@google/genai@latest";

      // ⚠️ THAY KEY MỚI NGAY! Key public sẽ hết quota nhanh
      const API_KEY = "AIzaSyDrnJ-Q1eGVXU5Vm7w-kq32U_Dyfc-1PEA";
      const ai = new GoogleGenAI({ apiKey: API_KEY });

      async function sendMessage() {
        const input = document.getElementById('chat-input');
        const messagesDiv = document.getElementById('chat-messages');
        const userText = input.value.trim();
        if (!userText) return;

        // Thêm tin nhắn user - dùng + để tránh EL parse
        messagesDiv.innerHTML += 
          '<div class="message user-message">' +
            userText.replace(/</g, '&lt;').replace(/>/g, '&gt;') +
          '</div>';
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
        input.value = '';

        try {
          const response = await ai.models.generateContent({
            model: "gemini-2.5-flash", // Model hiện tại (tháng 12/2025)
            contents: userText,
          });
          const text = response.text;

          // Render Markdown đẹp bằng marked.js
          const html = marked.parse(text);

          // Thêm tin nhắn AI
          messagesDiv.innerHTML += 
            '<div class="message ai-message">' +
              html +
            '</div>';
        } catch (error) {
          messagesDiv.innerHTML += 
            '<div class="message ai-message text-danger">' +
              '<strong>Lỗi:</strong> ' + error.message + '<br>' +
              '<small>(Thường do hết quota free tier ~20-50 requests/ngày. Tạo key mới hoặc enable billing)</small>' +
            '</div>';
        }
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
      }

      function openChat() {
        new bootstrap.Modal(document.getElementById('chatModal')).show();
      }

      document.getElementById('chat-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') sendMessage();
      });

      window.openChat = openChat;
      window.sendMessage = sendMessage;
    </script>