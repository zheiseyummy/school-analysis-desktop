<script setup lang="ts">
import { ClazzStaticsBO } from "@/api/analysis/types";

defineOptions({
  name: "ClazzStaticsTable",
  inheritAttrs: false,
});
const props = defineProps({
  loading: {
    type: Boolean,
    required: true,
  },
  clazzStaticsList: {
    type: Array<ClazzStaticsBO>,
    default: () => [],
  },
});
</script>
<template>
  <el-table
    ref="dataTableRef"
    v-loading="loading"
    :data="clazzStaticsList"
    highlight-current-row
    border
  >
    <el-table-column
      label="班级名称"
      prop="clazzName"
      width="100"
      align="center"
    />

    <el-table-column
      label="班级人数"
      prop="clazzStudentCount"
      width="100"
      align="center"
    />

    <el-table-column
      label="最高分"
      prop="maxScore"
      width="100"
      align="center"
    />

    <el-table-column
      label="最低分"
      prop="minScore"
      width="100"
      align="center"
    />

    <el-table-column
      label="平均分"
      prop="avgScore"
      width="100"
      align="center"
    />

    <el-table-column label="优秀" prop="aCount" align="center" />
    <el-table-column label="良好" prop="bCount" align="center" />
    <el-table-column label="中等" prop="cCount" align="center" />
    <el-table-column label="合格" prop="dCount" align="center" />
    <el-table-column label="不合格" prop="eCount" align="center" />
    <el-table-column label="优秀率" prop="aRate" align="center">
      <template #default="scope">
        {{ parseFloat((scope.row.aRate * 100).toFixed(10)) }}%
      </template>
    </el-table-column>
    <el-table-column label="良好率" prop="bRate" align="center">
      <template #default="scope">
        {{ parseFloat((scope.row.bRate * 100).toFixed(10)) }}%
      </template>
    </el-table-column>
    <el-table-column label="中等率" prop="cRate" align="center">
      <template #default="scope">
        {{ parseFloat((scope.row.cRate * 100).toFixed(10)) }}%
      </template>
    </el-table-column>
    <el-table-column label="合格率" prop="dRate" align="center">
      <template #default="scope">
        {{ parseFloat((scope.row.dRate * 100).toFixed(10)) }}%
      </template>
    </el-table-column>
    <el-table-column label="不合格率" prop="eRate" align="center">
      <template #default="scope">
        {{ parseFloat((scope.row.eRate * 100).toFixed(10)) }}%
      </template>
    </el-table-column>
  </el-table>
</template>
