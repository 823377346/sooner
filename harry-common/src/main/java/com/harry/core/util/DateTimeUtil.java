package com.harry.core.util;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * @author Tony Luo
 */
public abstract class DateTimeUtil {


    /**
     * Valid date time format :
     * yyyy-MM-dd, yyyy-MM-dd HH:mm, yyyy-MM-dd HH:mm:ss,
     * yyyyMMdd, yyyyMMdd HH:mm, yyyyMMdd HH:mm:ss,'2011-12-23T10:15:30Z'
     * @param dateTime
     * @return instant
     */
    public static Instant of(String dateTime) {
        return getInstant(dateTime);
    }

    /**
     * Valid date time format :
     * yyyy-MM-dd, yyyy-MM-dd HH:mm, yyyy-MM-dd HH:mm:ss,
     * yyyyMMdd, yyyyMMdd HH:mm, yyyyMMdd HH:mm:ss,'2011-12-23T10:15:30Z'
     * @param dateTime
     * @return instant
     */
    public static Instant getInstant(String dateTime) {
        return DateTimeFormat.of(dateTime);
    }


    /**
     * getCurrentTime
     * @return <code>Instant</code>
     */
    public static Instant getCurrentTime() {
        return  Instant.now();
    }

    /**
     * convert com.google.protobuf.Timestamp to Instant
     *
     * @param timestamp
     * @return instant
     */
    public static Instant toInstant(Timestamp timestamp) {
        if (null == timestamp) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    /**
     * Convert Instant to com.google.protobuf.Timestamp
     *
     * @param instant
     * @return timestamp
     */
    public static Timestamp toTimestamp(Instant instant) {
        if (null == instant) {
            return null;
        }
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond())
            .setNanos(instant.getNano()).build();
    }

    private enum DateTimeFormat {

        /**
         * Valid date time format : yyyy-MM-dd, yyyy-MM-dd HH:mm, yyyy-MM-dd HH:mm:ss,
         * yyyyMMdd, yyyyMMdd HH:mm, yyyyMMdd HH:mm:ss,'2011-12-23T10:15:30Z'
         */

        DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd"), Pattern.compile(
            "^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$")),
        DATE_TIME(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), Pattern.compile(
            "^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30))) (20|21|22|23|[01]\\d|\\d)((:[0-5]\\d))$")),
        DATE_TIME_AS_SECOND(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), Pattern.compile(
            "^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30))) (20|21|22|23|[01]\\d|\\d)((:[0-5]\\d))(:[0-5]\\d)$")),

        DATE_NO_HYPHEN(DateTimeFormatter.ofPattern("yyyyMMdd"), Pattern.compile(
            "^[0-9]{4}(((0[13578]|(10|12))(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)(0[1-9]|[1-2][0-9]|30)))$")),
        DATE_TIME_NO_HYPHEN(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"), Pattern.compile(
            "^[0-9]{4}(((0[13578]|(10|12))(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)(0[1-9]|[1-2][0-9]|30))) (20|21|22|23|[01]\\d|\\d)((:[0-5]\\d))$")),
        DATE_TIME_AS_SECOND_NO_HYPHEN(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"),
            Pattern.compile(
                "^[0-9]{4}(((0[13578]|(10|12))(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)(0[1-9]|[1-2][0-9]|30))) (20|21|22|23|[01]\\d|\\d)((:[0-5]\\d))(:[0-5]\\d)$")),

        /**
         * The ISO instant formatter that formats or parses an instant in UTC, such as
         * '2011-12-03T10:15:30Z'
         */
        ISO_INSTANT(DateTimeFormatter.ISO_INSTANT, Pattern.compile(
            "^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))T(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d))(:[0-5]\\d)Z$"));

        private final Pattern regexPattern;

        private final DateTimeFormatter dateTimeFormatter;

        DateTimeFormat(DateTimeFormatter dateTimeFormatter, Pattern regexPattern) {
            this.dateTimeFormatter = dateTimeFormatter;
            this.regexPattern = regexPattern;
        }

        public static Instant of(String dateTime) {
            DateTimeFormat dateTimeFormat = resolve(dateTime);
            if (dateTimeFormat == null) {
                throw new IllegalArgumentException(
                    "Date time format of [" + dateTime + "] is invalid.");
            }
            if (dateTimeFormat == DateTimeFormat.DATE
                || dateTimeFormat == DateTimeFormat.DATE_NO_HYPHEN) {
                LocalDate localDate = LocalDate.parse(dateTime, dateTimeFormat.dateTimeFormatter);
                // 东八区-北京时间
                return localDate.atStartOfDay().toInstant(ZoneOffset.of("+8"));
            } else if (dateTimeFormat == DateTimeFormat.ISO_INSTANT) {
                return Instant.parse(dateTime);
            }

            LocalDateTime time = LocalDateTime.parse(dateTime, dateTimeFormat.dateTimeFormatter);
            // 东八区-北京时间
            return time.toInstant(ZoneOffset.of("+8"));
        }

        private static DateTimeFormat resolve(String dateTime) {
            DateTimeFormat[] dateTimeFormatArray = values();
            for (DateTimeFormat dateTimeFormat : dateTimeFormatArray) {
                if (dateTimeFormat.regexPattern.matcher(dateTime).matches()) {
                    return dateTimeFormat;
                }
            }
            return null;
        }
    }

}
