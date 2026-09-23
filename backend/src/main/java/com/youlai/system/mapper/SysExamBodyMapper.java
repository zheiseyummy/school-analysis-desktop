package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.SysExamBody;
import com.youlai.system.model.query.ExamBodyPageQuery;
import com.youlai.system.model.vo.ExamBodyPageVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysExamBodyMapper extends BaseMapper<SysExamBody> {

    Page<ExamBodyPageVO> getExamBodyPage(Page<ExamBodyPageVO> page, ExamBodyPageQuery queryParams);
}
