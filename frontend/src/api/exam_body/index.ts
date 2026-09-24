import request from "@/utils/request";
import { AxiosPromise } from "axios";
import {
  ExamBodyQuery,
  ExamBodyPageVO,
  ScoreEntryQuery,
  ScoreEntryVO,
  ScoreEntryForm,
  ScoreImportPreview,
  ScoreImportBatch,
} from "./types";

/**
 * 获取考试主体分页列表
 *
 * @param queryParams
 */
export function getExamBodyPage(
  queryParams: ExamBodyQuery
): AxiosPromise<PageResult<ExamBodyPageVO[]>> {
  return request({
    url: "/api/v1/exam_body_s/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取成绩录入列表
 *
 * @param queryParams
 */
export function getScoreEntryList(
  queryParams: ScoreEntryQuery
): AxiosPromise<ScoreEntryVO[]> {
  return request({
    url: "/api/v1/exam_body_s/scoreEntryList",
    method: "get",
    params: queryParams,
  });
}

/**
 * 保存成绩
 *
 * @param data
 */
export function saveScore(data: ScoreEntryForm) {
  return request({
    url: "/api/v1/exam_body_s/saveScore",
    method: "post",
    data: data,
  });
}

/**
 * 考试班级所有课程成绩汇总数据
 *
 * @param id
 */
export function getExamBodyScorePreview(id: number): AxiosPromise<any> {
  return request({
    url: "/api/v1/exam_body_s/" + id + "/getExamBodyScorePreview",
    method: "get",
  });
}

/**
 * 下载成绩导入模板
 *
 * @returns
 */
export function downloadTemplateApi() {
  return request({
    url: "/api/v1/exam_body_s/template",
    method: "get",
    responseType: "arraybuffer",
  });
}

/**
 * 导入成绩
 *
 * @param file
 */
export function importScore(examId: number, file: File) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/api/v1/exam_body_s/_import",
    method: "post",
    params: { examId: examId },
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function getScoreImportStatus(examId: number): AxiosPromise<{ scoreCount: number; hasExistingScores: boolean }> {
  return request({
    url: "/api/v1/exam_body_s/import/status",
    method: "get",
    params: { examId },
  });
}

export function previewScoreImport(examId: number, file: File, blankPolicy: "KEEP" | "CLEAR") {
  const formData = new FormData();
  formData.append("file", file);
  return request<ScoreImportPreview>({
    url: "/api/v1/exam_body_s/import/preview",
    method: "post",
    params: { examId, blankPolicy },
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

export function confirmScoreImport(token: string, allowErrors: boolean) {
  return request<{ appliedChanges: number; skippedErrorRows: number; batchId?: string }>({
    url: "/api/v1/exam_body_s/import/confirm",
    method: "post",
    data: { token, allowErrors },
  });
}

export function getScoreImportLogs(examId?: number): AxiosPromise<ScoreImportBatch[]> {
  return request({
    url: "/api/v1/exam_body_s/import/logs",
    method: "get",
    params: examId ? { examId } : undefined,
  });
}

export function undoScoreImport(batchId: string) {
  return request<{ batchId: string; undoneChanges: number }>({
    url: "/api/v1/exam_body_s/import/undo/" + batchId,
    method: "post",
  });
}
