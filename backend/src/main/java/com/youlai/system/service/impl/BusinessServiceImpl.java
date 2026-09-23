package com.youlai.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.youlai.system.common.base.IBaseEnum;
import com.youlai.system.common.constant.SystemConstants;
import com.youlai.system.common.enums.GenderEnum;
import com.youlai.system.common.model.Column;
import com.youlai.system.common.util.ColumnUtils;
import com.youlai.system.common.util.HistogramUtils;
import com.youlai.system.common.util.ScoreUtils;
import com.youlai.system.converter.ExamConverter;
import com.youlai.system.model.bo.StudentCourseScoreBO;
import com.youlai.system.model.bo.StudentScoreRankingBO;
import com.youlai.system.model.dto.StudentInfo;
import com.youlai.system.model.entity.*;
import com.youlai.system.model.form.ScoreEntryForm;
import com.youlai.system.model.query.ScoreEntryQuery;
import com.youlai.system.model.vo.ExamPageVO;
import com.youlai.system.model.vo.ExamScoreVO;
import com.youlai.system.model.vo.ScoreEntryVO;
import com.youlai.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final SysExamBodyService examBodyService;

    private final SysClazzStudentService clazzStudentService;

    private final SysExamService examService;

    private final SysStudentService studentService;

    private final SysClazzService clazzService;

    private final SysGradeService gradeService;

    private final SysScoreService scoreService;

    private final SysArrangeService arrangeService;

    private final SysCourseService courseService;

    private final SysDictService dictService;

    private final ExamConverter examConverter;

    @Override
    public List<ScoreEntryVO> getScoreEntryList(ScoreEntryQuery query) {

        Long examBodyId = query.getExamBodyId();
        Long courseId = query.getCourseId();

        SysExamBody examBody = examBodyService.getById(examBodyId);
        Long examId = examBody.getExamId();

        SysExam exam = examService.getById(examId);

        Long clazzId = examBody.getGradeClazzId();

        List<Long> studentIdList = clazzStudentService.getStudentIdListBy(clazzId, exam.getYear());
        List<SysStudent> studentList = studentService.listByIds(studentIdList);
        Map<Long, SysScore> scoreMap = scoreService.getScoreMapByExamIdAndClazzIdAndCourseId(examId, clazzId, courseId);
        List<ScoreEntryVO> scoreEntryVOList = new ArrayList<>();
        studentList.forEach(student -> {
            ScoreEntryVO scoreEntryVO = new ScoreEntryVO();
            scoreEntryVO.setStudentId(student.getId());
            scoreEntryVO.setStudentCode(student.getCode());
            scoreEntryVO.setStudentName(student.getName());
            SysScore score = scoreMap.get(student.getId());
            if (score != null) {
                scoreEntryVO.setScore(score.getScore());
            }
            scoreEntryVOList.add(scoreEntryVO);
        });
        return scoreEntryVOList;
    }

    @Override
    public void saveScore(ScoreEntryForm scoreEntryForm) {
        Long examBodyId = scoreEntryForm.getExamBodyId();
        Long courseId = scoreEntryForm.getCourseId();
        SysCourse course = courseService.getById(courseId);
        SysExamBody examBody = examBodyService.getById(examBodyId);
        Long examId = examBody.getExamId();
        Long clazzId = examBody.getGradeClazzId();
        SysClazz clazz = clazzService.getById(clazzId);
        SysGrade grade = gradeService.getById(clazz.getGradeId());
        //考试ID、学生ID、课程ID 成绩表唯一性
        List<ScoreEntryVO> scoreEntryList = scoreEntryForm.getScoreList();
        SysArrange arrange = arrangeService.getOneByClazzIdAndCourseId(clazzId, courseId);
        scoreEntryList.stream().filter(it -> it.getScore() != null).forEach(scoreEntry -> {
            SysScore score = scoreService.getScoreByExamIdAndStudentIdAndCourseId(examId, scoreEntry.getStudentId(), courseId);
            if (score == null) {
                score = new SysScore();
                score.setExamId(examId);
                score.setGradeId(grade.getId());
                score.setGradeName(grade.getName());
                score.setClazzId(clazzId);
                score.setClazzName(clazz.getName());
                score.setStudentId(scoreEntry.getStudentId());
                score.setCourseId(courseId);
                score.setTeacherId(arrange.getTeacherId());
                score.setScore(scoreEntry.getScore());
                score.setDegree(ScoreUtils.scoreDegreeCalc(course.getFullScore().doubleValue(), scoreEntry.getScore()));
                scoreService.save(score);
            } else {
                score.setGradeId(grade.getId());
                score.setGradeName(grade.getName());
                score.setClazzId(clazzId);
                score.setClazzName(clazz.getName());
                score.setTeacherId(arrange.getTeacherId());
                score.setScore(scoreEntry.getScore());
                score.setDegree(ScoreUtils.scoreDegreeCalc(course.getFullScore().doubleValue(), scoreEntry.getScore()));
                scoreService.updateById(score);
            }
        });


    }

    @Override
    public Map<String, Object> clazzExamAllCourseScoreSummaryData(Long examBodyId, Long courseId) {
        Map<String, Object> resultMap = new HashMap<>();
        SysExamBody examBody = examBodyService.getById(examBodyId);
        Long examId = examBody.getExamId();
        SysExam exam = examService.getById(examId);
        ExamPageVO examPageVO = examConverter.entity2VO(exam);
        Map<String, String> typeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_EXAM_TYPE);
        Map<String, String> semesterMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SEMESTER);
        examPageVO.setSemesterStr(semesterMap.get(exam.getSemester().toString()));
        examPageVO.setExamTypeStr(typeMap.get(exam.getExamType()));
        resultMap.put("exam", examPageVO);
        Long clazzId = examBody.getGradeClazzId();
        SysClazz clazz = clazzService.getById(clazzId);
        resultMap.put("clazz", clazz);

        SysGrade grade = gradeService.getById(clazz.getGradeId());
        resultMap.put("grade", grade);

        //获取班级所有的课程列表
        List<Long> courseIdList = courseId != null ? arrangeService.getCourseIdListByClazzId(clazzId, courseId) : arrangeService.getCourseIdListByClazzId(clazzId);
        if (CollectionUtil.isEmpty(courseIdList)) {
            return new HashMap<>();
        }
        List<SysCourse> courseList = courseService.listByIds(courseIdList);
        resultMap.put("courseList", courseList);
        resultMap.put("courseSize", courseList.size());
        List<Long> studentIdList = clazzStudentService.getStudentIdListBy(clazzId, exam.getYear());
        //获取了班级所有的学生列表
        List<SysStudent> studentList = studentService.listByIds(studentIdList);
        resultMap.put("studentSize", studentList.size());

        // 表头数据构造
        List<Column> columns = new ArrayList<>();
        columns.add(ColumnUtils.buildFixedColumn("studentCode", "学号"));
        columns.add(ColumnUtils.buildFixedColumn("studentName", "姓名"));
        columns.add(ColumnUtils.buildFixedColumn("studentSex", "性别"));
        //表头动态列填充课程列表
        columnFillCourse(courseList, columns);

        //获取成绩明细数据
        List<SysScore> scoreList = scoreService.getScoreListByExamIdAndClazzId(examId, clazzId);
        Map<String, SysScore> scoreMap = scoreList.stream().collect(Collectors
                .toMap(it -> "S_" + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

        //获取班级总分排名
        List<StudentScoreRankingBO> rankingList = scoreService.getStudentSummaryScoreRanking(examId, clazzId);
        Map<Long, StudentScoreRankingBO> rankingMap = rankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));

        //获取年级总分排名
        List<StudentScoreRankingBO> gradeRankingList = scoreService.getGradeStudentSummaryScoreRanking(examId, clazz.getGradeId());
        Map<Long, StudentScoreRankingBO> gradeRankingMap = gradeRankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));

        //获取班级单科排名
        List<StudentScoreRankingBO> singleRankingList = scoreService.getStudentCourseScoreRanking(examId, clazzId);
        Map<String, StudentScoreRankingBO> singleRankingMap = singleRankingList.stream().collect(Collectors.toMap(it -> "S_"
                + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

        //获取年级单科排名
        List<StudentScoreRankingBO> gradeSingleRankingList = scoreService.getGradeStudentCourseScoreRanking(examId, clazz.getGradeId());
        Map<String, StudentScoreRankingBO> gradeSingleRankingMap = gradeSingleRankingList.stream().collect(Collectors.toMap(it -> "S_"
                + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));


        Map<String, String> scoreDegreeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SCORE_DEGREE);

        List<String> courseNameList = courseList.stream().map(SysCourse::getName).toList();
        resultMap.put("courseNameList", courseNameList);

        List<StudentCourseScoreBO> studentCourseScoreBOList = new ArrayList<>();

        // 表体数据构造
        List<Map<String, Object>> tableDataList = new ArrayList<>();
        studentList.forEach(student -> {
            Map<String, Object> tableData = new HashMap<>();
            tableData.put("studentCode", student.getCode());
            tableData.put("studentName", student.getName());
            tableData.put("studentSex", IBaseEnum.getLabelByValue(student.getSex(), GenderEnum.class));
            StudentCourseScoreBO studentCourseScoreBO = new StudentCourseScoreBO();
            studentCourseScoreBO.setStudentId(student.getId());
            studentCourseScoreBO.setStudentCode(student.getCode());
            studentCourseScoreBO.setStudentName(student.getName());
            studentCourseScoreBO.setCourseScoreList(new ArrayList<>());
            courseList.forEach(course -> {
                SysScore score = scoreMap.get("S_" + student.getId() + "_" + "C_" + course.getId());
                if (score != null) {
                    if (courseId != null) {
                        tableData.put("C_" + course.getId() + "_Score", score.getScore());
                    } else {
                        tableData.put("C_" + course.getId() + "_Score", ScoreUtils.renderScore(score.getScore()));
                    }
                    String degree = scoreDegreeMap.get(score.getDegree().toString());
                    tableData.put("C_" + course.getId() + "_Degree", ScoreUtils.renderBackground(degree));
                    studentCourseScoreBO.getCourseScoreList().add(score.getScore());
                } else {
                    tableData.put("C_" + course.getId() + "_Score", 0D);
                    tableData.put("C_" + course.getId() + "_Degree", "/");
                    studentCourseScoreBO.getCourseScoreList().add(0D);
                }

                StudentScoreRankingBO singleRanking = singleRankingMap.get("S_"
                        + student.getId() + "_" + "C_" + course.getId());
                if (singleRanking != null) {
                    tableData.put("C_" + course.getId() + "_ClazzRanking", singleRanking.getStudentRank());
                }

                StudentScoreRankingBO gradeSingleRanking = gradeSingleRankingMap.get("S_"
                        + student.getId() + "_" + "C_" + course.getId());
                if (gradeSingleRanking != null) {
                    tableData.put("C_" + course.getId() + "_GradeRanking", gradeSingleRanking.getStudentRank());
                }
            });

            studentCourseScoreBOList.add(studentCourseScoreBO);

            StudentScoreRankingBO rankingBO = rankingMap.get(student.getId());
            if (rankingBO != null) {
                tableData.put("totalScore", rankingBO.getStudentScore());
                tableData.put("clazzRanking", rankingBO.getStudentRank());
            } else {
                tableData.put("totalScore", 0D);
                tableData.put("clazzRanking", 0);
            }

            StudentScoreRankingBO gradeRankingBO = gradeRankingMap.get(student.getId());
            if (gradeRankingBO != null) {
                tableData.put("gradeRanking", gradeRankingBO.getStudentRank());
            } else {
                tableData.put("gradeRanking", 0);
            }

            tableDataList.add(tableData);
        });

        resultMap.put("studentCourseScoreList", studentCourseScoreBOList);
        if (courseId != null) {
            SysCourse course = courseList.stream().filter(it -> it.getId().equals(courseId)).findFirst().get();
            List<Double> singleCourseScoreList = tableDataList.stream().map(it -> (Double) it.get("C_" + courseId + "_Score")).toList();
            resultMap.put("histogramData", HistogramUtils.buildHistogram(course.getFullScore().doubleValue(), singleCourseScoreList));
            resultMap.put("lineTitleArray", HistogramUtils.buildLineTitle(course.getFullScore().doubleValue()));
            resultMap.put("fullScore", course.getFullScore());
            tableDataList.sort((o1, o2) -> Double.compare((Double) o2.get("C_" + courseId + "_Score"), (Double) o1.get("C_" + courseId + "_Score")));
        } else {
            tableDataList.sort((o1, o2) -> Double.compare((Double) o2.get("totalScore"), (Double) o1.get("totalScore")));
        }

        resultMap.put("columns", columns);
        resultMap.put("tableDataList", tableDataList);
        return resultMap;
    }

    @Override
    public Map<String, Object> clazzExamAllCourseScoreSummaryExportData(Long examBodyId) {
        Map<String, Object> resultMap = new HashMap<>();
        SysExamBody examBody = examBodyService.getById(examBodyId);
        Long examId = examBody.getExamId();
        SysExam exam = examService.getById(examId);
        Map<String, String> semesterMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SEMESTER);
        Long clazzId = examBody.getGradeClazzId();
        SysClazz clazz = clazzService.getById(clazzId);

        SysGrade grade = gradeService.getById(clazz.getGradeId());
        List<Long> courseIdList = arrangeService.getCourseIdListByClazzId(clazzId);
        List<SysCourse> courseList = courseService.listByIds(courseIdList);
        List<Long> studentIdList = clazzStudentService.getStudentIdListBy(clazzId, exam.getYear());
        List<SysStudent> studentList = studentService.listByIds(studentIdList);


        //获取成绩明细数据
        List<SysScore> scoreList = scoreService.getScoreListByExamIdAndClazzId(examId, clazzId);
        Map<String, SysScore> scoreMap = scoreList.stream().collect(Collectors
                .toMap(it -> "S_" + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

        //表格标题生成
        resultMap.put("title", exam.getYear() + grade.getName() + clazz.getName() + semesterMap.get(exam.getSemester().toString()) + exam.getName());

        //获取班级总分排名
        List<StudentScoreRankingBO> rankingList = scoreService.getStudentSummaryScoreRanking(examId, clazzId);
        Map<Long, StudentScoreRankingBO> rankingMap = rankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));
        List<List<Object>> resultList = new ArrayList<>();
        //构造第一行数据即是标题行
        List<Object> titleList = new ArrayList<>();
        titleList.add("名次");
        titleList.add("姓名");
        courseList.forEach(course -> titleList.add(course.getName()));
        titleList.add("总分");
        resultList.add(titleList);
        List<Map<String, Object>> tableDataList = new ArrayList<>();
        studentList.forEach(student -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentName", student.getName());
            StudentScoreRankingBO studentScoreRankingBO = rankingMap.get(student.getId());
            if (studentScoreRankingBO != null) {
                map.put("totalScore", studentScoreRankingBO.getStudentScore());
                map.put("clazzRanking", studentScoreRankingBO.getStudentRank());
            } else {
                map.put("totalScore", 0);
                map.put("clazzRanking", rankingList.size() + 1);
            }
            courseList.forEach(course -> {
                SysScore score = scoreMap.get("S_" + student.getId() + "_" + "C_" + course.getId());
                if (score != null) {
                    map.put("C_" + course.getId() + "_Score", score.getScore());
                } else {
                    map.put("C_" + course.getId() + "_Score", 0D);
                }
            });

            tableDataList.add(map);
        });
        tableDataList.sort((o1, o2) -> Double.compare((Double) o2.get("totalScore"), (Double) o1.get("totalScore")));
        tableDataList.forEach(tableData -> {
            List<Object> rowList = new ArrayList<>();
            rowList.add(tableData.get("clazzRanking"));
            rowList.add(tableData.get("studentName"));
            courseList.forEach(course -> {
                rowList.add(tableData.get("C_" + course.getId() + "_Score"));
            });
            rowList.add(tableData.get("totalScore"));
            resultList.add(rowList);
        });

        String[][] resultArray = new String[resultList.size()][];
        for (int i = 0; i < resultList.size(); i++) {
            resultArray[i] = new String[resultList.get(i).size()];
            for (int j = 0; j < resultList.get(i).size(); j++) {
                resultArray[i][j] = resultList.get(i).get(j).toString();
            }
        }
        resultMap.put("result", resultArray);

        return resultMap;
    }

    @Override
    public Map<String, Object> gradeExamAllCourseScoreSummaryData(Long gradeId, Long examId) {
        Map<String, Object> resultMap = new HashMap<>();
        //获取考试信息
        Map<String, String> typeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_EXAM_TYPE);
        Map<String, String> semesterMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SEMESTER);
        SysExam exam = examService.getById(examId);
        ExamPageVO examPageVO = examConverter.entity2VO(exam);
        examPageVO.setSemesterStr(semesterMap.get(exam.getSemester().toString()));
        examPageVO.setExamTypeStr(typeMap.get(exam.getExamType()));
        resultMap.put("exam", examPageVO);

        SysGrade grade = gradeService.getById(gradeId);
        resultMap.put("grade", grade);

        //获取年级所有的课程列表
        List<Long> clazzIdList = clazzService.clazzIdListByGradeId(gradeId);
        List<Long> courseIdList = arrangeService.getCourseIdListByClazzIdList(clazzIdList);
        List<SysCourse> courseList = courseService.listByIds(courseIdList);

        // 表头数据构造
        List<Column> columns = new ArrayList<>();
        columns.add(ColumnUtils.buildFixedColumn("year", "年度"));
        columns.add(ColumnUtils.buildFixedColumn("gradeName", "年级"));
        columns.add(ColumnUtils.buildFixedColumn("clazzName", "班级"));
        columns.add(ColumnUtils.buildFixedColumn("clazzType", "班级类型", 90));
        columns.add(ColumnUtils.buildFixedColumn("studentCode", "学号"));
        columns.add(ColumnUtils.buildFixedColumn("studentName", "姓名"));
        columns.add(ColumnUtils.buildFixedColumn("studentSex", "性别"));
        columnFillCourse(courseList, columns);


        //获取年级所有的学生列表
        List<StudentInfo> studentInfoList = clazzStudentService.getStudentInfoListByGradeIdAndYear(gradeId, exam.getYear());
        Map<String, String> clazzTypeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_CLAZZ_TYPE);


        //获取成绩明细数据
        List<SysScore> scoreList = scoreService.getScoreListByExamIdAndGradeId(examId, gradeId);
        Map<String, SysScore> scoreMap = scoreList.stream().collect(Collectors
                .toMap(it -> "S_" + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));
        Map<String, String> scoreDegreeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SCORE_DEGREE);
        //获取班级总分排名
        List<StudentScoreRankingBO> rankingList = scoreService.getStudentSummaryScoreClazzRanking(examId, gradeId);
        Map<Long, StudentScoreRankingBO> rankingMap = rankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));

        //获取年级总分排名
        List<StudentScoreRankingBO> gradeRankingList = scoreService.getGradeStudentSummaryScoreRanking(examId, gradeId);
        Map<Long, StudentScoreRankingBO> gradeRankingMap = gradeRankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));

        //获取班级单科排名
        List<StudentScoreRankingBO> singleRankingList = scoreService.getStudentCourseScoreClazzRanking(examId, gradeId);
        Map<String, StudentScoreRankingBO> singleRankingMap = singleRankingList.stream().collect(Collectors.toMap(it -> "S_"
                + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

        //获取年级单科排名
        List<StudentScoreRankingBO> gradeSingleRankingList = scoreService.getGradeStudentCourseScoreRanking(examId, gradeId);
        Map<String, StudentScoreRankingBO> gradeSingleRankingMap = gradeSingleRankingList.stream().collect(Collectors.toMap(it -> "S_"
                + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

        // 表体数据构造
        List<Map<String, Object>> tableDataList = new ArrayList<>();
        //循环年级学生列表
        studentInfoList.forEach(studentInfo -> {
            Map<String, Object> tableData = new HashMap<>();
            tableData.put("year", studentInfo.getYear());
            tableData.put("gradeName", studentInfo.getGradeName());
            tableData.put("clazzName", studentInfo.getClazzName());
            tableData.put("clazzType", clazzTypeMap.get(studentInfo.getClazzType()));
            tableData.put("studentCode", studentInfo.getStudentCode());
            tableData.put("studentName", studentInfo.getStudentName());
            tableData.put("studentSex", IBaseEnum.getLabelByValue(studentInfo.getStudentSex(), GenderEnum.class));

            //循环年级课程列表
            courseList.forEach(course -> {
                SysScore score = scoreMap.get("S_" + studentInfo.getStudentId() + "_" + "C_" + course.getId());
                if (score != null) {
                    tableData.put("C_" + course.getId() + "_Score", ScoreUtils.renderScore(score.getScore()));
                    String degree = scoreDegreeMap.get(score.getDegree().toString());
                    tableData.put("C_" + course.getId() + "_Degree", ScoreUtils.renderBackground(degree));
                }

                StudentScoreRankingBO singleRanking = singleRankingMap.get("S_"
                        + studentInfo.getStudentId() + "_" + "C_" + course.getId());
                if (singleRanking != null) {
                    tableData.put("C_" + course.getId() + "_ClazzRanking", singleRanking.getStudentRank());
                }

                StudentScoreRankingBO gradeSingleRanking = gradeSingleRankingMap.get("S_"
                        + studentInfo.getStudentId() + "_" + "C_" + course.getId());
                if (gradeSingleRanking != null) {
                    tableData.put("C_" + course.getId() + "_GradeRanking", gradeSingleRanking.getStudentRank());
                }


            });

            StudentScoreRankingBO rankingBO = rankingMap.get(studentInfo.getStudentId());
            if (rankingBO != null) {
                tableData.put("totalScore", rankingBO.getStudentScore());
                tableData.put("clazzRanking", rankingBO.getStudentRank());
            } else {
                tableData.put("totalScore", 0D);
                tableData.put("clazzRanking", 0);
            }

            StudentScoreRankingBO gradeRankingBO = gradeRankingMap.get(studentInfo.getStudentId());
            if (gradeRankingBO != null) {
                tableData.put("gradeRanking", gradeRankingBO.getStudentRank());
            } else {
                tableData.put("gradeRanking", 0);
            }

            tableDataList.add(tableData);
        });

        tableDataList.sort((o1, o2) -> Double.compare((Double) o2.get("totalScore"), (Double) o1.get("totalScore")));

        resultMap.put("columns", columns);
        resultMap.put("tableDataList", tableDataList);
        return resultMap;
    }

    @Override
    public Map<String, Object> studentAllScoreSummaryData(Long studentId) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> courseIdList = scoreService.getCourseIdListByStudentId(studentId);
        List<Long> examIdList = scoreService.getExamIdListByStudentId(studentId);

        List<SysCourse> courseList = courseService.listByIds(courseIdList);


        List<SysExam> examList = examService.listByIds(examIdList);
        // 表头数据构造
        List<Column> columns = new ArrayList<>();
        columns.add(ColumnUtils.buildFixedColumn("year", "年度"));
        columns.add(ColumnUtils.buildFixedColumn("gradeName", "年级"));
        columns.add(ColumnUtils.buildFixedColumn("clazzName", "班级"));
        columns.add(ColumnUtils.buildFixedColumn("semesterLabel", "学期"));
        columns.add(ColumnUtils.buildFixedColumn("examTypeLabel", "考试类型", 90));
        columns.add(ColumnUtils.buildFixedColumn("examDate", "考试时间", 150));
        columns.add(ColumnUtils.buildFixedColumn("examName", "考试名称", 150));
        columnFillCourse(courseList, columns);

        Map<String, String> typeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_EXAM_TYPE);
        Map<String, String> semesterMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SEMESTER);

        // 表体数据构造
        List<Map<String, Object>> tableDataList = new ArrayList<>();
        Map<String, String> scoreDegreeMap = dictService.mapDictOptions(SystemConstants.DICT_TYPE_SCORE_DEGREE);
        //循环考试
        examList.forEach(exam -> {
            Map<String, Object> tableData = new HashMap<>();
            tableData.put("year", exam.getYear());
            tableData.put("semesterLabel", semesterMap.get(exam.getSemester().toString()));
            tableData.put("examTypeLabel", typeMap.get(exam.getExamType()));
            tableData.put("examDate", exam.getExamDate());
            tableData.put("examName", exam.getName());
            Long clazzId = scoreService.getClazzIdByExamIdAndStudentId(exam.getId(), studentId);
            SysClazz clazz = clazzService.getById(clazzId);
            Long gradeId = scoreService.getGradeIdByClazzIdAndExamIdAndStudent(clazzId, exam.getId(), studentId);
            SysGrade grade = gradeService.getById(gradeId);
            tableData.put("gradeName", grade.getName());
            tableData.put("clazzName", clazz.getName());


            //获取成绩明细数据
            List<SysScore> scoreList = scoreService.getScoreListByExamIdAndClazzId(exam.getId(), clazzId);
            Map<String, SysScore> scoreMap = scoreList.stream().collect(Collectors
                    .toMap(it -> "S_" + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

            //获取班级总分排名
            List<StudentScoreRankingBO> rankingList = scoreService.getStudentSummaryScoreRanking(exam.getId(), clazzId);
            Map<Long, StudentScoreRankingBO> rankingMap = rankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));

            //获取年级总分排名
            List<StudentScoreRankingBO> gradeRankingList = scoreService.getGradeStudentSummaryScoreRanking(exam.getId(), clazz.getGradeId());
            Map<Long, StudentScoreRankingBO> gradeRankingMap = gradeRankingList.stream().collect(Collectors.toMap(StudentScoreRankingBO::getStudentId, it -> it));

            //获取班级单科排名
            List<StudentScoreRankingBO> singleRankingList = scoreService.getStudentCourseScoreRanking(exam.getId(), clazzId);
            Map<String, StudentScoreRankingBO> singleRankingMap = singleRankingList.stream().collect(Collectors.toMap(it -> "S_"
                    + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));

            //获取年级单科排名
            List<StudentScoreRankingBO> gradeSingleRankingList = scoreService.getGradeStudentCourseScoreRanking(exam.getId(), clazz.getGradeId());
            Map<String, StudentScoreRankingBO> gradeSingleRankingMap = gradeSingleRankingList.stream().collect(Collectors.toMap(it -> "S_"
                    + it.getStudentId() + "_" + "C_" + it.getCourseId(), it -> it));


            //循环课程
            courseList.forEach(course -> {
                SysScore score = scoreMap.get("S_" + studentId + "_" + "C_" + course.getId());
                if (score != null) {
                    tableData.put("C_" + course.getId() + "_Score", ScoreUtils.renderScore(score.getScore()));
                    String degree = scoreDegreeMap.get(score.getDegree().toString());
                    tableData.put("C_" + course.getId() + "_Degree", ScoreUtils.renderBackground(degree));

                } else {
                    tableData.put("C_" + course.getId() + "_Score", "/");
                    tableData.put("C_" + course.getId() + "_Degree", "/");

                }

                StudentScoreRankingBO singleRanking = singleRankingMap.get("S_"
                        + studentId + "_" + "C_" + course.getId());
                if (singleRanking != null) {
                    tableData.put("C_" + course.getId() + "_ClazzRanking", singleRanking.getStudentRank());
                }

                StudentScoreRankingBO gradeSingleRanking = gradeSingleRankingMap.get("S_"
                        + studentId + "_" + "C_" + course.getId());
                if (gradeSingleRanking != null) {
                    tableData.put("C_" + course.getId() + "_GradeRanking", gradeSingleRanking.getStudentRank());
                }
            });

            StudentScoreRankingBO rankingBO = rankingMap.get(studentId);
            if (rankingBO != null) {
                tableData.put("totalScore", rankingBO.getStudentScore());
                tableData.put("clazzRanking", rankingBO.getStudentRank());
            } else {
                tableData.put("totalScore", 0D);
                tableData.put("clazzRanking", 0);
            }

            StudentScoreRankingBO gradeRankingBO = gradeRankingMap.get(studentId);
            if (gradeRankingBO != null) {
                tableData.put("gradeRanking", gradeRankingBO.getStudentRank());
            } else {
                tableData.put("gradeRanking", 0);
            }

            tableDataList.add(tableData);
        });

        SysCourse totalCourse = new SysCourse();
        totalCourse.setId(-1L);
        totalCourse.setName("总分");
        List<SysCourse> allCourseList = new ArrayList<>();
        allCourseList.add(totalCourse);
        allCourseList.addAll(courseList);
        resultMap.put("courseList", allCourseList);
        resultMap.put("columns", columns);
        resultMap.put("tableDataList", tableDataList);
        return resultMap;
    }

    @Override
    public Map<String, Object> studentSingleCourseAnalysisData(Long studentId, Long courseId) {
        Map<String, Object> resultMap = new HashMap<>();
        SysStudent student = studentService.getById(studentId);
        SysCourse course = courseService.getById(courseId);
        resultMap.put("student", student);
        if (courseId == -1) {
            course = new SysCourse();
            course.setId(-1L);
            course.setName("总分");
        }
        resultMap.put("course", course);
        List<SysScore> scoreList = courseId != -1 ? scoreService.getScoreListByStudentIdAndCourseId(studentId, courseId) : scoreService.getAllExamSumScoreList(studentId);
        List<Long> examIdList = scoreList.stream().map(SysScore::getExamId).distinct().toList();
        List<SysExam> examList = examService.listByIds(examIdList);
        Map<Long, SysExam> examMap = examList.stream().collect(Collectors.toMap(SysExam::getId, it -> it));
        List<ExamScoreVO> examScoreVOList = new ArrayList<>();
        scoreList.forEach(score -> {
            ExamScoreVO examScoreVO = new ExamScoreVO();
            examScoreVO.setExamId(score.getExamId());
            SysExam exam = examMap.get(score.getExamId());
            if (exam != null) {
                examScoreVO.setExamDate(exam.getExamDate());
                examScoreVO.setExamName(exam.getName());
            }
            examScoreVO.setScore(score.getScore());
            examScoreVOList.add(examScoreVO);
        });
        resultMap.put("examScoreList", examScoreVOList);
        return resultMap;
    }

    private void columnFillCourse(List<SysCourse> courseList, List<Column> columns) {
        courseList.forEach(course -> {
            Column parent = ColumnUtils.buildColumn("C_" + course.getId(), course.getName());
            Column child1 = ColumnUtils.buildColumn("C_" + course.getId() + "_Score", "成绩");
            Column child2 = ColumnUtils.buildColumn("C_" + course.getId() + "_Degree", "等级");
            Column child3 = ColumnUtils.buildColumn("C_" + course.getId() + "_ClazzRanking", "班级排名", 90);
            Column child4 = ColumnUtils.buildColumn("C_" + course.getId() + "_GradeRanking", "年级排名", 90);
            parent.setChildren(Arrays.asList(child1, child2, child3, child4));
            columns.add(parent);
        });
        columns.add(ColumnUtils.buildRightFixedColumn("totalScore", "总分"));
        columns.add(ColumnUtils.buildRightFixedColumn("clazzRanking", "班级排名", 90));
        columns.add(ColumnUtils.buildRightFixedColumn("gradeRanking", "年级排名", 90));
    }
}
