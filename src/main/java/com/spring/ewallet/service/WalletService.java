package com.spring.ewallet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.ewallet.exception.InsufficientBalanceException;
import com.spring.ewallet.exception.InvalidTransferException;
import com.spring.ewallet.exception.UserNotFoundException;
import com.spring.ewallet.model.TransactionLog;
import com.spring.ewallet.model.User;
import com.spring.ewallet.repository.TransactionLogRepository;
import com.spring.ewallet.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final TransactionLogRepository transactionLogRepository;

    public WalletService(UserRepository userRepository,
                          TransactionLogRepository transactionLogRepository) {
        this.userRepository = userRepository;
        this.transactionLogRepository = transactionLogRepository;
    }

    //Mengambil data saldo user berdasarkan id
    @Transactional(readOnly = true)
    public User getBalance(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    //Transfer saldo antar user secara atomic
    //Baris user dikunci (SELECT ... FOR UPDATE) dengan urutan id mencegah deadlock saat ada transfer konkuren
    @Transactional
    public TransactionLog transfer(Long senderId, Long receiverId, BigDecimal amount) {
        if (senderId == null || receiverId == null) {
            throw new InvalidTransferException("senderId dan receiverId wajib diisi");
        }
        if (senderId.equals(receiverId)) {
            throw new InvalidTransferException("senderId dan receiverId tidak boleh sama");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Jumlah transfer harus lebih besar dari 0");
        }

        Long firstId = senderId < receiverId ? senderId : receiverId;
        Long secondId = senderId < receiverId ? receiverId : senderId;

        User first = userRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new UserNotFoundException(firstId));
        User second = userRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new UserNotFoundException(secondId));

        User sender = sender(first, second, senderId);
        User receiver = sender(first, second, receiverId);

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(senderId);
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        TransactionLog log = new TransactionLog(senderId, receiverId, amount, LocalDateTime.now());
        return transactionLogRepository.save(log);
    }

    private User sender(User a, User b, Long targetId) {
        return a.getId().equals(targetId) ? a : b;
    }
}
