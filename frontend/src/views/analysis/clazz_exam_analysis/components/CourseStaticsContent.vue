<script setup lang="ts">
import { ClazzExamAnalysisQuery } from "@/api/analysis/types";
import { getClazzExamAnalysisData } from "@/api/analysis";
import * as echarts from "echarts";
defineOptions({
  name: "CourseStaticsContent",
  inheritAttrs: false,
});

const queryParams = reactive<ClazzExamAnalysisQuery>({});

const props = defineProps({
  examId: {
    type: Number,
    required: false,
  },
  clazzId: {
    type: Number,
    required: false,
  },
  courseId: {
    type: Number,
    required: false,
  },
});

const columns = ref<any>();
const tableDataList = ref<any>();
const loading = ref<boolean>(false);
const histogramData = ref<any>();
const lineTitleArray = ref<any>();
const fullScore = ref<number>();

watch(
  [() => props.examId, () => props.clazzId, () => props.courseId],
  ([newExamId, newClazzId, newCourseId]) => {
    queryParams.examId = newExamId;
    queryParams.clazzId = newClazzId;
    queryParams.courseId = newCourseId;
    handleQuery();
  }
);

function handleQuery() {
  if (queryParams.examId && queryParams.clazzId && queryParams.courseId) {
    loading.value = true;
    getClazzExamAnalysisData(queryParams)
      .then(({ data }) => {
        columns.value = data.columns;
        tableDataList.value = data.tableDataList;
        histogramData.value = data.histogramData;
        lineTitleArray.value = data.lineTitleArray;
        fullScore.value = data.fullScore;
        renderRightChart();
      })
      .finally(() => (loading.value = false));
  }
}
const rightCanvas = ref(); //dom实例
let myRightChart = null; // echarts实例
const renderRightChart = () => {
  myRightChart = echarts.init(rightCanvas.value);
  let mylinedata = [];
  for (let i = 0; i < histogramData.value.length; i++) {
    mylinedata.push(histogramData.value[i][2]);
  }
  let mydata = histogramData.value.map(function (item: any, index: any) {
    return {
      value: item,
      itemStyle: {
        // color: '#9bbb59'
      },
    };
  });

  function renderItem(params: any, api: any) {
    let yValue = api.value(2);
    let start = api.coord([api.value(0), yValue]);
    let size = api.size([api.value(1) - api.value(0), yValue]);
    let style = api.style();

    return {
      type: "rect",
      shape: {
        x: start[0],
        y: start[1],
        width: size[0],
        height: size[1],
      },
      style: style,
    };
  }

  let option = {
    title: {
      text: "考试成绩统计直方图",
      left: "center",
    },
    tooltip: {},
    xAxis: [
      {
        scale: true,
        type: "value",
        min: 0,
        max: fullScore.value,
        interval: 10,
      },
      {
        type: "category",
        show: false,
        data: lineTitleArray.value,
      },
    ],
    yAxis: {},
    series: [
      {
        type: "custom",
        renderItem: renderItem,
        label: {
          show: true,
          position: "top",
        },
        dimensions: ["from", "to", "人数"],
        encode: {
          x: [0, 1],
          y: 2,
          tooltip: [0, 1, 2],
          itemName: 3,
        },
        data: mydata,
      },
      {
        type: "line",
        smooth: true,
        xAxisIndex: 1,
        itemStyle: {
          normal: {
            color: "#275F82", // 改变折线点的颜色
            lineStyle: {
              color: "#253A5D", // 改变折线颜色
            },
          },
        },
        data: mylinedata,
      },
    ],
  };
  myRightChart.setOption(option, true);
};

onMounted(() => {
  queryParams.examId = props.examId;
  queryParams.clazzId = props.clazzId;
  queryParams.courseId = props.courseId;
  handleQuery();
});
</script>
<template>
  <el-row>
    <el-col :span="12">
      <custom-table
        :loading="loading"
        :table-data-list="tableDataList"
        :columns="columns"
      />
    </el-col>
    <el-col :span="12">
      <div id="right-canvas" ref="rightCanvas"></div>
    </el-col>
  </el-row>
</template>
<style lang="scss" scoped>
#right-canvas {
  width: 100%;
  height: 600px;
}
</style>
