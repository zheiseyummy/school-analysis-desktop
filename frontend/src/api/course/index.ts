import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { CourseQuery, CoursePageVO, CourseForm } from "./types";

/**
 * 获取课程分页列表
 *
 * @param queryParams
 */
export function getCoursePage(
  queryParams: CourseQuery
): AxiosPromise<PageResult<CoursePageVO[]>> {
  return request({
    url: "/api/v1/courses/page",
    method: "get",
    params: queryParams,
  });
}

/**
 * 获取课程详情
 *
 * @param id
 */
export function getCourseForm(id: number): AxiosPromise<CourseForm> {
  return request({
    url: "/api/v1/courses/" + id + "/form",
    method: "get",
  });
}

/**
 * 添加课程
 *
 * @param data
 */
export function addCourse(data: CourseForm) {
  return request({
    url: "/api/v1/courses",
    method: "post",
    data: data,
  });
}

/**
 * 更新课程
 *
 * @param id
 * @param data
 */
export function updateCourse(id: number, data: CourseForm) {
  return request({
    url: "/api/v1/courses/" + id,
    method: "put",
    data: data,
  });
}

/**
 * 批量删除课程，多个以英文逗号(,)分割
 *
 * @param ids
 */
export function deleteCourses(ids: string) {
  return request({
    url: "/api/v1/courses/" + ids,
    method: "delete",
  });
}

/**
 * 课程下拉列表
 */
export function getCourseOptions(clazzId?: number): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/courses/options",
    method: "get",
    params: { clazzId: clazzId },
  });
}
