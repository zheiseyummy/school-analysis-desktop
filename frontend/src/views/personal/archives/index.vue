<script setup lang="ts">
defineOptions({
  name: "Archives",
  inheritAttrs: false,
});
import {
  getArchivesPage,
  getArchivesForm,
  addArchives,
  updateArchives,
  deleteArchivess,
} from "@/api/archives";

import {
  ArchivesQuery,
  ArchivesPageVO,
  ArchivesForm,
} from "@/api/archives/types";
import { getComplexClazzOptions } from "@/api/clazz";
import { getStudentOptions } from "@/api/student";

const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源
const studentList = ref<OptionType[]>(); //学生下拉数据源
const studentQueryList = ref<OptionType[]>(); //学生下拉数据源
/** 加载携带年级信息的班级下拉数据源 */
async function loadComplexClazzOptions() {
  getComplexClazzOptions().then((response) => {
    complexClazzList.value = response.data;
  });
}

/** 加载学生下拉数据源 */
async function loadStudentOptions() {
  if (formData.clazzId && formData.year) {
    getStudentOptions(formData.clazzId!, formData.year).then((response) => {
      studentList.value = response.data;
    });
  }
}
function conditionChange() {
  formData.studentId = undefined;
  loadStudentOptions();
}

function conditionQueryChange() {
  queryParams.studentId = undefined;
  if (queryParams.clazzId && queryParams.year) {
    getStudentOptions(queryParams.clazzId!, queryParams.year).then(
      (response) => {
        studentQueryList.value = response.data;
      }
    );
  }
}

const queryFormRef = ref(ElForm);
const archivesFormRef = ref(ElForm);

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ArchivesQuery>({
  pageNum: 1,
  pageSize: 10,
});

const archivesList = ref<ArchivesPageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const viewDialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<ArchivesForm>({
  sort: 1,
  status: 1,
  code: "",
  name: "",
});

const viewData = reactive<ArchivesForm>({
  sort: 1,
  status: 1,
  code: "",
  name: "",
});

const rules = reactive({
  name: [{ required: true, message: "请输入学情档案名称", trigger: "blur" }],
  code: [{ required: true, message: "请输入学情档案编码", trigger: "blur" }],
  year: [{ required: true, message: "请选择年度", trigger: "blur" }],
  clazzId: [{ required: true, message: "请选择班级", trigger: "blur" }],
  studentId: [{ required: true, message: "请选择学生", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  loading.value = true;
  getArchivesPage(queryParams)
    .then(({ data }) => {
      archivesList.value = data.list;
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

/** 打开学情档案表单弹窗 */
function openDialog(archivesId?: number) {
  dialog.visible = true;
  if (archivesId) {
    dialog.title = "修改学情档案";
    getArchivesForm(archivesId).then(({ data }) => {
      formData.year = data.year;
      formData.clazzId = data.clazzId;
      loadStudentOptions();
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "新增学情档案";
  }
}

function openViewDialog(archivesId?: number) {
  viewDialog.visible = true;
  viewDialog.title = "查看学情档案";
  getArchivesForm(archivesId!).then(({ data }) => {
    Object.assign(viewData, data);
  });
}

/** 学情档案保存提交 */
function handleSubmit() {
  archivesFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const archivesId = formData.id;
      if (archivesId) {
        updateArchives(archivesId, formData)
          .then(() => {
            ElMessage.success("修改成功");
            closeDialog();
            resetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        addArchives(formData)
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

function closeViewDialog() {
  viewDialog.visible = false;
}

/** 重置表单 */
function resetForm() {
  archivesFormRef.value.resetFields();
  archivesFormRef.value.clearValidate();

  formData.id = undefined;
  formData.sort = 1;
  formData.status = 1;
}

/** 删除学情档案 */
function handleDelete(archivesId?: number) {
  const archivesIds = [archivesId || ids.value].join(",");
  if (!archivesIds) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除已选中的数据项?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    deleteArchivess(archivesIds)
      .then(() => {
        ElMessage.success("删除成功");
        resetQuery();
      })
      .finally(() => (loading.value = false));
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
            placeholder="学情档案编号/名称"
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
            placeholder="请选择年度"
            @change="conditionQueryChange"
          />
        </el-form-item>
        <el-form-item label="班级" prop="clazzId">
          <el-select
            v-model="queryParams.clazzId"
            clearable
            placeholder="全部"
            class="!w-[200px]"
            @change="conditionQueryChange"
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

        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="queryParams.studentId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
          >
            <el-option
              v-for="item in studentQueryList"
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
        <el-button type="success" @click="openDialog()"
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
        :data="archivesList"
        highlight-current-row
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="学情档案编码" prop="code" width="150" />
        <el-table-column label="学情档案名称" prop="name" />
        <el-table-column label="年度" prop="year" width="80" align="center" />
        <el-table-column label="年级" prop="gradeName" width="90" />
        <el-table-column label="班级" prop="clazzName" width="90" />
        <el-table-column label="学生" prop="studentName" width="120" />
        <el-table-column label="关联教师" prop="teacherName" width="120" />
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
            <el-button
              type="primary"
              size="small"
              link
              @click="openViewDialog(scope.row.id)"
            >
              <i-ep-view />查看
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

    <!-- 学情档案表单弹窗 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="800px"
      @close="closeDialog"
    >
      <el-form
        ref="archivesFormRef"
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
            placeholder="请选择年度"
            @change="conditionChange"
          />
        </el-form-item>
        <el-form-item label="班级" prop="clazzId">
          <el-select
            v-model="formData.clazzId"
            clearable
            placeholder="请选择班级"
            @change="conditionChange"
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
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="formData.studentId"
            clearable
            placeholder="请选择学生"
          >
            <el-option
              v-for="item in studentList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="学情档案名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入学情档案名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学情档案编码" prop="code">
              <el-input
                v-model="formData.code"
                placeholder="请输入学情档案编码"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="formData.sort"
                controls-archives="right"
                :min="0"
                style="width: 100px"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="学习心向" prop="learningSet">
          <el-form-item label="学习需求" prop="learningNeeds">
            <el-input
              type="textarea"
              v-model="formData.learningNeeds"
              placeholder="请输入学习需求"
            />
          </el-form-item>
          <el-form-item label="学习动机" prop="learningMotivation">
            <el-input
              type="textarea"
              v-model="formData.learningMotivation"
              placeholder="请输入学习动机"
            />
          </el-form-item>

          <el-form-item label="学习兴趣" prop="learningInterests">
            <el-input
              type="textarea"
              v-model="formData.learningInterests"
              placeholder="请输入学习兴趣"
            />
          </el-form-item>
          <el-form-item label="学习态度" prop="learningAttitude">
            <el-input
              type="textarea"
              v-model="formData.learningAttitude"
              placeholder="请输入学习态度"
            />
          </el-form-item>

          <el-form-item
            style="width: 100%"
            label="学习自信心"
            prop="learningSelfConfidence"
          >
            <el-input
              type="textarea"
              v-model="formData.learningSelfConfidence"
              placeholder="请输入学习自信心"
            />
          </el-form-item>
        </el-form-item>

        <el-form-item label="学习习惯" prop="learningHabits">
          <el-form-item label="倾听" prop="listening">
            <el-input
              type="textarea"
              v-model="formData.listening"
              placeholder="请输入倾听"
            />
          </el-form-item>
          <el-form-item label="质疑" prop="query">
            <el-input
              type="textarea"
              v-model="formData.query"
              placeholder="请输入质疑"
            />
          </el-form-item>
          <el-form-item label="独立思考" prop="independentThinking">
            <el-input
              type="textarea"
              v-model="formData.independentThinking"
              placeholder="请输入独立思考"
            />
          </el-form-item>
          <el-form-item label="小组合作意识" prop="groupCooperationAwareness">
            <el-input
              type="textarea"
              v-model="formData.groupCooperationAwareness"
              placeholder="请输入小组合作意识"
            />
          </el-form-item>
          <el-form-item
            label="表达交流意识"
            prop="expressingWillingnessToCommunicate"
          >
            <el-input
              type="textarea"
              v-model="formData.expressingWillingnessToCommunicate"
              placeholder="请输入表达交流意识"
            />
          </el-form-item>
          <el-form-item label="记录意识" prop="recordAwareness">
            <el-input
              type="textarea"
              v-model="formData.recordAwareness"
              placeholder="请输入记录意识"
            />
          </el-form-item>
          <el-form-item label="自我反思意识" prop="selfReflectionConsciousness">
            <el-input
              type="textarea"
              v-model="formData.selfReflectionConsciousness"
              placeholder="请输入自我反思意识"
            />
          </el-form-item>
          <el-form-item label="复习整理意识" prop="reviewOrganizeAwareness">
            <el-input
              type="textarea"
              v-model="formData.reviewOrganizeAwareness"
              placeholder="请输入复习整理意识"
            />
          </el-form-item>
          <el-form-item label="预习意识" prop="previewAwareness">
            <el-input
              type="textarea"
              v-model="formData.previewAwareness"
              placeholder="请输入预习意识"
            />
          </el-form-item>
        </el-form-item>
        <el-form-item label="学科思维" prop="disciplinaryThinking">
          <el-input
            type="textarea"
            v-model="formData.disciplinaryThinking"
            placeholder="请输入学科思维"
          />
        </el-form-item>
        <el-form-item label="学科语言表达" prop="subjectLanguageExpression">
          <el-input
            type="textarea"
            v-model="formData.subjectLanguageExpression"
            placeholder="请输入学科语言表达"
          />
        </el-form-item>
        <el-form-item label="课外学习" prop="outOfClassActivities">
          <el-input
            type="textarea"
            v-model="formData.outOfClassActivities"
            placeholder="请输入课外学习"
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

    <!-- 学情档案查看弹窗 -->
    <el-dialog
      v-model="viewDialog.visible"
      :title="viewDialog.title"
      width="800px"
      @close="closeViewDialog"
    >
      <el-card>
        <el-descriptions
          title="学生个人信息"
          direction="vertical"
          :column="4"
          border
        >
          <el-descriptions-item label="档案编号">{{
            viewData.code
          }}</el-descriptions-item>
          <el-descriptions-item label="档案名称">{{
            viewData.name
          }}</el-descriptions-item>
          <el-descriptions-item label="关联教师" :span="2">
            {{ viewData.teacherName || "未关联" }}
          </el-descriptions-item>
          <el-descriptions-item label="年度">{{
            viewData.year
          }}</el-descriptions-item>
          <el-descriptions-item label="年级">{{
            viewData.gradeName
          }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{
            viewData.clazzName
          }}</el-descriptions-item>
          <el-descriptions-item label="学生">
            <el-tag size="small">{{ viewData.studentName }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <br />
        <el-descriptions
          title="学习心向"
          direction="vertical"
          :column="2"
          border
        >
          <el-descriptions-item label="学习需求">{{
            viewData.learningNeeds
          }}</el-descriptions-item>
          <el-descriptions-item label="学习动机">{{
            viewData.learningMotivation
          }}</el-descriptions-item>
          <el-descriptions-item label="学习兴趣">{{
            viewData.learningInterests
          }}</el-descriptions-item>
          <el-descriptions-item label="学习态度">{{
            viewData.learningAttitude
          }}</el-descriptions-item>
          <el-descriptions-item label="学习自信心">{{
            viewData.learningSelfConfidence
          }}</el-descriptions-item>
        </el-descriptions>
        <br />
        <el-descriptions
          title="学习习惯"
          direction="vertical"
          :column="3"
          border
        >
          <el-descriptions-item label="倾听">{{
            viewData.listening
          }}</el-descriptions-item>
          <el-descriptions-item label="质疑">{{
            viewData.query
          }}</el-descriptions-item>
          <el-descriptions-item label="独立思考">{{
            viewData.independentThinking
          }}</el-descriptions-item>
          <el-descriptions-item label="小组合作意识">{{
            viewData.groupCooperationAwareness
          }}</el-descriptions-item>
          <el-descriptions-item label="表达交流意愿">{{
            viewData.expressingWillingnessToCommunicate
          }}</el-descriptions-item>
          <el-descriptions-item label="记录意识">{{
            viewData.recordAwareness
          }}</el-descriptions-item>
          <el-descriptions-item label="自我反思意识">{{
            viewData.selfReflectionConsciousness
          }}</el-descriptions-item>
          <el-descriptions-item label="复习整理意识">{{
            viewData.reviewOrganizeAwareness
          }}</el-descriptions-item>
          <el-descriptions-item label="预习意识">{{
            viewData.previewAwareness
          }}</el-descriptions-item>
        </el-descriptions>
        <br />
        <el-descriptions
          title="学科思维"
          direction="vertical"
          :column="1"
          border
        >
          <el-descriptions-item label="学科思维">{{
            viewData.disciplinaryThinking
          }}</el-descriptions-item>
        </el-descriptions>
        <br />
        <el-descriptions
          title="学科语言表达"
          direction="vertical"
          :column="1"
          border
        >
          <el-descriptions-item label="学科语言表达">{{
            viewData.subjectLanguageExpression
          }}</el-descriptions-item>
        </el-descriptions>
        <br />
        <el-descriptions
          title="课外学习"
          direction="vertical"
          :column="1"
          border
        >
          <el-descriptions-item label="课外学习">{{
            viewData.outOfClassActivities
          }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-dialog>
  </div>
</template>
