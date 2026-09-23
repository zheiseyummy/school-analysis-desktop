import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { ArchivesQuery, ArchivesPageVO, ArchivesForm } from "./types";

/**
 * 获取学情档案分页列表
 *
 * @param queryParams
 */
export function getArchivesPage(
  queryParams: ArchivesQuery
): AxiosPromise<PageResult<ArchivesPageVO[]>> {
  return request({
    url: "/api/v1/archivess/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取学情档案详情
 *
 * @param id
 */
export function getArchivesForm(id: number): AxiosPromise<ArchivesForm> {
  return request({
    url: "/api/v1/archivess/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加学情档案
 *
 * @param data
 */
export function addArchives(data: ArchivesForm) {
  return request({
    url: "/api/v1/archivess",
    method: "post",
    data: data,
  });
}

/**
 * 更新学情档案
 *
 * @param id
 * @param data
 */
export function updateArchives(id: number, data: ArchivesForm) {
  return request({
    url: "/api/v1/archivess/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除学情档案，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteArchivess(ids: string) {
  return request({
    url: "/api/v1/archivess/" + ids,
    method: "delete",
  });
}

/**
 * 学情档案下拉列表
 */
export function getArchivesOptions(): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/archivess/options",
    method: "get",
  });
}
