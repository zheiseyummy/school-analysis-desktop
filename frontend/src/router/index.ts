import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";

export const Layout = () => import("@/layout/index.vue");

/**
 * 单机版固定业务路由。
 * Sidebar、Breadcrumb、TagsView 继续复用原有 Layout，只移除菜单对
 * sys_menu、sys_role_menu 和登录用户角色的依赖。
 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/redirect",
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
      },
    ],
  },
  // 没有首页，启动后直接进入第一个实际业务模块。
  { path: "/", redirect: "/base/grade", meta: { hidden: true } },
  {
    path: "/base",
    name: "BaseInformation",
    component: Layout,
    redirect: "/base/grade",
    meta: { title: "基础信息", icon: "peoples", alwaysShow: true },
    children: [
      {
        path: "grade",
        name: "Grade",
        component: () => import("@/views/school/grade/index.vue"),
        meta: { title: "年级管理", icon: "tree", keepAlive: true },
      },
      {
        path: "clazz",
        name: "Clazz",
        component: () => import("@/views/school/clazz/index.vue"),
        meta: { title: "班级管理", icon: "client", keepAlive: true },
      },
      {
        path: "student",
        name: "Student",
        component: () => import("@/views/school/student/index.vue"),
        meta: { title: "学生管理", icon: "user", keepAlive: true },
      },
      {
        path: "teacher",
        name: "Teacher",
        component: () => import("@/views/school/teacher/index.vue"),
        meta: { title: "教师资料", icon: "peoples", keepAlive: true },
      },
      {
        path: "course",
        name: "Course",
        component: () => import("@/views/school/course/index.vue"),
        meta: { title: "学科管理", icon: "document", keepAlive: true },
      },
    ],
  },
  {
    path: "/score",
    name: "ScoreManagement",
    component: Layout,
    redirect: "/score/exam",
    meta: { title: "成绩管理", icon: "table", alwaysShow: true },
    children: [
      {
        path: "exam",
        name: "Exam",
        component: () => import("@/views/school/exam/index.vue"),
        meta: { title: "考试管理", icon: "todolist", keepAlive: true },
      },
      {
        path: "entry",
        name: "ScoreEntry",
        component: () => import("@/views/score/score_entry/index.vue"),
        meta: { title: "成绩录入", icon: "edit", keepAlive: true },
      },
    ],
  },
  {
    path: "/analysis",
    name: "LearningAnalysis",
    component: Layout,
    redirect: "/analysis/grade",
    meta: { title: "学情分析", icon: "project", alwaysShow: true },
    children: [
      {
        path: "grade",
        name: "GradeExamAnalysis",
        component: () =>
          import("@/views/analysis/grade_exam_analysis/index.vue"),
        meta: { title: "年级分析", icon: "cascader", keepAlive: true },
      },
      {
        path: "clazz",
        name: "ClazzExamAnalysis",
        component: () =>
          import("@/views/analysis/clazz_exam_analysis/index.vue"),
        meta: { title: "班级分析", icon: "table", keepAlive: true },
      },
      {
        path: "student",
        name: "StudentScoreAnalysis",
        component: () =>
          import("@/views/analysis/student_score_analysis/index.vue"),
        meta: { title: "学生分析", icon: "user", keepAlive: true },
      },
    ],
  },
  {
    path: "/quality",
    name: "QualityEvaluation",
    component: Layout,
    redirect: "/quality/evaluation",
    meta: { title: "综合素质评价", icon: "star", alwaysShow: true },
    children: [{ path: "evaluation", name: "QualityEvaluationPage", component: () => import("@/views/quality/index.vue"), meta: { title: "综合素质评价", icon: "edit", keepAlive: true } }],
  },
  {
    path: "/data",
    name: "DataTools",
    component: Layout,
    redirect: "/data/backup",
    meta: { title: "数据工具", icon: "folder", alwaysShow: true },
    children: [
      {
        path: "backup",
        name: "DataBackup",
        component: () => import("@/views/data/backup/index.vue"),
        meta: { title: "数据备份", icon: "database", keepAlive: true },
      },
    ],
  },
  {
    path: "/404",
    component: () => import("@/views/error-page/404.vue"),
    meta: { hidden: true },
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/404",
    meta: { hidden: true },
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

export default router;
