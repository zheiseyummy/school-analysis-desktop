package com.youlai.system.plugin.easyexcel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.youlai.system.model.entity.*;
import com.youlai.system.model.form.ScoreEntryForm;
import com.youlai.system.model.vo.ScoreEntryVO;
import com.youlai.system.model.vo.ScoreImportVO;
import com.youlai.system.service.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 成绩批量导入
 */
@Slf4j
public class ScoreImportListener extends MyAnalysisEventListener<ScoreImportVO> {
    // 有效条数
    private int validCount;

    // 无效条数
    private int invalidCount;

    // 导入返回信息
    StringBuilder msg = new StringBuilder();

    private final Long examId;

    private final SysGradeService gradeService;

    private final SysClazzService clazzService;

    private final SysStudentService studentService;

    private final SysExamService examService;

    private final SysClazzStudentService clazzStudentService;

    private final SysExamBodyService examBodyService;

    private final SysArrangeService arrangeService;

    private final SysCourseService courseService;

    private final BusinessService businessService;

    public ScoreImportListener(Long examId) {
        this.examId = examId;

        this.gradeService = SpringUtil.getBean(SysGradeService.class);
        this.clazzService = SpringUtil.getBean(SysClazzService.class);
        this.studentService = SpringUtil.getBean(SysStudentService.class);
        this.examService = SpringUtil.getBean(SysExamService.class);
        this.clazzStudentService = SpringUtil.getBean(SysClazzStudentService.class);
        this.examBodyService = SpringUtil.getBean(SysExamBodyService.class);
        this.arrangeService = SpringUtil.getBean(SysArrangeService.class);
        this.courseService = SpringUtil.getBean(SysCourseService.class);
        this.businessService = SpringUtil.getBean(BusinessService.class);
    }


    @Override
    public String getMsg() {
        // 总结信息
        String summaryMsg = StrUtil.format("导入成绩结束：成功{}条，失败{}条；<br/>{}", validCount, invalidCount, msg);
        return summaryMsg;
    }

    @Override
    public void invoke(ScoreImportVO scoreImportVO, AnalysisContext analysisContext) {
        log.info("解析到一条用户数据:{}", JSONUtil.toJsonStr(scoreImportVO));
        // 校验数据
        StringBuilder validationMsg = new StringBuilder();
        if (StrUtil.isBlank(scoreImportVO.getGradeName())) {
            validationMsg.append("年级为空");
        }

        if (StrUtil.isBlank(scoreImportVO.getClazzName())) {
            validationMsg.append("班级为空");
        }

        if (StrUtil.isBlank(scoreImportVO.getStudentCode())) {
            validationMsg.append("学号为空");
        }

        if (StrUtil.isBlank(scoreImportVO.getStudentName())) {
            validationMsg.append("姓名为空");
        }

        if (validationMsg.isEmpty()) {
            SysGrade grade = gradeService.getByGradeName(scoreImportVO.getGradeName());
            if (grade == null) {
                invalidCount++;
                msg.append("第").append(validCount + invalidCount).append("行数据保存失败,年级不存在；<br/>");
            } else {
                SysClazz clazz = clazzService.getByClazzName(grade.getId(), scoreImportVO.getClazzName());
                if (clazz == null) {
                    invalidCount++;
                    msg.append("第").append(validCount + invalidCount).append("行数据保存失败,班级不存在；<br/>");
                } else {
                    SysStudent student = studentService.getByCode(scoreImportVO.getStudentCode());
                    if (student == null) {
                        invalidCount++;
                        msg.append("第").append(validCount + invalidCount).append("行数据保存失败,学生不存在；<br/>");
                    } else {
                        SysExam exam = examService.getById(examId);
                        Boolean exist = clazzStudentService.existStudentInClazz(clazz.getId(), student.getId(), exam.getYear());
                        if (!exist) {
                            invalidCount++;
                            msg.append("第").append(validCount + invalidCount).append("行数据保存失败,班级不存在该学生；<br/>");
                        } else {
                            SysExamBody examBody = examBodyService.getIdByExamIdAndClazzId(examId, clazz.getId());
                            if (examBody == null) {
                                invalidCount++;
                                msg.append("第").append(validCount + invalidCount).append("行数据保存失败,考试不存在该考试对象班级；<br/>");
                            } else {
                                List<Long> courseIdList = arrangeService.getCourseIdListByClazzId(clazz.getId());
                                List<SysCourse> courseList = courseService.listByIds(courseIdList);

                                courseList.forEach(course -> {
                                    if ("语文".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getChineseScore());
                                    } else if ("数学".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getMathScore());
                                    } else if ("英语".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getEnglishScore());
                                    } else if ("物理".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getPhysicsScore());
                                    } else if ("化学".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getChemistryScore());
                                    } else if ("生物".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getOrganismScore());
                                    } else if ("地理".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getGeographyScore());
                                    } else if ("历史".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getHistoryScore());
                                    } else if ("政治".equals(course.getName())) {
                                        saveScore(examBody, student, course, scoreImportVO.getPoliticsScore());
                                    }

                                });
                                validCount++;
                            }
                        }
                    }
                }
            }
        } else {
            invalidCount++;
            msg.append("第" + (validCount + invalidCount) + "行数据校验失败：").append(validationMsg + "<br/>");
        }

    }

    private void saveScore(SysExamBody examBody, SysStudent student, SysCourse course, Double score) {
        if (score != null && (score < 0 || (course.getFullScore() != null && score > course.getFullScore()))) {
            invalidCount++;
            msg.append("学生").append(student.getName()).append("的").append(course.getName())
                    .append("成绩超出有效范围；<br/>");
            return;
        }
        ScoreEntryForm scoreEntryForm = new ScoreEntryForm();
        scoreEntryForm.setExamBodyId(examBody.getId());
        scoreEntryForm.setCourseId(course.getId());
        ScoreEntryVO scoreEntryVO = new ScoreEntryVO();
        scoreEntryVO.setStudentId(student.getId());
        scoreEntryVO.setScore(score);
        scoreEntryForm.setScoreList(List.of(scoreEntryVO));
        businessService.saveScore(scoreEntryForm);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
