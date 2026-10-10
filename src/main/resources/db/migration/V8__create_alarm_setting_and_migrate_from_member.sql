-- 1. alarm_setting 테이블 생성
CREATE TABLE alarm_setting (
    alarm_setting_id          BIGINT      NOT NULL AUTO_INCREMENT,
    all_enabled               BOOLEAN     NOT NULL,
    chat_enabled              BOOLEAN     NOT NULL,
    chat_alarm_time           TIME        NOT NULL,
    shared_journal_enabled    BOOLEAN     NOT NULL,
    journal_reminder_enabled  BOOLEAN     NOT NULL,
    journal_reminder_time     TIME        NOT NULL,
    -- 아래 두 컬럼은 ERD에는 없지만 Member의 기존 필드 이전용 (불필요하면 삭제)
    next_send_at              DATETIME(6) NULL,
    last_send_at              DATETIME(6) NULL,
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
    shared_journal_enabled,
    journal_reminder_enabled,
    journal_reminder_time,
    next_send_at,
    last_send_at,
    member_id
)
SELECT
    m.push_alarm,            -- pushAlarm      -> allEnabled
    m.push_alarm,            -- 신규 항목: 전체 알림 값을 따라가도록 초기화
    m.daily_alarm_time,                    -- chatAlarmTime
    m.push_alarm,            -- 신규 항목: 전체 알림 값을 따라가도록 초기화
    m.push_alarm,            -- 신규 항목: 전체 알림 값을 따라가도록 초기화
    m.daily_alarm_time,      -- dailyAlarmTime -> journalReminderTime
    m.next_send_at,
    m.last_send_at,
    m.member_id
FROM member m;
