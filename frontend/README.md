# 成绩分析系统本地前端

Vue 3 + Vite + TypeScript + Element Plus 前端，默认连接本机后端 `http://127.0.0.1:8989`。系统免登录，启动后直接进入年级管理。

## 本地运行

```powershell
pnpm install
pnpm dev
```

开发地址：`http://127.0.0.1:3000`。生产构建和预览：

```powershell
pnpm typecheck
pnpm build:prod
pnpm preview --host 127.0.0.1 --port 4173
```

系统只保留本地业务页面：基础信息、成绩管理、学情分析、综合素质评价、高中选科与数据工具。不要直接双击 `index.html`，请通过 Vite 服务访问。
