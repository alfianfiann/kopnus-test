CREATE TABLE kopnus_test.transfer (
                          transfer_id UUID PRIMARY KEY,
                          sender_wallet_id UUID NOT NULL,
                          receiver_wallet_id UUID NOT NULL,
                          amount NUMERIC(19,2) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          created_date TIMESTAMPTZ DEFAULT now() NOT NULL,
                          executed_at TIMESTAMPTZ,

                          CONSTRAINT chk_transfer_amount CHECK (amount > 0)
);

CREATE INDEX idx_transfer_sender
    ON kopnus_test.transfer(sender_wallet_id);

CREATE INDEX idx_transfer_receiver
    ON kopnus_test.transfer(receiver_wallet_id);