import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { StudentSubjectSelectionRecord, StudentSubjectSelectionVersion } from "./types";

export function getSelectionVersions(gradeId?: number): AxiosPromise<StudentSubjectSelectionVersion[]> {
  return request({
    url: "/api/v1/student-subject-selections/versions",
    method: "get",
    params: { gradeId },
  });
}

export function addSelectionVersion(data: StudentSubjectSelectionVersion): AxiosPromise<StudentSubjectSelectionVersion> {
  return request({
    url: "/api/v1/student-subject-selections/versions",
    method: "post",
    data,
  });
}

export function getSelectionRecords(versionId: number): AxiosPromise<StudentSubjectSelectionRecord[]> {
  return request({
    url: "/api/v1/student-subject-selections/versions/" + versionId + "/records",
    method: "get",
  });
}

export function getSelectionStudentOptions(versionId: number): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/student-subject-selections/versions/" + versionId + "/student-options",
    method: "get",
  });
}

export function saveSelectionRecords(versionId: number, records: StudentSubjectSelectionRecord[]) {
  return request({
    url: "/api/v1/student-subject-selections/versions/" + versionId + "/records",
    method: "put",
    data: { records },
  });
}
