package com.youlai.system;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.youlai.system.model.dto.StudentInfo;
import com.youlai.system.service.SysClazzStudentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@org.junit.jupiter.api.Tag("legacy-integration")
@SpringBootTest
@Slf4j
public class SysClazzStudentTests {

    @Autowired
    private SysClazzStudentService clazzStudentService;


    @Test
    public void testSelect01() {
        List<Long> studentIdList = clazzStudentService.getStudentIdListBy(3l, DateUtil.thisYear());
        System.out.println(JSONUtil.toJsonPrettyStr(studentIdList));
    }

    @Test
    public void testSelect02() {
        List<StudentInfo> studentInfoList = clazzStudentService.getStudentInfoListByGradeIdAndYear(3L, 2024);
        System.out.println(JSONUtil.toJsonPrettyStr(studentInfoList));
    }
}
