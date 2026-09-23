package com.youlai.system;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.youlai.system.common.model.Age;
import com.youlai.system.common.util.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HutoolTest {

    @Test
    public void test01() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 设置出生日期（这里假设为1980年5月20日）
        int yearOfBirth = 1991;
        int monthOfBirth = 3;
        int dayOfBirth = 6;
        LocalDate birthdate = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth);

        // 计算两个日期之间的时间段
        Period period = Period.between(birthdate, currentDate);

        // 输出结果
        System.out.println("今天是" + currentDate.getYear() + "年");
        System.out.println("我已经" + period.getYears() + "岁了");
        System.out.println("还有" + (period.getMonths()) + "个月没到十八周岁");

        System.out.println(period.getYears() + "岁" + period.getMonths() + "月" + period.getDays() + "天");
    }


    @Test
    public void test02() {
        // 假设出生日期是1990年1月1日
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 计算年龄
        Period period = Period.between(birthDate, currentDate);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        // 输出结果
        System.out.println("年龄：" + years + "岁");

        // 计算剩余的月份和天数
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

        // 输出结果
        System.out.println("月份：" + months + "个月");
        System.out.println("天数：" + days + "天");
    }

    @Test
    public void test03() {
        //年龄：34岁
        //月份：2个月
        //天数：5天
        // 假设出生日期是1990年1月1日
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Age age = DateUtils.getAgeByBirthDay(birthDate);
        System.out.println(JSONUtil.toJsonPrettyStr(age));
    }

    @Test
    public void test04() {
        String sentence = "3岁4月5天";

        // 定义正则表达式模式，匹配数字部分
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(sentence);

        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void test05() {
        String input = "3岁4月5天";

        // 定义正则表达式模式，匹配中文字符
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]+");

        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }


    @Test
    public void test06() {
        String input = "3 岁 4 月 5 天";

        // 定义正则表达式模式，匹配中文字符
        Pattern pattern = Pattern.compile("\\d+\\s+[\u4e00-\u9fa5]+");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            System.out.println(matcher.group().replaceAll("\\s",""));
        }
    }

    @Test
    public void test07() {
        String input = "3 岁 4 月 5 天";

        // 定义正则表达式模式，匹配中文字符
        Pattern pattern = Pattern.compile("(\\d+)\\s+([\u4e00-\u9fa5]+)");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
           System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }

    @Test
    public void test08() {
        String input = "3 岁 4 月 5 天";
        Long days = DateUtils.yearMonthDayChineseToDays(input);
        System.out.println(days);
    }


    @Test
    public void test09() {
        System.out.println(DateUtil.ageOfNow("1990-01-30"));
    }
}
