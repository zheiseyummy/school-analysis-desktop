import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { ClazzQuery, ClazzPageVO, ClazzForm } from "./types";

/**
 * 获取班级分页列表
 *
 * @param queryParams
 */
export function getClazzPage(
  queryParams: ClazzQuery
): AxiosPromise<PageResult<ClazzPageVO[]>> {
  return request({
    url: "/api/v1/clazzs/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取班级详情
 *
 * @param id
 */
export function getClazzForm(id: number): AxiosPromise<ClazzForm> {
  return request({
    url: "/api/v1/clazzs/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加班级
 *
 * @param data
 */
export function addClazz(data: ClazzForm) {
  return request({
    url: "/api/v1/clazzs",
    method: "post",
    data: data,
  });
}

/**
 * 更新班级
 *
 * @param id
 * @param data
 */
export function updateClazz(id: number, data: ClazzForm) {
  return request({
    url: "/api/v1/clazzs/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除班级，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteClazzs(ids: string) {
  return request({
    url: "/api/v1/clazzs/" + ids,
    method: "delete",
  });
}

/**
 * 班级下拉列表
 */
export function getClazzOptions(): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/clazzs/options",
    method: "get",
  });
}

/**
 * 携带年级的班级下拉列表
 */
export function getComplexClazzOptions(): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/clazzs/complex_options",
    method: "get",
  });
}
