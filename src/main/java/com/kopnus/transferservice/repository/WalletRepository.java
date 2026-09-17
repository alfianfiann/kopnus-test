package com.kopnus.transferservice.repository;

import com.kopnus.transferservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUserId(UUID userId);

    @Query(
            value = """
            SELECT *
            FROM kopnus_test.wallet
            WHERE user_id = :userId
            FOR UPDATE
            """,
            nativeQuery = true
    )
    Optional<Wallet> findByUserIdForUpdate(
            @Param("userId") UUID userId
    );

    @Query(
            value = """
            SELECT *
            FROM kopnus_test.wallet
            WHERE user_id IN (:walletIds)
            ORDER BY user_id
            FOR UPDATE
            """,
            nativeQuery = true
    )
    List<Wallet> findAllByIdForUpdate(
            @Param("walletIds") List<UUID> walletIds
    );

    @Modifying
    @Query(
            value = """
            UPDATE kopnus_test.wallet
            SET balance = balance - :amount
            WHERE user_id = :senderId
              AND balance >= :amount
            """,
            nativeQuery = true
    )
    int debit(
            @Param("senderId") UUID senderId,
            @Param("amount") java.math.BigDecimal amount
    );

    @Modifying
    @Query(
            value = """
            UPDATE kopnus_test.wallet
            SET balance = balance + :amount
            WHERE user_id = :receiverId
            """,
            nativeQuery = true
    )
    int credit(
            @Param("receiverId") UUID receiverId,
            @Param("amount") java.math.BigDecimal amount
    );
}