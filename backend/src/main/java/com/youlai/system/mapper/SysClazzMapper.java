package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.bo.GradeClazzBO;
import com.youlai.system.model.entity.SysClazz;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysClazzMapper extends BaseMapper<SysClazz> {

    List<GradeClazzBO> getAllGradeClazzCountNameList();
}
