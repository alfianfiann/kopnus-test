package com.kopnus.transferservice.dto.response;

import com.kopnus.transferservice.enums.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponse {
    private UUID transferId;
    private UUID senderUserId;
    private UUID receiverUserId;
    private BigDecimal amount;
    private TransferStatus status;
}