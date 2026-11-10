import { ElMessageBox } from "element-plus";
import service from "../utils/axios";

// 通用的post请求
export async function post(url, data) {
  let response = {
    code: "",
    data: "",
    msg: "",
    success: true,
  };

  try {
    const res = await service({
      url: url,
      method: "post",
      data: data || {},
    });

    if (res.data === undefined && res.data !== 0) {
      res.data = {};
    }
    response = res;
  } catch (ex) {
    response.success = false;
    response.msg = ex;
    response.data = {};
  }

  return response;
}

// 带确认框的删除请求
export async function postDelete(url, data) {
  try {
    await ElMessageBox.confirm("你确定要删除吗?", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    return await post(url, data);
  } catch {
    return { success: false, msg: "用户取消操作" };
  }
}

// 更新单个字段的请求
export async function postSingleUpdate(getUrl, saveUrl, id, content, obj) {
  try {
    await ElMessageBox.confirm(content, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const { data } = await post(getUrl, { id: id });
    const updatedData = { ...data, ...obj };
    return await post(saveUrl, updatedData);
  } catch {
    return { success: false, msg: "用户取消操作" };
  }
}

// 文件上传请求
export async function postUpload(url, body) {
  let response = {
    code: "",
    data: "",
    msg: "",
    success: true,
  };

  try {
    const res = await service({
      url: url,
      method: "post",
      headers: {
        "Content-Type": "multipart/form-data",
      },
      data: body || {},
    });
    response = res;
  } catch (ex) {
    response.success = false;
    response.msg = ex;
    response.data = {};
  }

  return response;
}

export default {
  post,
  postDelete,
  postSingleUpdate,
  postUpload,
};
