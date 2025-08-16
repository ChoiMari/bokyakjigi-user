package com.yakgurt.bokyakjigi.user.exception;

public class MissingRefreshTokenException extends RuntimeException {
    public  MissingRefreshTokenException(String message) {
        super(message);
    }
}
