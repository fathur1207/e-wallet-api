package com.spring.ewallet.dto;

import java.math.BigDecimal;

public class BalanceResponse {

    private Long userId;
    private String name;
    private BigDecimal balance;

    public BalanceResponse() {
    }

    public BalanceResponse(Long userId, String name, BigDecimal balance) {
        this.userId = userId;
        this.name = name;
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
