package com.kopnus.transferservice.service;

import com.kopnus.transferservice.dto.request.ExecuteTransferRequest;
import com.kopnus.transferservice.dto.request.InquiryRequest;
import com.kopnus.transferservice.dto.response.CheckBalanceResponse;
import com.kopnus.transferservice.dto.response.ExecuteTransferResponse;
import com.kopnus.transferservice.dto.response.InquiryResponse;
import com.kopnus.transferservice.entity.Transfer;
import com.kopnus.transferservice.entity.Wallet;
import com.kopnus.transferservice.enums.TransferStatus;
import com.kopnus.transferservice.exception.CheckBalanceException;
import com.kopnus.transferservice.exception.InquiryException;
import com.kopnus.transferservice.exception.TransferException;
import com.kopnus.transferservice.repository.TransferRepository;
import com.kopnus.transferservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;

    @Transactional(readOnly = true)
    public CheckBalanceResponse checkBalance(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CheckBalanceException("Wallet not found"));

        return CheckBalanceResponse.builder()
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .build();
    }

    @Transactional
    public InquiryResponse inquiry(InquiryRequest request) {
        validateInquiryRequest(request);

        Wallet sender = walletRepository
                .findByUserId(request.getSenderUserId())
                .orElseThrow(() -> new InquiryException("Sender not found"));

        Wallet receiver = walletRepository
                .findByUserId(request.getReceiverUserId())
                .orElseThrow(() -> new InquiryException("Receiver not found"));

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InquiryException("Insufficient balance");
        }

        Transfer transfer = Transfer.builder()
                .senderWalletId(sender.getUserId())
                .receiverWalletId(receiver.getUserId())
                .amount(request.getAmount())
                .status(TransferStatus.INQUIRY)
                .createdDate(Instant.now())
                .build();

        transferRepository.save(transfer);

        return InquiryResponse.builder()
                .transferId(transfer.getTransferId())
                .senderUserId(sender.getUserId())
                .receiverUserId(receiver.getUserId())
                .amount(transfer.getAmount())
                .status(transfer.getStatus())
                .build();
    }

    @Transactional
    public ExecuteTransferResponse executeTransfer(ExecuteTransferRequest request) {

        // Langsung update status di awal, mencegah race condition, concurrency-safe
        Transfer transfer = transferRepository.updateStatusOnProcess(request.getTransferId())
                .orElseThrow(() -> new TransferException("Transfer ID either does not exist, is currently being processed, or has already been executed."));

        // Get sender and receiver wallets
        List<Wallet> wallets = walletRepository.findAllByIdForUpdate(List.of(transfer.getSenderWalletId(), transfer.getReceiverWalletId()));

        Wallet senderWallet = wallets.stream()
                .filter(wallet -> wallet.getUserId().equals(transfer.getSenderWalletId()))
                .findFirst()
                .orElseThrow(() -> new TransferException("Sender Wallet Not Found"));

        Wallet receiverWallet = wallets.stream()
                .filter(wallet -> wallet.getUserId().equals(transfer.getReceiverWalletId()))
                .findFirst()
                .orElseThrow(() -> new TransferException("Receiver Wallet Not Found"));

        // Debit sender
        int debitedRows = walletRepository.debit(senderWallet.getUserId(), transfer.getAmount());

        if (debitedRows == 0) {
            throw new TransferException("Insufficient balance");
        }

        // Credit receiver
        int creditedRows = walletRepository.credit(
                receiverWallet.getUserId(),
                transfer.getAmount()
        );

        if (creditedRows == 0) {
            throw new TransferException("Failed to credit receiver wallet");
        }

        // Set status to EXECUTED
        transfer.setStatus(TransferStatus.EXECUTED);
        transfer.setExecutedAt(Instant.now());

        transferRepository.save(transfer);

        return new ExecuteTransferResponse("Transfer Success");
    }

    private void validateInquiryRequest(InquiryRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InquiryException("Invalid amount");
        }

        if (request.getSenderUserId().equals(request.getReceiverUserId())) {
            throw new InquiryException("Cannot transfer to same wallet");
        }
    }
}