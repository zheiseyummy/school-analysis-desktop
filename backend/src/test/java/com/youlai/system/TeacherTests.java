package com.youlai.system;

import com.youlai.system.common.util.ExcelUtils;
import com.youlai.system.model.vo.TeacherImportVO;
import com.youlai.system.plugin.easyexcel.TeacherImportListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@org.junit.jupiter.api.Tag("legacy-integration")
@SpringBootTest
@Slf4j
public class TeacherTests {

    @Test
    public void testImport() throws IOException {
        InputStream is = Files.newInputStream(Paths.get("C:\\code\\20240303\\youlai-boot\\src\\main\\resources\\excel-templates\\教师导入模板.xlsx"));
        TeacherImportListener listener = new TeacherImportListener();
        ExcelUtils.importExcel(is, TeacherImportVO.class, listener);
        System.out.println(listener.getMsg());
    }
}
