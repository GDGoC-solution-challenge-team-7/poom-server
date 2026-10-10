ALTER TABLE journal
    ADD CONSTRAINT uk_journal_member_date UNIQUE (member_id, journal_date),
    ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    ADD CONSTRAINT chk_journal_visibility CHECK (visibility IN ('PRIVATE', 'COUPLE'));
