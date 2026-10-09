<script setup lang="ts">
import {
  confirmLocalScoreImport,
  getLocalScoreAnalysis,
  getLocalScoreDatasets,
  getLocalScoreExamStudents,
  previewLocalScoreImport,
} from "@/api/local-score-analysis";
import type { UploadFile, UploadInstance } from "element-plus";

defineOptions({ name: "LocalScoreAnalysis" });

type Dataset = { id: number; name: string; stage?: string; gradeName?: string; className?: string; examCount: number; studentCount: number };
type Exam = { id: number; name: string; studentCount: number; subjectStats: any[] };

const datasets = ref<Dataset[]>([]);
const datasetId = ref<number>();
const dataset = ref<any>();
const exams = ref<Exam[]>([]);
const examId = ref<number>();
const students = ref<any[]>([]);
const subjects = ref<string[]>([]);
const loading = ref(false);
const importDialog = ref(false);
const previewDialog = ref(false);
const uploadRef = ref<UploadInstance>();
const importFile = ref<File>();
const preview = ref<any>();
const previewing = ref(false);
const confirming = ref(false);
const importForm = reactive({ datasetName: "", stage: "初中", gradeName: "", className: "", examName: "" });

const currentExam = computed(() => exams.value.find((item) => item.id === examId.value));

async function loadDatasets() {
  const { data } = await getLocalScoreDatasets();
  datasets.value = data ?? [];
  if (datasetId.value && !datasets.value.some((item) => item.id === datasetId.value)) datasetId.value = undefined;
  if (!datasetId.value && datasets.value.length) datasetId.value = datasets.value[0].id;
  if (datasetId.value) await loadAnalysis();
}

async function loadAnalysis() {
  if (!datasetId.value) {
    dataset.value = undefined;
    exams.value = [];
    students.value = [];
    subjects.value = [];
    return;
  }
  loading.value = true;
  try {
    const { data } = await getLocalScoreAnalysis(datasetId.value);
    dataset.value = data.dataset;
    exams.value = data.exams ?? [];
    if (!exams.value.some((item) => item.id === examId.value)) examId.value = exams.value[0]?.id;
    await loadExamStudents();
  } finally {
    loading.value = false;
  }
}

async function loadExamStudents() {
  students.value = [];
  subjects.value = [];
  if (!datasetId.value || !examId.value) return;
  const { data } = await getLocalScoreExamStudents(datasetId.value, examId.value);
  students.value = data.students ?? [];
  subjects.value = data.subjects ?? [];
}

function selectDataset(id: number) {
  const item = datasets.value.find((row) => row.id === id);
  if (item) {
    importForm.datasetName = item.name;
    importForm.stage = item.stage ?? "";
    importForm.gradeName = item.gradeName ?? "";
    importForm.className = item.className ?? "";
  }
  examId.value = undefined;
  loadAnalysis();
}

function openImport() {
  importFile.value = undefined;
  preview.value = undefined;
  uploadRef.value?.clearFiles();
  if (dataset.value) {
    importForm.datasetName = dataset.value.name;
    importForm.stage = dataset.value.stage ?? "";
    importForm.gradeName = dataset.value.gradeName ?? "";
    importForm.className = dataset.value.className ?? "";
  }
  importForm.examName = "";
  importDialog.value = true;
}

function onFileChange(file: UploadFile) {
  importFile.value = file.raw;
}

async function previewImport() {
  if (!importFile.value) return ElMessage.warning("请先选择成绩文件");
  if (!importForm.datasetName || !importForm.gradeName || !importForm.className || !importForm.examName) return ElMessage.warning("请填写数据集、年级、班级和考试名称");
  previewing.value = true;
  try {
    const form = new FormData();
    form.append("datasetName", importForm.datasetName);
    form.append("stage", importForm.stage);
    form.append("gradeName", importForm.gradeName);
    form.append("className", importForm.className);
    form.append("examName", importForm.examName);
    form.append("file", importFile.value);
    const { data } = await previewLocalScoreImport(form);
    preview.value = data;
    previewDialog.value = true;
  } finally {
    previewing.value = false;
  }
}

async function confirmImport() {
  if (!preview.value?.confirmable) return;
  confirming.value = true;
  try {
    const { data } = await confirmLocalScoreImport(preview.value.token);
    ElMessage.success(`已导入 ${data.students} 名学生、${data.subjects} 门科目，共 ${data.values} 个成绩/状态单元`);
    previewDialog.value = false;
    importDialog.value = false;
    preview.value = undefined;
    await loadDatasets();
    const match = datasets.value.find((item) => item.name === importForm.datasetName);
    if (match) {
      datasetId.value = match.id;
      examId.value = undefined;
      await loadAnalysis();
      const exam = exams.value.find((item) => item.name === importForm.examName);
      if (exam) {
        examId.value = exam.id;
        await loadExamStudents();
      }
    }
  } finally {
    confirming.value = false;
  }
}

function scoreText(student: any, subject: string) {
  const cell = student.scores?.[subject];
  if (!cell) return "—";
  if (cell.status === "NORMAL") return cell.score;
  return cell.status === "ABSENT" ? "缺考" : cell.status === "UNSELECTED" ? "未选科" : "缺失";
}

function formatStat(value: any) {
  return value == null ? "—" : Number(value).toFixed(1);
}

onMounted(loadDatasets);
</script>

<template>
  <div class="app-container standalone-page">
    <div class="page-heading">
      <div><div class="eyebrow">STANDALONE SCORE ANALYSIS</div><h2>独立成绩分析</h2><p>适用于没有基础学生档案的成绩表；此处数据只进入独立分析数据集，不会改动年级、班级和学生资料。</p></div>
      <el-button type="primary" @click="openImport"><i-ep-upload />导入成绩文件</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-row">
        <div><span class="field-label">分析数据集</span><el-select v-model="datasetId" clearable filterable placeholder="选择已有数据集" class="dataset-select" @change="selectDataset"><el-option v-for="item in datasets" :key="item.id" :label="`${item.name} · ${item.gradeName ?? ''} ${item.className ?? ''}`" :value="item.id" /></el-select></div>
        <div v-if="dataset" class="context-chips"><el-tag effect="plain">{{ dataset.stage || "未指定学段" }}</el-tag><el-tag effect="plain" type="success">{{ dataset.gradeName }} · {{ dataset.className }}</el-tag><el-tag effect="plain" type="info">{{ dataset.examCount }} 场考试</el-tag><el-tag effect="plain" type="warning">{{ dataset.studentCount }} 名学生</el-tag></div>
      </div>
    </el-card>

    <template v-if="dataset">
      <el-card shadow="never" class="results-card" v-loading="loading">
        <template #header><div class="section-header"><div><strong>考试分析</strong><span>选择考试查看学生成绩和科目统计</span></div><el-select v-model="examId" placeholder="选择考试" class="exam-select" @change="loadExamStudents"><el-option v-for="item in exams" :key="item.id" :label="item.name" :value="item.id" /></el-select></div></template>
        <el-empty v-if="!exams.length" description="当前数据集尚无考试，请导入成绩文件" />
        <template v-else-if="currentExam">
          <div class="stat-grid"><div v-for="stat in currentExam.subjectStats" :key="stat.subject" class="stat-card"><span>{{ stat.subject }}</span><strong>{{ formatStat(stat.average) }}</strong><small>平均分 · {{ stat.scoredCount }} 有效</small><div>最高 {{ formatStat(stat.highest) }}　最低 {{ formatStat(stat.lowest) }}</div><small v-if="stat.absentCount || stat.unselectedCount || stat.missingCount">缺考 {{ stat.absentCount }} · 未选 {{ stat.unselectedCount }} · 空白 {{ stat.missingCount }}</small></div></div>
          <div class="table-heading"><div><strong>{{ currentExam.name }}</strong><span>{{ students.length }} 名学生 · {{ subjects.length }} 门科目</span></div><el-tag v-if="!preview?.studentCodePresent" type="warning" effect="plain">本场次按姓名对应，未关联基础学生资料</el-tag></div>
          <el-table :data="students" border stripe height="540" row-key="studentId"><el-table-column type="index" label="#" width="54" fixed="left" /><el-table-column prop="code" label="学号" width="128" fixed="left"><template #default="scope">{{ scope.row.code || "—" }}</template></el-table-column><el-table-column prop="name" label="姓名" width="100" fixed="left" /><el-table-column v-for="subject in subjects" :key="subject" :label="subject" min-width="96" align="center"><template #default="scope"><span :class="{ 'status-cell': typeof scoreText(scope.row, subject) === 'string' }">{{ scoreText(scope.row, subject) }}</span></template></el-table-column></el-table>
        </template>
      </el-card>
    </template>
    <el-card v-else shadow="never" class="empty-card"><el-empty :description="datasets.length ? '请选择分析数据集，或直接导入成绩文件' : '还没有独立分析数据集，请导入成绩文件开始'" /></el-card>

    <el-dialog v-model="importDialog" title="导入独立成绩文件" width="620px" destroy-on-close>
      <el-alert type="info" :closable="false" show-icon title="独立数据集不会写入基础资料，也不要求学生预先建档。" class="dialog-alert" />
      <el-form label-width="110px" class="import-form">
        <el-form-item label="数据集名称" required><el-input v-model="importForm.datasetName" placeholder="例如：2026届初三成绩" /></el-form-item>
        <el-form-item label="学段"><el-select v-model="importForm.stage" class="full-width"><el-option label="初中" value="初中" /><el-option label="高中" value="高中" /><el-option label="其他/未指定" value="其他" /></el-select></el-form-item>
        <el-form-item label="年级" required><el-input v-model="importForm.gradeName" placeholder="例如：初三" /></el-form-item>
        <el-form-item label="班级" required><el-input v-model="importForm.className" placeholder="例如：6班" /></el-form-item>
        <el-form-item label="考试名称" required><el-input v-model="importForm.examName" placeholder="例如：2026年3月市一模" /></el-form-item>
        <el-form-item label="成绩文件" required><el-upload ref="uploadRef" :auto-upload="false" :limit="1" accept=".xlsx,.xls" :on-change="onFileChange" :on-exceed="(files: any[]) => { uploadRef?.clearFiles(); onFileChange(files[0]); }"><el-button><i-ep-folder-opened />选择 Excel 文件</el-button><template #tip><div class="el-upload__tip">支持表头顺序变化和合并单元格；学号可缺省，未提供学号时要求姓名在数据集中唯一。</div></template></el-upload></el-form-item>
      </el-form>
      <template #footer><el-button @click="importDialog = false">取消</el-button><el-button type="primary" :loading="previewing" @click="previewImport">解析并预览</el-button></template>
    </el-dialog>

    <el-dialog v-model="previewDialog" title="确认独立成绩导入" width="620px">
      <el-descriptions :column="2" border><el-descriptions-item label="数据集">{{ preview?.datasetName }}</el-descriptions-item><el-descriptions-item label="考试">{{ preview?.examName }}</el-descriptions-item><el-descriptions-item label="工作表">{{ preview?.sheetName }}</el-descriptions-item><el-descriptions-item label="表头行">第 {{ preview?.headerRow }} 行</el-descriptions-item><el-descriptions-item label="学生行数">{{ preview?.rowCount }}</el-descriptions-item><el-descriptions-item label="学号列">{{ preview?.studentCodePresent ? '有' : '无，按唯一姓名匹配' }}</el-descriptions-item><el-descriptions-item label="识别科目" :span="2">{{ preview?.subjects?.join('、') }}</el-descriptions-item></el-descriptions>
      <el-alert v-if="preview?.errorCount" type="error" :closable="false" show-icon :title="`发现 ${preview.errorCount} 个问题，暂不能确认导入`" class="dialog-alert"><template #default><div v-for="item in preview.errors" :key="item">{{ item }}</div></template></el-alert>
      <el-alert v-else type="success" :closable="false" show-icon title="表头与学生行检查通过；确认后才会保存到本地分析数据集。" class="dialog-alert" />
      <template #footer><el-button @click="previewDialog = false">返回</el-button><el-button type="primary" :disabled="!preview?.confirmable" :loading="confirming" @click="confirmImport">确认导入</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.standalone-page { min-height: calc(100vh - 84px); padding: 20px; background: #f5f7fb; }
.page-heading, .section-header, .table-heading, .filter-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-heading { align-items: flex-start; margin-bottom: 16px; }
.eyebrow { color: #5b8ff9; font-size: 11px; font-weight: 700; letter-spacing: .12em; }
.page-heading h2 { margin: 4px 0 6px; color: #263449; font-size: 24px; }
.page-heading p { margin: 0; color: #8994a8; font-size: 13px; }
.filter-card, .results-card, .empty-card { margin-bottom: 16px; border: 1px solid #e9edf5; border-radius: 12px; }
.field-label { display: inline-block; margin-right: 10px; color: #64748b; font-size: 13px; }
.dataset-select { width: 370px; }
.context-chips { display: flex; flex-wrap: wrap; gap: 8px; }
.section-header > div strong, .table-heading strong { margin-right: 10px; color: #263449; }
.section-header > div span, .table-heading span { color: #8994a8; font-size: 12px; }
.exam-select { width: 260px; }
.stat-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(175px, 1fr)); gap: 10px; margin-bottom: 18px; }
.stat-card { padding: 13px 15px; border: 1px solid #edf0f5; border-radius: 9px; background: #fbfcfe; }
.stat-card > span { display: block; color: #667085; font-size: 12px; }
.stat-card strong { display: block; margin: 7px 0 3px; color: #3977e8; font-size: 23px; }
.stat-card small, .stat-card div { color: #98a2b3; font-size: 11px; }
.stat-card div { margin-top: 5px; }
.table-heading { margin-bottom: 10px; }
.status-cell { color: #98a2b3; font-size: 12px; }
.dialog-alert { margin-bottom: 16px; }
.import-form { margin: 18px 8px 0 0; }
.full-width { width: 100%; }
.empty-card { padding: 32px; }
@media (max-width: 900px) { .filter-row, .page-heading, .section-header, .table-heading { align-items: flex-start; flex-direction: column; }.dataset-select, .exam-select { width: min(100%, 370px); }.context-chips { margin-top: 12px; } }
</style>
