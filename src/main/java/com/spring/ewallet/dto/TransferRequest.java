package com.spring.ewallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {

    @NotNull(message = "senderId wajib diisi")
    private Long senderId;

    @NotNull(message = "receiverId wajib diisi")
    private Long receiverId;

    @NotNull(message = "amount wajib diisi")
    @DecimalMin(value = "0.01", message = "amount harus lebih besar dari 0")
    private BigDecimal amount;

    public TransferRequest() {
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
