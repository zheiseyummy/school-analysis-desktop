import request from "@/utils/request";

export function getQualityConfig() {
  return request({
    url: "/api/v1/quality/config",
    method: "get",
  });
}
export function getJuniorQualityClasses() {
  return request({
    url: "/api/v1/quality/classes",
    method: "get",
  });
}
export function getQualityClassStudents(clazzId: number) {
  return request({
    url: "/api/v1/quality/class/" + clazzId,
    method: "get",
  });
}
export function getStudentQuality(studentId: number) {
  return request({
    url: "/api/v1/quality/student/" + studentId,
    method: "get",
  });
}
export function saveStudentQualitySemester(studentId: number, semester: string, ratings: Record<string, string>) {
  return request({
    url: "/api/v1/quality/student/" + studentId + "/semester/" + semester,
    method: "post",
    data: ratings,
  });
}
export function getStudentQualitySummary(studentId: number) {
  return request({
    url: "/api/v1/quality/student/" + studentId + "/summary",
    method: "get",
  });
}
export function importQualityWorkbook(clazzId: number, file: File) {
  const formData = new FormData(); formData.append("file", file);
  return request({
    url: "/api/v1/quality/import",
    method: "post",
    params: { clazzId }, data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}
export function exportQualityWorkbook(clazzId: number) {
  return request({
    url: "/api/v1/quality/export",
    method: "get",
    params: { clazzId }, responseType: "arraybuffer",
  });
}
export function getQualityFinal(clazzId: number) {
  return request({
    url: "/api/v1/quality/final/" + clazzId,
    method: "get",
  });
}
export function getQualityMissingReviews(clazzId: number) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/missing-reviews",
    method: "get",
  });
}
export function updateQualityMissingReview(clazzId: number, studentId: number, semester: string, data: { status: string; remark?: string }) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/missing-reviews/" + studentId + "/" + semester,
    method: "put",
    data,
  });
}
export function updateQualityFinalRatios(clazzId: number, data: { aRatio: number; bRatio: number; cRatio: number }) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/ratios",
    method: "put",
    data,
  });
}
export function exportQualityFinal(clazzId: number) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/export",
    method: "get",
    responseType: "arraybuffer",
  });
}
export function generateQualityFinal(clazzId: number) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/generate",
    method: "post",
  });
}
export function lockQualityFinal(clazzId: number, locked: boolean) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/lock",
    method: "post",
    params: { locked },
  });
}
export function updateQualityFinalLevel(clazzId: number, studentId: number, dimension: string, level: string) {
  return request({
    url: "/api/v1/quality/final/" + clazzId + "/level",
    method: "put",
    data: { studentId, dimension, level },
  });
}
