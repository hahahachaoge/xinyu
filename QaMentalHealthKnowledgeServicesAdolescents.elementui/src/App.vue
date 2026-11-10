<template>
  <el-config-provider :locale="zhCn">
    <div class="app-container">
      <!-- 路由视图 -->
      <router-view></router-view>
    </div>
  </el-config-provider>
</template>

<script>
import zhCn from "element-plus/dist/locale/zh-cn.mjs";

export default {
  name: 'App',
  setup() {
    return { zhCn };
  },
}
</script>

<style  lang="scss">


:root {
  --content-width: 95%;
}

/* 应用容器样式 */
.app-container {
  background-color: #f5f7fa;
  min-height: 100vh;
  width: 100%;
}

/* 面包屑导航样式 */
.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-bottom: 20px;

  &-label {
    font-weight: bold;
    margin-right: 10px;
  }
}


.box-card {
  margin-bottom: 20px;
}
</style>
