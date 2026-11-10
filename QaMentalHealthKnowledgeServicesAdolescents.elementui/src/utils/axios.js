import { useCommonStore } from "@/store";
import axios from "axios";
import { ElMessage } from "element-plus";


// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 120000,
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从 store 获取 token 并添加到请求头
    const commonStore = useCommonStore();
    const token = commonStore.token;

    if (token) {
      config.headers["Authorization"] = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    const { success, code, msg } = res;
    if (success == true) {
      return res;
    } else {
      if (code == 500) {
        ElMessage({
          message: msg,
          type: "error",
          duration: 2 * 1000,
        });
        return res;
      } else if (code == 401) {
        ElMessage({
          message: "用户信息已过期",
          type: "error",
          duration: 2 * 1000,
        });
        return Promise.reject(new Error("用户信息已过期"));
      } else {
        return Promise.reject(new Error(msg || "Error"));
      }
    }
  },
  (error) => {
    // 处理网络错误
    if (error.message.indexOf("Network Error") !== -1) {
      ElMessage({
        message: "后端服务没有启动",
        type: "error",
        duration: 2 * 1000,
      });
      return Promise.reject(new Error("后端服务没有启动"));
    }

 
    ElMessage({
      message: error.message,
      type: "error",
      duration: 2 * 1000,
    });
    return Promise.reject(error);
  }
);

export default service;
