package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.bo.TeacherSexCountBO;
import com.youlai.system.model.entity.SysTeacher;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysTeacherMapper extends BaseMapper<SysTeacher> {



    /**
     * 获取教师的性别统计
     *
     * @return
     */
    List<TeacherSexCountBO> getAllTeacherSexCount();
}
