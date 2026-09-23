package com.youlai.system;

import com.youlai.system.common.enums.StatusEnum;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.service.SysGradeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@org.junit.jupiter.api.Tag("legacy-integration")
@SpringBootTest
@Slf4j
public class SysGradeTests {

    @Autowired
    private SysGradeService gradeService;

    @Test
    public void testInsert() {
        SysGrade grade = new SysGrade();
        grade.setName("大班");
        grade.setSort(1);
        grade.setStatus(StatusEnum.ENABLE.getValue());
        gradeService.save(grade);
    }
}
