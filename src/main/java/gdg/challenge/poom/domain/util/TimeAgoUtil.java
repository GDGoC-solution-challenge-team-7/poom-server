package gdg.challenge.poom.domain.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeAgoUtil {
    public static String toTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(createdAt, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "s";
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
}
