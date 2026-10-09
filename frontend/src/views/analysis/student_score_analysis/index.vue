<script setup lang="ts">
import { StudentScoreAnalysisQuery } from "@/api/analysis/types";
import { getComplexClazzOptions } from "@/api/clazz";
import { getStudentOptions } from "@/api/student";
import { exportStudentAnalysis, exportStudentAnalysisBatch, exportStudentAnalysisPdf, getStudentScoreAnalysisData } from "@/api/analysis/index";
import { getSelfExamOptions } from "@/api/exam";
import { downloadFile } from "@/utils/download";
import { CoursePageVO } from "@/api/course/types";
import { TabsPaneContext } from "element-plus";
defineOptions({
  name: "StudentScoreAnalysis",
  inheritAttrs: false,
});
const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源
const studentList = ref<OptionType[]>(); //学生下拉数据源
const examList = ref<OptionType[]>([]);
const queryFormRef = ref(ElForm);

const queryParams = reactive<StudentScoreAnalysisQuery>({});

const loading = ref(false);

const columns = ref<any>();
const tableDataList = ref<any>();

const rules = reactive({
  year: [{ required: true, message: "请选择年度", trigger: "blur" }],
  clazzId: [{ required: true, message: "请选择班级", trigger: "blur" }],
  studentId: [{ required: true, message: "请选择学生", trigger: "blur" }],
  examId: [{ required: true, message: "请选择考试", trigger: "blur" }],
});

const courseList = ref<CoursePageVO[]>();

const tabsActiveName = ref();
const studentOverview = computed(() => {
  const rows = (tableDataList.value ?? []).filter((row: any) => row.totalScore !== undefined && row.totalScore !== null).slice().sort((a: any, b: any) => String(a.examDate ?? "").localeCompare(String(b.examDate ?? "")));
  const totals = rows.map((row: any) => Number(row.totalScore));
  const courses = (courseList.value ?? []).filter((course: any) => course.id !== -1).map((course: any) => {
    const scores = rows.map((row: any) => Number(row[`C_${course.id}_Score`])).filter((score: number) => Number.isFinite(score));
    return { name: course.name, average: scores.length ? scores.reduce((sum: number, score: number) => sum + score, 0) / scores.length : 0 };
  }).sort((a: any, b: any) => b.average - a.average);
  const latest = rows[rows.length - 1]; const previous = rows[rows.length - 2];
  return { rows, average: totals.length ? totals.reduce((sum: number, value: number) => sum + value, 0) / totals.length : 0, maximum: totals.length ? Math.max(...totals) : 0, minimum: totals.length ? Math.min(...totals) : 0, latestChange: latest && previous ? Number(latest.totalScore) - Number(previous.totalScore) : 0, strongest: courses.slice(0, 3), weakest: courses.slice(-3).reverse() };
});

/** 加载携带年级信息的班级下拉数据源 */
async function loadComplexClazzOptions() {
  getComplexClazzOptions().then((response) => {
    complexClazzList.value = response.data;
  });
}

/** 加载学生下拉数据源 */
async function loadStudentOptions() {
  if (queryParams.clazzId && queryParams.year) {
    const clazzId = queryParams.clazzId;
    const year = queryParams.year;
    const response = await getStudentOptions(clazzId, year);
    if (queryParams.clazzId === clazzId && queryParams.year === year) {
      studentList.value = response.data;
    }
  }
}

function clearAnalysisResults() {
  columns.value = [];
  tableDataList.value = [];
  courseList.value = [];
}

function yearChange() {
  queryParams.clazzId = undefined;
  queryParams.studentId = undefined;
  queryParams.examId = undefined;
  studentList.value = [];
  examList.value = [];
  clearAnalysisResults();
}

async function clazzChange() {
  queryParams.studentId = undefined;
  queryParams.examId = undefined;
  studentList.value = [];
  examList.value = [];
  clearAnalysisResults();
  loadStudentOptions();
  if (queryParams.clazzId) {
    const clazzId = queryParams.clazzId;
    const { data } = await getSelfExamOptions(clazzId);
    if (queryParams.clazzId === clazzId) examList.value = data ?? [];
  }
}

function examChange() {
  clearAnalysisResults();
  if (queryParams.studentId && queryParams.examId) handleQuery();
}

function studentChange() {
  clearAnalysisResults();
  if (queryParams.studentId && queryParams.examId) handleQuery();
}

function handleQuery() {
  queryFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      getStudentScoreAnalysisData(queryParams)
        .then(({ data }) => {
          columns.value = data.columns;
          tableDataList.value = data.tableDataList;
          courseList.value = data.courseList;
          if (courseList.value) {
            tabsActiveName.value = courseList.value![0].id;
          }
        })
        .catch(() => {
          columns.value = [];
          tableDataList.value = [];
          courseList.value = [];
        })
        .finally(() => (loading.value = false));
    }
  });
}

function handleExport() {
  if (!queryParams.studentId || !queryParams.examId || !studentOverview.value.rows.length) return;
  exportStudentAnalysis({ studentId: queryParams.studentId, examId: queryParams.examId }).then((response: any) => downloadFile(response, "学生个人分析.xlsx"));
}

function handleExportPdf() {
  if (!queryParams.studentId || !queryParams.examId) return;
  exportStudentAnalysisPdf({ studentId: queryParams.studentId, examId: queryParams.examId }).then((response: any) => downloadFile(response, "学生个人成绩分析.pdf", "application/pdf"));
}

function handleBatchExport() {
  if (!queryParams.clazzId || !queryParams.examId) return;
  exportStudentAnalysisBatch({ clazzId: queryParams.clazzId, examId: queryParams.examId, format: "both" }).then((response: any) => downloadFile(response, "学生个人成绩分析报告.zip", "application/zip"));
}

function resetQuery() {
  queryFormRef.value.resetFields();
  studentList.value = [];
  examList.value = [];
  columns.value = [];
  tableDataList.value = [];
  courseList.value = [];
}

onMounted(() => {
  loadComplexClazzOptions();
});

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab.props.name, event);
};
</script>
<template>
  <div class="app-container">
    <div class="search-container">
      <el-form
        ref="queryFormRef"
        :model="queryParams"
        :rules="rules"
        :inline="true"
      >
        <el-form-item label="年度" prop="year">
          <el-date-picker
            v-model="queryParams.year"
            type="year"
            format="YYYY"
            value-format="YYYY"
            placeholder="请选择考试年度"
            @change="yearChange"
          />
        </el-form-item>
        <el-form-item label="班级" prop="clazzId">
          <el-select
            v-model="queryParams.clazzId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
            @change="clazzChange"
          >
            <el-option-group
              v-for="group in complexClazzList"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="child in group.children"
                :key="child.value"
                :label="child.label"
                :value="child.value"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="考试" prop="examId">
          <el-select v-model="queryParams.examId" clearable class="!w-[220px]" placeholder="请选择考试" @change="examChange">
            <el-option v-for="item in examList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="queryParams.studentId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
            @change="studentChange"
          >
            <el-option
              v-for="item in studentList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"
            ><i-ep-search />搜索</el-button
          >
          <el-button @click="resetQuery"><i-ep-refresh />重置</el-button>
          <el-button type="success" :disabled="!queryParams.studentId || !queryParams.examId || !studentOverview.rows.length" @click="handleExport"><i-ep-download />导出 Excel</el-button>
          <el-button type="warning" :disabled="!queryParams.studentId || !queryParams.examId || !studentOverview.rows.length" @click="handleExportPdf"><i-ep-document />导出 PDF</el-button>
          <el-button type="info" :disabled="!queryParams.clazzId || !queryParams.examId" @click="handleBatchExport"><i-ep-files />批量导出班级</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-card shadow="never" class="table-container">
      <el-row>
        <el-col :span="24">
          <custom-table
            :loading="loading"
            :table-data-list="tableDataList"
            :columns="columns"
          />
        </el-col>
      </el-row>
      <el-card v-if="studentOverview.rows.length" shadow="never" class="mt-3">
        <template #header>学生个人成绩画像</template>
        <el-descriptions :column="5" border>
          <el-descriptions-item label="考试次数">{{ studentOverview.rows.length }}</el-descriptions-item>
          <el-descriptions-item label="平均总分">{{ studentOverview.average.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="最高总分">{{ studentOverview.maximum.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="最低总分">{{ studentOverview.minimum.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="最近变化"><el-tag :type="studentOverview.latestChange >= 0 ? 'success' : 'danger'">{{ studentOverview.latestChange >= 0 ? '+' : '' }}{{ studentOverview.latestChange.toFixed(2) }}</el-tag></el-descriptions-item>
        </el-descriptions>
        <el-row :gutter="12" class="mt-3">
          <el-col :span="12"><el-table :data="studentOverview.rows" border size="small"><el-table-column prop="examName" label="考试" /><el-table-column prop="totalScore" label="总分" /><el-table-column prop="clazzRanking" label="班级排名" /><el-table-column prop="gradeRanking" label="年级排名" /><el-table-column prop="examDate" label="考试日期" /></el-table></el-col>
          <el-col :span="12"><el-table :data="studentOverview.strongest" border size="small"><el-table-column prop="name" label="优势学科" /><el-table-column prop="average" label="平均分"><template #default="scope">{{ Number(scope.row.average).toFixed(2) }}</template></el-table-column></el-table><el-table :data="studentOverview.weakest" border size="small" class="mt-3"><el-table-column prop="name" label="薄弱学科" /><el-table-column prop="average" label="平均分"><template #default="scope">{{ Number(scope.row.average).toFixed(2) }}</template></el-table-column></el-table></el-col>
        </el-row>
      </el-card>

      <el-row class="mt-3">
        <el-col :span="24">
          <el-tabs
            v-model="tabsActiveName"
            type="border-card"
            @tab-click="handleClick"
          >
            <el-tab-pane
              v-for="item in courseList"
              :label="item.name"
              :key="item.id"
              :name="item.id"
              :lazy="true"
            >
              <StudentCourseScore
                :student-id="queryParams.studentId"
                :course-id="item.id"
              />
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>
<style>
.item {
  margin-top: 10px;
  margin-right: 40px;
}

.letter-circle-success {
  width: 20px;
  height: 20px;
  color: white;
  background-color: #67c23a;
  border-radius: 50%;
}

.letter-circle-error {
  width: 20px;
  height: 20px;
  color: white;
  background-color: #f56c6c;
  border-radius: 50%;
}
</style>
