<script setup lang="ts">
defineOptions({
  name: "Course",
  inheritAttrs: false,
});
import {
  getCoursePage,
  getCourseForm,
  addCourse,
  updateCourse,
  deleteCourses,
} from "@/api/course";

import { CourseQuery, CoursePageVO, CourseForm } from "@/api/course/types";

const queryFormRef = ref(ElForm);
const courseFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<CourseQuery>({
  pageNum: 1,
  pageSize: 10,
});

const courseList = ref<CoursePageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<CourseForm>({
  sort: 1,
  status: 1,
  code: "",
  name: "",
});

const rules = reactive({
  name: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入课程编码", trigger: "blur" }],
  subjectType: [{ required: true, message: "请选择课程类型", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
  fullScore: [{ required: true, message: "请输入课程满分", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  loading.value = true;
  getCoursePage(queryParams)
    .then(({ data }) => {
      courseList.value = data.list;
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

/** 打开课程表单弹窗 */
function openDialog(courseId?: number) {
  dialog.visible = true;
  if (courseId) {
    dialog.title = "修改课程";
    getCourseForm(courseId).then(({ data }) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增课程";
  }
}

/** 课程保存提交 */
function handleSubmit() {
  courseFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const courseId = formData.id;
      if (courseId) {
        updateCourse(courseId, formData)
          .then(() => {
            ElMessage.success("修改成功");
            closeDialog();
            resetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        addCourse(formData)
          .then(() => {
            ElMessage.success("新增成功");
            closeDialog();
            resetQuery();
          })
          .finally(() => (loading.value = false));
      }
    }
  });
}

/** 关闭表单弹窗 */
function closeDialog() {
  dialog.visible = false;
  resetForm();
}

/** 重置表单 */
function resetForm() {
  courseFormRef.value.resetFields();
  courseFormRef.value.clearValidate();

  formData.id = undefined;
  formData.sort = 1;
  formData.status = 1;
}

/** 删除课程 */
function handleDelete(courseId?: number) {
  const courseIds = [courseId || ids.value].join(",");
  if (!courseIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteCourses(courseIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
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
            placeholder="课程"
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
        <el-button
          type="success"
          @click="openDialog()"
          ><i-ep-plus />新增</el-button
        >
        <el-button
          type="danger"
          :disabled="ids.length === 0"
          @click="handleDelete()"
          ><i-ep-delete />删除</el-button
        >
      </template>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="courseList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          label="课程类型"
          prop="subjectTypeLabel"
          width="120"
          align="center"
        />
        <el-table-column
          label="课程名称"
          prop="name"
          width="180"
          align="center"
        />
        <el-table-column label="课程编码" prop="code" width="150" />
        <el-table-column label="课程满分" prop="fullScore" width="150" />
        <el-table-column
          label="人员数量"
          prop="userCount"
          width="120"
          align="center"
        />
        <el-table-column label="人员列表" prop="userList" min-width="300" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" width="80" prop="sort" />

        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="openDialog(scope.row.id)"
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

    <!-- 课程表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="closeDialog"
    >
      <el-form
        ref="courseFormRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入课程名称" />
        </el-form-item>

        <el-form-item label="课程编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入课程编码" />
        </el-form-item>
        <el-form-item label="课程类型" prop="subjectType">
          <dictionary v-model="formData.subjectType" type-code="subjectType" />
        </el-form-item>
        <el-form-item label="课程满分" prop="fullScore">
          <el-input-number
            v-model="formData.fullScore"
            placeholder="请输入课程满分"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            controls-course="right"
            :min="0"
            style="width: 100px"
          />
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
