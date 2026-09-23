package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.bo.DictTypeDictBO;
import com.youlai.system.model.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {

    /**
     * 获取字典类型下字典情况统计列表
     *
     * @return
     */
    List<DictTypeDictBO> getAllDictTypeDictList();

}




