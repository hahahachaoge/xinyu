window.parseUrls = function parseUrls(urlString) {
  if (!urlString) return [];
  //如果不存在,则在最后面追加1个,
  urlString = urlString + "";

  if (!urlString.endsWith(",")) {
    urlString += ",";
  }
  return urlString
    .split(",")
    .filter((url) => url.trim() !== "")
    .map((url) => {
      const pathParts = url.split("/");
      const fileName = pathParts[pathParts.length - 1];
      const fileExtension = fileName.split(".").pop().toLowerCase();

      return {
        name: fileName,
        type: fileExtension,
        url: url.trim(),
      };
    });
};

/**
 * 得到完整的时间格式 年-月-日 时分秒
 */
window.formatYMDHMS = function formatYMDHMS(val) {
  if (!val) return "";
  let date = new Date(val);
  if (!date) return "";

  var y = date.getFullYear();
  var m = (date.getMonth() + 1).toString();
  m = parseInt(m) < 10 ? "0" + m : m;
  var d = date.getDate().toString();
  d = parseInt(d) < 10 ? "0" + d : d;
  var h = date.getHours().toString();
  h = parseInt(h) < 10 ? "0" + h : h;
  var minute = date.getMinutes().toString();
  minute = parseInt(minute) < 10 ? "0" + minute : minute;
  var second = date.getSeconds().toString();
  second = parseInt(second) < 10 ? "0" + second : second;
  return y + "-" + m + "-" + d + " " + h + ":" + minute + ":" + second;
};
/**
 * 得到完整的时间格式 年-月-日
 */
window.formatYMD = function formatYMD(val) {
  if (!val) return "";
  let date = new Date(val);
  if (!date) return "";

  var y = date.getFullYear();
  var m = (date.getMonth() + 1).toString();
  m = parseInt(m) < 10 ? "0" + m : m;
  var d = date.getDate().toString();
  d = parseInt(d) < 10 ? "0" + d : d;
  var h = date.getHours().toString();
  h = parseInt(h) < 10 ? "0" + h : h;
  var minute = date.getMinutes().toString();
  minute = parseInt(minute) < 10 ? "0" + minute : minute;
  var second = date.getSeconds().toString();
  second = parseInt(second) < 10 ? "0" + second : second;
  return y + "-" + m + "-" + d + " ";
};

/**
 * 得到时分秒格式
 */
window.formatHMS = function formatHMS(val) {
  if (!val) return "";
  let date = new Date(val);
  if (!date) return "";

  var y = date.getFullYear();
  var m = (date.getMonth() + 1).toString();
  m = parseInt(m) < 10 ? "0" + m : m;
  var d = date.getDate().toString();
  d = parseInt(d) < 10 ? "0" + d : d;
  var h = date.getHours().toString();
  h = parseInt(h) < 10 ? "0" + h : h;
  var minute = date.getMinutes().toString();
  minute = parseInt(minute) < 10 ? "0" + minute : minute;
  var second = date.getSeconds().toString();
  second = parseInt(second) < 10 ? "0" + second : second;
  return h + ":" + minute + ":" + second;
};
/**
 * 获取路径中的文件名称
 */
window.getFileNameByPath = function getFileNameByPath(path) {
  if (!path) {
    return "";
  }
  var pos1 = path.lastIndexOf("/");
  var pos2 = path.lastIndexOf("\\");
  var pos = Math.max(pos1, pos2);
  if (pos < 0) {
    return path;
  } else {
    return path.substring(pos + 1);
  }
};
/**
 * 获取路径中的文件格式
 */
window.getFileTypeByPath = function getFileTypeByPath(path) {
  if (!path) {
    return "";
  }
  var pos = path.lastIndexOf(".");
  if (pos < 0) {
    return "";
  } else {
    return path.substring(pos + 1);
  }
};
/**
 * 将url数组转换为对象数组
 */
window.fullConvertUrlArray = function fullConvertUrlArray(url) {
  if (!url) return [];
  return url
    .split(",")
    .filter((url) => url.trim() !== "")
    .map((url) => {
      const pathParts = url.split("/");
      const fileName = pathParts[pathParts.length - 1];
      const fileExtension = fileName.split(".").pop().toLowerCase();

      return {
        name: fileName,
        type: fileExtension,
        url: url.trim(),
      };
    });
};
/**
 * 保留2位小数
 */
window.toFixed2 = function toFixed2(value) {
  if (value) {
    return value.toFixed(2);
  }
  return "0.00";
};

/**
 * 保留3位小数
 */
window.toFixed3 = function toFixed3(value) {
  if (value) {
    return value.toFixed(3);
  }
  return "0.000";
};
/**
 * 手机号格式化
 */
window.phoneFormat = function phoneFormat(value) {
  if (value) {
    return value.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
  }
  return "";
};
/**
 * 姓名格式化
 */
window.nameFormat = function nameFormat(value) {
  if (value) {
    if (value.length <= 1) {
      return value;
    }
    const firstName = value.charAt(0);
    const restName = value.substring(1);
    const stars = "*".repeat(restName.length);
    return firstName + stars;
  }
  return "";
};

/**邮箱格式化 */
window.emailFormat = function emailFormat(value) {
  if (value) {
    return value.replace(/(\w{3})[\w.-]+@([\w.]+\w)/, "$1****$2");
  }
  return "";
};
/**
 * 替换html标签
 */
window.replaceHtml = function replaceHtml(value) {
  if (value) {
    return value.replace(/<[^>]+>|&[^>]+;/g, "").trim();
  }
};

/**
 * 根据视频的路径获取视频的时长
 */
window.getVideoDuration = function getVideoDuration(url) {
  if (!url) {
    return 0;
  }
  return new Promise(function (resolve, reject) {
    let video = document.createElement("video");
    video.src = url;
    // 监听元数据加载完成的事件
    video.addEventListener("loadedmetadata", function () {
      // 获取视频时长
      const duration = video.duration;
      resolve(duration);
    });
  });
};
/**
 * 根据视频的路径获取视频的第一帧并且生成1个FormData
 * @param {string} url - 视频的URL路径
 * @param {string} fileName - 生成的图片文件名（可选，默认为'video-thumbnail.jpg'）
 * @returns {Promise<FormData>} 返回包含视频第一帧图片的FormData对象
 */
window.getVideoFirstFrame = function getVideoFirstFrame(
  url,
  fileName = "video-thumbnail.jpg"
) {
  if (!url) {
    return Promise.reject(new Error("视频URL不能为空"));
  }

  return new Promise(function (resolve, reject) {
    try {
      // 创建video元素
      const video = document.createElement("video");
      video.crossOrigin = "anonymous"; // 处理跨域问题
      video.src = url;
      video.currentTime = 0.1; // 设置到0.1秒，避免第一帧可能是黑屏

      // 监听视频元数据加载完成
      video.addEventListener("loadeddata", function () {
        try {
          // 创建canvas元素
          const canvas = document.createElement("canvas");
          canvas.width = video.videoWidth;
          canvas.height = video.videoHeight;

          // 获取canvas上下文并绘制视频帧
          const ctx = canvas.getContext("2d");
          ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

          // 将canvas转换为Blob
          canvas.toBlob(
            function (blob) {
              if (blob) {
                // 创建FormData并添加文件
                const formData = new FormData();
                const file = new File([blob], fileName, { type: "image/jpeg" });
                formData.append("file", file);

                // 清理资源
                video.remove();
                canvas.remove();

                resolve(formData);
              } else {
                reject(new Error("生成视频缩略图失败"));
              }
            },
            "image/jpeg",
            0.9
          ); // JPEG格式，质量0.9
        } catch (error) {
          reject(new Error("绘制视频帧失败: " + error.message));
        }
      });

      // 监听加载错误
      video.addEventListener("error", function () {
        reject(new Error("视频加载失败，请检查URL是否正确"));
      });

      // 开始加载视频
      video.load();
    } catch (error) {
      reject(new Error("创建视频元素失败: " + error.message));
    }
  });
};
