<template>
    <!-- 富文本按钮组件 -->
    <div class="rich-button">
        <!-- 点击按钮 -->
        <el-button type="primary" link @click="showDialog">
            <el-icon>
                <View />
            </el-icon>
            查看详情
        </el-button>

        <!-- 富文本内容弹窗 -->
        <el-dialog v-model="dialogVisible" title="内容" width="50%" :lock-scroll="true" :before-close="handleClose"
            :z-index="9999" append-to-body destroy-on-close>
            <!-- 富文本内容展示区域 -->
            <div class="rich-content" v-html="content"></div>
        </el-dialog>
    </div>
</template>

<script setup>
import { View } from '@element-plus/icons-vue';
import { ref } from 'vue';

// 定义组件属性
const props = defineProps({
    content: {
        type: String,
        default: ''
    }
})

// 弹窗显示状态
const dialogVisible = ref(false)

// 显示弹窗
const showDialog = () => {
    dialogVisible.value = true
}

// 关闭弹窗
const handleClose = () => {
    dialogVisible.value = false
}
</script>

<style scoped>
/* 富文本按钮容器 */
.rich-button {
    display: inline-block;
    position: relative;
}

/* 富文本内容样式 */
.rich-content {
    max-height: 500px;
    overflow-y: auto;
    padding: 10px;
    line-height: 1.5;
}

/* 确保弹窗在最上层 */
:deep(.el-dialog) {
    position: relative;
    z-index: 9999;
}

:deep(.el-dialog__wrapper) {
    z-index: 9999;
}

:deep(.v-modal) {
    z-index: 9998;
}

/* 富文本内容中的图片样式 */
.rich-content img {
    max-width: 100%;
    height: auto;
}

/* 富文本内容中的表格样式 */
.rich-content table {
    width: 100%;
    border-collapse: collapse;
    margin: 10px 0;
}

.rich-content th,
.rich-content td {
    border: 1px solid #dcdfe6;
    padding: 8px;
}

/* 富文本内容中的链接样式 */
.rich-content a {
    color: #409eff;
    text-decoration: none;
}

.rich-content a:hover {
    text-decoration: underline;
}
</style>