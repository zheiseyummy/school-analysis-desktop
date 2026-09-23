# 本地开发构建工具

这些工具仅供开发、测试和制作最终发行物使用。目标绿色目录应自带所需运行环境，个人日常使用无需配置 Maven；当前尚未完成桌面 EXE 打包。

`build-backend.ps1` 优先使用项目 `.tools` 目录内的 JDK 和 Maven，其次使用当前进程已有的 `JAVA_HOME`、`PATH`。脚本仅临时调整当前进程环境，结束时恢复原值，不修改 Windows 全局设置，也不会自动安装工具。

建议目录：

```text
.tools/
  jdk-17.../
    bin/java.exe
  apache-maven-3.../
    bin/mvn.cmd
  maven-repository/
```

在项目目录运行：

```powershell
# 编译并运行后端测试
pwsh -NoProfile -File .\scripts\build-backend.ps1

# 仅编译
.\scripts\build-backend.ps1 -MavenArguments @('compile', '-DskipTests')

# 依赖已下载完整后，断网构建
.\scripts\build-backend.ps1 -Offline
```

首次构建需要从 Maven 仓库下载开源依赖，缓存保存在项目 `.tools/maven-repository`，不会读取或上传学生数据。`-Offline` 只在所需依赖和构建插件已经缓存后才能成功。

工具仅从官方站点下载，解压前必须与官方校验文件核对：

- Microsoft OpenJDK 17 Windows x64 ZIP：[官方下载与 SHA-256](https://learn.microsoft.com/java/openjdk/download)。
- Apache Maven 3 ZIP：[官方下载与 SHA-512](https://maven.apache.org/download.cgi)。

`.tools`、后端 `target`、依赖缓存和运行数据均已列入项目 `.gitignore`，不提交进源码仓库。
