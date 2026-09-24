import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { StudentFollowup, StudentFollowupForm } from "./types";

export function getStudentFollowups(studentId: number): AxiosPromise<StudentFollowup[]> {
  return request({
    url: "/api/v1/student-followups/student/" + studentId,
    method: "get",
  });
}

export function addStudentFollowup(studentId: number, data: StudentFollowupForm): AxiosPromise<StudentFollowup> {
  return request({
    url: "/api/v1/student-followups/student/" + studentId,
    method: "post",
    data,
  });
}

export function updateStudentFollowup(id: number, data: StudentFollowupForm): AxiosPromise<StudentFollowup> {
  return request({
    url: "/api/v1/student-followups/" + id,
    method: "put",
    data,
  });
}

export function deleteStudentFollowup(id: number) {
  return request({
    url: "/api/v1/student-followups/" + id,
    method: "delete",
  });
}
