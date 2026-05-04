package gdg.challenge.poom.domain.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtil {
    public static String toTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(createdAt, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "h";
        }

        long days = hours / 24;
        if (days < 7) {
            return days + "d";
        }

        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + "w";
        }

        long months = days / 30;
        if (months < 12) {
            return months + "m";
        }

        long years = days / 365;
        return years + "y";
    }

    public static LocalDateTime calculateNextSendAt(LocalTime alarmTime) {
        LocalDateTime nextSendAt = LocalDate.now().atTime(alarmTime);
        if (nextSendAt.isBefore(LocalDateTime.now())) {
            nextSendAt = nextSendAt.plusDays(1);
        }
        return nextSendAt;
    }
}
