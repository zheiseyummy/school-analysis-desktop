import request from "@/utils/request";

export function getLocalScoreDatasets() {
  return request({
    url: "/api/v1/local-score-analysis/datasets",
    method: "get",
  });
}

export function previewLocalScoreImport(formData: FormData) {
  return request({
    url: "/api/v1/local-score-analysis/preview",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

export function confirmLocalScoreImport(token: string) {
  return request({
    url: "/api/v1/local-score-analysis/confirm",
    method: "post",
    data: { token },
  });
}

export function getLocalScoreAnalysis(datasetId: number) {
  return request({
    url: "/api/v1/local-score-analysis/datasets/" + datasetId + "/analysis",
    method: "get",
  });
}

export function getLocalScoreExamStudents(datasetId: number, examId: number) {
  return request({
    url: "/api/v1/local-score-analysis/datasets/" + datasetId + "/exams/" + examId + "/students",
    method: "get",
  });
}
