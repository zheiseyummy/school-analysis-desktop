import axios, { AxiosResponse } from "axios";

// 过渡阶段通过本机后端访问数据；单用户界面不再发送登录凭据。
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
});

service.interceptors.response.use(
  (response: AxiosResponse) => {
    // Excel/PDF 导出保留响应头，供调用页面取得文件名。
    if (response.data instanceof ArrayBuffer || response.data instanceof Blob) {
      return response;
    }
    const { code, msg } = response.data;
    if (code === "00000") {
      return response.data;
    }
    ElMessage.error(msg || "操作失败");
    return Promise.reject(new Error(msg || "操作失败"));
  },
  (error: unknown) => {
    const message = axios.isAxiosError(error)
      ? error.response?.data?.msg ||
        (error.code === "ECONNABORTED"
          ? "本地服务响应超时，请稍后重试"
          : error.response
            ? "本地服务请求失败"
            : "无法连接本地服务，请确认服务已启动")
      : "请求失败";
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default service;
