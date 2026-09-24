import request from "@/utils/request";
import { AxiosPromise } from "axios";
import { ScoreRule } from "./types";

export function getScoreRules(): AxiosPromise<ScoreRule[]> {
  return request({
    url: "/api/v1/score-rules",
    method: "get",
  });
}

export function addScoreRule(data: ScoreRule): AxiosPromise<ScoreRule> {
  return request({
    url: "/api/v1/score-rules",
    method: "post",
    data,
  });
}

export function updateScoreRule(id: number, data: ScoreRule): AxiosPromise<ScoreRule> {
  return request({
    url: "/api/v1/score-rules/" + id,
    method: "put",
    data,
  });
}

export function deleteScoreRule(id: number) {
  return request({
    url: "/api/v1/score-rules/" + id,
    method: "delete",
  });
}
