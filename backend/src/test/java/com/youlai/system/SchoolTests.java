package com.youlai.system;

import cn.hutool.json.JSONUtil;
import com.youlai.system.common.model.Option;
import com.youlai.system.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@org.junit.jupiter.api.Tag("legacy-integration")
@SpringBootTest
@Slf4j
public class SchoolTests {
    @Autowired
    private SchoolService schoolService;

    @Test
    public void testSelect01() {
        List<Option<Long>> optionList = schoolService.listGradeClazzOptions();
        System.out.println(JSONUtil.toJsonPrettyStr(optionList));
    }

    @Test
    public void testSelect02() {
        List<Option<Long>> optionList = schoolService.listStudentOptions(3l, null);
        System.out.println(JSONUtil.toJsonPrettyStr(optionList));
    }
}
