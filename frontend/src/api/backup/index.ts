import request from "@/utils/request";

export function listLocalBackups() {
  return request({
    url: "/api/v1/local-backup",
    method: "get",
  });
}

export function createLocalBackup() {
  return request({
    url: "/api/v1/local-backup",
    method: "post",
  });
}

export function restoreLocalBackup(fileName: string) {
  return request({
    url: "/api/v1/local-backup/restore",
    method: "post",
    params: { fileName },
  });
}
