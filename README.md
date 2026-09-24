# 学情分析系统：单机桌面版改造工作区

本目录是继续开发的工作副本，保留原 Vue 3 / Element Plus 界面和核心业务。目标是 Windows 离线、免登录、SQLite + 本地文件的绿色 EXE；**当前仍是分阶段迁移中的开发工程，不是最终桌面软件**。

## 当前已完成

- 保留年级、班级、学生、教师资料、任教关系、学科、考试、成绩及现有分析接口。
- 固定本地菜单，不再使用多人账号、JWT、动态权限、问卷、题库和兴趣院校模块。
- 后端默认使用本地 SQLite `backend/data/school.db`，文件链路使用本地 `files/` 目录。
- 已完成本地备份、立即备份、恢复确认和最近 30 个备份管理页面。
- 年级、班级、学生分析已增加教师诊断、历次考试对比、进退步、临界生、偏科和个人分析 Excel 导出。
- 初中综合素质评价第一版已完成六学期导入、学号匹配、五维评价、最终分档、复核锁定和导出。
- 已移除首页，启动后直接进入“年级管理”；档案源码保留，但未加入固定菜单。

## 当前未完成

- 学校实际样表的完整导入识别、重导预览确认、缺考状态和变更日志撤销。
- 考试级满分/计分配置、高中选科与赋分、学生备注跟进和正式 Excel/PDF 报告模板。
- MySQL/MinIO 等过渡依赖清理、Tauri 和 Windows 10/11 绿色 EXE。

## 回家继续开发

```powershell
git clone https://github.com/zheiseyummy/school-analysis-desktop.git
cd school-analysis-desktop
git checkout master
cd frontend
pnpm install --frozen-lockfile --ignore-scripts
```

先在一个终端运行后端，再在另一个终端运行前端。当前演示数据库不提交到仓库，代码仓库和本地数据分开保存。

## 开发验证

在项目根目录运行：

```powershell
node scripts/check-contracts.mjs
powershell -NoProfile -File scripts/build-backend.ps1
```

在 `frontend` 目录运行：

建议使用 Node.js 18+、pnpm、JDK 17 和 Maven；依赖版本由锁文件固定。

```powershell
pnpm install --frozen-lockfile --ignore-scripts
pnpm typecheck
pnpm build:prod
pnpm dev
```

开发页面为 `http://127.0.0.1:3000`，生产资源预览使用 `pnpm preview`（`http://127.0.0.1:4173`）。两者均将 API 代理到本机 `127.0.0.1:8989`。端口占用时直接报错，不自动换端口。

Windows 下可直接双击项目根目录的 `启动本地前端.bat`，但要先在另一个终端启动后端：

```powershell
cd backend
mvn spring-boot:run
```

后端只监听 `127.0.0.1:8989`，首次启动会创建本地 `data/`、`files/`、`backup/` 和 `export/` 目录。不要双击 `frontend/index.html`，应通过 Vite 地址访问。

启动生产预览后，可在根目录执行 `node scripts/smoke-frontend.mjs`，使用本机 Chrome/Edge 检查免登录默认业务页与本地资源加载，结果及截图位于 `.test-results/`。这不是数据库业务联调。

后端构建工具的便携配置见 [scripts/README.md](scripts/README.md)。构建工具仅用于开发，不是最终用户需要安装的运行环境。构建测试默认不执行原包中依赖真实数据库的旧集成测试。

## 数据与恢复

本地数据位于 `backend/data/`、`files/`、`backup/` 和 `export/`，均已加入 `.gitignore`，不会上传学生数据。回家继续开发时只需克隆仓库；如需带数据，请通过系统备份功能单独转移备份文件。不要把原包完整 SQL 当作精简数据库安装脚本直接导入正式环境。

工作开始前的前端与 `outputs` 文档已备份到相邻审查目录中的 `继续改造前工作副本-20260923.zip`；原始后端仍保存在相邻审查目录和用户提供的原 ZIP 中。被删除的外围源码可从这些文件恢复。

当前阶段的实际完成项、验证边界及下一步，以仓库 [`docs/单机桌面版改造-阶段进度.md`](docs/单机桌面版改造-阶段进度.md) 和 [`docs/项目需求与完成状态总表.md`](docs/项目需求与完成状态总表.md) 为准。回家后克隆 `master` 分支即可继续开发。
