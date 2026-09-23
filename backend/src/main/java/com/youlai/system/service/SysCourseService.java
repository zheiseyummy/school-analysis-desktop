package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.form.CourseForm;
import com.youlai.system.model.query.CoursePageQuery;
import com.youlai.system.model.vo.CoursePageVO;

import java.util.List;
import java.util.Map;

/**
 * 课程业务接口
 */
public interface SysCourseService extends IService<SysCourse> {

    /**
     * 课程分页列表
     *
     * @param queryParams
     * @return
     */
    Page<CoursePageVO> getCoursePage(CoursePageQuery queryParams);

    /**
     * 保存课程
     *
     * @param courseForm
     * @return
     */
    boolean saveCourse(CourseForm courseForm);


    /**
     * 更新课程
     *
     * @param courseForm
     * @return
     */
    boolean updateCourse(Long courseId, CourseForm courseForm);

    /**
     * 查询单个课程
     *
     * @param courseId
     * @return
     */
    CourseForm getCourseForm(Long courseId);

    /**
     * 删除课程列表
     *
     * @param idsStr
     * @return
     */
    boolean deleteCourses(String idsStr);

    /**
     * 删除课程列表
     *
     * @param idList
     * @return
     */
    boolean deleteCourses(List<Long> idList);

    /**
     * 课程下拉列表
     *
     * @return
     */
    List<Option> listCourseOptions();


    Map<Long, String> allCourseIdNameMap();

}
