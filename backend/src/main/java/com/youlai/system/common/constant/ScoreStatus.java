package com.youlai.system.common.constant;

/** 成绩单元格状态。BLANK 不落成绩记录，正常 0 分仍使用 NORMAL。 */
public final class ScoreStatus {
    public static final String NORMAL = "NORMAL";
    public static final String ABSENT = "ABSENT";
    public static final String NOT_SELECTED = "NOT_SELECTED";

    private ScoreStatus() { }

    public static boolean isStored(String status) {
        return ABSENT.equals(status) || NOT_SELECTED.equals(status);
    }
}
