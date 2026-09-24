package com.youlai.system.model.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScoreImportConfirmForm {
    @NotBlank(message = "导入预览已失效，请重新上传")
    private String token;
    /** 有错误行时仍允许用户确认提交有效行；错误行永远不会写入。 */
    private Boolean allowErrors = false;
}
