/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author hades
 */
public class SendOTPResult {

    private boolean success;
    private String message;

    private SendOTPResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static SendOTPResult success(String msg) {
        return new SendOTPResult(true, msg);
    }

    public static SendOTPResult error(String msg) {
        return new SendOTPResult(false, msg);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
