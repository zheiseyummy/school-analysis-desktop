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
  importScore,
} from "@/api/exam_body";

import {
  ExamBodyQuery,
  ExamBodyPageVO,
  ScoreEntryQuery,
  ScoreEntryVO,
  ScoreEntryForm,
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
  loadExamOptions();
  importDialog.title = "成绩导入";
  importDialog.visible = true;
}

function closeScoreImportDialog() {
  importDialog.visible = false;
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
      importScore(importData?.examId, importData?.file).then((response) => {
        ElMessage.success(response.data);
        closeScoreImportDialog();
        resetQuery();
      });
    }
  });
}, 3000);
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
                />
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
      </el-form>
      <!-- 弹窗底部操作按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleScoreImportSubmit"
            >确 定</el-button
          >
          <el-button @click="closeScoreImportDialog">取 消</el-button>
        </div>
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
</style>
