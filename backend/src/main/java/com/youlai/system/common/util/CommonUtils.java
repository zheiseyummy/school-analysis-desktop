package com.youlai.system.common.util;

import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysGrade;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    public static List<Option<String>> getOptions(List<SysGrade> gradeList, List<SysClazz> clazzList) {
        List<Option<String>> optionList = new ArrayList<>();
        gradeList.forEach(grade -> {
            Option<String> parent = new Option<>("G_" + grade.getId(), grade.getName());
            parent.setChildren(new ArrayList<>());

            clazzList.forEach(clazz -> {
                if (grade.getId().equals(clazz.getGradeId())) {
                    Option<String> child = new Option<>("C_" + clazz.getId(), clazz.getName());
                    parent.getChildren().add(child);
                }
            });
            optionList.add(parent);
        });

        return optionList;
    }
}
