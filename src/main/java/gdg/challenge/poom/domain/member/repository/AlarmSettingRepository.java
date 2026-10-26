package gdg.challenge.poom.domain.member.repository;

import gdg.challenge.poom.domain.member.entity.AlarmSetting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {
    Optional<AlarmSetting> findByMemberId(Long memberId);

    @Query("""
        select a from AlarmSetting a
        join fetch a.member
        where a.chatEnabled = true
          and a.chatNextSendAt <= :now
        order by a.chatNextSendAt
    """)
    List<AlarmSetting> findDueChat(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("""
        select a from AlarmSetting a
        join fetch a.member
        where a.journalReminderEnabled = true
          and a.journalNextSendAt <= :now
        order by a.journalNextSendAt
    """)
    List<AlarmSetting> findDueJournal(@Param("now") LocalDateTime now, Pageable pageable);
}
