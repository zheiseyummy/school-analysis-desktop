# 综合素质评价页面 UI pattern match

## 页面业务对象

综合素质评价班级工作台：左侧学生名单，右侧六学期五维评价编辑；最终评定和缺失成绩确认通过对话框承接当前班级上下文。

## 采用的 patterns

- `vite-shadcn-admin-shell`：沿用现有后台 shell，不改动导航和路由；页面内部采用 Page Header + toolbar + content section 的节奏。
- `faceted-filter-table`：将学生名单、最终评定和缺失确认设计为带状态 badge、行上下文和固定操作列的表格。
- `loading-empty-error-set`：补齐空学生列表、未选择学生、待确认缺失、锁定结果和保存/导入 loading 等业务状态；缺失成绩以 needs-review 阻断最终等级生成。

## 适配说明

项目是 Vue3 + Element Plus，不引入 React/Tailwind 示例代码，仅提取布局、状态和信息层级原则，保留现有应用 shell 与组件体系。
