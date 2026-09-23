package com.youlai.system.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.system.common.model.Age;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类
 *
 * @author haoxr
 * @since 2.4.2
 */
public class DateUtils {


    public static Long yearMonthDayChineseToDays(String yearMonthDayChinese) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*([\u4e00-\u9fa5]+)");
        Long sum = 0L;
        Matcher matcher = pattern.matcher(yearMonthDayChinese);
        while (matcher.find()) {
            String num = matcher.group(1);
            String unit = matcher.group(2);
            switch (unit) {
                case "岁":
                    sum += Long.parseLong(num) * 365;
                    break;
                case "月":
                    sum += Long.parseLong(num) * 31;
                    break;
                case "天":
                    sum += Long.parseLong(num);
            }
        }

        return sum;
    }

    public static Long getRealYearMonthDay(Integer year, Integer month, Integer day) {
        Long sum = 0L;
        if (year != null) {
            sum += year * 365L;
        }

        if (month != null) {
            sum += month * 31L;
        }

        if (day != null) {
            sum += day;
        }

        return sum;
    }

    public static String getChineseAge(LocalDate birthDate) {
        return getChineseAge(birthDate, true, true, true);
    }


    public static String getChineseAge(LocalDate birthDate, boolean year, boolean month, boolean day) {
        Age age = getAgeByBirthDay(birthDate);
        StringBuffer sb = new StringBuffer();
        if (age.getYear() > 0 && year) {
            sb.append(age.getYear()).append("岁");
        }

        if (age.getMonth() > 0 && month) {
            sb.append(age.getMonth()).append("月");
        }

        if (age.getDay() > 0 && day) {
            sb.append(age.getDay()).append("天");
        }

        return sb.toString();
    }

    public static String getChineseAge(Integer year, Integer month, Integer day) {
        StringBuffer sb = new StringBuffer();
        if (year != null) {
            sb.append(year).append("岁");
        }

        if (month != null) {
            sb.append(month).append("月");
        }

        if (day != null) {
            sb.append(day).append("天");
        }

        return sb.toString();
    }


    public static Age getAgeByBirthDay(LocalDate birthDate) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 计算年龄
        Period period = Period.between(birthDate, currentDate);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        if (months < 0) {
            years--;
            months += 12;
        }
        if (days < 0) {
            months--;
            if (months < 0) {
                years--;
                months += 12;
            }
            days += LocalDate.of(birthDate.getYear(), birthDate.getMonthValue(), birthDate.getDayOfMonth()).lengthOfMonth();
        }

        Age age = new Age();
        age.setYear(years);
        age.setMonth(months);
        age.setDay(days);
        return age;
    }


    /**
     * 区间日期格式化为数据库日期格式
     * <p>
     * eg：2021-01-01 → 2021-01-01 00:00:00
     *
     * @param obj                要处理的对象
     * @param startTimeFieldName 起始时间字段名
     * @param endTimeFieldName   结束时间字段名
     */
    public static void toDatabaseFormat(Object obj, String startTimeFieldName, String endTimeFieldName) {
        Field startTimeField = ReflectUtil.getField(obj.getClass(), startTimeFieldName);
        Field endTimeField = ReflectUtil.getField(obj.getClass(), endTimeFieldName);

        if (startTimeField != null) {
            processDateTimeField(obj, startTimeField, startTimeFieldName, "yyyy-MM-dd 00:00:00");
        }

        if (endTimeField != null) {
            processDateTimeField(obj, endTimeField, endTimeFieldName, "yyyy-MM-dd 23:59:59");
        }
    }

    /**
     * 处理日期字段
     *
     * @param obj           要处理的对象
     * @param field         字段
     * @param fieldName     字段名
     * @param targetPattern 目标数据库日期格式
     */
    private static void processDateTimeField(Object obj, Field field, String fieldName, String targetPattern) {
        Object fieldValue = ReflectUtil.getFieldValue(obj, fieldName);
        if (fieldValue != null) {
            // 得到原始的日期格式
            String pattern = field.isAnnotationPresent(DateTimeFormat.class) ? field.getAnnotation(DateTimeFormat.class).pattern() : "yyyy-MM-dd";
            // 转换为日期对象
            DateTime dateTime = DateUtil.parse(StrUtil.toString(fieldValue), pattern);
            // 转换为目标数据库日期格式
            ReflectUtil.setFieldValue(obj, fieldName, dateTime.toString(targetPattern));
        }
    }
}
