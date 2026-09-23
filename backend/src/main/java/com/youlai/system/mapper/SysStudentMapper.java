package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.bo.StudentSexCountBO;
import com.youlai.system.model.entity.SysStudent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysStudentMapper extends BaseMapper<SysStudent> {

    /**
     * 获取学生的性别数量统计
     *
     * @return
     */
    List<StudentSexCountBO> getAllStudentSexCount();
}
