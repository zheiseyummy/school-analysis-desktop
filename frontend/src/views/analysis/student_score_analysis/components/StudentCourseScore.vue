<script setup lang="ts">
import { StudentScoreAnalysisQuery, ExamScoreBO } from "@/api/analysis/types";
import { getStudentSingleCourseAnalysisData } from "@/api/analysis";
import * as echarts from "echarts";
import { StudentPageVO } from "@/api/student/types";
import { CoursePageVO } from "@/api/course/types";
defineOptions({
  name: "StudentCourseScore",
  inheritAttrs: false,
});

const student = ref<StudentPageVO>();
const course = ref<CoursePageVO>();
const queryParams = reactive<StudentScoreAnalysisQuery>({});

const props = defineProps({
  studentId: {
    type: Number,
    required: false,
  },
  courseId: {
    type: Number,
    required: false,
  },
});

const loading = ref<boolean>(false);

const examScoreList = ref<ExamScoreBO[]>();

watch(
  [() => props.studentId, () => props.courseId],
  ([newStudentId, newCourseId]) => {
    queryParams.studentId = newStudentId;
    queryParams.courseId = newCourseId;
    handleQuery();
  }
);

function handleQuery() {
  if (queryParams.studentId && queryParams.courseId) {
    loading.value = true;
    getStudentSingleCourseAnalysisData(queryParams)
      .then(({ data }) => {
        examScoreList.value = data.examScoreList;
        student.value = data.student;
        course.value = data.course;
        renderCenterChart();
      })
      .finally(() => (loading.value = false));
  }
}

const centerCanvas = ref(); //dom实例
let myCenterChart = null; // echarts实例
const renderCenterChart = () => {
  myCenterChart = echarts.init(centerCanvas.value);
  let titleList = examScoreList.value?.map((it) => it.examName);
  let dataList = examScoreList.value?.map((it) => it.score);
  let option = {
    xAxis: {
      type: "category",
      data: titleList,
      axisLabel: {
        rotate: 30, // 设置标签倾斜角度，单位为度
      },
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        data: dataList,
        type: "line",
        label: {
          show: true,
          position: "top",
        },
      },
    ],
  };
  myCenterChart.setOption(option);
};
onMounted(() => {
  queryParams.studentId = props.studentId;
  queryParams.courseId = props.courseId;
  handleQuery();
});
</script>
<template>
  <el-row :gutter="10">
    <el-col :span="24">
      <el-card>
        <template #header>
          <div class="card-header">
            <span
              ><el-tag type="success" v-if="student">{{ student.name }}</el-tag
              >&nbsp;&nbsp;<el-tag type="warning" v-if="course">{{
                course.name
              }}</el-tag
              >&nbsp;&nbsp;单科历次考试成绩变化折线图</span
            >
          </div>
        </template>
        <div id="center-canvas" ref="centerCanvas"></div>
      </el-card>
    </el-col>
  </el-row>
</template>
<style lang="scss" scoped>
#center-canvas {
  width: 100%;
  height: 600px;
}
</style>
