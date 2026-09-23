package com.youlai.system.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "班级年度")
@Data
public class ClazzYearBO {

    @Schema(description = "班级Id")
    private Long clazzId;

    @Schema(description = "年度")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer year;
}
