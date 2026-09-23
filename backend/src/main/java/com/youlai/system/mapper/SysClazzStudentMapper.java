package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.bo.ClazzStudentBO;
import com.youlai.system.model.bo.StudentClazzBO;
import com.youlai.system.model.dto.StudentInfo;
import com.youlai.system.model.entity.SysClazzStudent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysClazzStudentMapper extends BaseMapper<SysClazzStudent> {

    /**
     * 获取班级下学生数量和姓名列表
     *
     * @param year
     * @return
     */
    List<ClazzStudentBO> getAllClazzStudentCountNameList(Integer year);


    /**
     * 获取学生下就读班级情况列表
     *
     * @param studentIdList
     * @return
     */
    List<StudentClazzBO> getStudentClazzCountNameList(List<Long> studentIdList);


    /**
     * 根据年级和年度查询年级下的所有学生信息
     *
     * @param gradeId
     * @param year
     * @return
     */
    List<StudentInfo> getStudentInfoListByGradeIdAndYear(Long gradeId, Integer year);

    /**
     * 根据班级ID集合和年度查询班级ID集合下的所有学生信息
     *
     * @param clazzIdList
     * @param year
     * @return
     */
    List<StudentInfo> getStudentInfoListByClazzIdListAndYear(List<Long> clazzIdList, Integer year);
}
