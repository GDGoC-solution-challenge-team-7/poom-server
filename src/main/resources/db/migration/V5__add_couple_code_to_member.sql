ALTER TABLE member ADD COLUMN couple_code varchar(6) DEFAULT NULL;
ALTER TABLE member ADD UNIQUE KEY uk_member_couple_code (couple_code);
