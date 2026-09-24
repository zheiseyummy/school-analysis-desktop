<script setup lang="ts">
defineOptions({
  name: "ClazzExamAnalysis",
  inheritAttrs: false,
});
import {
  ClazzExamAnalysisQuery,
  CourseStaticsBO,
  StudentCourseScoreBO,
} from "@/api/analysis/types";

import {
  getClazzExamAnalysisData,
  getClazzExamAnalysisDataToPdf,
  getClazzExamAnalysisDataToExcel,
  getClazzSubjectWarnings,
} from "@/api/analysis";
import { getComplexClazzOptions } from "@/api/clazz";
import { getOptions } from "@/api/exam";
import { GradePageVO } from "@/api/grade/types";
import { ClazzPageVO } from "@/api/clazz/types";
import { ExamPageVO } from "@/api/exam/types";
import { CoursePageVO } from "@/api/course/types";
import * as echarts from "echarts";
import { TabsPaneContext } from "element-plus";
const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源
const examList = ref<OptionType[]>(); //考试列表下拉数据源
const queryFormRef = ref(ElForm);
const loading = ref(false);

const columns = ref<any>();
const tableDataList = ref<any>();

const grade = ref<GradePageVO>({});
const clazz = ref<ClazzPageVO>({});
const exam = ref<ExamPageVO>({});

const courseStaticsList = ref<CourseStaticsBO[]>([]);
const subjectWarnings = ref<any[]>([]);

const queryParams = reactive<ClazzExamAnalysisQuery>({});

const courseNameList = ref<string[]>([]);

const studentCourseScoreList = ref<StudentCourseScoreBO[]>([]);

const courseList = ref<CoursePageVO[]>();

const tabsActiveName = ref();

/** 加载携带年级信息的班级下拉数据源 */
async function loadComplexClazzOptions() {
  getComplexClazzOptions().then((response) => {
    complexClazzList.value = response.data;
  });
}

/** 加载考试下拉数据源 */
async function loadExamOptions() {
  getOptions(queryParams).then((response) => {
    examList.value = response.data;
  });
}

const rules = reactive({
  clazzId: [{ required: true, message: "请选择班级", trigger: "blur" }],
  examId: [{ required: true, message: "请选择考试", trigger: "blur" }],
});

/** 查询 */
function handleQuery() {
  queryFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      getClazzExamAnalysisData(queryParams)
        .then(({ data }) => {
          columns.value = data.columns;
          tableDataList.value = data.tableDataList;
          grade.value = data.grade;
          clazz.value = data.clazz;
          exam.value = data.exam;
          courseStaticsList.value = data.courseStaticsList;
          if (queryParams.clazzId && queryParams.gradeId && queryParams.examId) {
            getClazzSubjectWarnings({ clazzId: queryParams.clazzId, gradeId: queryParams.gradeId, examId: queryParams.examId }).then(({ data }) => (subjectWarnings.value = data));
          }
          courseNameList.value = data.courseNameList;
          studentCourseScoreList.value = data.studentCourseScoreList;
          courseList.value = data.courseList;
          if (courseList.value) {
            tabsActiveName.value = courseList.value![0].id;
          }

          renderLeftChart();
          renderCenterChart();
          renderRightChart();
          renderStudentChart();
        })
        .catch(() => {
          columns.value = [];
          tableDataList.value = [];
          grade.value = {};
          clazz.value = {};
          exam.value = {};
        })
        .finally(() => (loading.value = false));
    }
  });
}

/** 打印 */
function handlePrinter() {
  queryFormRef.value.validate((valid: any) => {
    if (valid) {
      getClazzExamAnalysisDataToPdf(queryParams).then((response) => {
        const content = response.data;
        const blob = new Blob([content], { type: "application/pdf" });
        if ("download" in document.createElement("a")) {
          const elink = document.createElement("a");
          // elink.download = filename
          elink.style.display = "none";
          elink.href = URL.createObjectURL(blob);
          elink.target = "_blank";
          document.body.appendChild(elink);
          elink.click();
          URL.revokeObjectURL(elink.href);
          document.body.removeChild(elink);
        } else {
          if (navigator.msSaveBlob) {
            navigator.msSaveBlob(blob, "成绩单.pdf");
          }
        }
      });
    }
  });
}

/**导出 */

function handleExport() {
  queryFormRef.value.validate((valid: any) => {
    if (valid) {
      getClazzExamAnalysisDataToExcel(queryParams).then((response) => {
        const fileData = response.data;
        const fileName = decodeURI(
          response.headers["content-disposition"].split(";")[1].split("=")[1]
        );
        const fileType =
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

        const blob = new Blob([fileData], { type: fileType });
        const downloadUrl = window.URL.createObjectURL(blob);

        const downloadLink = document.createElement("a");
        downloadLink.href = downloadUrl;
        downloadLink.download = fileName;

        document.body.appendChild(downloadLink);
        downloadLink.click();

        document.body.removeChild(downloadLink);
        window.URL.revokeObjectURL(downloadUrl);
      });
    }
  });
}

/** 重置查询 */
function resetQuery() {
  columns.value = [];
  tableDataList.value = [];
  courseStaticsList.value = [];
  grade.value = {};
  clazz.value = {};
  exam.value = {};
  queryFormRef.value.resetFields();
  loadExamOptions();
  renderLeftChart();
  renderCenterChart();
  renderRightChart();
  renderStudentChart();
}
onMounted(() => {
  loadComplexClazzOptions();
  loadExamOptions();
});

function conditionChange() {
  loadExamOptions();
}

const leftCanvas = ref();
const centerCanvas = ref(); // dom实例
const rightCanvas = ref(); //dom实例
const studentCanvas = ref();

let myLeftChart = null;
let myCenterChart = null; // echarts实例
let myRightChart = null; // echarts实例
let myStudentChart = null;

const renderStudentChart = () => {
  myStudentChart = echarts.init(studentCanvas.value);
  let title = [];
  for (let i = 0; i < studentCourseScoreList.value.length; i++) {
    title.push(studentCourseScoreList.value[i].studentName);
  }
  let seriesData = [];
  for (let i = 0; i < courseNameList.value.length; i++) {
    let obj: any = {
      name: courseNameList.value[i],
      type: "line",
      stack: "total",
      smooth: true,
      label: {
        show: true,
        position: "top",
      },
      areaStyle: {},
      emphasis: {
        focus: "series",
      },
      data: [],
    };
    for (let j = 0; j < studentCourseScoreList.value.length; j++) {
      obj.data.push(studentCourseScoreList.value[j].courseScoreList![i]);
    }
    seriesData.push(obj);
  }

  let option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {
      data: courseNameList.value,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    yAxis: {
      type: "value",
    },
    xAxis: {
      type: "category",
      data: title,
      axisLabel: {
        interval: 0,
        formatter: function (value: any) {
          return value.split("").join("\n");
        },
      },
    },
    series: seriesData,
  };

  // 使用刚指定的配置项和数据显示图表。
  myStudentChart.setOption(option, true);
};

const renderLeftChart = () => {
  myLeftChart = echarts.init(leftCanvas.value);
  let legendList = ["优秀", "良好", "中等", "合格", "不合格"];
  let seriesList: any = [];
  legendList.forEach((item, index) => {
    let obj: any = {
      name: item,
      type: "bar",
      stack: "Total",
      areaStyle: {},
      emphasis: {
        focus: "series",
      },
      label: {
        show: true,
      },
      data: [],
    };
    if (index == 0) {
      courseStaticsList.value.forEach((v) => obj.data.push(v.aCount!));
    }

    if (index == 1) {
      courseStaticsList.value.forEach((v) => obj.data.push(v.bCount!));
    }

    if (index == 2) {
      courseStaticsList.value.forEach((v) => obj.data.push(v.cCount!));
    }

    if (index == 3) {
      courseStaticsList.value.forEach((v) => obj.data.push(v.dCount!));
    }

    if (index == 4) {
      courseStaticsList.value.forEach((v) => obj.data.push(v.eCount!));
    }
    seriesList.push(obj);
  });

  myLeftChart.setOption({
    // title: {
    //   text: 'Stacked Area Chart'
    // },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
        label: {
          backgroundColor: "#6a7985",
        },
      },
    },
    legend: {
      data: legendList,
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: [
      {
        type: "category",
        // boundaryGap: false,
        data: courseStaticsList.value.map((v) => v.courseName),
      },
    ],
    yAxis: [
      {
        type: "value",
      },
    ],
    series: seriesList,
  });
};

const renderCenterChart = () => {
  let aCount = 0;
  let bCount = 0;
  let cCount = 0;
  let dCount = 0;
  let eCount = 0;
  courseStaticsList.value.forEach((item) => {
    aCount = aCount + item.aCount!;
    bCount = bCount + item.bCount!;
    cCount = cCount + item.cCount!;
    dCount = dCount + item.dCount!;
    eCount = eCount + item.eCount!;
  });

  myCenterChart = echarts.init(centerCanvas.value);
  myCenterChart.setOption({
    tooltip: {
      trigger: "item",
    },
    legend: {
      top: "5%",
      left: "center",
    },
    series: [
      {
        name: "成绩等级",
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        padAngle: 5,
        itemStyle: {
          borderRadius: 10,
        },
        label: {
          show: true,
          normal: {
            show: true,
            formatter: "{b}: {d}%",
          },
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 40,
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: true,
        },
        data: [
          { value: aCount, name: "优秀" },
          { value: bCount, name: "良好" },
          { value: cCount, name: "中等" },
          { value: dCount, name: "合格" },
          { value: eCount, name: "不合格" },
        ],
      },
    ],
  });
};

const renderRightChart = () => {
  myRightChart = echarts.init(rightCanvas.value);
  let legendList = courseStaticsList.value.map((v) => v.courseName);
  let seriesList = courseStaticsList.value.map((v) => {
    let obj = {
      name: v.courseName,
      type: "line",
      stack: "Total",
      areaStyle: {},
      emphasis: {
        focus: "series",
      },
      label: {
        show: true,
      },
      data: [v.aCount, v.bCount, v.cCount, v.dCount, v.eCount],
    };
    return obj;
  });
  myRightChart.setOption({
    // title: {
    //   text: 'Stacked Area Chart'
    // },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
        label: {
          backgroundColor: "#6a7985",
        },
      },
    },
    legend: {
      data: legendList,
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: [
      {
        type: "category",
        // boundaryGap: false,
        data: ["优秀", "良好", "中等", "合格", "不合格"],
      },
    ],
    yAxis: [
      {
        type: "value",
      },
    ],
    series: seriesList,
  });
};

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab.props.name, event);
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
        <el-form-item label="班级" prop="clazzId">
          <el-select
            v-model="queryParams.clazzId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
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
          <el-button type="success" @click="handlePrinter"
            ><i-ep-printer />打印</el-button
          >
          <el-button type="warning" @click="handleExport"
            ><i-ep-download />导出</el-button
          >
        </el-form-item>
      </el-form>
    </div>
        <el-card shadow="never" class="table-container">
      <template #header>
        <el-descriptions
          v-if="exam.id && grade.id && clazz.id"
          :column="5"
          border
        >
          <el-descriptions-item label="班级"
            >{{ exam.year }}/{{ grade.name }}/{{
              clazz.name
            }}</el-descriptions-item
          >
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
        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>班级考试单科成绩等级数量情况统计（左）</span>
              </div>
            </template>
            <div id="left-canvas" ref="leftCanvas"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>班级考试全科成绩等级数量占比统计（中）</span>
              </div>
            </template>
            <div id="center-canvas" ref="centerCanvas"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>班级考试等级单科成绩数量情况统计（右）</span>
              </div>
            </template>
            <div id="right-canvas" ref="rightCanvas"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row class="mt-3">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>班级学生全科考试成绩情况统计</span>
              </div>
            </template>
            <div id="student-canvas" ref="studentCanvas"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row class="mt-3">
        <el-col :span="24">
          <el-tabs
            v-model="tabsActiveName"
            type="border-card"
            @tab-click="handleClick"
          >
            <el-tab-pane
              v-for="item in courseList"
              :label="item.name"
              :key="item.id"
              :name="item.id"
              :lazy="true"
            >
              <CourseStaticsContent
                :exam-id="queryParams.examId"
                :clazz-id="queryParams.clazzId"
                :course-id="item.id"
              />
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
      <el-card v-if="subjectWarnings.length" class="mt-3" shadow="never">
        <template #header>学科落后预警</template>
        <el-table :data="subjectWarnings" size="small">
          <el-table-column prop="courseName" label="学科" />
          <el-table-column prop="clazzAverage" label="班级均分" />
          <el-table-column prop="gradeAverage" label="年级均分" />
          <el-table-column prop="difference" label="分差" />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>
<style lang="scss" scoped>
.page-container {
  box-sizing: border-box;
  min-height: 100%;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

#left-canvas {
  width: 100%;
  height: 400px;
}

#center-canvas {
  width: 100%;
  height: 400px;
}

#right-canvas {
  width: 100%;
  height: 400px;
}

#student-canvas {
  width: 100%;
  height: 400px;
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
