package com.youlai.system.common.util;

import cn.hutool.core.util.NumberUtil;

public class ScoreUtils {


    public static String renderScore(Double score) {

        return "<span style='color:blue'>" + score + "</span>";
    }

    public static String renderBackground(String degree) {
        if ("A".equals(degree)) {
            return "<div  class=\"letter-circle-success\">" + degree + "</div>";
        } else if ("E".equals(degree)) {
            return "<div  class=\"letter-circle-error\">" + degree + "</div>";
        } else {
            return degree;
        }
    }


    /**
     * 计算等级
     *
     * @param fullScore
     * @param realScore
     * @return
     */
    public static Integer scoreDegreeCalc(Double fullScore, Double realScore) {
        Double rate = realScore / fullScore;
        if (rate >= 0.9D) {
            return 1;
        } else if (rate >= 0.8D) {
            return 2;
        } else if (rate >= 0.7D) {
            return 3;
        } else if (rate >= 0.6D) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 计算优秀率
     *
     * @param aCount
     * @param bCount
     * @param cCount
     * @param dCount
     * @param eCount
     * @return
     */
    public static Double aRate(Integer aCount, Integer bCount, Integer cCount, Integer dCount, Integer eCount) {
        Integer totalCount = aCount + bCount + cCount + dCount + eCount;
        return NumberUtil.round(aCount.doubleValue() / totalCount.doubleValue(), 2).doubleValue();
    }

    /**
     * 计算良好率
     *
     * @param aCount
     * @param bCount
     * @param cCount
     * @param dCount
     * @param eCount
     * @return
     */
    public static Double bRate(Integer aCount, Integer bCount, Integer cCount, Integer dCount, Integer eCount) {
        Integer totalCount = aCount + bCount + cCount + dCount + eCount;
        return NumberUtil.round((aCount.doubleValue() + bCount.doubleValue()) / totalCount.doubleValue(), 2).doubleValue();
    }

    /**
     * 计算中等率
     *
     * @param aCount
     * @param bCount
     * @param cCount
     * @param dCount
     * @param eCount
     * @return
     */
    public static Double cRate(Integer aCount, Integer bCount, Integer cCount, Integer dCount, Integer eCount) {
        Integer totalCount = aCount + bCount + cCount + dCount + eCount;
        return NumberUtil.round((aCount.doubleValue() + bCount.doubleValue() + cCount.doubleValue()) / totalCount.doubleValue(), 2).doubleValue();
    }

    /**
     * 计算合格率
     *
     * @param aCount
     * @param bCount
     * @param cCount
     * @param dCount
     * @param eCount
     * @return
     */
    public static Double dRate(Integer aCount, Integer bCount, Integer cCount, Integer dCount, Integer eCount) {
        Integer totalCount = aCount + bCount + cCount + dCount + eCount;
        return NumberUtil.round((aCount.doubleValue() + bCount.doubleValue() + cCount.doubleValue() + dCount.doubleValue()) / totalCount.doubleValue(), 2).doubleValue();
    }

    /**
     * 计算不合格率
     *
     * @param aCount
     * @param bCount
     * @param cCount
     * @param dCount
     * @param eCount
     * @return
     */
    public static Double eRate(Integer aCount, Integer bCount, Integer cCount, Integer dCount, Integer eCount) {
        Integer totalCount = aCount + bCount + cCount + dCount + eCount;
        return NumberUtil.round(eCount.doubleValue() / totalCount.doubleValue(), 2).doubleValue();
    }
}
