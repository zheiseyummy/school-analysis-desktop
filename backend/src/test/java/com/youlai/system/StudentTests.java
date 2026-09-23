package com.youlai.system;

import com.youlai.system.common.util.ExcelUtils;
import com.youlai.system.model.vo.StudentImportVO;
import com.youlai.system.plugin.easyexcel.StudentImportListener;
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
public class StudentTests {

    @Test
    public void testImport() throws IOException {
        InputStream is = Files.newInputStream(Paths.get("C:\\code\\20240303\\youlai-boot\\src\\main\\resources\\excel-templates\\学生导入模版.xlsx"));
        StudentImportListener listener = new StudentImportListener();
        ExcelUtils.importExcel(is, StudentImportVO.class, listener);
        System.out.println(listener.getMsg());
    }
}
