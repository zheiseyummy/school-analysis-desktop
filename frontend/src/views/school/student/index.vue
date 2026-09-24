<script setup lang="ts">
defineOptions({
  name: "Student",
  inheritAttrs: false,
});
import {
  getStudentPage,
  getStudentForm,
  addStudent,
  updateStudent,
  deleteStudents,
  downloadTemplateApi,
  exportStudent,
  importStudent,
} from "@/api/student";

import { StudentQuery, StudentPageVO, StudentForm } from "@/api/student/types";
import {
  getStudentFollowups,
  addStudentFollowup,
  updateStudentFollowup,
  deleteStudentFollowup,
} from "@/api/student-followup";
import {
  StudentFollowup,
  StudentFollowupForm,
} from "@/api/student-followup/types";
import type { UploadFile } from "element-plus";
import { getComplexClazzOptions } from "@/api/clazz";

import type { UploadInstance } from "element-plus";
import { genFileId } from "element-plus";
const uploadRef = ref<UploadInstance>(); // 上传组件

const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源

const queryFormRef = ref(ElForm);
const studentFormRef = ref(ElForm);
const followupFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<StudentQuery>({
  pageNum: 1,
  pageSize: 10,
});

const studentList = ref<StudentPageVO[]>();

const dialog = reactive({
  title: "",
  type: "student-form",
  width: 800,
  visible: false,
});

const formData = reactive<StudentForm>({
  sort: 1,
  status: 1,
  code: "",
  name: "",
  clazzList: [],
});

const rules = reactive({
  name: [{ required: true, message: "请输入学生名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入学生学号", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
  sex: [{ required: true, message: "请选择性别", trigger: "blur" }],
  year: [{ required: true, message: "请选择入学年份", trigger: "blur" }],
  phone: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

/** 加载携带年级信息的班级下拉数据源 */
async function loadComplexClazzOptions() {
  getComplexClazzOptions().then((response) => {
    complexClazzList.value = response.data;
  });
}

/** 查询 */
function handleQuery() {
  loading.value = true;
  getStudentPage(queryParams)
    .then(({ data }) => {
      studentList.value = data.list;
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

/** 打开学生表单弹窗 */
function openDialog(type: string, studentId?: number) {
  dialog.visible = true;
  dialog.type = type;
  if (dialog.type === "student-form") {
    dialog.width = 800;
    if (studentId) {
      dialog.title = "修改学生";
      getStudentForm(studentId).then(({ data }) => {
        Object.assign(formData, data, { clazzList: data.clazzList ?? [] });
      });
    } else {
      dialog.title = "新增学生";
    }
  } else if (dialog.type === "student-import") {
    // 学生导入弹窗
    dialog.title = "导入学生";
    dialog.width = 600;
  }
}

/** 学生保存提交 */
function handleSubmit() {
  if (dialog.type === "student-form") {
    studentFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const studentId = formData.id;
        if (studentId) {
          updateStudent(studentId, formData)
            .then(() => {
              ElMessage.success("修改成功");
              closeDialog();
              resetQuery();
            })
            .finally(() => (loading.value = false));
        } else {
          addStudent(formData)
            .then(() => {
              ElMessage.success("新增成功");
              closeDialog();
              resetQuery();
            })
            .finally(() => (loading.value = false));
        }
      }
    });
  } else if (dialog.type === "student-import") {
    if (!importData?.file) {
      ElMessage.warning("上传Excel文件不能为空");
      return false;
    }
    importStudent(importData?.file).then((response) => {
      ElMessage.success(response.data);
      closeDialog();
      resetQuery();
    });
  }
}

/** 关闭表单弹窗 */
function closeDialog() {
  dialog.visible = false;
  if (dialog.type === "student-form") {
    resetForm();
  } else if (dialog.type === "student-import") {
    importData.file = undefined;
    importData.fileList = [];
  }
}

/** 重置表单 */
function resetForm() {
  studentFormRef.value.resetFields();
  studentFormRef.value.clearValidate();

  formData.id = undefined;
  formData.clazzList = [];
  formData.sort = 1;
  formData.status = 1;
}

function openFollowupDialog(row: StudentPageVO) {
  if (!row.id) return;
  selectedStudent.value = row;
  followupDialog.visible = true;
  followupLoading.value = true;
  getStudentFollowups(row.id)
    .then(({ data }) => (followupList.value = data))
    .finally(() => (followupLoading.value = false));
}

function resetFollowupForm() {
  followupFormRef.value?.resetFields?.();
  followupFormRef.value?.clearValidate?.();
  followupForm.learningStatus = "正常学习";
  followupForm.specialSituation = "";
  followupForm.followupContent = "";
  followupForm.nextAction = "";
  followupForm.followupDate = new Date().toISOString().slice(0, 10);
}

function openFollowupForm(row?: StudentFollowup) {
  resetFollowupForm();
  if (row) {
    followupFormDialog.title = "编辑跟进记录";
    Object.assign(followupForm, {
      learningStatus: row.learningStatus,
      specialSituation: row.specialSituation ?? "",
      followupContent: row.followupContent,
      nextAction: row.nextAction ?? "",
      followupDate: row.followupDate,
    });
  } else {
    followupFormDialog.title = "新增跟进记录";
  }
  followupFormDialog.visible = true;
  (followupForm as StudentFollowupForm & { id?: number }).id = row?.id;
}

function handleFollowupSubmit() {
  followupFormRef.value.validate((valid: boolean) => {
    if (!valid || !selectedStudent.value.id) return;
    followupLoading.value = true;
    const id = (followupForm as StudentFollowupForm & { id?: number }).id;
    const request = id
      ? updateStudentFollowup(id, followupForm)
      : addStudentFollowup(selectedStudent.value.id, followupForm);
    request
      .then(() => {
        ElMessage.success(id ? "跟进记录已更新" : "跟进记录已添加");
        followupFormDialog.visible = false;
        return getStudentFollowups(selectedStudent.value.id!);
      })
      .then(({ data }) => (followupList.value = data))
      .finally(() => (followupLoading.value = false));
  });
}

function handleFollowupDelete(row: StudentFollowup) {
  if (!row.id) return;
  ElMessageBox.confirm("确认删除这条跟进记录吗？", "提示", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(() => {
    followupLoading.value = true;
    deleteStudentFollowup(row.id!)
      .then(() => {
        ElMessage.success("跟进记录已删除");
        return getStudentFollowups(selectedStudent.value.id!);
      })
      .then(({ data }) => (followupList.value = data))
      .finally(() => (followupLoading.value = false));
  });
}

/** 删除学生 */
function handleDelete(studentId?: number) {
  const studentIds = [studentId || ids.value].join(",");
  if (!studentIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteStudents(studentIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

const addItem = () => {
  (formData.clazzList ??= []).push({
    clazzId: undefined,
    year: undefined,
  });
};
const deleteItem = (index: number) => {
  formData.clazzList?.splice(index, 1);
};

// 学生导入数据
const importData = reactive<{ file?: File; fileList: UploadFile[] }>({
  file: undefined,
  fileList: [],
});

const followupDialog = reactive({ visible: false, title: "学生跟进记录" });
const followupFormDialog = reactive({ visible: false, title: "新增跟进记录" });
const followupLoading = ref(false);
const followupList = ref<StudentFollowup[]>([]);
const selectedStudent = ref<StudentPageVO>({});
const followupForm = reactive<StudentFollowupForm>({
  learningStatus: "正常学习",
  specialSituation: "",
  followupContent: "",
  nextAction: "",
  followupDate: new Date().toISOString().slice(0, 10),
});
const followupRules = reactive({
  learningStatus: [
    { required: true, message: "请选择学习状态", trigger: "change" },
  ],
  followupContent: [
    { required: true, message: "请输入跟进记录", trigger: "blur" },
  ],
  followupDate: [
    { required: true, message: "请选择跟进日期", trigger: "change" },
  ],
});

/** 下载导入模板 */
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

/** 导出学生 */
function handleExport() {
  exportStudent(queryParams).then((response: any) => {
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

onMounted(() => {
  loadComplexClazzOptions();
  handleQuery();
});
</script>
<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="关键字">
          <el-input
            v-model="queryParams.keywords"
            placeholder="学号/姓名/手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="入学年份" prop="year">
          <el-date-picker
            v-model="queryParams.year"
            type="year"
            format="YYYY"
            value-format="YYYY"
            clearable
            placeholder="请选择入学年份"
          />
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
          <div>
            <el-button type="success" @click="openDialog('student-form')"
              ><i-ep-plus />新增</el-button
            >
            <el-button
              type="danger"
              :disabled="ids.length === 0"
              @click="handleDelete()"
              ><i-ep-delete />删除</el-button
            >
          </div>

          <div>
            <el-dropdown split-button>
              导入
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="downloadTemplate">
                    <i-ep-download />下载模板</el-dropdown-item
                  >
                  <el-dropdown-item @click="openDialog('student-import')">
                    <i-ep-top />导入数据</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button class="ml-3" @click="handleExport"
              ><template #icon><i-ep-download /></template>导出</el-button
            >
          </div>
        </div>
      </template>
      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="studentList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          align="center"
          label="学生学号"
          prop="code"
          width="120"
        />
        <el-table-column label="学生姓名" prop="name" width="150" />
        <el-table-column
          label="性别"
          align="center"
          prop="sexLabel"
          width="60"
        />
        <el-table-column
          label="出生日期"
          align="center"
          prop="birthDay"
          width="100"
        />
        <!-- <el-table-column label="电话" prop="phone" /> -->
        <el-table-column
          label="入学年份"
          prop="year"
          align="center"
          width="90"
        />
        <el-table-column align="center" label="班级列表" width="180">
          <template #default="scope">
            <span v-html="scope.row.clazzNameList"></span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" />
        <!-- <el-table-column label="创建时间" prop="createTime" /> -->
        <!-- <el-table-column label="班级数量" prop="clazzCount" width="90" /> -->
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="230">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="openFollowupDialog(scope.row)"
            >
              <i-ep-chat-line-round />跟进
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              @click="openDialog('student-form', scope.row.id)"
            >
              <i-ep-edit />编辑
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              @click="handleDelete(scope.row.id)"
            >
              <i-ep-delete />删除
            </el-button>
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

    <!-- 学生表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      :width="dialog.width"
      @close="closeDialog"
    >
      <el-form
        v-if="dialog.type === 'student-form'"
        ref="studentFormRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="学生学号" prop="code">
              <el-input v-model="formData.code" placeholder="请输入学生学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入学生姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <dictionary v-model="formData.sex" type-code="gender" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1" label="正常" />
                <el-radio :value="0" label="停用" />
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="入学年份" prop="year">
              <el-date-picker
                v-model="formData.year"
                type="year"
                format="YYYY"
                value-format="YYYY"
                placeholder="请选择入学年份"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDay">
              <el-date-picker
                v-model="formData.birthDay"
                type="date"
                placeholder="请选择出生日期"
                size="default"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 未分班学生保持空列表，仅在主动添加后校验班级和年度。 -->
        <el-form-item label="班级归属">
          <el-button type="primary" plain @click="addItem">
            <i-ep-plus />添加班级
          </el-button>
        </el-form-item>
        <el-row v-for="(item, index) in formData.clazzList" :key="index">
          <el-col :span="10">
            <el-form-item
              label="班级"
              :prop="'clazzList.' + index + '.clazzId'"
              :rules="[
                { required: true, message: '班级不能为空', trigger: 'blur' },
              ]"
            >
              <el-select v-model="item.clazzId" placeholder="请选择班级">
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
              </el-select> </el-form-item
          ></el-col>
          <el-col :span="10">
            <el-form-item
              label="年度"
              :prop="'clazzList.' + index + '.year'"
              :rules="[
                { required: true, message: '年度不能为空', trigger: 'blur' },
              ]"
            >
              <el-date-picker
                v-model="item.year"
                type="year"
                format="YYYY"
                value-format="YYYY"
                placeholder="请选择年度"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label-width="15px">
              <el-button type="danger" @click="deleteItem(index)">
                移除
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col>
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="formData.remark"
                :rows="2"
                type="textarea"
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 学生导入表单 -->
      <el-form
        v-else-if="dialog.type === 'student-import'"
        :model="importData"
        label-width="100px"
      >
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

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
          <el-button @click="closeDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="followupDialog.visible"
      :title="`【${selectedStudent.name || ''}】学习跟进`"
      width="980px"
    >
      <div class="followup-toolbar">
        <div class="followup-summary">
          学号：{{ selectedStudent.code || "-" }}　学生备注：{{
            selectedStudent.remark || "暂无"
          }}
        </div>
        <el-button type="primary" @click="openFollowupForm()">
          <i-ep-plus />新增跟进
        </el-button>
      </div>
      <el-table v-loading="followupLoading" :data="followupList" border stripe>
        <el-table-column label="日期" prop="followupDate" width="120" />
        <el-table-column label="学习状态" prop="learningStatus" width="120">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.learningStatus === '正常学习' ? 'success' : 'warning'
              "
            >
              {{ scope.row.learningStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="特殊情况"
          prop="specialSituation"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column
          label="跟进记录"
          prop="followupContent"
          min-width="260"
          show-overflow-tooltip
        />
        <el-table-column
          label="下一步"
          prop="nextAction"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="openFollowupForm(scope.row as StudentFollowup)"
              >编辑</el-button
            >
            <el-button
              type="danger"
              link
              @click="handleFollowupDelete(scope.row as StudentFollowup)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="followupDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="followupFormDialog.visible"
      :title="followupFormDialog.title"
      width="620px"
    >
      <el-form
        ref="followupFormRef"
        :model="followupForm"
        :rules="followupRules"
        label-width="100px"
      >
        <el-form-item label="学习状态" prop="learningStatus">
          <el-select
            v-model="followupForm.learningStatus"
            placeholder="请选择学习状态"
            style="width: 100%"
          >
            <el-option label="正常学习" value="正常学习" />
            <el-option label="需要关注" value="需要关注" />
            <el-option label="重点跟进" value="重点跟进" />
            <el-option label="暂未评估" value="暂未评估" />
          </el-select>
        </el-form-item>
        <el-form-item label="特殊情况" prop="specialSituation">
          <el-input
            v-model="followupForm.specialSituation"
            type="textarea"
            :rows="2"
            placeholder="可填写家庭、健康、出勤或其他需要关注的情况"
          />
        </el-form-item>
        <el-form-item label="跟进记录" prop="followupContent">
          <el-input
            v-model="followupForm.followupContent"
            type="textarea"
            :rows="4"
            placeholder="记录本次沟通、辅导或观察情况"
          />
        </el-form-item>
        <el-form-item label="下一步" prop="nextAction">
          <el-input
            v-model="followupForm.nextAction"
            type="textarea"
            :rows="2"
            placeholder="填写下一次跟进计划"
          />
        </el-form-item>
        <el-form-item label="跟进日期" prop="followupDate">
          <el-date-picker
            v-model="followupForm.followupDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button
          type="primary"
          :loading="followupLoading"
          @click="handleFollowupSubmit"
          >保存</el-button
        >
        <el-button @click="followupFormDialog.visible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<style>
.followup-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.followup-summary {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
