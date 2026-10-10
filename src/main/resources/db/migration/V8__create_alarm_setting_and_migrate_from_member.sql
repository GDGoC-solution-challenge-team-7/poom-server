-- 1. alarm_setting 테이블 생성
CREATE TABLE alarm_setting (
    alarm_setting_id          BIGINT      NOT NULL AUTO_INCREMENT,
    all_enabled               BOOLEAN     NOT NULL,
    chat_enabled              BOOLEAN     NOT NULL,
    chat_alarm_time           TIME        NOT NULL,
    chat_next_send_at         DATETIME(6) NULL,
    chat_last_send_at         DATETIME(6) NULL,
    shared_journal_enabled    BOOLEAN     NOT NULL,
    journal_reminder_enabled  BOOLEAN     NOT NULL,
    journal_reminder_time     TIME        NOT NULL,
    journal_next_send_at      DATETIME(6) NULL,
    journal_last_send_at      DATETIME(6) NULL,
    created_at                DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),  -- BaseEntity에 맞게 조정
    updated_at                DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),  -- BaseEntity에 맞게 조정
    member_id                 BIGINT      NOT NULL,
    PRIMARY KEY (alarm_setting_id),
    CONSTRAINT uk_alarm_setting_member UNIQUE (member_id),
    CONSTRAINT fk_alarm_setting_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
        ON DELETE CASCADE
);

-- 2. 기존 Member 데이터를 alarm_setting으로 이전
INSERT INTO alarm_setting (
    all_enabled,
    chat_enabled,
    chat_alarm_time,
    chat_next_send_at,
    chat_last_send_at,
    shared_journal_enabled,
    journal_reminder_enabled,
    journal_reminder_time,
    journal_next_send_at,
    journal_last_send_at,
    member_id
)
SELECT
    m.push_alarm,            -- all_enabled
    m.push_alarm,            -- chat_enabled (전체 알림 값 따라감)
    m.daily_alarm_time,      -- chat_alarm_time
    m.next_send_at,          -- chat_next_send_at
    m.last_send_at,          -- chat_last_send_at
    m.push_alarm,            -- shared_journal_enabled
    m.push_alarm,            -- journal_reminder_enabled
    m.daily_alarm_time,      -- journal_reminder_time
    m.daily_alarm_time,                    -- journal_next_send_at (신규)
    m.last_send_at,                    -- journal_last_send_at (신규)
    m.member_id
FROM member m;
