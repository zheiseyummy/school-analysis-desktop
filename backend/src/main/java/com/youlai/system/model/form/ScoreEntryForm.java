package com.youlai.system.model.form;

import com.youlai.system.model.vo.ScoreEntryVO;
import lombok.Data;

import java.util.List;

@Data
public class ScoreEntryForm {
    private Long examBodyId;

    private Long courseId;

    private List<ScoreEntryVO> scoreList;
}
