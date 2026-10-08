ALTER TABLE couple
    ADD COLUMN disconnected_at TIMESTAMP NULL,
    ADD COLUMN delete_scheduled_at TIMESTAMP NULL,
    ADD CONSTRAINT chk_member_order CHECK (member_a_id < member_b_id),
    ADD CONSTRAINT uq_couple_pair UNIQUE (member_a_id, member_b_id);
