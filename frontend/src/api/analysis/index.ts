import request from "@/utils/request";
import { AxiosPromise } from "axios";
import {
  ClazzExamAnalysisQuery,
  StudentScoreAnalysisQuery,
  ExamScoreBO,
} from "./types";

/**
 * 获取班级考试维度分析数据
 *
 * @param queryParams
 */
export function getClazzExamAnalysisData(
  queryParams: ClazzExamAnalysisQuery
): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/clazzExamAnalysisData",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取班级考试维度分析数据(PDF)
 *
 * @param queryParams
 */
export function getClazzExamAnalysisDataToPdf(
  queryParams: ClazzExamAnalysisQuery
) {
  return request({
    url: "/api/v1/analysis/clazzExamAnalysisDataToPdf",
    method: "get",
    params: queryParams,
    responseType: "arraybuffer",
  });
}

/**
 * 获取班级考试维度分析数据(Excel导出)
 *
 * @param queryParams
 */
export function getClazzExamAnalysisDataToExcel(
  queryParams: ClazzExamAnalysisQuery
) {
  return request({
    url: "/api/v1/analysis/clazzExamAnalysisDataToExcel",
    method: "get",
    params: queryParams,
    responseType: "arraybuffer",
  });
}

/**
 * 获取年级考试维度分析数据
 *
 * @param queryParams
 */
export function getGradeExamAnalysisData(
  queryParams: ClazzExamAnalysisQuery
): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/gradeExamAnalysisData",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取个人所有成绩分析数据
 *
 * @param queryParams
 */
export function getStudentScoreAnalysisData(
  queryParams: StudentScoreAnalysisQuery
): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/studentScoreAnalysisData",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取个人单个学科成绩分析数据
 *
 * @param queryParams
 */
export function getStudentSingleCourseAnalysisData(
  queryParams: StudentScoreAnalysisQuery
): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/studentSingleScoreAnalysisData",
    method: "get",
    params: queryParams,
  });
}

export function getGradeInsights(params: { gradeId: number; examId: number; excellentLine?: number; passLine?: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/gradeInsights",
    method: "get",
    params,
  });
}

export function getTeacherAnalysisData(params: { examId: number; gradeId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/teacherAnalysisData",
    method: "get",
    params,
  });
}

export function getGradeExamTrend(params: { gradeId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/gradeExamTrend",
    method: "get",
    params,
  });
}

export function getClazzExamTrend(params: { clazzId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/clazzExamTrend",
    method: "get",
    params,
  });
}

export function getClazzSubjectProgress(params: { clazzId: number; gradeId: number; currentExamId: number; previousExamId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/clazzSubjectProgress",
    method: "get",
    params,
  });
}

export function getStudentProgressRanking(params: { clazzId: number; currentExamId: number; previousExamId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/studentProgressRanking",
    method: "get",
    params,
  });
}

export function getClazzSubjectWarnings(params: { clazzId: number; gradeId: number; examId: number; threshold?: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/clazzSubjectWarnings",
    method: "get",
    params,
  });
}

export function getClazzSubjectBalance(params: { clazzId: number; examId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/clazzSubjectBalance",
    method: "get",
    params,
  });
}

export function getProgressBands(params: { gradeId: number; currentExamId: number; previousExamId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/progressBands",
    method: "get",
    params,
  });
}

export function getStudentBiasAnalysis(params: { gradeId: number; examId: number }): AxiosPromise<any> {
  return request({
    url: "/api/v1/analysis/studentBiasAnalysis",
    method: "get",
    params,
  });
}

export function exportStudentHistory(params: { gradeId: number; examIds: string }) {
  return request({
    url: "/api/v1/analysis/studentHistoryToExcel",
    method: "get",
    params,
    responseType: "arraybuffer",
  });
}
