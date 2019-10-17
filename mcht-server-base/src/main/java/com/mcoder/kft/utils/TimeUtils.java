package com.mcoder.kft.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
public class TimeUtils {
    public static final String FORMAT_1 = "yyyyMMddHHmmss";

    public static String getCurrentDateString() {
        LocalDateTime ldt = LocalDateTime.now();
        return ldt.format(DateTimeFormatter.ofPattern(FORMAT_1));
    }

    public static void main(String[] args) {
        System.out.println(TimeUtils.getCurrentDateString());
    }
}
