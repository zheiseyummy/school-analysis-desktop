<script setup lang="ts">
defineOptions({
  name: "Teacher",
  inheritAttrs: false,
});
import {
  getTeacherPage,
  getTeacherForm,
  addTeacher,
  updateTeacher,
  deleteTeachers,
  downloadTemplateApi,
  exportTeacher,
  importTeacher,
  getTeacherArrangements,
  replaceTeacherArrangements,
} from "@/api/teacher";
import { getGradeOptions } from "@/api/grade";
import { getClazzPage } from "@/api/clazz";
import { getCourseOptions } from "@/api/course";
import type { ClazzPageVO } from "@/api/clazz/types";
import type { ArrangeForm } from "@/api/arrange/types";

import { TeacherQuery, TeacherPageVO, TeacherForm } from "@/api/teacher/types";
import type { UploadFile } from "element-plus";
import type { UploadInstance } from "element-plus";

import { genFileId } from "element-plus";

const queryFormRef = ref(ElForm);
const teacherFormRef = ref(ElForm);
const uploadRef = ref<UploadInstance>(); // 上传组件

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<TeacherQuery>({
  pageNum: 1,
  pageSize: 10,
});

const teacherList = ref<TeacherPageVO[]>();

const dialog = reactive({
  title: "",
  type: "teacher-form",
  width: 800,
  visible: false,
});

const formData = reactive<TeacherForm>({
  sort: 1,
  status: 1,
  code: "",
  name: "",
});

type TeachingRow = ArrangeForm & { gradeId?: number; gradeName?: string; clazzName?: string; courseName?: string };
const gradeOptions = ref<OptionType[]>([]);
const clazzOptions = ref<ClazzPageVO[]>([]);
const courseOptions = ref<OptionType[]>([]);
const teachingRows = ref<TeachingRow[]>([]);
const teachingDraft = reactive<{ gradeId?: number; clazzId?: number; courseId?: number }>({});

const clazzOptionsForGrade = computed(() =>
  clazzOptions.value.filter((item) => !teachingDraft.gradeId || item.gradeId === teachingDraft.gradeId)
);

async function loadTeachingOptions(teacherId?: number) {
  const [grades, clazzes, courses] = await Promise.all([
    getGradeOptions(),
    getClazzPage({ pageNum: 1, pageSize: 1000 }),
    getCourseOptions(),
  ]);
  gradeOptions.value = grades.data || [];
  clazzOptions.value = clazzes.data?.list || [];
  courseOptions.value = courses.data || [];
  teachingRows.value = [];
  if (teacherId) {
    const { data } = await getTeacherArrangements(teacherId);
    teachingRows.value = (data || []).map((item) => {
      const clazz = clazzOptions.value.find((c) => c.id === item.clazzId);
      const course = courseOptions.value.find((c) => Number(c.value) === item.courseId);
      return { ...item, gradeId: clazz?.gradeId, gradeName: gradeOptions.value.find((g) => Number(g.value) === clazz?.gradeId)?.label, clazzName: clazz?.name, courseName: course?.label };
    });
  }
}

function addTeachingRow() {
  if (!teachingDraft.gradeId || !teachingDraft.clazzId || !teachingDraft.courseId) {
    ElMessage.warning("请先选择年级、班级和任教学科");
    return;
  }
  if (teachingRows.value.some((item) => item.clazzId === teachingDraft.clazzId && item.courseId === teachingDraft.courseId)) {
    ElMessage.warning("该班级的该学科已经配置");
    return;
  }
  const clazz = clazzOptions.value.find((c) => c.id === teachingDraft.clazzId);
  const course = courseOptions.value.find((c) => Number(c.value) === teachingDraft.courseId);
  const grade = gradeOptions.value.find((g) => Number(g.value) === teachingDraft.gradeId);
  teachingRows.value.push({ clazzId: teachingDraft.clazzId, courseId: teachingDraft.courseId, status: 1, teacherId: formData.id, gradeId: teachingDraft.gradeId, gradeName: grade?.label, clazzName: clazz?.name, courseName: course?.label });
  teachingDraft.clazzId = undefined;
  teachingDraft.courseId = undefined;
}

function removeTeachingRow(index: number) {
  teachingRows.value.splice(index, 1);
}

const rules = reactive({
  name: [{ required: true, message: "请输入教师名称", trigger: "blur" }],
  phone: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

/** 查询 */
function handleQuery() {
  loading.value = true;
  getTeacherPage(queryParams)
    .then(({ data }) => {
      teacherList.value = data.list;
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
  handleQuery();
}

/** 行checkbox 选中事件 */
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
}

/** 打开教师表单弹窗 */
function openDialog(type: string, teacherId?: number) {
  dialog.visible = true;
  dialog.type = type;
  if (dialog.type === "teacher-form") {
    dialog.width = 980;
    loadTeachingOptions(teacherId);
    if (teacherId) {
      dialog.title = "修改教师";
      getTeacherForm(teacherId).then(({ data }) => {
        Object.assign(formData, data);
      });
    } else {
      dialog.title = "新增教师";
      Object.assign(formData, { id: undefined, code: "", name: "", sex: 1, phone: undefined, status: 1 });
    }
  } else if (dialog.type === "teacher-import") {
    // 教师导入弹窗
    dialog.title = "导入教师";
    dialog.width = 600;
  }
}

/** 教师保存提交 */
function handleSubmit() {
  if (dialog.type === "teacher-form") {
    teacherFormRef.value.validate((valid: any) => {
      if (valid) {
        loading.value = true;
        const teacherId = formData.id;
        if (teacherId) {
          updateTeacher(teacherId, formData)
            .then(() => replaceTeacherArrangements(teacherId, teachingRows.value.map(({ clazzId, courseId, status, sort, remark }) => ({ clazzId, courseId, status, sort, remark, teacherId: teacherId }))))
            .then(() => {
              ElMessage.success("修改成功");
              closeDialog();
              resetQuery();
            })
            .finally(() => (loading.value = false));
        } else {
          addTeacher(formData)
            .then((response: any) => {
              const savedId = response.data?.id || formData.id;
              if (!savedId) return;
              return replaceTeacherArrangements(savedId, teachingRows.value.map(({ clazzId, courseId, status, sort, remark }) => ({ clazzId, courseId, status, sort, remark, teacherId: savedId })));
            })
            .then(() => {
              ElMessage.success("新增成功");
              closeDialog();
              resetQuery();
            })
            .finally(() => (loading.value = false));
        }
      }
    });
  } else if (dialog.type === "teacher-import") {
    if (!importData?.file) {
      ElMessage.warning("上传Excel文件不能为空");
      return false;
    }
    importTeacher(importData?.file).then((response) => {
      ElMessage.success(response.data);
      closeDialog();
      resetQuery();
    });
  }
}

/** 关闭表单弹窗 */
function closeDialog() {
  dialog.visible = false;
  if (dialog.type === "teacher-form") {
    resetForm();
  } else if (dialog.type === "teacher-import") {
    importData.file = undefined;
    importData.fileList = [];
  }
}

/** 重置表单 */
function resetForm() {
  teacherFormRef.value.resetFields();
  teacherFormRef.value.clearValidate();

  formData.id = undefined;
  formData.sort = 1;
  formData.status = 1;
  teachingRows.value = [];
  teachingDraft.gradeId = undefined;
  teachingDraft.clazzId = undefined;
  teachingDraft.courseId = undefined;
}

/** 删除教师 */
function handleDelete(teacherId?: number) {
  const teacherIds = [teacherId || ids.value].join(",");
  if (!teacherIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteTeachers(teacherIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

// 教师导入数据
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

/** 导出教师 */
function handleExport() {
  exportTeacher(queryParams).then((response: any) => {
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
            placeholder="教师姓名"
            clearable
            @keyup.enter="handleQuery"
          />
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
            <el-button type="success" @click="openDialog('teacher-form')"
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
                  <el-dropdown-item @click="openDialog('teacher-import')">
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
        :data="teacherList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="教师姓名" prop="name">
          <template #default="scope">
            <el-link type="primary" @click="openDialog('teacher-form', scope.row.id)">{{ scope.row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="所带班级" min-width="180">
          <template #default="scope">
            <span v-if="scope.row.clazzNames" class="teacher-class-list">{{ scope.row.clazzNames }}</span>
            <el-text v-else type="info">暂未配置</el-text>
          </template>
        </el-table-column>
        <el-table-column label="任教学科" min-width="140">
          <template #default="scope">
            <span v-if="scope.row.courseNames">{{ scope.row.courseNames }}</span>
            <el-text v-else type="info">—</el-text>
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

    <!-- 教师表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      :width="dialog.width"
      @close="closeDialog"
    >
      <el-form
        v-if="dialog.type === 'teacher-form'"
        ref="teacherFormRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="教师姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入教师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
          </el-col>
        </el-row>

        <el-form-item label="任教班级">
          <div class="teaching-picker">
            <el-select v-model="teachingDraft.gradeId" clearable placeholder="选择年级" class="teaching-picker__grade" @change="teachingDraft.clazzId = undefined">
              <el-option v-for="item in gradeOptions" :key="item.value" :label="item.label" :value="Number(item.value)" />
            </el-select>
            <el-select v-model="teachingDraft.clazzId" clearable placeholder="选择班级" class="teaching-picker__clazz" :disabled="!teachingDraft.gradeId">
              <el-option v-for="item in clazzOptionsForGrade" :key="item.id" :label="item.name" :value="Number(item.id)" />
            </el-select>
            <el-select v-model="teachingDraft.courseId" clearable placeholder="选择任教学科" class="teaching-picker__course">
              <el-option v-for="item in courseOptions" :key="item.value" :label="item.label" :value="Number(item.value)" />
            </el-select>
            <el-button type="primary" plain @click="addTeachingRow"><i-ep-plus />添加</el-button>
          </div>
          <div v-if="teachingRows.length" class="teaching-tags">
            <el-tag v-for="(item, index) in teachingRows" :key="`${item.clazzId}-${item.courseId}`" closable @close="removeTeachingRow(index)">
              {{ item.gradeName || "未分年级" }} · {{ item.clazzName || "未命名班级" }} · {{ item.courseName || "未命名学科" }}
            </el-tag>
          </div>
          <el-text v-else type="info" class="teaching-empty">请按“年级—班级—学科”添加任教关系</el-text>
        </el-form-item>
      </el-form>

      <!-- 教师导入表单 -->
      <el-form
        v-else-if="dialog.type === 'teacher-import'"
        :model="importData"
        label-width="100px"
      >
        <el-form-item label="Excel文件">
          <el-upload
            ref="uploadRef"
            action=""
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
            :limit="1"
            :auto-upload="false"
            :file-list="importData.fileList"
            :on-change="handleFileChange"
            :on-exceed="handleFileExceed"
          >
            <el-button type="primary"><i-ep-upload />选择 Excel 文件</el-button>
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
<style scoped lang="scss">
.teaching-picker {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;

  &__grade { width: 145px; }
  &__clazz { width: 165px; }
  &__course { width: 165px; }
}

.teaching-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.teaching-empty {
  display: block;
  margin-top: 8px;
}

.teacher-class-list {
  color: var(--el-color-primary);
  font-weight: 500;
}
</style>
