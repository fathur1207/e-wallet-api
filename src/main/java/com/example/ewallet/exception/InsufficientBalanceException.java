package com.example.ewallet.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long userId) {
        super("Saldo user dengan id " + userId + " tidak mencukupi untuk melakukan transfer");
    }
}
