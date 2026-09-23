package com.youlai.system.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.common.model.Option;
import com.youlai.system.converter.ArchivesConverter;
import com.youlai.system.mapper.SysArchivesMapper;
import com.youlai.system.model.entity.SysArchives;
import com.youlai.system.model.form.ArchivesForm;
import com.youlai.system.model.query.ArchivesPageQuery;
import com.youlai.system.model.vo.ArchivesPageVO;
import com.youlai.system.service.SysArchivesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学情档案业务接口实现
 */
@Service
@RequiredArgsConstructor
public class SysArchivesServiceImpl extends ServiceImpl<SysArchivesMapper, SysArchives> implements SysArchivesService {

    private final ArchivesConverter archivesConverter;


    @Override
    public Page<ArchivesPageVO> getArchivesPage(ArchivesPageQuery queryParams) {
        // 查询参数
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();
        Integer year = queryParams.getYear();
        Long clazzId = queryParams.getClazzId();
        Long studentId = queryParams.getStudentId();
        Long teacherId = queryParams.getTeacherId();


        LambdaQueryWrapper<SysArchives> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(year != null, SysArchives::getYear, year);
        queryWrapper.eq(clazzId != null, SysArchives::getClazzId, clazzId);
        queryWrapper.eq(studentId != null, SysArchives::getStudentId, studentId);
        queryWrapper.eq(teacherId != null, SysArchives::getTeacherId, teacherId);
        queryWrapper.and(StrUtil.isNotBlank(keywords),
                        wrapper ->
                                wrapper.like(StrUtil.isNotBlank(keywords), SysArchives::getName, keywords)
                                        .or()
                                        .like(StrUtil.isNotBlank(keywords), SysArchives::getCode, keywords)
                );
        //查询数据
        Page<SysArchives> archivesPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return archivesConverter.entity2Page(archivesPage);
    }

    @Override
    public boolean saveArchives(ArchivesForm archivesForm) {
        String name = archivesForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysArchives>().eq(SysArchives::getName, name));
        Assert.isTrue(nameCount == 0, "学情档案名称已存在");

        String code = archivesForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysArchives>().eq(SysArchives::getCode, code));
        Assert.isTrue(codeCount == 0, "学情档案编号已存在");

        // 实体转换
        SysArchives archives = archivesConverter.form2Entity(archivesForm);
        return save(archives);
    }

    @Override
    public boolean updateArchives(Long archivesId, ArchivesForm archivesForm) {
        String name = archivesForm.getName();
        long nameCount = this.count(new LambdaQueryWrapper<SysArchives>()
                .eq(SysArchives::getName, name)
                .ne(SysArchives::getId, archivesId)
        );
        Assert.isTrue(nameCount == 0, "学情档案名称已存在");

        String code = archivesForm.getCode();
        long codeCount = this.count(new LambdaQueryWrapper<SysArchives>()
                .eq(SysArchives::getCode, code)
                .ne(SysArchives::getId, archivesId));
        Assert.isTrue(codeCount == 0, "学情档案编号已存在");

        // form -> entity
        SysArchives entity = archivesConverter.form2Entity(archivesForm);
        entity.setId(archivesId);

        // 修改学情档案
        return this.updateById(entity);
    }

    @Override
    public ArchivesForm getArchivesForm(Long archivesId) {
        SysArchives entity = this.getById(archivesId);
        return archivesConverter.entity2Form(entity);
    }

    @Override
    public boolean deleteArchivess(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "学情档案删除数据为空");
        List<Long> ids = Arrays.stream(idsStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean deleteArchivess(List<Long> idList) {
        return this.removeByIds(idList);
    }

    @Override
    public List<Option> listArchivesOptions() {
        // 查询数据
        List<SysArchives> archivesList = this.list(new LambdaQueryWrapper<SysArchives>()
                .select(SysArchives::getId, SysArchives::getName)
                .orderByAsc(SysArchives::getSort)
        );

        // 实体转换
        return archivesConverter.entities2Options(archivesList);
    }

    @Override
    public Map<Long, String> allArchivesIdNameMap() {
        List<Option> optionList = listArchivesOptions();
        return optionList.stream().collect(Collectors.toMap(it -> Long.valueOf(it.getValue().toString()), Option::getLabel));
    }
}
