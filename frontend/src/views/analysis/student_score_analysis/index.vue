<script setup lang="ts">
import { StudentScoreAnalysisQuery } from "@/api/analysis/types";
import { getComplexClazzOptions } from "@/api/clazz";
import { getStudentOptions } from "@/api/student";
import { getStudentScoreAnalysisData } from "@/api/analysis/index";
import { CoursePageVO } from "@/api/course/types";
import { TabsPaneContext } from "element-plus";
defineOptions({
  name: "StudentScoreAnalysis",
  inheritAttrs: false,
});
const complexClazzList = ref<OptionType[]>(); //携带年级的班级下拉数据源
const studentList = ref<OptionType[]>(); //学生下拉数据源
const queryFormRef = ref(ElForm);

const queryParams = reactive<StudentScoreAnalysisQuery>({});

const loading = ref(false);

const columns = ref<any>();
const tableDataList = ref<any>();

const rules = reactive({
  year: [{ required: true, message: "请选择年度", trigger: "blur" }],
  clazzId: [{ required: true, message: "请选择班级", trigger: "blur" }],
  studentId: [{ required: true, message: "请选择学生", trigger: "blur" }],
});

const courseList = ref<CoursePageVO[]>();

const tabsActiveName = ref();

/** 加载携带年级信息的班级下拉数据源 */
async function loadComplexClazzOptions() {
  getComplexClazzOptions().then((response) => {
    complexClazzList.value = response.data;
  });
}

/** 加载学生下拉数据源 */
async function loadStudentOptions() {
  if (queryParams.clazzId && queryParams.year) {
    getStudentOptions(queryParams.clazzId!, queryParams.year).then(
      (response) => {
        studentList.value = response.data;
      }
    );
  }
}
function conditionChange() {
  loadStudentOptions();
}

function handleQuery() {
  queryFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      getStudentScoreAnalysisData(queryParams)
        .then(({ data }) => {
          columns.value = data.columns;
          tableDataList.value = data.tableDataList;
          courseList.value = data.courseList;
          if (courseList.value) {
            tabsActiveName.value = courseList.value![0].id;
          }
        })
        .catch(() => {
          columns.value = [];
          tableDataList.value = [];
          courseList.value = [];
        })
        .finally(() => (loading.value = false));
    }
  });
}

function resetQuery() {
  queryFormRef.value.resetFields();
  columns.value = [];
  tableDataList.value = [];
  courseList.value = [];
}

onMounted(() => {
  loadComplexClazzOptions();
});

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
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="queryParams.studentId"
            clearable
            class="!w-[200px]"
            placeholder="全部"
            @change="handleQuery"
          >
            <el-option
              v-for="item in studentList"
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
              <StudentCourseScore
                :student-id="queryParams.studentId"
                :course-id="item.id"
              />
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>
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
