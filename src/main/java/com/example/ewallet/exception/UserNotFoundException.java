package com.example.ewallet.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User dengan id " + userId + " tidak ditemukan");
    }
}
