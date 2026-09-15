package com.spring.ewallet.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.ewallet.dto.BalanceResponse;
import com.spring.ewallet.dto.TransferRequest;
import com.spring.ewallet.dto.TransferResponse;
import com.spring.ewallet.model.TransactionLog;
import com.spring.ewallet.model.User;
import com.spring.ewallet.service.WalletService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long userId) {
        User user = walletService.getBalance(userId);
        BalanceResponse response = new BalanceResponse(user.getId(), user.getName(), user.getBalance());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionLog log = walletService.transfer(
                request.getSenderId(),
                request.getReceiverId(),
                request.getAmount()
        );

        TransferResponse response = new TransferResponse(
                log.getId(),
                log.getSenderId(),
                log.getReceiverId(),
                log.getAmount(),
                log.getCreatedAt(),
                "Transfer berhasil"
        );
        return ResponseEntity.ok(response);
    }
}
