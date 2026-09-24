import request from "@/utils/request";

export function getQualityConfig() { return request({ url: "/api/v1/quality/config", method: "get" }); }
export function getStudentQuality(studentId: number) { return request({ url: `/api/v1/quality/student/${studentId}`, method: "get" }); }
export function saveStudentQualitySemester(studentId: number, semester: string, ratings: Record<string, string>) {
  return request({ url: `/api/v1/quality/student/${studentId}/semester/${semester}`, method: "post", data: ratings });
}
export function getStudentQualitySummary(studentId: number) { return request({ url: `/api/v1/quality/student/${studentId}/summary`, method: "get" }); }
