/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author hades
 */
public class UserOTP {

    private long userId;
    private String otpCode;
    private LocalDateTime otpCreatedAt;
    private int sendCount;
    private LocalDateTime lastSend;

    public UserOTP() {
    }

    public UserOTP(long userId, String otpCode, LocalDateTime otpCreatedAt, int sendCount, LocalDateTime lastSend) {
        this.userId = userId;
        this.otpCode = otpCode;
        this.otpCreatedAt = otpCreatedAt;
        this.sendCount = sendCount;
        this.lastSend = lastSend;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getOtpCreatedAt() {
        return otpCreatedAt;
    }

    public void setOtpCreatedAt(LocalDateTime otpCreatedAt) {
        this.otpCreatedAt = otpCreatedAt;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public LocalDateTime getLastSend() {
        return lastSend;
    }

    public void setLastSend(LocalDateTime lastSend) {
        this.lastSend = lastSend;
    }

    @Override
    public String toString() {
        return "UserOTP{"
                + "userId=" + userId
                + ", otpCode='" + otpCode + '\''
                + ", otpCreatedAt=" + otpCreatedAt
                + ", sendCount=" + sendCount
                + ", lastSend=" + lastSend
                + '}';
    }
}
