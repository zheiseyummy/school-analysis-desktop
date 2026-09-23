package com.youlai.system.common.model;

import lombok.Data;

import java.util.List;

/**
 * 渲染动态表格表头
 *
 */
@Data
public class Column {

    private String key;

    private String prop;

    private String label;

    private Object fixed = false;

    private String type = "String";

    private Integer width;

    private Boolean sortable = false;

    private List<Column> children;

}
