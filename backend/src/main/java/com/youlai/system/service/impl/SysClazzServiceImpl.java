package com.youlai.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.ClazzConverter;
import com.youlai.system.mapper.SysClazzMapper;
import com.youlai.system.model.bo.GradeClazzBO;
import com.youlai.system.model.entity.SysClazz;
import com.youlai.system.model.form.ClazzForm;
import com.youlai.system.model.query.ClazzPageQuery;
import com.youlai.system.model.vo.ClazzPageVO;
import com.youlai.system.service.SysClazzService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级业务接口
 */
@Service
@RequiredArgsConstructor
public class SysClazzServiceImpl extends ServiceImpl<SysClazzMapper, SysClazz> implements SysClazzService {
    private final ClazzConverter clazzConverter;

    @Override
    public Page<ClazzPageVO> getClazzPage(ClazzPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();

        Long managerId = queryParams.getManagerId();
        Long gradeId = queryParams.getGradeId();

        LambdaQueryWrapper<SysClazz> queryWrapper = new LambdaQueryWrapper<SysClazz>()
                .eq(managerId != null, SysClazz::getManagerId, managerId)
                .eq(gradeId != null, SysClazz::getGradeId, gradeId)
                .and(StrUtil.isNotBlank(keywords),
                        wrapper ->
                                wrapper.like(StrUtil.isNotBlank(keywords), SysClazz::getName, keywords)
                                        .or().like(StrUtil.isNotBlank(keywords), SysClazz::getCode, keywords));
        //查询数据
        Page<SysClazz> clazzPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);

        return clazzConverter.entity2Page(clazzPage);
    }

    @Override
    public boolean saveClazz(ClazzForm clazzForm) {

        String code = clazzForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysClazz>().eq(SysClazz::getGradeId, clazzForm.getGradeId()).eq(SysClazz::getCode, code));
        Assert.isTrue(codeCount == 0, "班级编号已存在");

        String name = clazzForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysClazz>().eq(SysClazz::getGradeId, clazzForm.getGradeId()).eq(SysClazz::getName, name));
        Assert.isTrue(nameCount == 0, "班级名称已存在");
        // 实体转换
        SysClazz clazz = clazzConverter.form2Entity(clazzForm);
        return save(clazz);
    }

    @Override
    public boolean updateClazz(Long clazzId, ClazzForm clazzForm) {

        String code = clazzForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysClazz>()
                .eq(SysClazz::getGradeId, clazzForm.getGradeId())
                .eq(SysClazz::getCode, code)
                .ne(SysClazz::getId, clazzId)
        );
        Assert.isTrue(codeCount == 0, "班级编号已存在");

        String name = clazzForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysClazz>()
                .eq(SysClazz::getGradeId, clazzForm.getGradeId())
                .eq(SysClazz::getName, name)
                .ne(SysClazz::getId, clazzId)
        );
        Assert.isTrue(nameCount == 0, "班级名称已存在");

        // form -> entity
        SysClazz entity = clazzConverter.form2Entity(clazzForm);

        // 修改班级
        return this.updateById(entity);
    }

    @Override
    public ClazzForm getClazzForm(Long clazzId) {
        SysClazz entity = this.getById(clazzId);
        return clazzConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteClazzs(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "班级删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteClazzs(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listClazzOptions() {
        // 查询数据
        List<SysClazz> clazzList = this.list(new LambdaQueryWrapper<SysClazz>()
                .select(SysClazz::getId, SysClazz::getName)
                .orderByAsc(SysClazz::getSort)
        );

        // 实体转换
        return clazzConverter.entities2Options(clazzList);
    }

    @Override
    public List<GradeClazzBO> getAllGradeClazzCountNameList() {
        return this.baseMapper.getAllGradeClazzCountNameList();
    }

    @Override
    public Map<Long, GradeClazzBO> getAllGradeClazzCountNameMap() {
        return getAllGradeClazzCountNameList().stream().collect(Collectors.toMap(GradeClazzBO::getGradeId, it -> it));
    }

    @Override
    public SysClazz getByClazzName(Long gradeId, String clazzName) {
        LambdaQueryWrapper<SysClazz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazz::getGradeId, gradeId);
        queryWrapper.eq(SysClazz::getName, clazzName);
        return getOne(queryWrapper);
    }

    @Override
    public Long countByGradeIdList(List<Long> gradeIdList) {
        LambdaQueryWrapper<SysClazz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysClazz::getGradeId, gradeIdList);
        return count(queryWrapper);
    }

    @Override
    public List<Long> clazzIdListByGradeId(Long gradeId) {
        LambdaQueryWrapper<SysClazz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClazz::getGradeId, gradeId);
        queryWrapper.select(SysClazz::getId);
        return list(queryWrapper).stream().map(SysClazz::getId).toList();
    }

    @Override
    public List<Long> gradeIdListByClazzIdList(List<Long> clazzIdList) {
        LambdaQueryWrapper<SysClazz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysClazz::getId, clazzIdList);
        queryWrapper.select(SysClazz::getGradeId);
        return list(queryWrapper).stream().map(SysClazz::getGradeId).distinct().toList();
    }
}
