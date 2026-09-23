package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.bo.ClazzStudentBO;
import com.youlai.system.model.bo.ClazzYearBO;
import com.youlai.system.model.bo.StudentClazzBO;
import com.youlai.system.model.dto.StudentInfo;
import com.youlai.system.model.entity.SysClazzStudent;

import java.util.List;
import java.util.Map;

public interface SysClazzStudentService extends IService<SysClazzStudent> {


    /**
     * 保存学生班级年段关系
     *
     * @param studentId
     * @param clazzId
     * @param year
     * @return
     */
    boolean saveOrUpdateClazzStudent(Long studentId, Long clazzId, Integer year);

    /**
     * 根据学生ID查询学生的班级设置
     *
     * @param studentId
     * @return
     */
    List<ClazzYearBO> getByStudentId(Long studentId);


    /**
     * 根据学生ID删除该学生的班级设置
     *
     * @param studentId
     */
    void deleteByStudentId(Long studentId);

    /**
     * 获取班级下学生数量和姓名列表
     *
     * @param year
     * @return
     */
    List<ClazzStudentBO> getAllClazzStudentCountNameList(Integer year);


    /**
     * 获取班级下学生数量和姓名列表
     *
     * @param year
     * @return
     */
    Map<Long, ClazzStudentBO> getAllClazzStudentCountNameMap(Integer year);


    /**
     * 根据班级和年度查询学生ID集合
     *
     * @param clazzId
     * @param year
     * @return
     */
    List<Long> getStudentIdListBy(Long clazzId, Integer year);

    /**
     * 获取学生的就读过的班级情况列表
     *
     * @param studentIdList
     * @return
     */
    List<StudentClazzBO> getStudentClazzCountNameList(List<Long> studentIdList);

    /**
     * 获取学生的就读过的班级情况列表
     *
     * @param studentIdList
     * @return
     */
    Map<Long, StudentClazzBO> getStudentClazzCountNameMap(List<Long> studentIdList);

    /**
     * 获取最新的一条记录
     *
     * @param studentId
     * @return
     */
    SysClazzStudent getLastOneBy(Long studentId);


    /**
     * 获取某一年班级的学生数量
     *
     * @param clazzId
     * @param year
     * @return
     */
    Long studentCountBy(Long clazzId, Integer year);


    /**
     * 删除学生关联信息
     *
     * @param studentId
     */
    void removeByStudentId(Long studentId);


    /**
     * 删除班级关联信息
     *
     * @param clazzId
     */
    void removeByClazzId(Long clazzId);


    /**
     * 根据年级和年度查询年级下的所有学生信息
     *
     * @param gradeId
     * @param year
     * @return
     */
    List<StudentInfo> getStudentInfoListByGradeIdAndYear(Long gradeId, Integer year);

    List<StudentInfo> getStudentInfoListByClazzIdListAndYear(List<Long> clazzIdList, Integer year);

    Long getClazzIdByCondition(List<Long> clazzIdList, Long studentId, Integer year);

    boolean existStudentInClazz(Long clazzId, Long studentId, Integer year);
}
