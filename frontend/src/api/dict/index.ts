import request from "@/utils/request";
import { AxiosPromise } from "axios";

/** 性别、班级类型等业务表单使用的字典选项。 */
export function getDictOptions(typeCode: string): AxiosPromise<OptionType[]> {
  return request({
    url: "/api/v1/dict/" + typeCode + "/options",
    method: "get",
  });
}
