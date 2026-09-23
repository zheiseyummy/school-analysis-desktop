package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.common.model.Option;
import com.youlai.system.model.entity.SysArchives;
import com.youlai.system.model.form.ArchivesForm;
import com.youlai.system.model.query.ArchivesPageQuery;
import com.youlai.system.model.vo.ArchivesPageVO;

import java.util.List;
import java.util.Map;

/**
 * 学情档案业务接口
 */
public interface SysArchivesService extends IService<SysArchives> {

    /**
     * 学情档案分页列表
     *
     * @param queryParams
     * @return
     */
    Page<ArchivesPageVO> getArchivesPage(ArchivesPageQuery queryParams);

    /**
     * 保存学情档案
     *
     * @param archivesForm
     * @return
     */
    boolean saveArchives(ArchivesForm archivesForm);


    /**
     * 更新学情档案
     *
     * @param archivesForm
     * @return
     */
    boolean updateArchives(Long archivesId, ArchivesForm archivesForm);

    /**
     * 查询单个学情档案
     *
     * @param archivesId
     * @return
     */
    ArchivesForm getArchivesForm(Long archivesId);

    /**
     * 删除学情档案列表
     *
     * @param idsStr
     * @return
     */
    boolean deleteArchivess(String idsStr);

    /**
     * 删除学情档案列表
     *
     * @param idList
     * @return
     */
    boolean deleteArchivess(List<Long> idList);

    /**
     * 学情档案下拉列表
     *
     * @return
     */
    List<Option> listArchivesOptions();


    Map<Long, String> allArchivesIdNameMap();

}
