package com.kopnus.transferservice.repository;

import com.kopnus.transferservice.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepository
        extends JpaRepository<Transfer, UUID> {

    @Query(
            value = """
            SELECT *
            FROM transfer
            WHERE id = :transferId
            FOR UPDATE
            """,
            nativeQuery = true
    )
    Optional<Transfer> findByIdForUpdate(@Param("transferId") UUID transferId);

    @Query(
            value = """
            UPDATE kopnus_test.transfer
            SET status = 'ON_PROCESS'
            WHERE transfer_id = :transferId
            AND status = 'INQUIRY'
            RETURNING *
            """,
            nativeQuery = true
    )
    Optional<Transfer> updateStatusOnProcess(
            @Param("transferId") UUID transferId
    );
}