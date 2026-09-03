package com.extreme.humanresources.user.exception;

public class InvalidCurrentPasswordException extends RuntimeException {

    public InvalidCurrentPasswordException() {
        super("Mật khẩu hiện tại không chính xác");
    }
}
