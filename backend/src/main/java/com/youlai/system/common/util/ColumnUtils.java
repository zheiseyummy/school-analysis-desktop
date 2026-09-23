package com.youlai.system.common.util;

import com.youlai.system.common.model.Column;

public class ColumnUtils {
    public static Column buildColumn(String key, String label) {
        return buildColumn(key, label, null);
    }

    public static Column buildFixedColumn(String key, String label, Integer width) {
        Column column = buildColumn(key, label);
        column.setFixed(true);
        column.setWidth(width);
        return column;
    }
    public static Column buildFixedColumn(String key, String label) {
        Column column = buildColumn(key, label);
        column.setFixed(true);
        return column;
    }

    public static Column buildRightFixedColumn(String key, String label) {
        Column column = buildColumn(key, label);
        column.setFixed("right");
        return column;
    }

    public static Column buildRightFixedColumn(String key, String label, Integer width) {
        Column column = buildColumn(key, label, width);
        column.setFixed("right");
        return column;
    }


    public static Column buildColumn(String key, String label, Integer width) {
        Column column = new Column();
        column.setKey(key);
        column.setProp(key);
        column.setLabel(label);
        column.setWidth(width);
        return column;
    }
}
