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
