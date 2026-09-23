package com.youlai.system;

import cn.hutool.json.JSONUtil;
import com.youlai.system.model.bo.CourseStaticsBO;
import com.youlai.system.model.bo.StudentScoreRankingBO;
import com.youlai.system.service.BusinessService;
import com.youlai.system.service.SysScoreService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@org.junit.jupiter.api.Tag("legacy-integration")
@SpringBootTest
@Slf4j
public class ScoreTests {

    @Autowired
    private SysScoreService scoreService;

    @Autowired
    private BusinessService businessService;


    @Test
    public void test01() {
        List<StudentScoreRankingBO> list = scoreService.getStudentSummaryScoreRanking(13L, 3L);
        System.out.println(JSONUtil.toJsonPrettyStr(list));
    }

    @Test
    public void test02() {
        List<CourseStaticsBO> list = scoreService.getCourseStaticsList(13L, 3L);
        System.out.println(JSONUtil.toJsonPrettyStr(list));
    }


    @Test
    public void test03() {
        List<StudentScoreRankingBO> list = scoreService.getStudentSummaryScoreClazzRanking(13L, 3L);
        System.out.println(JSONUtil.toJsonPrettyStr(list));
    }

    @Test
    public void test04() {
        List<StudentScoreRankingBO> list = scoreService.getStudentCourseScoreClazzRanking(13L, 3L);
        System.out.println(JSONUtil.toJsonPrettyStr(list));
    }

    @Test
    public void test05() {
        List<Long> courseIdList = scoreService.getCourseIdListByStudentId(2L);
        List<Long> examIdList = scoreService.getExamIdListByStudentId(2L);
        System.out.println(JSONUtil.toJsonPrettyStr(courseIdList));
        System.out.println(JSONUtil.toJsonPrettyStr(examIdList));
    }

    @Test
    public void test06() {
        Map<String, Object> resultMap = businessService.clazzExamAllCourseScoreSummaryExportData(267L);
        System.out.println(JSONUtil.toJsonPrettyStr(resultMap));
    }

}
