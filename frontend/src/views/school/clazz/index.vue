<script setup lang="ts">
defineOptions({
  name: "Clazz",
  inheritAttrs: false,
});
import {
  getClazzPage,
  getClazzForm,
  addClazz,
  updateClazz,
  deleteClazzs,
} from "@/api/clazz";

import { ClazzQuery, ClazzPageVO, ClazzForm } from "@/api/clazz/types";
import { getTeacherOptions } from "@/api/teacher";
import { getGradeOptions } from "@/api/grade";
const teacherList = ref<OptionType[]>(); //教师下拉数据源
const gradeList = ref<OptionType[]>(); //年级下拉数据源

/** 加载教师下拉数据源 */
async function loadTeacherOptions() {
  getTeacherOptions().then((response) => {
    teacherList.value = response.data;
  });
}

/** 加载年级下拉数据源 */
async function loadGradeOptions() {
  getGradeOptions().then((response) => {
    gradeList.value = response.data;
  });
}

const queryFormRef = ref(ElForm);
const clazzFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ClazzQuery>({
  pageNum: 1,
  pageSize: 10,
});

const clazzList = ref<ClazzPageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<ClazzForm>({
  sort: 1,
  status: 1,
  name: "",
  code: "",
});

const rules = reactive({
  name: [{ required: true, message: "请输入班级名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入班级编号", trigger: "blur" }],
  gradeId: [{ required: true, message: "请选择所属年级", trigger: "blur" }],
  clazzType: [{ required: true, message: "请选择班级类型", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  loading.value = true;
  getClazzPage(queryParams)
    .then(({ data }) => {
      clazzList.value = data.list;
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

/** 打开班级表单弹窗 */
function openDialog(clazzId?: number) {
  dialog.visible = true;
  if (clazzId) {
    dialog.title = "修改班级";
    getClazzForm(clazzId).then(({ data }) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增班级";
  }
}

/** 班级保存提交 */
function handleSubmit() {
  clazzFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const clazzId = formData.id;
      if (clazzId) {
        updateClazz(clazzId, formData)
          .then(() => {
            ElMessage.success("修改成功");
            closeDialog();
            resetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        addClazz(formData)
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
  clazzFormRef.value.resetFields();
  clazzFormRef.value.clearValidate();

  formData.id = undefined;
  formData.code = "";
  formData.name = "";
  formData.sort = 1;
  formData.status = 1;
}

/** 删除班级 */
function handleDelete(clazzId?: number) {
  const clazzIds = [clazzId || ids.value].join(",");
  if (!clazzIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteClazzs(clazzIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

const arrangeDataDialog = reactive({
  title: "",
  visible: false,
});

const selectedClazz = reactive({
  clazzId: -1,
  clazzName: "",
}); // 当前选中的班级

/** 打开教学安排数据弹窗 */
function openArrangeDialog(row: ClazzPageVO) {
  arrangeDataDialog.visible = true;
  arrangeDataDialog.title =
    "【" +
    row.gradeName +
    "/" +
    row.name +
    "/" +
    row.clazzTypeLabel +
    "】教学安排数据";

  selectedClazz.clazzId = row.id!;
  selectedClazz.clazzName =
    row.gradeName + "/" + row.name! + "/" + row.clazzTypeLabel;
}

/**  关闭教学安排数据弹窗 */
function closeArrangeDialog() {
  arrangeDataDialog.visible = false;
}

onMounted(() => {
  loadTeacherOptions();
  loadGradeOptions();
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
            placeholder="班级编号/名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="所属年级" prop="gradeId">
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

        <el-form-item label="班级主任" prop="managerId">
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
        :data="clazzList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          label="班级类型"
          prop="clazzTypeLabel"
          width="100"
          align="center"
        />
        <el-table-column
          label="年级"
          prop="gradeName"
          width="80"
          align="center"
        />
        <el-table-column
          label="班级编号"
          prop="code"
          width="120"
          align="center"
        />
        <el-table-column
          label="班级名称"
          prop="name"
          width="150"
          align="center"
        />

        <el-table-column
          label="班级主任"
          prop="managerName"
          width="120"
          align="center"
        />
        <el-table-column
          label="教学数量"
          prop="arrangeCount"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag type="primary" effect="dark" v-if="scope.row.arrangeCount">
              {{ scope.row.arrangeCount }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="教学列表" prop="arrangeNameList" />
        <el-table-column
          label="学生数量"
          prop="studentCount"
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag type="success" effect="dark" v-if="scope.row.studentCount">
              {{ scope.row.studentCount }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="学生列表" prop="studentNameList" />
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
              link
              size="small"
              @click.stop="openArrangeDialog(scope.row)"
              ><i-ep-Collection />教学安排</el-button
            >
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

    <!-- 班级表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="closeDialog"
    >
      <el-form
        ref="clazzFormRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="所属年级" prop="gradeId">
          <el-select v-model="formData.gradeId" placeholder="请选择所属年级">
            <el-option
              v-for="item in gradeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级编号" prop="code">
          <el-input v-model="formData.code" placeholder="请输入班级编号" />
        </el-form-item>
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="班级类型" prop="clazzType">
          <dictionary v-model="formData.clazzType" type-code="clazzType" />
        </el-form-item>
        <el-form-item label="班级主任" prop="managerId">
          <el-select v-model="formData.managerId" placeholder="请选择班级主任">
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
            controls-clazz="right"
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

    <!--教学安排数据弹窗-->
    <el-dialog
      v-model="arrangeDataDialog.visible"
      :title="arrangeDataDialog.title"
      width="1000px"
      @close="closeArrangeDialog"
    >
      <arrange-item
        v-model:clazzId="selectedClazz.clazzId"
        v-model:clazzName="selectedClazz.clazzName"
      />
    </el-dialog>
  </div>
</template>
