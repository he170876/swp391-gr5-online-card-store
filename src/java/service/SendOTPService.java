/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.UserOTPDAO;
import java.time.LocalDateTime;
import model.SendOTPResult;
import model.User;
import model.UserOTP;
import util.OTPGenerator;

/**
 *
 * @author hades
 */
public class SendOTPService {

    private static final int OTP_EXPIRE_MINUTES = 5;

    // ===== METHOD CHUNG =====
    private SendOTPResult sendOTP(
            User user,
            String subject,
            String contentPrefix
    ) {

        UserOTPDAO otpDAO = new UserOTPDAO();
        UserOTP lastOtp = otpDAO.getByUserId(user.getId());
        LocalDateTime now = LocalDateTime.now();

        if (lastOtp != null) {

            if (lastOtp.getLastSend().plusSeconds(60).isAfter(now)) {
                return SendOTPResult.error("Vui lòng đợi 60 giây trước khi gửi lại OTP!");
            }

            if (lastOtp.getSendCount() >= 5) {

                LocalDateTime blockUntil = lastOtp.getLastSend().plusMinutes(30);

                if (blockUntil.isAfter(now)) {
                    long minutesLeft = java.time.Duration.between(now, blockUntil).toMinutes();
                    long secondsLeft = java.time.Duration.between(now, blockUntil).getSeconds() % 60;

                    return SendOTPResult.error(
                            "Bạn đã gửi quá nhiều lần! Vui lòng đợi "
                            + minutesLeft + " phút " + secondsLeft + " giây."
                    );
                }

                otpDAO.deleteOTP(user.getId());
            }
        }

        String otp = new OTPGenerator().getOtpCode();

        UserOTP newOtp = new UserOTP();
        newOtp.setUserId(user.getId());
        newOtp.setOtpCode(otp);

        otpDAO.insertOrUpdate(newOtp);

        try {
            EmailService.sendEmail(
                    user.getEmail(),
                    subject,
                    contentPrefix + otp + "\nCó hiệu lực trong "
                    + OTP_EXPIRE_MINUTES + " phút."
            );
        } catch (Exception e) {
            e.printStackTrace();
            return SendOTPResult.error("Không thể gửi OTP. Vui lòng thử lại!");
        }

        return SendOTPResult.success("Mã OTP đã được gửi!");
    }

    // ===== FORGOT PASSWORD =====
    public SendOTPResult sendForgotPasswordOTP(User user) {
        return sendOTP(
                user,
                "Khôi phục mật khẩu",
                "Mã OTP khôi phục mật khẩu của bạn là: "
        );
    }

    // ===== REGISTER =====
    public SendOTPResult sendRegisterOTP(User user) {
        return sendOTP(
                user,
                "Xác minh tài khoản",
                "Mã OTP xác minh tài khoản của bạn là: "
        );
    }
}
