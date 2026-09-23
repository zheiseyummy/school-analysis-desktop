import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { StudentQuery, StudentPageVO, StudentForm } from "./types";

/**
 * 获取学生分页列表
 *
 * @param queryParams
 */
export function getStudentPage(
  queryParams: StudentQuery
): AxiosPromise<PageResult<StudentPageVO[]>> {
  return request({
    url: "/api/v1/students/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取学生详情
 *
 * @param id
 */
export function getStudentForm(id: number): AxiosPromise<StudentForm> {
  return request({
    url: "/api/v1/students/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加学生
 *
 * @param data
 */
export function addStudent(data: StudentForm) {
  return request({
    url: "/api/v1/students",
    method: "post",
    data: data,
  });
}

/**
 * 更新学生
 *
 * @param id
 * @param data
 */
export function updateStudent(id: number, data: StudentForm) {
  return request({
    url: "/api/v1/students/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除学生，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteStudents(ids: string) {
  return request({
    url: "/api/v1/students/" + ids,
    method: "delete",
  });
}

/**
 * 学生下拉列表
 */
export function getStudentOptions(
  clazzId: number,
  year?: number
): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/students/" + clazzId + "/options",
    method: "get",
    params: { year: year },
  });
}

/**
 * 下载学生导入模板
 *
 * @returns
 */
export function downloadTemplateApi() {
  return request({
    url: "/api/v1/students/template",
    method: "get",
    responseType: "arraybuffer",
  });
}

/**
 * 导出学生
 *
 * @param queryParams
 * @returns
 */
export function exportStudent(queryParams: StudentQuery) {
  return request({
    url: "/api/v1/students/_export",
    method: "get",
    params: queryParams,
    responseType: "arraybuffer",
  });
}

/**
 * 导入学生
 *
 * @param file
 */
export function importStudent(file: File) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/api/v1/students/_import",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}
