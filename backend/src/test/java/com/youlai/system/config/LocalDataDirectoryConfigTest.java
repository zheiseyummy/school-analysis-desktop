package com.youlai.system.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDataDirectoryConfigTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsAllWritableDesktopDirectoriesFromOneRoot() throws Exception {
        Path root = temporaryDirectory.resolve("成绩分析系统数据");
        Path database = root.resolve("data/school.db");
        LocalDataDirectoryConfig config = new LocalDataDirectoryConfig(
                root.toString(),
                "jdbc:sqlite:" + database,
                root.resolve("files").toString(),
                root.resolve("logs").toString());

        config.createDirectories();

        for (String directory : new String[]{"data", "backup", "export", "files", "logs"}) {
            assertThat(root.resolve(directory)).isDirectory();
        }
        assertThat(Files.exists(database)).isFalse();
    }

    @Test
    void ignoresInMemoryDatabaseParent() {
        assertThat(LocalDataDirectoryConfig.sqliteDatabaseParent("jdbc:sqlite::memory:")).isNull();
        assertThat(LocalDataDirectoryConfig.sqliteDatabaseParent("jdbc:h2:mem:test")).isNull();
    }
}
