import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { GradeQuery, GradePageVO, GradeForm } from "./types";

/**
 * 获取年级分页列表
 *
 * @param queryParams
 */
export function getGradePage(
  queryParams: GradeQuery
): AxiosPromise<PageResult<GradePageVO[]>> {
  return request({
    url: "/api/v1/grades/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取年级详情
 *
 * @param id
 */
export function getGradeForm(id: number): AxiosPromise<GradeForm> {
  return request({
    url: "/api/v1/grades/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加年级
 *
 * @param data
 */
export function addGrade(data: GradeForm) {
  return request({
    url: "/api/v1/grades",
    method: "post",
    data: data,
  });
}

/**
 * 更新年级
 *
 * @param id
 * @param data
 */
export function updateGrade(id: number, data: GradeForm) {
  return request({
    url: "/api/v1/grades/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除年级，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteGrades(ids: string) {
  return request({
    url: "/api/v1/grades/" + ids,
    method: "delete",
  });
}

/**
 * 年级下拉列表
 */
export function getGradeOptions(): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/grades/options",
    method: "get",
  });
}
