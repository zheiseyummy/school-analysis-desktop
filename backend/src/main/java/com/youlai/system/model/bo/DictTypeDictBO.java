package com.youlai.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "类型下字典数据情况统计")
public class DictTypeDictBO {

    @Schema(description = "字典类型值")
    private String typeCode;

    @Schema(description = "字典数量")
    private Long dictCount;

    @Schema(description = "字典名称列表")
    private String dictNameList;
}
