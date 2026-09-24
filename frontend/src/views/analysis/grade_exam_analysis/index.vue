<script setup lang="ts">
import {
  ClazzExamAnalysisQuery,
  ClazzStaticsBO,
  CourseStaticsBO,
  CourseClazzStaticsBO,
} from "@/api/analysis/types";
import { getOptions } from "@/api/exam";
import { getGradeOptions } from "@/api/grade";
defineOptions({
  name: "GradeExamAnalysis",
  inheritAttrs: false,
});
import { getGradeExamAnalysisData, getTeacherAnalysisData } from "@/api/analysis";
import * as echarts from "echarts";
import { GradePageVO } from "@/api/grade/types";
import { ExamPageVO } from "@/api/exam/types";
const examList = ref<OptionType[]>(); //考试列表下拉数据源
const gradeList = ref<OptionType[]>(); //年级下拉数据源
const queryFormRef = ref(ElForm);
const loading = ref(false);

const grade = ref<GradePageVO>({});
const exam = ref<ExamPageVO>({});

const columns = ref<any>();
const tableDataList = ref<any>();
const queryParams = reactive<ClazzExamAnalysisQuery>({});

const courseStaticsList = ref<CourseStaticsBO[]>([]);
const clazzStaticsList = ref<ClazzStaticsBO[]>([]);
const courseClazzStaticsList = ref<CourseClazzStaticsBO[]>([]);
const teacherAnalysisList = ref<any[]>([]);
/** 加载考试下拉数据源 */
async function loadExamOptions() {
  getOptions(queryParams).then((response) => {
    examList.value = response.data;
  });
}

/** 加载年级下拉数据源 */
async function loadGradeOptions() {
  getGradeOptions().then((response) => {
    gradeList.value = response.data;
  });
}

const rules = reactive({
  gradeId: [{ required: true, message: "请选择年级", trigger: "blur" }],
  examId: [{ required: true, message: "请选择考试", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  queryFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      getGradeExamAnalysisData(queryParams)
        .then(({ data }) => {
          columns.value = data.columns;
          tableDataList.value = data.tableDataList;
          courseStaticsList.value = data.courseStaticsList;
          clazzStaticsList.value = data.clazzStaticsList;
          courseClazzStaticsList.value = data.courseClazzStaticsList;
          getTeacherAnalysisData({ gradeId: queryParams.gradeId!, examId: queryParams.examId! }).then(({ data }) => (teacherAnalysisList.value = data));
          grade.value = data.grade;
          exam.value = data.exam;
          renderLeftChart();
          renderRightChart();
        })
        .catch(() => {
          columns.value = [];
          tableDataList.value = [];
          courseStaticsList.value = [];
          clazzStaticsList.value = [];
          courseClazzStaticsList.value = [];
          grade.value = {};
          exam.value = {};
        })
        .finally(() => (loading.value = false));
    }
  });
}

/** 重置查询 */
function resetQuery() {
  columns.value = [];
  tableDataList.value = [];
  courseStaticsList.value = [];
  clazzStaticsList.value = [];
  courseClazzStaticsList.value = [];
  grade.value = {};
  exam.value = {};
  queryFormRef.value.resetFields();
  loadExamOptions();
}
onMounted(() => {
  loadGradeOptions();
  loadExamOptions();
});

function conditionChange() {
  loadExamOptions();
}

const leftCanvas = ref();
const rightCanvas = ref(); //dom实例
let myLeftChart = null;
let myRightChart = null; // echarts实例

const renderLeftChart = () => {
  let legendData = courseStaticsList.value.map((it) => it.courseName);
  let seriesData = courseStaticsList.value.map((it) => {
    let obj = {
      name: it.courseName,
      value: [it.aRate, it.bRate, it.cRate, it.dRate, it.eRate],
    };
    return obj;
  });
  myLeftChart = echarts.init(leftCanvas.value);
  let option = {
    // title: {
    //   text: "Basic Radar Chart",
    // },
    legend: {
      data: legendData,
    },
    tooltip: {
      trigger: "item",
    },
    radar: {
      // shape: "circle",
      indicator: [
        { name: "优秀率", max: 1 },
        { name: "良好率", max: 1 },
        { name: "中等率", max: 1 },
        { name: "合格率", max: 1 },
        { name: "不合格率", max: 1 },
      ],
    },
    series: [
      {
        name: "Budget vs spending",
        type: "radar",
        // areaStyle: {},
        data: seriesData,
      },
    ],
  };

  myLeftChart.setOption(option);
};

const renderRightChart = () => {
  let legendData = clazzStaticsList.value.map((it) => it.clazzName);
  let seriesData = clazzStaticsList.value.map((it) => {
    let obj = {
      name: it.clazzName,
      value: [it.aRate, it.bRate, it.cRate, it.dRate, it.eRate],
    };
    return obj;
  });
  myRightChart = echarts.init(rightCanvas.value);
  let option = {
    // title: {
    //   text: "Basic Radar Chart",
    // },
    legend: {
      data: legendData,
    },
    tooltip: {
      trigger: "item",
    },
    radar: {
      shape: "circle",
      indicator: [
        { name: "优秀率", max: 1 },
        { name: "良好率", max: 1 },
        { name: "中等率", max: 1 },
        { name: "合格率", max: 1 },
        { name: "不合格率", max: 1 },
      ],
    },
    series: [
      {
        name: "Budget vs spending",
        type: "radar",
        areaStyle: {},
        data: seriesData,
      },
    ],
  };

  myRightChart.setOption(option);
};
</script>
<template>
  <div class="app-container">
    <div class="search-container">
      <el-form
        ref="queryFormRef"
        :model="queryParams"
        :rules="rules"
        :inline="true"
      >
        <el-form-item label="年度" prop="year">
          <el-date-picker
            v-model="queryParams.year"
            type="year"
            format="YYYY"
            value-format="YYYY"
            placeholder="请选择考试年度"
            @change="conditionChange"
          />
        </el-form-item>
        <el-form-item label="年级" prop="gradeId">
          <el-select
            v-model="queryParams.gradeId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
            @change="conditionChange"
          >
            <el-option
              v-for="item in gradeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="学期" prop="semester" style="width: 268px">
          <dictionary
            @change="conditionChange"
            v-model="queryParams.semester"
            type-code="semester"
          />
        </el-form-item>
        <el-form-item label="考试类型" prop="examType" style="width: 268px">
          <dictionary
            @change="conditionChange"
            v-model="queryParams.examType"
            type-code="examType"
          />
        </el-form-item>

        <el-form-item label="考试" prop="examId">
          <el-select
            v-model="queryParams.examId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
          >
            <el-option
              v-for="item in examList"
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
        <el-descriptions v-if="exam.id && grade.id" :column="5" border>
          <el-descriptions-item label="年级"
            >{{ exam.year }}/{{ grade.name }}
          </el-descriptions-item>
          <el-descriptions-item label="学期">{{
            exam.semesterStr
          }}</el-descriptions-item>
          <el-descriptions-item label="考试类型">{{
            exam.examTypeStr
          }}</el-descriptions-item>
          <el-descriptions-item label="考试名称"
            >{{ exam.name }}
          </el-descriptions-item>
          <el-descriptions-item label="考试时间"
            >{{ exam.examDate }}
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <el-row>
        <el-col :span="24">
          <custom-table
            :loading="loading"
            :table-data-list="tableDataList"
            :columns="columns"
          />
        </el-col>
      </el-row>
      <el-row class="mt-3">
        <el-col :span="24">
          <CourseStaticsTable
            v-if="courseStaticsList && courseStaticsList.length > 0"
            :loading="loading"
            :course-statics-list="courseStaticsList"
          />
        </el-col>
      </el-row>
      <el-row class="mt-3">
        <el-col :span="24">
          <ClazzStaticsTable
            v-if="clazzStaticsList && clazzStaticsList.length > 0"
            :loading="loading"
            :clazz-statics-list="clazzStaticsList"
          />
        </el-col>
      </el-row>
      <el-row class="mt-3">
        <el-col :span="24">
          <CourseClazzStaticsTable
            v-if="courseClazzStaticsList && courseClazzStaticsList.length > 0"
            :loading="loading"
            :course-clazz-statics-list="courseClazzStaticsList"
          />
        </el-col>
      </el-row>
      <el-row v-if="teacherAnalysisList.length" class="mt-3">
        <el-col :span="24">
          <el-card shadow="never">
            <template #header>任课教师成绩分析</template>
            <el-table :data="teacherAnalysisList" size="small">
              <el-table-column prop="teacherName" label="教师" />
              <el-table-column prop="courseName" label="学科" />
              <el-table-column prop="clazzName" label="班级" />
              <el-table-column prop="averageScore" label="班级均分" />
              <el-table-column prop="gradeAverageScore" label="年级均分" />
              <el-table-column prop="averageDifference" label="均分差" />
              <el-table-column prop="excellentRate" label="优秀率" />
              <el-table-column prop="passRate" label="及格率" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      <el-row class="mt-3">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>年级下各学科考试成绩等级区间情况统计（左）</span>
              </div>
            </template>
            <div id="left-canvas" ref="leftCanvas"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>年级下各班级考试成绩等级区间情况统计（右）</span>
              </div>
            </template>
            <div id="right-canvas" ref="rightCanvas"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>
<style lang="scss" scoped>
#left-canvas {
  width: 100%;
  height: 500px;
}

#right-canvas {
  width: 100%;
  height: 500px;
}
</style>
<style>
.item {
  margin-top: 10px;
  margin-right: 40px;
}

.letter-circle-success {
  width: 20px;
  height: 20px;
  color: white;
  background-color: #67c23a;
  border-radius: 50%;
}

.letter-circle-error {
  width: 20px;
  height: 20px;
  color: white;
  background-color: #f56c6c;
  border-radius: 50%;
}
</style>
