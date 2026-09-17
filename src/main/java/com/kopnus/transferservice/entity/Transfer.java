package com.kopnus.transferservice.entity;

import com.kopnus.transferservice.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfer", schema = "kopnus_test")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transfer_id")
    private UUID transferId;

    @Column(name = "sender_wallet_id")
    private UUID senderWalletId;

    @Column(name = "receiver_wallet_id")
    private UUID receiverWalletId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransferStatus status;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "executed_at")
    private Instant executedAt;
}