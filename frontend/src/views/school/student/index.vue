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
import type { UploadFile } from "element-plus";
import { uploadFileApi } from "@/api/file";
import { getComplexClazzOptions } from "@/api/clazz";

import type { UploadInstance } from "element-plus";
import { genFileId } from "element-plus";
const uploadRef = ref<UploadInstance>(); // 上传组件

const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源

const queryFormRef = ref(ElForm);
const studentFormRef = ref(ElForm);

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
  formData.avatar = undefined;
  formData.clazzList = [];
  formData.sort = 1;
  formData.status = 1;
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

async function onChange(file: UploadFile) {
  uploadFileApi(file.raw as File).then(({ data }) => {
    formData.avatar = data.url;
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
      <div class="images" v-viewer>
        <el-table
          ref="dataTableRef"
          v-loading="loading"
          :data="studentList"
          highlight-current-row
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="头像" align="center" prop="avatar" width="60">
            <template #default="scope">
              <img :src="scope.row.avatar" class="user-avatar" />
            </template>
          </el-table-column>
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
          <el-table-column label="年龄" align="center" prop="age" width="100" />
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
          <el-table-column fixed="right" label="操作" width="160">
            <template #default="scope">
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
      </div>
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
          <el-col :span="6">
            <el-form-item label="个人头像">
              <el-upload
                ref="upload"
                class="avatar-uploader"
                action="javascript:void(0);"
                :show-file-list="false"
                :auto-upload="false"
                :on-change="onChange"
                accept="image/*"
              >
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <div class="images" v-viewer>
              <img
                v-if="formData.avatar"
                :src="formData.avatar"
                class="avatar"
              />
            </div>
          </el-col>
          <el-col :span="12">
            <el-row>
              <el-col :span="24">
                <el-form-item label="学生学号" prop="code">
                  <el-input
                    v-model="formData.code"
                    placeholder="请输入学生学号"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="学生姓名" prop="name">
                  <el-input
                    v-model="formData.name"
                    placeholder="请输入学生姓名"
                  />
                </el-form-item>
              </el-col>
            </el-row>
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
  </div>
</template>
<style>
.avatar-uploader .el-upload {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  width: 96px;
  height: 96px;
  font-size: 28px;
  color: #8c939d;
  text-align: center;
}

.avatar {
  display: block;
  width: 96px;
  height: 96px;
  margin-left: 15px;
}

.user-avatar {
  display: block;
  width: 40px;
  height: 40px;
  border-radius: 20px;
}
</style>
