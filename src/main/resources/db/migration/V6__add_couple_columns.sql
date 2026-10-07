ALTER TABLE couple
    ADD COLUMN disconnected_at TIMESTAMP NULL,
    ADD COLUMN delete_scheduled_at TIMESTAMP NULL;
