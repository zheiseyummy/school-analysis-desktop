package com.youlai.system.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class QualityScoring {
    public static final String[] SEMESTERS = {"junior_1_1", "junior_1_2", "junior_2_1", "junior_2_2", "junior_3_1", "junior_3_2"};
    public static final String[] DIMENSIONS = {"思想品德", "学业水平", "身心健康", "艺术素养", "实践与创新"};
    private static final double[] MAXIMUM = {4, 6, 6, 9, 10, 15};
    private static final double[] A = {.8, 1.2, 1.2, 1.8, 2, 3};
    private static final double[] B = {.72, 1.08, 1.08, 1.62, 1.8, 2.7};
    private static final double[] C = {.64, .96, .96, 1.44, 1.6, 2.4};
    private QualityScoring() {}
    public static double score(String semester, String level) {
        int i = java.util.Arrays.asList(SEMESTERS).indexOf(semester);
        if (i < 0 || level == null) return 0D;
        return switch (level.toUpperCase()) { case "A" -> A[i]; case "B" -> B[i]; case "C" -> C[i]; default -> 0D; };
    }
    public static Map<String, Double> semesterMaximums() {
        Map<String, Double> result = new LinkedHashMap<>(); for (int i = 0; i < SEMESTERS.length; i++) result.put(SEMESTERS[i], MAXIMUM[i]); return result;
    }
}
