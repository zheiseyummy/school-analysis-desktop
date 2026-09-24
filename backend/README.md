# 成绩分析系统本地后端

这是成绩分析系统的本地 Spring Boot 过渡后端，服务只监听 `127.0.0.1:8989`，免登录运行，数据保存在 `data/school.db`，上传文件保存在 `files/`。

## 本地运行

在仓库根目录执行：

```powershell
cd backend
..\.tools\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

也可以使用根目录的启动脚本。前端开发服务地址为 `http://127.0.0.1:3000`，生产预览地址为 `http://127.0.0.1:4173`。

## 检查与打包

```powershell
$env:JAVA_HOME="..\.tools\jdk-17.0.20.1+1"
..\.tools\apache-maven-3.9.16\bin\mvn.cmd test
..\.tools\apache-maven-3.9.16\bin\mvn.cmd -DskipTests package
```

SQLite 结构以 `src/main/resources/schema.sql` 和启动迁移配置为准。当前版本不需要 MySQL、Redis、MinIO、远程对象存储或服务器部署；最终 Windows EXE 将在 Tauri 阶段接入。
