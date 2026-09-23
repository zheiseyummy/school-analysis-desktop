package com.youlai.system.plugin.easyexcel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.youlai.system.common.base.IBaseEnum;
import com.youlai.system.common.enums.GenderEnum;
import com.youlai.system.common.enums.StatusEnum;
import com.youlai.system.converter.TeacherConverter;
import com.youlai.system.model.entity.SysTeacher;
import com.youlai.system.model.form.TeacherForm;
import com.youlai.system.model.vo.TeacherImportVO;
import com.youlai.system.service.SysTeacherService;
import lombok.extern.slf4j.Slf4j;

/**
 * 教师批量导入
 */
@Slf4j
public class TeacherImportListener extends MyAnalysisEventListener<TeacherImportVO>{


    // 有效条数
    private int validCount;

    // 无效条数
    private int invalidCount;

    // 导入返回信息
    StringBuilder msg = new StringBuilder();

    private final SysTeacherService teacherService;

    private final TeacherConverter teacherConverter;

    public TeacherImportListener() {
        this.teacherConverter = SpringUtil.getBean(TeacherConverter.class);
        this.teacherService = SpringUtil.getBean(SysTeacherService.class);
    }



    @Override
    public String getMsg() {
        // 总结信息
        String summaryMsg = StrUtil.format("导入教师结束：成功{}条，失败{}条；<br/>{}", validCount, invalidCount, msg);
        return summaryMsg;
    }

    @Override
    public void invoke(TeacherImportVO teacherImportVO, AnalysisContext analysisContext) {
        // 校验数据
        StringBuilder validationMsg = new StringBuilder();
        if (StrUtil.isBlank(teacherImportVO.getCode())) {
            validationMsg.append("工号为空；");
        }

        if (StrUtil.isBlank(teacherImportVO.getName())) {
            validationMsg.append("姓名为空；");
        }


        if (StrUtil.isBlank(teacherImportVO.getSexLabel())) {
            validationMsg.append("性别为空；");
        }


        if (validationMsg.isEmpty()) {
            Integer sex = (Integer) IBaseEnum.getValueByLabel(teacherImportVO.getSexLabel(), GenderEnum.class);
            teacherImportVO.setSex(sex);

            TeacherForm teacherForm = teacherConverter.importVo2Entity(teacherImportVO);
            teacherForm.setStatus(StatusEnum.ENABLE.getValue());
            SysTeacher db = teacherService.getByCode(teacherImportVO.getCode());
            if (db != null) {
                teacherForm.setId(db.getId());
                teacherService.updateTeacher(db.getId(), teacherForm);
            } else {
                teacherService.saveTeacher(teacherForm);
            }
            validCount++;

        } else {
            invalidCount++;
            msg.append("第" + (validCount + invalidCount) + "行数据校验失败：").append(validationMsg + "<br/>");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
