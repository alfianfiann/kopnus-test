package com.kopnus.transferservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequest {
    @NotNull
    private UUID senderUserId;

    @NotNull
    private UUID receiverUserId;

    @NotNull
    private BigDecimal amount;
}