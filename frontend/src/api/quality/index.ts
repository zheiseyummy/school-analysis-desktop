import request from "@/utils/request";

export function getQualityConfig() {
  return request({
    url: "/api/v1/quality-independent/config",
    method: "get",
  });
}
export function getJuniorQualityClasses() {
  return request({
    url: "/api/v1/quality-independent/datasets",
    method: "get",
  });
}
export function getQualityClassStudents(clazzId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/students",
    method: "get",
  });
}
export function getStudentQuality(datasetId: number, studentId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + datasetId + "/students/" + studentId + "/records",
    method: "get",
  });
}
export function saveStudentQualitySemester(datasetId: number, studentId: number, semester: string, ratings: Record<string, string>) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + datasetId + "/students/" + studentId + "/semester/" + semester,
    method: "post",
    data: ratings,
  });
}
export function getStudentQualitySummary(datasetId: number, studentId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + datasetId + "/students/" + studentId + "/summary",
    method: "get",
  });
}
export function importQualityWorkbook(file: File) {
  const formData = new FormData(); formData.append("file", file);
  return request({
    url: "/api/v1/quality-independent/import",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}
export function exportQualityWorkbook(clazzId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/export",
    method: "get",
    responseType: "arraybuffer",
  });
}
export function getQualityFinal(clazzId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final",
    method: "get",
  });
}
export function getQualityMissingReviews(clazzId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/missing-reviews",
    method: "get",
  });
}
export function updateQualityMissingReview(clazzId: number, studentId: number, semester: string, data: { status: string; remark?: string }) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/missing-reviews/" + studentId + "/" + semester,
    method: "put",
    data,
  });
}
export function updateQualityFinalRatios(clazzId: number, data: { aRatio: number; bRatio: number; cRatio: number }) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/ratios",
    method: "put",
    data,
  });
}
export function exportQualityFinal(clazzId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/export",
    method: "get",
    responseType: "arraybuffer",
  });
}
export function generateQualityFinal(clazzId: number) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/generate",
    method: "post",
  });
}
export function lockQualityFinal(clazzId: number, locked: boolean) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/lock",
    method: "post",
    params: { locked },
  });
}
export function updateQualityFinalLevel(clazzId: number, studentId: number, dimension: string, level: string) {
  return request({
    url: "/api/v1/quality-independent/datasets/" + clazzId + "/final/level",
    method: "put",
    data: { studentId, dimension, level },
  });
}
