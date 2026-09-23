<script setup lang="ts">
import { CourseClazzStaticsBO } from "@/api/analysis/types";
import { TableColumnCtx } from "element-plus";

defineOptions({
  name: "ClazzStaticsTable",
  inheritAttrs: false,
});
const props = defineProps({
  loading: {
    type: Boolean,
    required: true,
  },
  courseClazzStaticsList: {
    type: Array<CourseClazzStaticsBO>,
    default: () => [],
  },
});

interface SpanMethodProps {
  row: any;
  column: TableColumnCtx<any>;
  rowIndex: number;
  columnIndex: number;
}
const spanMethod = ({
  row,
  column,
  rowIndex,
  columnIndex,
}: SpanMethodProps) => {
  //定义需要合并的列字段，有哪些列需要合并，就自定义添加字段即可
  const fields = ["courseName", "fullScore"];
  // 当前行的数据
  const cellValue = row[column.property];
  // 判断只合并定义字段的列数据
  if (cellValue && fields.includes(column.property)) {
    const prevRow = props.courseClazzStaticsList[rowIndex - 1]; //上一行数据
    let nextRow = props.courseClazzStaticsList[rowIndex + 1]; //下一行数据
    // 当上一行的数据等于当前行数据时，当前行单元格隐藏
    if (prevRow && prevRow[column.property] === cellValue) {
      return { rowspan: 0, colspan: 0 };
    } else {
      // 反之，则循环判断若下一行数据等于当前行数据，则当前行开始进行合并单元格
      let countRowspan = 1; //用于合并计数多少单元格
      while (nextRow && nextRow[column.property] === cellValue) {
        nextRow = props.courseClazzStaticsList[++countRowspan + rowIndex];
      }
      if (countRowspan > 1) {
        return { rowspan: countRowspan, colspan: 1 };
      }
    }
  }
};
</script>
<template>
  <el-table
    ref="dataTableRef"
    v-loading="loading"
    :data="courseClazzStaticsList"
    :span-method="spanMethod"
    highlight-current-row
    border
  >
    <el-table-column
      label="课程名称"
      prop="courseName"
      width="100"
      align="center"
    />
    <el-table-column
      label="课程满分"
      prop="fullScore"
      width="100"
      align="center"
    />
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
