import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { TeacherQuery, TeacherPageVO, TeacherForm } from "./types";

/**
 * 获取教师分页列表
 *
 * @param queryParams
 */
export function getTeacherPage(
  queryParams: TeacherQuery
): AxiosPromise<PageResult<TeacherPageVO[]>> {
  return request({
    url: "/api/v1/teachers/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取教师详情
 *
 * @param id
 */
export function getTeacherForm(id: number): AxiosPromise<TeacherForm> {
  return request({
    url: "/api/v1/teachers/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加教师
 *
 * @param data
 */
export function addTeacher(data: TeacherForm) {
  return request({
    url: "/api/v1/teachers",
    method: "post",
    data: data,
  });
}

/**
 * 更新教师
 *
 * @param id
 * @param data
 */
export function updateTeacher(id: number, data: TeacherForm) {
  return request({
    url: "/api/v1/teachers/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除教师，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteTeachers(ids: string) {
  return request({
    url: "/api/v1/teachers/" + ids,
    method: "delete",
  });
}

/**
 * 教师下拉列表
 */
export function getTeacherOptions(
  arrangeId?: number
): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/teachers/options",
    method: "get",
    params: { arrangeId },
  });
}

/**
 * 下载教师导入模板
 *
 * @returns
 */
export function downloadTemplateApi() {
  return request({
    url: "/api/v1/teachers/template",
    method: "get",
    responseType: "arraybuffer",
  });
}

/**
 * 导出教师
 *
 * @param queryParams
 * @returns
 */
export function exportTeacher(queryParams: TeacherQuery) {
  return request({
    url: "/api/v1/teachers/_export",
    method: "get",
    params: queryParams,
    responseType: "arraybuffer",
  });
}

/**
 * 导入教师
 *
 * @param file
 */
export function importTeacher(file: File) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/api/v1/teachers/_import",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}
