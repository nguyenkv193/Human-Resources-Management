package com.extreme.humanresources.user.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("Username đã tồn tại: " + username);
    }
}
