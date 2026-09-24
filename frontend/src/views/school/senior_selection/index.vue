<script setup lang="ts">
import { getGradePage } from "@/api/grade";
import { getCourseOptions } from "@/api/course";
import {
  addSelectionVersion,
  getSelectionRecords,
  getSelectionStudentOptions,
  getSelectionVersions,
  saveSelectionRecords,
} from "@/api/student-subject-selection";
import { StudentSubjectSelectionRecord, StudentSubjectSelectionVersion } from "@/api/student-subject-selection/types";
import { addScoreRule, deleteScoreRule, getScoreRules } from "@/api/score-rule";
import { ScoreRule } from "@/api/score-rule/types";

defineOptions({ name: "SeniorSelection", inheritAttrs: false });

const activeTab = ref("selection");
const gradeList = ref<any[]>([]);
const courseList = ref<OptionType[]>([]);
const versions = ref<StudentSubjectSelectionVersion[]>([]);
const selectedGradeId = ref<number>();
const selectedVersionId = ref<number>();
const records = ref<StudentSubjectSelectionRecord[]>([]);
const studentOptions = ref<OptionType[]>([]);
const rules = ref<ScoreRule[]>([]);
const loading = ref(false);
const versionDialog = ref(false);
const recordDialog = ref(false);
const versionForm = reactive<StudentSubjectSelectionVersion>({ gradeId: 0, name: "", effectiveDate: "", status: 1 });
const recordForm = reactive<StudentSubjectSelectionRecord>({ studentId: 0, combinationCode: "", electiveCourseIds: [] });

const highSchoolCourses = computed(() => courseList.value);

async function loadBaseOptions() {
  const [grades, courses] = await Promise.all([
    getGradePage({ pageNum: 1, pageSize: 100 }),
    getCourseOptions(),
  ]);
  gradeList.value = (grades.data?.list || []).filter((item: any) => item.stage === "高中");
  courseList.value = courses.data || [];
  if (!selectedGradeId.value && gradeList.value.length) selectedGradeId.value = gradeList.value[0].id;
  await loadVersions();
}

async function loadVersions() {
  versions.value = selectedGradeId.value ? (await getSelectionVersions(selectedGradeId.value)).data || [] : [];
  if (selectedVersionId.value && !versions.value.some((item) => item.id === selectedVersionId.value)) selectedVersionId.value = undefined;
  if (!selectedVersionId.value && versions.value.length) selectedVersionId.value = versions.value[0].id;
  await loadRecords();
}

async function loadRecords() {
  records.value = [];
  studentOptions.value = [];
  if (!selectedVersionId.value) return;
  const [recordResponse, studentResponse] = await Promise.all([
    getSelectionRecords(selectedVersionId.value),
    getSelectionStudentOptions(selectedVersionId.value),
  ]);
  records.value = recordResponse.data || [];
  studentOptions.value = studentResponse.data || [];
}

function openVersionDialog() {
  Object.assign(versionForm, { gradeId: selectedGradeId.value || 0, name: "", effectiveDate: "", status: 1, note: "" });
  versionDialog.value = true;
}

function saveVersion() {
  if (!versionForm.gradeId || !versionForm.name.trim()) return ElMessage.warning("请选择高中年级并填写版本名称");
  addSelectionVersion(versionForm).then(({ data }) => {
    ElMessage.success("选科版本已创建"); versionDialog.value = false; selectedGradeId.value = versionForm.gradeId; selectedVersionId.value = data.id; loadVersions();
  });
}

function openRecordDialog() {
  Object.assign(recordForm, { studentId: 0, trackCourseId: undefined, electiveCourseIds: [], combinationCode: "", note: "" });
  recordDialog.value = true;
}

function addRecord() {
  if (!recordForm.studentId || !recordForm.combinationCode.trim()) return ElMessage.warning("请选择学生并填写组合编码");
  const exists = records.value.find((item) => item.studentId === recordForm.studentId);
  if (exists) Object.assign(exists, JSON.parse(JSON.stringify(recordForm)));
  else records.value.push(JSON.parse(JSON.stringify(recordForm)));
  recordDialog.value = false;
}

function removeRecord(row: any) {
  records.value = records.value.filter((item) => item.studentId !== row.studentId);
}

function saveRecords() {
  if (!selectedVersionId.value) return ElMessage.warning("请先创建或选择选科版本");
  loading.value = true;
  saveSelectionRecords(selectedVersionId.value, records.value).then(() => ElMessage.success("选科名单已保存")).finally(() => (loading.value = false));
}

function addRule() {
  const form: ScoreRule = { name: "新赋分规则", method: "PENDING", targetFullScore: 100, remark: "具体政策规则待确认" };
  addScoreRule(form).then(() => { ElMessage.success("赋分规则模块已创建"); loadRules(); });
}

function removeRule(row: any) {
  if (!row.id) return;
  deleteScoreRule(row.id).then(() => { ElMessage.success("规则已删除"); loadRules(); });
}

function loadRules() { getScoreRules().then(({ data }) => (rules.value = data || [])); }

watch(selectedGradeId, () => { selectedVersionId.value = undefined; loadVersions(); });
watch(selectedVersionId, loadRecords);
onMounted(() => { loadBaseOptions(); loadRules(); });
</script>

<template>
  <div class="app-container">
    <el-alert title="高中选科采用 3+1+2 固定班级模式；当前先保存选科历史版本和赋分规则配置，具体赋分政策确认后再接入计算。" type="info" :closable="false" show-icon class="mb-4" />
    <el-tabs v-model="activeTab">
      <el-tab-pane label="选科组合" name="selection">
        <div class="search-container">
          <el-form inline>
            <el-form-item label="高中年级">
              <el-select v-model="selectedGradeId" class="!w-[220px]" placeholder="请选择高中年级">
                <el-option v-for="grade in gradeList" :key="grade.id" :label="grade.name" :value="grade.id!" />
              </el-select>
            </el-form-item>
            <el-form-item label="选科版本">
              <el-select v-model="selectedVersionId" class="!w-[260px]" placeholder="请选择版本" clearable>
                <el-option v-for="version in versions" :key="version.id" :label="version.name" :value="version.id!" />
              </el-select>
            </el-form-item>
            <el-button type="primary" @click="openVersionDialog">新建版本</el-button>
            <el-button type="success" :disabled="!selectedVersionId" @click="openRecordDialog">添加学生组合</el-button>
            <el-button :loading="loading" :disabled="!selectedVersionId" @click="saveRecords">保存名单</el-button>
          </el-form>
        </div>
        <el-card shadow="never">
          <template #header><span>学生选科组合（历史版本不会被新版本覆盖）</span></template>
          <el-table :data="records" border stripe>
            <el-table-column prop="studentCode" label="学号" width="150" />
            <el-table-column prop="studentName" label="姓名" width="120" />
            <el-table-column prop="combinationCode" label="组合编码" width="160" />
            <el-table-column prop="trackCourseName" label="首选科目" width="120" />
            <el-table-column label="再选科目" min-width="220">
              <template #default="scope">{{ (scope.row.electiveCourseNames || []).join(" + ") || "-" }}</template>
            </el-table-column>
            <el-table-column prop="source" label="来源" width="100" />
            <el-table-column label="操作" width="90"><template #default="scope"><el-button link type="danger" @click="removeRecord(scope.row)">移除</el-button></template></el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="赋分规则模块" name="rules">
        <el-card shadow="never">
          <template #header><div class="flex items-center justify-between"><span>赋分规则配置（算法待确认）</span><el-button type="primary" @click="addRule">新增占位规则</el-button></div></template>
          <el-table :data="rules" border stripe>
            <el-table-column prop="name" label="规则名称" min-width="180" />
            <el-table-column prop="method" label="计算方式" width="140"><template #default="scope">{{ scope.row.method === "PENDING" ? "待定" : scope.row.method }}</template></el-table-column>
            <el-table-column prop="targetFullScore" label="目标满分" width="110" />
            <el-table-column prop="remark" label="说明" min-width="260" />
            <el-table-column label="操作" width="90"><template #default="scope"><el-button link type="danger" @click="removeRule(scope.row)">删除</el-button></template></el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="versionDialog" title="新建高中选科版本" width="520px">
      <el-form label-width="100px">
        <el-form-item label="高中年级"><el-select v-model="versionForm.gradeId" class="w-full"><el-option v-for="grade in gradeList" :key="grade.id" :label="grade.name" :value="grade.id!" /></el-select></el-form-item>
        <el-form-item label="版本名称"><el-input v-model="versionForm.name" placeholder="如：2026级高一入学选科" /></el-form-item>
        <el-form-item label="生效日期"><el-date-picker v-model="versionForm.effectiveDate" value-format="YYYY-MM-DD" type="date" class="w-full" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="versionForm.note" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="saveVersion">保存</el-button><el-button @click="versionDialog = false">取消</el-button></template>
    </el-dialog>

    <el-dialog v-model="recordDialog" title="添加学生选科组合" width="620px">
      <el-form label-width="100px">
        <el-form-item label="学生"><el-select v-model="recordForm.studentId" filterable class="w-full" placeholder="请选择学生"><el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="组合编码"><el-input v-model="recordForm.combinationCode" placeholder="如：物理-化学-生物" /></el-form-item>
        <el-form-item label="首选科目"><el-select v-model="recordForm.trackCourseId" clearable class="w-full"><el-option v-for="item in highSchoolCourses" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="再选科目"><el-select v-model="recordForm.electiveCourseIds" multiple :multiple-limit="2" clearable class="w-full"><el-option v-for="item in highSchoolCourses" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="recordForm.note" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="addRecord">加入名单</el-button><el-button @click="recordDialog = false">取消</el-button></template>
    </el-dialog>
  </div>
</template>
