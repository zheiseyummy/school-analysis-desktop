<script setup lang="ts">
defineOptions({
  name: "ScoreEntry",
  inheritAttrs: false,
});
import {
  getExamBodyPage,
  getScoreEntryList,
  saveScore,
  getExamBodyScorePreview,
  downloadTemplateApi,
  previewScoreImport,
  confirmScoreImport,
  getScoreImportLogs,
  undoScoreImport,
} from "@/api/exam_body";

import {
  ExamBodyQuery,
  ExamBodyPageVO,
  ScoreEntryQuery,
  ScoreEntryVO,
  ScoreEntryForm,
  ScoreImportPreview,
  ScoreImportBatch,
} from "@/api/exam_body/types";

import type { UploadInstance } from "element-plus";
import { genFileId } from "element-plus";

const uploadRef = ref<UploadInstance>(); // 上传组件

import { getGradeOptions } from "@/api/grade";
import { getComplexClazzOptions } from "@/api/clazz";
import { getOptions } from "@/api/exam";
import { ClazzExamAnalysisQuery } from "@/api/analysis/types";
const gradeList = ref<OptionType[]>(); //年级下拉数据源
const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源
const examList = ref<OptionType[]>(); //考试列表下拉数据源

const queryFormRef = ref(ElForm);
const courseFormRef = ref(ElForm);
const scoreImportFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ExamBodyQuery>({
  pageNum: 1,
  pageSize: 10,
});

const examBodyList = ref<ExamBodyPageVO[]>();

const queryExamParams = reactive<ClazzExamAnalysisQuery>({});

/** 加载年级下拉数据源 */
async function loadGradeOptions() {
  getGradeOptions().then((response) => {
    gradeList.value = response.data;
  });
}

/** 加载携带年级信息的班级下拉数据源 */
async function loadComplexClazzOptions() {
  getComplexClazzOptions().then((response) => {
    complexClazzList.value = response.data;
  });
}

/** 加载考试下拉数据源 */
async function loadExamOptions() {
  queryExamParams.year = importData.year;
  getOptions(queryExamParams).then((response) => {
    examList.value = response.data;
  });
}

/** 查询 */
function handleQuery() {
  loading.value = true;
  getExamBodyPage(queryParams)
    .then(({ data }) => {
      examBodyList.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 重置查询 */
function resetQuery() {
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  queryParams.clazzId = undefined;
  handleQuery();
}

/** 行checkbox 选中事件 */
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
}

const dialog = reactive({
  title: "",
  visible: false,
  courseName: "",
  teacherName: "",
  scoreCount: 0,
  fullScore: 100,
});

const viewDialog = reactive({
  title: "",
  visible: false,
});

const importDialog = reactive({
  title: "",
  width: 600,
  visible: false,
});

const previewDialog = reactive({ visible: false });
const importPreview = ref<ScoreImportPreview>();
const importBlankPolicy = ref<"KEEP" | "CLEAR">("KEEP");
const importSubmitting = ref(false);
const mappingSubmitting = ref(false);
const manualMapping = reactive<Record<string, number | undefined>>({});
const mappingFields = [
  { key: "gradeName", label: "年级", required: true },
  { key: "clazzName", label: "班级", required: true },
  { key: "studentCode", label: "学号/考号", required: true },
  { key: "studentName", label: "姓名", required: true },
  { key: "chineseScore", label: "语文", required: false },
  { key: "mathScore", label: "数学", required: false },
  { key: "englishScore", label: "英语", required: false },
  { key: "physicsScore", label: "物理", required: false },
  { key: "chemistryScore", label: "化学", required: false },
  { key: "organismScore", label: "生物", required: false },
  { key: "geographyScore", label: "地理", required: false },
  { key: "historyScore", label: "历史", required: false },
  { key: "politicsScore", label: "政治/道法", required: false },
];
const importHistoryDialog = reactive({ visible: false });
const importHistoryList = ref<ScoreImportBatch[]>([]);
const importHistoryLoading = ref(false);

//成绩录入变量定义
const scoreEntryList = ref<ScoreEntryVO[]>();
const scoreEntryInfo = ref<ExamBodyPageVO>({});
const formData = reactive<ScoreEntryForm>({});

const importData = reactive({
  year: undefined,
  examId: undefined,
  file: undefined,
  fileList: [],
});

// 校验规则
const rules = reactive({
  year: [{ required: true, message: "所属年度不能为空", trigger: "blur" }],
  examId: [{ required: true, message: "所属考试不能为空", trigger: "blur" }],
});
//成绩预览变量定义
const columns = ref<any>();
const tableDataList = ref<any>();

/**
 * 打开成绩录入弹出框
 */
function openDialog(examBody: ExamBodyPageVO, item: any) {
  dialog.visible = true;
  dialog.title = "成绩录入界面";
  dialog.courseName = item.courseName;
  dialog.teacherName = item.teacherName;
  dialog.scoreCount = item.scoreCount;
  dialog.fullScore = item.fullScore;
  const query: ScoreEntryQuery = {
    examBodyId: examBody.id,
    courseId: item.courseId,
  };
  formData.examBodyId = examBody.id;
  formData.courseId = item.courseId;
  scoreEntryList.value = [];
  scoreEntryInfo.value = examBody;
  getScoreEntryList(query).then((response) => {
    scoreEntryList.value = response.data;
  });
}

function openViewDialog(examBody: ExamBodyPageVO) {
  viewDialog.visible = true;
  viewDialog.title =
    examBody.year +
    "/" +
    examBody.gradeName +
    "/" +
    examBody.clazzName +
    "/" +
    examBody.semesterStr +
    "/" +
    examBody.examTypeStr +
    "/" +
    examBody.examName +
    "  成绩预览界面";
  loading.value = true;
  columns.value = [];
  tableDataList.value = [];
  getExamBodyScorePreview(examBody.id!)
    .then(({ data }) => {
      columns.value = data.columns;
      tableDataList.value = data.tableDataList;
    })
    .finally(() => {
      loading.value = false;
    });
}

/**
 * 成绩录入提交操作
 */
function handleSubmit() {
  courseFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      formData.scoreList = scoreEntryList.value;
      saveScore(formData)
        .then(() => {
          ElMessage.success("成绩保存成功");
          closeDialog();
          resetQuery();
        })
        .finally(() => (loading.value = false));
    }
  });
}

/** 关闭成绩录入表单弹窗 */
function closeDialog() {
  dialog.visible = false;
  resetForm();
}

function closeViewDialog() {
  viewDialog.visible = false;
}

/** 重置成绩录入表单 */
function resetForm() {
  courseFormRef.value.resetFields();
  courseFormRef.value.clearValidate();
}
onMounted(() => {
  loadGradeOptions();
  loadComplexClazzOptions();
  handleQuery();
});

function conditionChange() {
  loadExamOptions();
}

function downloadTemplate() {
  downloadTemplateApi().then((response: any) => {
    const fileData = response.data;
    const fileName = decodeURI(
      response.headers["content-disposition"].split(";")[1].split("=")[1]
    );
    const fileType =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

    const blob = new Blob([fileData], { type: fileType });
    const downloadUrl = window.URL.createObjectURL(blob);

    const downloadLink = document.createElement("a");
    downloadLink.href = downloadUrl;
    downloadLink.download = fileName;

    document.body.appendChild(downloadLink);
    downloadLink.click();

    document.body.removeChild(downloadLink);
    window.URL.revokeObjectURL(downloadUrl);
  });
}

/** Excel文件 Change */
function handleFileChange(file: any) {
  importData.file = file.raw;
}

/** Excel文件 Exceed  */
function handleFileExceed(files: any) {
  uploadRef.value!.clearFiles();
  const file = files[0];
  file.uid = genFileId();
  uploadRef.value!.handleStart(file);
  importData.file = file;
}

function openScoreImportDialog() {
  resetImportFile();
  loadExamOptions();
  importDialog.title = "成绩导入";
  importDialog.visible = true;
}

function closeScoreImportDialog() {
  importDialog.visible = false;
  resetImportFile();
}

function scoreStatusLabel(status?: string) {
  return status === "ABSENT" ? "缺考" : status === "NOT_SELECTED" ? "未选科" : status === "NORMAL" ? "正常" : "未录入";
}

function resetImportFile() {
  importData.file = undefined;
  importData.fileList = [];
  uploadRef.value?.clearFiles();
}

function closeImportPreview() {
  previewDialog.visible = false;
  importPreview.value = undefined;
  resetImportFile();
  Object.keys(manualMapping).forEach((key) => delete manualMapping[key]);
}

function syncDetectedMapping(data: ScoreImportPreview) {
  Object.keys(manualMapping).forEach((key) => delete manualMapping[key]);
  Object.entries(data.detectedColumnIndexes || {}).forEach(([key, index]) => {
    manualMapping[key] = Number(index);
  });
}

function remapScoreImport() {
  if (!importData.examId || !importData.file) {
    ElMessage.warning("原始 Excel 文件已不存在，请重新选择文件");
    return;
  }
  const mapping = Object.fromEntries(
    Object.entries(manualMapping).filter(([, value]) => value !== undefined).map(([key, value]) => [key, Number(value)])
  ) as Record<string, number>;
  mappingSubmitting.value = true;
  previewScoreImport(importData.examId, importData.file, importBlankPolicy.value, mapping)
    .then(({ data }) => {
      importPreview.value = data;
      syncDetectedMapping(data);
      if (data.mappingComplete) ElMessage.success("列映射已确认，请继续核对导入差异");
    })
    .finally(() => (mappingSubmitting.value = false));
}

function openImportHistory() {
  importHistoryDialog.visible = true;
  importHistoryLoading.value = true;
  getScoreImportLogs()
    .then(({ data }) => (importHistoryList.value = data))
    .finally(() => (importHistoryLoading.value = false));
}

function handleImportUndo(row: ScoreImportBatch) {
  ElMessageBox.confirm(
    `确认撤销批次“${row.fileName || row.batchId}”的 ${row.changeCount - row.undoneCount} 项成绩变更？`,
    "撤销成绩导入",
    { type: "warning", confirmButtonText: "确认撤销", cancelButtonText: "取消" }
  ).then(() => {
    importHistoryLoading.value = true;
    undoScoreImport(row.batchId)
      .then(({ data }) => {
        ElMessage.success(`已撤销 ${data.undoneChanges} 项成绩变更`);
        row.undoneCount = row.changeCount;
        row.canUndo = false;
        resetQuery();
      })
      .finally(() => (importHistoryLoading.value = false));
  });
}

const handleScoreImportSubmit = useThrottleFn(() => {
  scoreImportFormRef.value.validate((valid: any) => {
    if (valid) {
      if (!importData?.examId) {
        ElMessage.warning("请选择考试");
        return false;
      }
      if (!importData?.file) {
        ElMessage.warning("上传Excel文件不能为空");
        return false;
      }
      importSubmitting.value = true;
      previewScoreImport(importData.examId, importData.file, importBlankPolicy.value)
        .then(({ data }) => {
          importPreview.value = data;
          syncDetectedMapping(data);
          importDialog.visible = false;
          previewDialog.visible = true;
        })
        .finally(() => (importSubmitting.value = false));
    }
  });
}, 3000);

const handleImportConfirm = useThrottleFn(() => {
  if (!importPreview.value) return;
  importSubmitting.value = true;
  confirmScoreImport(importPreview.value.token, importPreview.value.errorRows > 0)
    .then(({ data }) => {
      ElMessage.success(`导入完成：已应用 ${data.appliedChanges} 项变更，跳过 ${data.skippedErrorRows} 行错误数据`);
      closeImportPreview();
      resetQuery();
    })
    .finally(() => (importSubmitting.value = false));
}, 1000);
</script>
<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="关键字">
          <el-input
            v-model="queryParams.keywords"
            placeholder="考试编码/名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="年度" prop="year">
          <el-date-picker
            v-model="queryParams.year"
            type="year"
            format="YYYY"
            value-format="YYYY"
            placeholder="请选择考试年度"
          />
        </el-form-item>
        <el-form-item label="学期" prop="semester" style="width: 268px">
          <dictionary v-model="queryParams.semester" type-code="semester" />
        </el-form-item>
        <el-form-item label="考试类型" prop="examType" style="width: 268px">
          <dictionary v-model="queryParams.examType" type-code="examType" />
        </el-form-item>

        <el-form-item label="年级" prop="gradeId">
          <el-select
            v-model="queryParams.gradeId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
          >
            <el-option
              v-for="item in gradeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="班级">
          <el-select
            v-model="queryParams.clazzId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
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

        <el-form-item>
          <el-button type="primary" @click="handleQuery"
            ><i-ep-search />搜索</el-button
          >
          <el-button @click="resetQuery"><i-ep-refresh />重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-card shadow="never" class="table-container">
      <template #header>
        <div class="flex justify-between">
          <div></div>
          <div>
            <el-dropdown split-button>
              导入
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="downloadTemplate">
                    <i-ep-download />下载模板</el-dropdown-item
                  >
                  <el-dropdown-item @click="openScoreImportDialog">
                    <i-ep-top />导入数据</el-dropdown-item
                  >
                  <el-dropdown-item @click="openImportHistory">
                    <i-ep-tickets />导入记录</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="examBodyList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="年度" prop="year" width="120" align="center" />
        <el-table-column
          label="学期"
          prop="semesterStr"
          width="120"
          align="center"
        />
        <el-table-column
          label="考试类型"
          prop="examTypeStr"
          width="120"
          align="center"
        />
        <el-table-column
          label="考试名称"
          prop="examName"
          width="180"
          align="center"
        />
        <el-table-column
          label="考试时间"
          prop="examDate"
          width="180"
          align="center"
        />
        <el-table-column
          label="年级"
          prop="gradeName"
          width="120"
          align="center"
        />
        <el-table-column
          label="班级"
          prop="clazzName"
          width="120"
          align="center"
        />
        <el-table-column
          label="学生数量"
          prop="studentCount"
          width="100"
          align="center"
        >
          <template #default="scope">
            {{ scope.row.studentCount }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="成绩录入操作区域" align="center">
          <template #default="scope">
            <template v-for="item in scope.row.courseList" :key="item.id">
              <el-badge :value="item.scoreCount" class="item">
                <el-button
                  @click="openDialog(scope.row, item)"
                  v-if="item.subjectType == '1'"
                  type="success"
                >
                  {{ item.courseName }}
                </el-button>
                <el-button
                  @click="openDialog(scope.row, item)"
                  v-if="item.subjectType == '2'"
                  type="primary"
                >
                  {{ item.courseName }}
                </el-button>
                <el-button
                  @click="openDialog(scope.row, item)"
                  v-if="item.subjectType == '3'"
                  type="warning"
                >
                  {{ item.courseName }}
                </el-button>
              </el-badge>
            </template>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="成绩结果操作区域"
          align="center"
          width="150"
        >
          <template #default="scope">
            <el-button @click="openViewDialog(scope.row)"> 汇总 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery"
      />
    </el-card>

    <!-- 课程表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="600px"
      @close="closeDialog"
    >
      <el-card>
        <template #header>
          <div class="card-header">
            <el-descriptions
              direction="vertical"
              :column="4"
              :size="'default'"
              border
            >
              <el-descriptions-item label="班级">
                {{ scoreEntryInfo.year }} / {{ scoreEntryInfo.gradeName }}/
                {{ scoreEntryInfo.clazzName }}
              </el-descriptions-item>
              <el-descriptions-item label="学期">{{
                scoreEntryInfo.semesterStr
              }}</el-descriptions-item>
              <el-descriptions-item label="课程">
                {{ dialog.courseName }}({{ dialog.fullScore }})
              </el-descriptions-item>
              <el-descriptions-item label="教师">
                {{ dialog.teacherName }}
              </el-descriptions-item>

              <el-descriptions-item label="考试类型">{{
                scoreEntryInfo.examTypeStr
              }}</el-descriptions-item>
              <el-descriptions-item label="考试名称">
                {{ scoreEntryInfo.examName }}
              </el-descriptions-item>
              <el-descriptions-item label="考试时间">
                {{ scoreEntryInfo.examDate }}
              </el-descriptions-item>
              <el-descriptions-item label="应考情况">
                <span>{{ dialog.scoreCount }}</span
                >/<span>{{ scoreEntryInfo.studentCount }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
        <el-form
          ref="courseFormRef"
          :model="scoreEntryList"
          label-width="100px"
        >
          <el-row>
            <el-col
              :span="12"
              v-for="item in scoreEntryList"
              :key="item.studentId"
            >
              <el-form-item :label="item.studentName">
                <el-input-number
                  :min="0"
                  :max="dialog.fullScore"
                  v-model="item.score"
                  :disabled="item.status === 'ABSENT' || item.status === 'NOT_SELECTED'"
                />
                <el-select v-model="item.status" class="score-status" placeholder="状态">
                  <el-option label="正常" value="NORMAL" />
                  <el-option label="缺考" value="ABSENT" />
                  <el-option label="未选科" value="NOT_SELECTED" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 课程预览弹窗 -->
    <el-dialog
      v-model="viewDialog.visible"
      :title="viewDialog.title"
      width="90%"
      @close="closeViewDialog"
    >
      <el-card>
        <custom-table
          :loading="loading"
          :table-data-list="tableDataList"
          :columns="columns"
        />
      </el-card>
    </el-dialog>

    <!-- 导入成绩弹窗 -->
    <el-dialog
      v-model="importDialog.visible"
      :title="importDialog.title"
      :width="importDialog.width"
      append-to-body
      @close="closeScoreImportDialog"
    >
      <!-- 成绩导入表单 -->
      <el-form
        ref="scoreImportFormRef"
        :model="importData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="年度" prop="year">
          <el-date-picker
            v-model="importData.year"
            type="year"
            format="YYYY"
            value-format="YYYY"
            placeholder="请选择考试年度"
            @change="conditionChange"
          />
        </el-form-item>

        <el-form-item label="考试" prop="examId">
          <el-tree-select
            v-model="importData.examId"
            placeholder="请选择考试"
            :data="examList"
            filterable
            check-strictly
          />
        </el-form-item>

        <el-form-item label="Excel文件">
          <el-upload
            ref="uploadRef"
            action=""
            drag
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
            :limit="1"
            :auto-upload="false"
            :file-list="importData.fileList"
            :on-change="handleFileChange"
            :on-exceed="handleFileExceed"
          >
            <el-icon class="el-icon--upload">
              <i-ep-upload-filled />
            </el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或
              <em>点击上传</em>
            </div>
            <template #tip>
              <div>xls/xlsx files</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="空白成绩处理">
          <el-radio-group v-model="importBlankPolicy">
            <el-radio label="KEEP">保留原成绩（推荐）</el-radio>
            <el-radio label="CLEAR">清除原成绩</el-radio>
          </el-radio-group>
          <div class="form-tip">系统会先预览差异，空白不会自动当作缺考或 0 分。</div>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部操作按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="importSubmitting" @click="handleScoreImportSubmit"
            >确 定</el-button
          >
          <el-button @click="closeScoreImportDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="previewDialog.visible"
      title="确认成绩导入"
      width="1000px"
      append-to-body
      @close="closeImportPreview"
    >
      <template v-if="importPreview">
        <el-alert
          :type="!importPreview.mappingComplete ? 'error' : importPreview.errorRows ? 'warning' : 'success'"
          :closable="false"
          show-icon
          :title="!importPreview.mappingComplete ? '表头未完成识别，暂不能写入成绩' : `共解析 ${importPreview.totalRows} 行，有效 ${importPreview.validRows} 行，错误 ${importPreview.errorRows} 行，待应用 ${importPreview.changeCount} 项变更`"
        />
        <div class="import-mapping-summary">
          <div class="mapping-summary-head">
            <strong>表头识别结果</strong>
            <span>{{ importPreview.sheetName || "工作表" }} · 第 {{ importPreview.headerRowNumber || "—" }} 行表头</span>
          </div>
          <div class="mapping-tags">
            <el-tag v-for="(source, field) in importPreview.headerMappings" :key="field" size="small" effect="plain">
              {{ field }} ← {{ source }}
            </el-tag>
            <span v-if="!Object.keys(importPreview.headerMappings || {}).length" class="muted">未识别到可用列</span>
          </div>
          <div v-if="importPreview.mappingWarnings?.length" class="mapping-warnings">
            <div v-for="warning in importPreview.mappingWarnings" :key="warning">{{ warning }}</div>
          </div>
        </div>
        <el-alert
          v-if="!importPreview.mappingComplete"
          type="warning"
          :closable="false"
          show-icon
          title="可以在下方手动指定列，确认后重新预览。未完成映射不会写入成绩。"
        />
        <div v-if="!importPreview.mappingComplete" class="manual-mapping-panel">
          <div class="manual-mapping-grid">
            <div v-for="field in mappingFields" :key="field.key" class="manual-mapping-item">
              <span>{{ field.label }}<em v-if="field.required">*</em></span>
              <el-select v-model="manualMapping[field.key]" clearable filterable size="small" placeholder="请选择原始列">
                <el-option
                  v-for="(label, index) in importPreview.availableColumns"
                  :key="index"
                  :label="`${label}（第 ${Number(index) + 1} 列）`"
                  :value="Number(index)"
                />
              </el-select>
            </div>
          </div>
          <el-button type="primary" plain :loading="mappingSubmitting" @click="remapScoreImport">按映射重新预览</el-button>
        </div>
        <div class="preview-tip">
          空白处理：{{ importPreview.blankPolicy === "KEEP" ? "保留原成绩" : "清除原成绩" }}；空白不会自动判为缺考或 0 分。
        </div>
        <el-collapse v-if="importPreview.errors.length" class="preview-section">
          <el-collapse-item title="查看错误行（错误行不会写入）" name="errors">
            <el-scrollbar max-height="180px">
              <div v-for="error in importPreview.errors" :key="error" class="preview-error">{{ error }}</div>
            </el-scrollbar>
          </el-collapse-item>
        </el-collapse>
        <el-table :data="importPreview.changes" border stripe max-height="360px" class="preview-section">
          <el-table-column prop="rowNumber" label="行" width="70" />
          <el-table-column prop="studentCode" label="学号" width="130" />
          <el-table-column prop="studentName" label="姓名" width="110" />
          <el-table-column prop="courseName" label="科目" width="110" />
          <el-table-column prop="action" label="操作" width="100">
            <template #default="scope">
              {{ scope.row.action === "ADD" ? "新增" : scope.row.action === "UPDATE" ? "更新" : "清除" }}
            </template>
          </el-table-column>
          <el-table-column prop="oldScore" label="原成绩" />
          <el-table-column prop="newScore" label="新成绩" />
          <el-table-column label="状态" width="150">
            <template #default="scope">
              {{ scoreStatusLabel(scope.row.newStatus || scope.row.oldStatus) }}
            </template>
          </el-table-column>
        </el-table>
      </template>
      <template #footer>
        <el-button @click="closeImportPreview">取消</el-button>
        <el-button type="primary" :loading="importSubmitting" :disabled="!importPreview || !importPreview.mappingComplete || !importPreview.changeCount" @click="handleImportConfirm">确认提交有效变更</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importHistoryDialog.visible" title="成绩导入记录" width="900px">
      <el-table v-loading="importHistoryLoading" :data="importHistoryList" border stripe>
        <el-table-column label="导入时间" prop="createdAt" width="180" />
        <el-table-column label="文件名" prop="fileName" min-width="220" show-overflow-tooltip />
        <el-table-column label="变更数" prop="changeCount" width="90" align="center" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.canUndo" type="warning">可撤销</el-tag>
            <el-tag v-else type="info">已撤销</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.canUndo" type="danger" link @click="handleImportUndo(scope.row as ScoreImportBatch)">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="importHistoryDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>
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

.form-tip,
.preview-tip {
  width: 100%;
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.score-status {
  width: 96px;
  margin-left: 8px;
}

.preview-section {
  margin-top: 14px;
}

.preview-error {
  padding: 3px 8px;
  color: var(--el-color-danger);
}

.import-mapping-summary {
  padding: 12px 14px;
  margin: 12px 0;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-fill-color-lighter);
}

.mapping-summary-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.mapping-summary-head strong {
  color: var(--el-text-color-primary);
  font-size: 13px;
}

.mapping-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.mapping-warnings {
  padding-top: 8px;
  color: var(--el-color-warning-dark-2);
  font-size: 12px;
  line-height: 1.7;
}

.manual-mapping-panel {
  padding: 12px 14px;
  margin: 12px 0;
  border: 1px dashed var(--el-color-primary-light-5);
  border-radius: 6px;
  background: var(--el-color-primary-light-9);
}

.manual-mapping-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px 12px;
  margin-bottom: 12px;
}

.manual-mapping-item {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.manual-mapping-item > span {
  flex: 0 0 72px;
  color: var(--el-text-color-regular);
  font-size: 12px;
}

.manual-mapping-item em {
  margin-left: 2px;
  color: var(--el-color-danger);
  font-style: normal;
}

.manual-mapping-item .el-select {
  min-width: 0;
  flex: 1;
}

@media (max-width: 900px) {
  .manual-mapping-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
