# 成绩分析系统

这是一个面向个人 Windows 电脑使用的学校成绩分析系统开发仓库。系统保留 Vue 3 + Element Plus + ECharts 界面，后端使用 Spring Boot，业务数据使用本地 SQLite，目标是最终制作成 Windows 10/11 可离线运行的绿色 EXE。

> 当前版本是“本地开发运行版”，还没有完成 Tauri 和最终 EXE 打包。不要把当前 JAR 当成最终发行物。

## 目录

- `frontend/`：Vue 3 前端。
- `backend/`：Spring Boot 本地后端、SQLite 初始化和业务接口。
- `docs/`：需求、进度、核对和交接文档。
- `scripts/`：前端冒烟、接口契约和后端构建脚本。
- `启动本地前端.bat`：Windows 前端开发服务快捷启动。

重要文档：

1. [本次对话需求与开发记录](docs/本次对话需求与开发记录.md)
2. [需求文档逐项核对](docs/需求文档逐项核对.md)
3. [成绩分析系统计划与进度](docs/成绩分析系统计划与进度.md)
4. [项目需求与完成状态总表](docs/项目需求与完成状态总表.md)
5. [单机桌面版改造阶段进度](docs/单机桌面版改造-阶段进度.md)

## 当前功能

- 固定单用户菜单和免登录，启动后直接进入年级管理。
- 年级、班级、学生、教师、学科、考试和成绩管理。
- SQLite 本地数据库、本地文件存储、备份、恢复和最近 30 份备份管理。
- 成绩 Excel 上传、校验、差异预览、确认提交、批次记录和撤销第一版。
- 正常 0 分、缺考、未选科三种成绩状态；免考、缓考、作弊、违纪暂不加入。
- 考试级科目、满分和计入总分配置第一版。
- 年级/班级/学生分析、教师任教班级对比、历次考试对比、进退步、临界生、偏科和学生个人分析导出。
- 学科独立分析及通用 Excel/PDF 报告第一版。
- 高中 3+1+2 选科组合历史和赋分配置框架；具体赋分算法按要求暂停。
- 初中综合素质评价六学期、五维度、九下名单范围、缺失确认、教师复核、锁定和导出第一版；确认存在缺失学期的学生五维最终结果为 `N/A`，不参与 A/B/C 比例和排名。
- 学生学习状态和跟进记录第一版。
- 学生管理不展示头像和年龄；教师管理不展示头像、年龄、出生日期、入职年份和备注。

## 尚未完成

- 学校真实成绩样表的多工作表、合并单元格和状态词全链路联调。
- 考试级计总分配置在所有分析、排名和报告中的统一口径。
- 学校正式版班级、年级、教师 Excel/PDF 模板。
- 高中赋分政策、计算群体和算法。
- 无开发环境 Windows 10/11 离线验收。
- Tauri 随包运行时、绿色 EXE、升级和数据迁移策略。

## 在另一台 Windows 电脑继续开发

```powershell
git clone https://github.com/zheiseyummy/school-analysis-desktop.git
cd school-analysis-desktop
git checkout master
```

建议环境：Node.js 18+、pnpm、Java 17、Maven。仓库提供便携工具时，优先使用 `.tools` 目录；工具说明见 [scripts/README.md](scripts/README.md)。

安装前端依赖：

```powershell
cd frontend
pnpm install --frozen-lockfile --ignore-scripts
cd ..
```

## 启动本地服务

### 1. 启动后端

在项目根目录执行：

```powershell
pwsh -NoProfile -File .\scripts\build-backend.ps1 -MavenArguments @('spring-boot:run')
```

或者进入 `backend/` 后执行：

```powershell
mvn spring-boot:run
```

后端地址：`http://127.0.0.1:8989`。首次启动会创建：

- `backend/data/school.db`：SQLite 数据库。
- `backend/files/`：本地上传文件。
- `backend/backup/`：备份文件。
- `backend/export/`：导出文件。

### 2. 启动前端

另开一个 PowerShell：

```powershell
cd frontend
pnpm dev
```

打开 `http://127.0.0.1:3000`。也可以双击根目录的 `启动本地前端.bat`。

生产资源预览：

```powershell
cd frontend
pnpm build:prod
pnpm preview
```

预览地址为 `http://127.0.0.1:4173`。不要双击 `frontend/index.html`，浏览器不能直接以 `file://` 正常运行 Vue 应用。

## 使用顺序

1. 进入“基础信息”，维护年级、班级、学生、教师和学科。
2. 在学生资料中建立班级年度关系，在教师资料中维护教师基础信息。
3. 进入“考试管理”创建考试，配置本场科目、各科满分和是否计入总分。
4. 进入“成绩录入”，上传 Excel，先查看校验结果和差异预览，再确认提交。
5. 对缺考和未选科使用明确状态；正常 0 分必须保留为正常成绩。
6. 使用“学情分析”查看年级、班级、学生、教师和学科分析，并按需导出报告。
7. 初中综合素质评价先导入六学期数据，检查缺失确认；最终范围由最后一个学期名单确定。确认缺失/转入后，存在缺失学期的学生最终结果显示 `N/A`，不参与 A/B/C 比例和排名；其余等级建议需教师复核。
8. 定期在数据工具中执行备份；跨电脑只转移脱敏备份，不复制真实数据库到 GitHub。

## 数据和隐私

运行数据、上传文件、备份和导出目录均已加入 `.gitignore`，不会随源码提交。不要把以下内容加入 Git：`backend/data/`、`backend/files/`、`backend/backup/`、`backend/export/`、真实学生 Excel 或真实报告。

删除或迁移数据库前，先从系统执行一次备份并确认备份文件可读。不要把原始 ZIP 的 MySQL SQL 直接导入当前 SQLite 环境，也不要把真实学生数据发送到外部接口或对象存储。

## 开发验证

前端：

```powershell
cd frontend
pnpm typecheck
pnpm build:prod
```

后端、接口和浏览器冒烟：

```powershell
cd ..
node scripts/check-contracts.mjs
pwsh -NoProfile -File .\scripts\build-backend.ps1
node scripts/smoke-frontend.mjs
```

`smoke-frontend.mjs` 需要本地前后端和 Chrome/Edge 可用；结果及截图位于 `.test-results/`。后端测试报告位于 `backend/target/surefire-reports/`。

## 常见问题

### 页面空白或提示本地服务请求失败

确认后端终端仍在运行，并检查 `http://127.0.0.1:8989` 端口是否被占用。前端只代理到本机 8989，不需要启动 MySQL、Redis 或 MinIO。

### 端口被占用

关闭占用 `3000`、`4173` 或 `8989` 的旧进程后再启动。项目不会自动改用其他端口，避免前端代理地址不一致。

### 跨电脑没有原来的数据

源码仓库不包含本地数据库，这是刻意的隐私保护。通过系统备份功能导出备份文件，再在另一台电脑恢复；不要直接提交 `school.db`。

### 导入结果和学校表格不一致

先保留原表的脱敏副本，记录工作表名称、表头行、状态文字和分数规则，再在《需求文档逐项核对》中登记，不能直接猜测学校规则。

## Git 交接流程

```powershell
git status
git pull --ff-only origin master
# 完成修改和本地验证后
git add README.md docs backend frontend scripts 启动本地前端.bat
git commit -m "docs: update project handoff and usage guide"
git push origin master
```

提交前必须确认 `git status` 中没有真实数据库、备份、上传文件、导出文件和学校原始样表。后续开发优先阅读 [需求文档逐项核对](docs/需求文档逐项核对.md) 和 [成绩分析系统计划与进度](docs/成绩分析系统计划与进度.md)，从最高优先级未完成工作开始。
