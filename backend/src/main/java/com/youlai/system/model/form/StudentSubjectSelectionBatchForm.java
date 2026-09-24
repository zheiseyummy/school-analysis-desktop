package com.youlai.system.model.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class StudentSubjectSelectionBatchForm {
    @NotEmpty(message = "选科记录不能为空")
    @Valid
    private List<StudentSubjectSelectionRecordForm> records;
}
