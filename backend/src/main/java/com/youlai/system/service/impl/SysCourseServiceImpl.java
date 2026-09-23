package com.youlai.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.CourseConverter;
import com.youlai.system.mapper.SysCourseMapper;
import com.youlai.system.model.entity.SysCourse;
import com.youlai.system.model.form.CourseForm;
import com.youlai.system.model.query.CoursePageQuery;
import com.youlai.system.model.vo.CoursePageVO;
import com.youlai.system.service.SysCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程业务接口实现
 */
@Service
@RequiredArgsConstructor
public class SysCourseServiceImpl extends ServiceImpl<SysCourseMapper, SysCourse> implements SysCourseService {

    private final CourseConverter courseConverter;


    @Override
    public Page<CoursePageVO> getCoursePage(CoursePageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();

        LambdaQueryWrapper<SysCourse> queryWrapper = new LambdaQueryWrapper<SysCourse>()
                .and(StrUtil.isNotBlank(keywords),
                        wrapper ->
                                wrapper.like(StrUtil.isNotBlank(keywords), SysCourse::getName, keywords)
                                        .or()
                                        .like(StrUtil.isNotBlank(keywords), SysCourse::getCode, keywords)
                );
        //查询数据
        Page<SysCourse> coursePage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return courseConverter.entity2Page(coursePage);
    }

    @Override
    public boolean saveCourse(CourseForm courseForm) {
        String name = courseForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysCourse>().eq(SysCourse::getName, name));
        Assert.isTrue(nameCount == 0, "课程名称已存在");

        String code = courseForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysCourse>().eq(SysCourse::getCode, code));
        Assert.isTrue(codeCount == 0, "课程编号已存在");

        // 实体转换
        SysCourse course = courseConverter.form2Entity(courseForm);
        return save(course);
    }

    @Override
    public boolean updateCourse(Long courseId, CourseForm courseForm) {
        String name = courseForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysCourse>()
                .eq(SysCourse::getName, name)
                .ne(SysCourse::getId, courseId)
        );
        Assert.isTrue(nameCount == 0, "课程名称已存在");

        String code = courseForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysCourse>()
                .eq(SysCourse::getCode, code)
                .ne(SysCourse::getId, courseId));
        Assert.isTrue(codeCount == 0, "课程编号已存在");

        // form -> entity
        SysCourse entity = courseConverter.form2Entity(courseForm);

        // 修改课程
        return this.updateById(entity);
    }

    @Override
    public CourseForm getCourseForm(Long courseId) {
        SysCourse entity = this.getById(courseId);
        return courseConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteCourses(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "课程删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteCourses(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listCourseOptions() {
        // 查询数据
        List<SysCourse> courseList = this.list(new LambdaQueryWrapper<SysCourse>()
                .select(SysCourse::getId, SysCourse::getName)
                .orderByAsc(SysCourse::getSort)
        );

        // 实体转换
        return courseConverter.entities2Options(courseList);
    }

    @Override
    public Map<Long, String> allCourseIdNameMap() {
        List<Option> optionList = listCourseOptions();
        return optionList.stream().collect(Collectors.toMap(it -> Long.valueOf(it.getValue().toString()), Option::getLabel));
    }
}
