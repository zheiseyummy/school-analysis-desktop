package com.youlai.system.plugin.easyexcel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.youlai.system.common.base.IBaseEnum;
import com.youlai.system.common.enums.GenderEnum;
import com.youlai.system.common.enums.StatusEnum;
import com.youlai.system.converter.StudentConverter;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.entity.SysGrade;
import com.youlai.system.model.entity.SysStudent;
import com.youlai.system.model.form.StudentForm;
import com.youlai.system.model.vo.StudentImportVO;
import com.youlai.system.service.SysClazzService;
import com.youlai.system.service.SysClazzStudentService;
import com.youlai.system.service.SysGradeService;
import com.youlai.system.service.SysStudentService;
import lombok.extern.slf4j.Slf4j;

/**
 * 学生批量导入
 */
@Slf4j
public class StudentImportListener extends MyAnalysisEventListener<StudentImportVO> {

    // 有效条数
    private int validCount;

    // 无效条数
    private int invalidCount;

    // 导入返回信息
    StringBuilder msg = new StringBuilder();

    private final SysStudentService studentService;

    private final StudentConverter studentConverter;

    private final SysClazzService clazzService;

    private final SysGradeService gradeService;

    private final SysClazzStudentService clazzStudentService;

    public StudentImportListener() {
        this.studentConverter = SpringUtil.getBean(StudentConverter.class);
        this.studentService = SpringUtil.getBean(SysStudentService.class);
        this.clazzService = SpringUtil.getBean(SysClazzService.class);
        this.gradeService = SpringUtil.getBean(SysGradeService.class);
        this.clazzStudentService = SpringUtil.getBean(SysClazzStudentService.class);
    }


    @Override
    public String getMsg() {
        // 总结信息
        String summaryMsg = StrUtil.format("导入学生结束：成功{}条，失败{}条；<br/>{}", validCount, invalidCount, msg);
        return summaryMsg;
    }

    @Override
    public void invoke(StudentImportVO studentImportVO, AnalysisContext analysisContext) {
        // 校验数据
        StringBuilder validationMsg = new StringBuilder();
        if (studentImportVO.getYear() == null) {
            validationMsg.append("入学年份为空；");
        }

        if (studentImportVO.getClazzYear() == null) {
            validationMsg.append("年度为空；");
        }

        if (StrUtil.isBlank(studentImportVO.getGradeName())) {
            validationMsg.append("年级为空；");
        }

        if (StrUtil.isBlank(studentImportVO.getClazzName())) {
            validationMsg.append("班级为空；");
        }

        if (StrUtil.isBlank(studentImportVO.getCode())) {
            validationMsg.append("学号为空；");
        }

        if (StrUtil.isBlank(studentImportVO.getName())) {
            validationMsg.append("学生为空；");
        }


        if (StrUtil.isBlank(studentImportVO.getSexLabel())) {
            validationMsg.append("性别为空；");
        }



        if (validationMsg.isEmpty()) {


            Integer sex = (Integer) IBaseEnum.getValueByLabel(studentImportVO.getSexLabel(), GenderEnum.class);
            studentImportVO.setSex(sex);
            StudentForm studentForm = studentConverter.importVo2Entity(studentImportVO);
            studentForm.setStatus(StatusEnum.ENABLE.getValue());
            SysGrade grade = gradeService.getByGradeName(studentImportVO.getGradeName());

            if (grade == null) {
                invalidCount++;
                msg.append("第").append(validCount + invalidCount).append("行数据保存失败,年级不存在；<br/>");
            } else {
                SysClazz clazz = clazzService.getByClazzName(grade.getId(), studentImportVO.getClazzName());
                if (clazz == null) {
                    invalidCount++;
                    msg.append("第").append(validCount + invalidCount).append("行数据保存失败,班级不存在；<br/>");
                } else {
                    SysStudent db = studentService.getByCode(studentImportVO.getCode());
                    if (db != null) {
                        studentForm.setId(db.getId());
                        studentService.updateStudent(db.getId(), studentForm);
                    } else {
                        studentService.saveStudent(studentForm);
                    }

                    clazzStudentService.saveOrUpdateClazzStudent(studentForm.getId(), clazz.getId(), studentImportVO.getClazzYear());
                    validCount++;
                }
            }
        } else {
            invalidCount++;
            msg.append("第" + (validCount + invalidCount) + "行数据校验失败：").append(validationMsg + "<br/>");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
