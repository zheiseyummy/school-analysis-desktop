import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { ExamQuery, ExamPageVO, ExamForm } from "./types";
import { ClazzExamAnalysisQuery } from "@/api/analysis/types";

/**
 * 获取考试分页列表
 *
 * @param queryParams
 */
export function getExamPage(
  queryParams: ExamQuery
): AxiosPromise<PageResult<ExamPageVO[]>> {
  return request({
    url: "/api/v1/exams/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取考试详情
 *
 * @param id
 */
export function getExamForm(id: number): AxiosPromise<ExamForm> {
  return request({
    url: "/api/v1/exams/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加考试
 *
 * @param data
 */
export function addExam(data: ExamForm) {
  return request({
    url: "/api/v1/exams",
    method: "post",
    data: data,
  });
}

/**
 * 更新考试
 *
 * @param id
 * @param data
 */
export function updateExam(id: number, data: ExamForm) {
  return request({
    url: "/api/v1/exams/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除考试，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteExams(ids: string) {
  return request({
    url: "/api/v1/exams/" + ids,
    method: "delete",
  });
}

/**
 * 体检安排下拉列表
 */
export function getSelfExamOptions(
  clazzId?: number
): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/exams/options",
    method: "get",
    params: { clazzId: clazzId },
  });
}

/**
 * 考试下拉列表
 * @returns
 */

export function getOptions(
  queryParams: ClazzExamAnalysisQuery
): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/exams/options/exam",
    method: "get",
    params: queryParams,
  });
}

/**
 *  拉取年级班级树形结构列表
 */
export function getExamOptions(): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/exams/gradeClazz/options",
    method: "get",
  });
}

/**
 * 获取考试的年级或者班级ID集合
 *
 * @param queryParams
 */
export function getExamGradeClazzIds(examId: number): AxiosPromise<number[]> {
  return request({
    url: "/api/v1/exams/" + examId + "/gradeClazzIds",
    method: "get",
  });
}

/**
 * 分配年级班级给考试
 *
 * @param queryParams
 */
export function updateExamGradeClazzs(
  examId: number,
  data: string[]
): AxiosPromise<any> {
  return request({
    url: "/api/v1/exams/" + examId + "/gradeClazzIds",
    method: "put",
    data: data,
  });
}
