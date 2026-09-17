package com.kopnus.transferservice.controller;

import com.kopnus.transferservice.dto.request.ExecuteTransferRequest;
import com.kopnus.transferservice.dto.request.InquiryRequest;
import com.kopnus.transferservice.dto.response.CheckBalanceResponse;
import com.kopnus.transferservice.dto.response.ExecuteTransferResponse;
import com.kopnus.transferservice.dto.response.InquiryResponse;
import com.kopnus.transferservice.service.TransferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
public class TransferController {

    @Autowired
    TransferService transferService;

    @GetMapping("/check-balance/{userId}")
    public CheckBalanceResponse checkBalance(@PathVariable UUID userId) {
        return transferService.checkBalance(userId);
    }

    @PostMapping("/inquiry")
    public InquiryResponse inquiry(@Valid @RequestBody InquiryRequest request) {
        return transferService.inquiry(request);
    }

    @PostMapping("/transfer")
    public ExecuteTransferResponse executeTransfer(@Valid @RequestBody ExecuteTransferRequest request) {
        return transferService.executeTransfer(request);
    }
}