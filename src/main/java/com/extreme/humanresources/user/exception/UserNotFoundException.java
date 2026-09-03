package com.extreme.humanresources.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Không tìm thấy user với id: " + id);
    }
}
