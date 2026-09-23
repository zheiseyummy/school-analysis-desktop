<script setup lang="ts">
defineOptions({
  name: "Grade",
  inheritAttrs: false,
});
import {
  getGradePage,
  getGradeForm,
  addGrade,
  updateGrade,
  deleteGrades,
} from "@/api/grade";

import { GradeQuery, GradePageVO, GradeForm } from "@/api/grade/types";
import { getTeacherOptions } from "@/api/teacher";
const teacherList = ref<OptionType[]>(); //教师下拉数据源

/** 加载教师下拉数据源 */
async function loadTeacherOptions() {
  getTeacherOptions().then((response) => {
    teacherList.value = response.data;
  });
}

const queryFormRef = ref(ElForm);
const gradeFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<GradeQuery>({
  pageNum: 1,
  pageSize: 10,
});

const gradeList = ref<GradePageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<GradeForm>({
  sort: 1,
  status: 1,
  name: "",
  code: "",
});

const rules = reactive({
  name: [{ required: true, message: "请输入年级名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入年级编号", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  loading.value = true;
  getGradePage(queryParams)
    .then(({ data }) => {
      gradeList.value = data.list;
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

/** 打开年级表单弹窗 */
function openDialog(gradeId?: number) {
  dialog.visible = true;
  if (gradeId) {
    dialog.title = "修改年级";
    getGradeForm(gradeId).then(({ data }) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增年级";
  }
}

/** 年级保存提交 */
function handleSubmit() {
  gradeFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const gradeId = formData.id;
      if (gradeId) {
        updateGrade(gradeId, formData)
          .then(() => {
            ElMessage.success("修改成功");
            closeDialog();
            resetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        addGrade(formData)
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
  gradeFormRef.value.resetFields();
  gradeFormRef.value.clearValidate();

  formData.id = undefined;
  formData.code = "";
  formData.name = "";
  formData.sort = 1;
  formData.status = 1;
}

/** 删除年级 */
function handleDelete(gradeId?: number) {
  const gradeIds = [gradeId || ids.value].join(",");
  if (!gradeIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteGrades(gradeIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

onMounted(() => {
  loadTeacherOptions();
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
            placeholder="年级编号/名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="年级主任" prop="managerId">
          <el-select
            v-model="queryParams.managerId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
          >
            <el-option
              v-for="item in teacherList"
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
        :data="gradeList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          label="年级编号"
          prop="code"
          width="120"
          align="center"
        />
        <el-table-column
          label="年级名称"
          prop="name"
          width="120"
          align="center"
        />
        <el-table-column
          label="年级主任"
          prop="managerName"
          width="120"
          align="center"
        />
        <el-table-column
          label="班级数量"
          prop="clazzCount"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag type="success" effect="dark" v-if="scope.row.clazzCount">
              {{ scope.row.clazzCount }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="班级列表" prop="clazzNameList" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="sort" width="80" />

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

    <!-- 年级表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="closeDialog"
    >
      <el-form
        ref="gradeFormRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="年级编号" prop="code">
          <el-input v-model="formData.code" placeholder="请输入年级编号" />
        </el-form-item>
        <el-form-item label="年级名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入年级名称" />
        </el-form-item>
        <el-form-item label="年级主任" prop="managerId">
          <el-select v-model="formData.managerId" placeholder="请选择年级主任">
            <el-option
              v-for="item in teacherList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
            controls-grade="right"
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
