<script setup lang="ts">
import {
  getCourseAnalysisData,
  getCourseAnalysisToExcel,
  getCourseAnalysisToPdf,
} from "@/api/analysis";
import { CourseAnalysisQuery, CourseAnalysisResult } from "@/api/analysis/types";
import { getComplexClazzOptions } from "@/api/clazz";
import { getOptions } from "@/api/exam";
import { getGradeOptions } from "@/api/grade";
import { getCourseOptions } from "@/api/course";

defineOptions({ name: "CourseAnalysis", inheritAttrs: false });

const queryFormRef = ref();
const queryParams = reactive<CourseAnalysisQuery>({});
const gradeList = ref<OptionType[]>([]);
const clazzGroups = ref<OptionType[]>([]);
const examList = ref<OptionType[]>([]);
const courseList = ref<OptionType[]>([]);
const loading = ref(false);
const result = ref<CourseAnalysisResult>({ rows: [], summary: {} });

const rules = reactive({
  gradeId: [{ required: true, message: "请选择年级", trigger: "change" }],
  examId: [{ required: true, message: "请选择考试", trigger: "change" }],
  courseId: [{ required: true, message: "请选择学科", trigger: "change" }],
});

async function loadOptions() {
  const [grades, clazzes, courses] = await Promise.all([
    getGradeOptions(),
    getComplexClazzOptions(),
    getCourseOptions(),
  ]);
  gradeList.value = grades.data || [];
  clazzGroups.value = clazzes.data || [];
  courseList.value = courses.data || [];
  await loadExamOptions();
}

async function loadExamOptions() {
  const response = await getOptions(queryParams as any);
  examList.value = response.data || [];
}

function handleClazzChange(value?: number) {
  const group = clazzGroups.value.find((item: any) =>
    item.children?.some((child: any) => Number(child.value) === Number(value))
  );
  if (group) queryParams.gradeId = Number(group.value);
  loadExamOptions();
}

function handleGradeChange() {
  queryParams.clazzId = undefined;
  loadExamOptions();
}

function handleQuery() {
  queryFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    loading.value = true;
    getCourseAnalysisData(queryParams)
      .then(({ data }) => (result.value = data || { rows: [], summary: {} }))
      .catch(() => (result.value = { rows: [], summary: {} }))
      .finally(() => (loading.value = false));
  });
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  queryParams.clazzId = undefined;
  result.value = { rows: [], summary: {} };
  loadExamOptions();
}

function download(response: any, fallbackName: string, type: string) {
  const blob = new Blob([response.data], { type });
  const link = document.createElement("a");
  link.href = window.URL.createObjectURL(blob);
  const disposition = response.headers?.["content-disposition"] || "";
  const encoded = disposition.split("filename=")[1];
  link.download = encoded ? decodeURIComponent(encoded.replace(/^\"|\"$/g, "")) : fallbackName;
  link.click();
  window.URL.revokeObjectURL(link.href);
}

function exportExcel() {
  getCourseAnalysisToExcel(queryParams).then((response) =>
    download(response, "学科分析报告.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  );
}

function exportPdf() {
  getCourseAnalysisToPdf(queryParams).then((response) =>
    download(response, "学科分析报告.pdf", "application/pdf")
  );
}

onMounted(loadOptions);
</script>

<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :rules="rules" inline>
        <el-form-item label="年级" prop="gradeId">
          <el-select v-model="queryParams.gradeId" clearable class="!w-[180px]" placeholder="请选择年级" @change="handleGradeChange">
            <el-option v-for="item in gradeList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="queryParams.clazzId" clearable class="!w-[180px]" placeholder="全部班级" @change="handleClazzChange">
            <el-option-group v-for="group in clazzGroups" :key="group.value" :label="group.label">
              <el-option v-for="item in (group.children || [])" :key="item.value" :label="item.label" :value="item.value" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="考试" prop="examId">
          <el-select v-model="queryParams.examId" clearable class="!w-[220px]" placeholder="请选择考试">
            <el-option v-for="item in examList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="学科" prop="courseId">
          <el-select v-model="queryParams.courseId" clearable class="!w-[160px]" placeholder="请选择学科">
            <el-option v-for="item in courseList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card v-if="result.title" shadow="never" class="mb-4">
      <template #header>
        <div class="flex items-center justify-between">
          <span>{{ result.title }}</span>
          <div>
            <el-button type="success" plain @click="exportExcel">导出正式 Excel</el-button>
            <el-button type="danger" plain @click="exportPdf">导出正式 PDF</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="分析范围">{{ result.scopeName }}</el-descriptions-item>
        <el-descriptions-item label="满分">{{ result.fullScore }}</el-descriptions-item>
        <el-descriptions-item label="参考人数">{{ result.summary?.['参考人数'] }}</el-descriptions-item>
        <el-descriptions-item label="有效成绩">{{ result.summary?.['有效成绩人数'] }}</el-descriptions-item>
        <el-descriptions-item label="平均分">{{ result.summary?.['平均分'] }}</el-descriptions-item>
        <el-descriptions-item label="中位数">{{ result.summary?.['中位数'] }}</el-descriptions-item>
        <el-descriptions-item label="及格率">{{ result.summary?.['及格率'] }}</el-descriptions-item>
        <el-descriptions-item label="优秀率">{{ result.summary?.['优秀率'] }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never">
      <template #header><span>学生成绩明细（缺考、未选科不参与统计）</span></template>
      <el-table v-loading="loading" :data="result.rows || []" stripe border>
        <el-table-column prop="rank" label="排名" width="80" />
        <el-table-column prop="studentCode" label="学号" min-width="120" />
        <el-table-column prop="studentName" label="姓名" min-width="100" />
        <el-table-column prop="clazzName" label="班级" min-width="120" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="scope">{{ scope.row.score == null ? "-" : scope.row.score }}</template>
        </el-table-column>
        <el-table-column prop="statusLabel" label="状态" width="100" />
        <el-table-column prop="percent" label="得分率" width="100">
          <template #default="scope">{{ scope.row.percent == null ? "-" : `${scope.row.percent.toFixed(2)}%` }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
