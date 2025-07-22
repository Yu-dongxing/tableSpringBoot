package com.wzz.table.utils;

import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * 日期时间字符串解析工具
 */
@Log4j2
public class DateTimeUtil {

    private static final List<String> DATETIME_FORMATS = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy.MM.dd",
            "yyyy-MM",
            "yyyy/MM",
            "yyyy.MM",
            "MM-dd HH:mm:ss",
            "MM/dd HH:mm:ss",
            "MM.dd HH:mm:ss",
            "MM-dd",
            "MM/dd",
            "MM.dd",
            "HH:mm:ss",
            "HH:mm"
    );

    /**
     * 尝试将日期字符串转换为 LocalDateTime
     * @param dateTimeStr 日期字符串
     * @return LocalDateTime，失败返回 null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        log.info("日期字符串转换中：" + dateTimeStr);
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            log.info("日期字符串为空");
            return null;
        }

        dateTimeStr = dateTimeStr.trim();

        for (String format : DATETIME_FORMATS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

                // 带年月日时分秒
                if (format.contains("yyyy") && format.contains("MM") && format.contains("dd") && (format.contains("HH") || format.contains("mm") || format.contains("ss"))) {
                    return LocalDateTime.parse(dateTimeStr, formatter);
                }
                // 只有年月日
                else if (format.contains("yyyy") && format.contains("MM") && format.contains("dd")) {
                    LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                    return date.atStartOfDay();
                }
                // 只有年月
                else if (format.contains("yyyy") && format.contains("MM") && !format.contains("dd")) {
                    YearMonth ym = YearMonth.parse(dateTimeStr, formatter);
                    return ym.atDay(1).atStartOfDay();
                }
                // 只有月日时分秒
                else if (format.contains("MM") && format.contains("dd") && (format.contains("HH") || format.contains("mm") || format.contains("ss"))) {
                    int year = Year.now().getValue();
                    LocalDate date = LocalDate.parse(year + "-" + dateTimeStr.substring(0, 5), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String timePart = dateTimeStr.substring(6);
                    LocalTime time = LocalTime.parse(timePart, DateTimeFormatter.ofPattern(format.substring(6)));
                    return LocalDateTime.of(date, time);
                }
                // 只有月日
                else if (format.contains("MM") && format.contains("dd")) {
                    int year = Year.now().getValue();
                    LocalDate date = LocalDate.parse(year + "-" + dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    return date.atStartOfDay();
                }
                // 只有时分秒
                else if (format.contains("HH")) {
                    LocalTime time = LocalTime.parse(dateTimeStr, formatter);
                    return LocalDateTime.of(LocalDate.now(), time);
                }
            } catch (DateTimeParseException | StringIndexOutOfBoundsException ignored) {
                log.info("日期字符串解析错误");
                // 忽略本格式解析错误
            }
        }
        return null;
    }

    // 简单测试
    public static void main(String[] args) {
        String[] testStrs = {
                "2025-07-22 15:32:11",
                "2025/07/22 15:32:11",
                "2025.07.22 15:32:11",
                "2025-07-22",
                "2025/07/22",
                "2025.07.22",
                "2025-07",
                "07-22 15:32:11",
                "07-22",
                "15:32:11",
                "2025.07",
                "07/22 15:32",
                "15:32",
                ""
        };
        for (String s : testStrs) {
            LocalDateTime ldt = parseDateTime(s);
            System.out.println(s + " -> " + (ldt != null ? ldt : "无法解析"));
        }
    }
}
