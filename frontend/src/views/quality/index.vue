<script setup lang="ts">
import { getQualityConfig, getStudentQuality, getStudentQualitySummary, saveStudentQualitySemester } from "@/api/quality";
const config = ref<any>({ dimensions: [], semesters: [] });
const studentId = ref<number>(); const semester = ref(""); const ratings = reactive<Record<string, string>>({});
const records = ref<any[]>([]); const summary = ref<any>({ dimensionTotals: {} });
onMounted(() => getQualityConfig().then(({ data }) => { config.value = data; semester.value = data.semesters[0]; data.dimensions.forEach((d: string) => (ratings[d] = "N/A")); }));
function load() { if (!studentId.value) return; getStudentQuality(studentId.value).then(({ data }) => (records.value = data)); getStudentQualitySummary(studentId.value).then(({ data }) => (summary.value = data)); }
function save() { if (studentId.value && semester.value) saveStudentQualitySemester(studentId.value, semester.value, ratings).then(() => { ElMessage.success("评价已保存"); load(); }); }
</script>
<template>
  <div class="app-container"><el-card shadow="never"><template #header>初中综合素质评价</template>
    <el-form inline><el-form-item label="学生ID"><el-input-number v-model="studentId" :min="1" /></el-form-item><el-form-item><el-button @click="load">查询</el-button></el-form-item></el-form>
    <el-divider />
    <el-form inline v-if="config.semesters.length"><el-form-item label="学期"><el-select v-model="semester"><el-option v-for="s in config.semesters" :key="s" :label="s" :value="s" /></el-select></el-form-item><el-form-item v-for="d in config.dimensions" :key="d" :label="d"><el-select v-model="ratings[d]"><el-option label="A" value="A" /><el-option label="B" value="B" /><el-option label="C" value="C" /><el-option label="N/A" value="N/A" /></el-select></el-form-item><el-button type="primary" @click="save">保存本学期评价</el-button></el-form>
    <el-divider /><el-descriptions title="五维累计分" :column="5" border><el-descriptions-item v-for="d in config.dimensions" :key="d" :label="d">{{ summary.dimensionTotals?.[d] ?? 0 }}</el-descriptions-item></el-descriptions>
    <el-table :data="records" class="mt-3"><el-table-column prop="semester" label="学期" /><el-table-column prop="dimension" label="维度" /><el-table-column prop="level" label="等级" /><el-table-column prop="comment" label="备注" /></el-table>
  </el-card></div>
</template>
