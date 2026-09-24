<script setup lang="ts">
defineOptions({
  name: "Exam",
  inheritAttrs: false,
});
import {
  getExamPage,
  getExamForm,
  addExam,
  updateExam,
  deleteExams,
  getExamOptions,
  getExamGradeClazzIds,
  updateExamGradeClazzs,
} from "@/api/exam";

import { ExamQuery, ExamPageVO, ExamForm } from "@/api/exam/types";

const queryFormRef = ref(ElForm);
const examFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ExamQuery>({
  pageNum: 1,
  pageSize: 10,
});

const examList = ref<ExamPageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<ExamForm>({
  sort: 1,
  status: 1,
  code: "",
  name: "",
  examType: "考试",
});

const rules = reactive({
  name: [{ required: true, message: "请输入考试名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入考试编码", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
  year: [{ required: true, message: "请选择年度", trigger: "blur" }],
  examType: [{ required: true, message: "请选择考试类型", trigger: "blur" }],
  examDate: [{ required: true, message: "请选择考试日期", trigger: "blur" }],
  semester: [{ required: true, message: "请选择学期", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  loading.value = true;
  getExamPage(queryParams)
    .then(({ data }) => {
      examList.value = data.list;
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

/** 打开考试表单弹窗 */
function openDialog(examId?: number) {
  dialog.visible = true;
  if (examId) {
    dialog.title = "修改考试";
    getExamForm(examId).then(({ data }) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增考试";
    formData.examType = "考试";
  }
}

/** 考试保存提交 */
function handleSubmit() {
  examFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const examId = formData.id;
      if (examId) {
        updateExam(examId, formData)
          .then(() => {
            ElMessage.success("修改成功");
            closeDialog();
            resetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        addExam(formData)
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
  examFormRef.value.resetFields();
  examFormRef.value.clearValidate();

  formData.id = undefined;
  formData.sort = 1;
  formData.year = undefined;
  formData.status = 1;
}

/** 删除考试 */
function handleDelete(examId?: number) {
  const examIds = [examId || ids.value].join(",");
  if (!examIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteExams(examIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
  });
}
const menuRef = ref(ElTree);
const menuDialogVisible = ref(false);
const menuList = ref<OptionType[]>([]);
interface CheckedExam {
  id?: number;
  name?: string;
}
let checkedExam: CheckedExam = reactive({});

/** 打开考试设置对象弹窗 */
function openGradeClazzDialog(row: ExamPageVO) {
  const examId = row.id;
  if (examId) {
    checkedExam = {
      id: examId,
      name: row.name,
    };
    menuDialogVisible.value = true;
    loading.value = true;

    getExamOptions().then((response) => {
      menuList.value = response.data;
      // 回显考试已拥有的年级或者班级
      getExamGradeClazzIds(examId)
        .then(({ data }) => {
          const checkedMenuIds = data;
          checkedMenuIds.forEach((menuId) =>
            menuRef.value.setChecked(menuId, true, false)
          );
        })
        .finally(() => {
          loading.value = false;
        });
    });
  }
}

/** 考试分配年级和班级保存提交 */
function handleExamGradeClazzSubmit() {
  const examId = checkedExam.id;
  if (examId) {
    const checkedMenuIds: string[] = menuRef.value
      .getCheckedNodes(false, true)
      .map((node: any) => node.value);

    loading.value = true;
    updateExamGradeClazzs(examId, checkedMenuIds)
      .then(() => {
        ElMessage.success("考试对象设置成功");
        menuDialogVisible.value = false;
        resetQuery();
      })
      .finally(() => {
        loading.value = false;
      });
  }
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
        :data="examList"
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
        <el-table-column label="考试编码" prop="code" width="150" />
        <el-table-column label="考试名称" prop="name" />

        <el-table-column label="考试对象" width="150" align="center">
          <template #default="scope">
            <el-tooltip
              v-if="scope.row.gradeClazzCount"
              class="box-item"
              effect="customized"
              :content="scope.row.gradeClazzList"
              raw-content
              placement="top"
            >
              <el-tag effect="light">{{ scope.row.gradeClazzCount }}</el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          label="样本数"
          align="center"
          width="80"
          prop="sampleCount"
        >
          <template #default="scope">
            <el-tag effect="dark" v-if="scope.row.sampleCount">
              {{ scope.row.sampleCount }}</el-tag
            >
          </template>
        </el-table-column>

        <el-table-column
          label="考试日期"
          prop="examDate"
          width="180"
          align="center"
        />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" width="80" prop="sort" />

        <el-table-column fixed="right" label="操作" width="250">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="openGradeClazzDialog(scope.row)"
            >
              <i-ep-setting />考试设置
            </el-button>
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

    <!-- 体检安排表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="closeDialog"
    >
      <el-form
        ref="examFormRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="年度" prop="year">
          <el-date-picker
            v-model="formData.year"
            type="year"
            format="YYYY"
            value-format="YYYY"
            placeholder="请选择考试年度"
          />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <dictionary v-model="formData.semester" type-code="semester" />
        </el-form-item>
        <el-form-item label="考试名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入考试名称" />
        </el-form-item>

        <el-form-item label="考试编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入考试编码" />
        </el-form-item>

        <el-form-item label="考试类型" prop="examType">
          <el-input v-model="formData.examType" readonly />
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker
            v-model="formData.examDate"
            type="datetime"
            format="YYYY-MM-DD hh:mm:ss"
            value-format="YYYY-MM-DD hh:mm:ss"
            placeholder="请选择考试日期"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            controls-arrange="right"
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

    <!-- 考试设置弹窗  -->
    <el-dialog
      v-model="menuDialogVisible"
      :title="'【' + checkedExam.name + '】考试对象设置'"
      width="800px"
    >
      <el-scrollbar v-loading="loading" max-height="600px">
        <el-tree
          ref="menuRef"
          node-key="value"
          show-checkbox
          :data="menuList"
          :default-expand-all="true"
        >
          <template #default="{ data }">
            {{ data.label }}
          </template>
        </el-tree>
      </el-scrollbar>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleExamGradeClazzSubmit"
            >确 定</el-button
          >
          <el-button @click="menuDialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<style>
.el-popper.is-customized {
  /* Set padding to ensure the height is 32px */
  padding: 6px 12px;
  background: linear-gradient(90deg, rgb(159 229 151), rgb(204 229 129));
}

.el-popper.is-customized .el-popper__arrow::before {
  right: 0;
  background: linear-gradient(45deg, #b2e68d, #bce689);
}
</style>
