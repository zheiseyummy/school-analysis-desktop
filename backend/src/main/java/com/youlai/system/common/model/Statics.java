package com.youlai.system.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Statics {

    @Schema(description = "最高分")
    private Double maxScore;

    @Schema(description = "最低分")
    private Double minScore;

    @Schema(description = "平均分")
    private Double avgScore;

    @Schema(description = "优秀数量")
    @JsonProperty("aCount")
    private Integer aCount;
    @Schema(description = "良好数量")
    @JsonProperty("bCount")
    private Integer bCount;
    @Schema(description = "中等数量")
    @JsonProperty("cCount")
    private Integer cCount;
    @Schema(description = "合格数量")
    @JsonProperty("dCount")
    private Integer dCount;
    @Schema(description = "不合格数量")
    @JsonProperty("eCount")
    private Integer eCount;
    @Schema(description = "优秀率")
    @JsonProperty("aRate")
    private Double aRate;
    @Schema(description = "良好率")
    @JsonProperty("bRate")
    private Double bRate;
    @Schema(description = "中等率")
    @JsonProperty("cRate")
    private Double cRate;
    @Schema(description = "合格率")
    @JsonProperty("dRate")
    private Double dRate;
    @Schema(description = "不合格率")
    @JsonProperty("eRate")
    private Double eRate;
}
