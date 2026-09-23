import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { ArrangeQuery, ArrangeForm, ArrangePageResult } from "./types";

/**
 * 获取学生的教学安排列表
 *
 * @param studentId 学生ID
 */
export function getArrangeOptions(
  studentId: number
): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/arrange/" + studentId + "/options",
    method: "get",
  });
}

/**
 * 教学安排分页列表
 */
export function getArrangePage(
  queryParams: ArrangeQuery
): AxiosPromise<ArrangePageResult> {
  return request({
    url: "/api/v1/arrange/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取教学安排表单数据
 *
 * @param id
 */
export function getArrangeFormData(id: number): AxiosPromise<ArrangeForm> {
  return request({
    url: "/api/v1/arrange/" + id + "/form",
    method: "get",
  });
}

/**
 * 新增教学安排
 *
 * @param data
 */
export function addArrange(data: ArrangeForm) {
  return request({
    url: "/api/v1/arrange",
    method: "post",
    data: data,
  });
}

/**
 * 修改教学安排项
 *
 * @param id
 * @param data
 */
export function updateArrange(id: number, data: ArrangeForm) {
  return request({
    url: "/api/v1/arrange/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 删除教学安排
 *
 * @param ids 教学安排项ID，多个以英文逗号(,)分割
 */
export function deleteArrange(ids: string) {
  return request({
    url: "/api/v1/arrange/" + ids,
    method: "delete",
  });
}
