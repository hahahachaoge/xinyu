<template>
    <!-- 文件查看按钮和弹窗 -->
    <span class="file-link-container">
        <el-button type="primary" link @click="showImport">
            <el-icon>
                <Document />
            </el-icon>
            文件查看
        </el-button>

        <el-dialog v-model="show" title="文件查看" width="70%" :lock-scroll="true" class="file-dialog"
            :before-close="handleClose" :z-index="9999" append-to-body destroy-on-close>
            <el-table :data="files" style="width: 100%">
                <!-- 文件名称列 -->
                <el-table-column prop="name" label="文件名称" align="center">
                    <template #default="{ row }">
                        <el-tag>{{ row.name }}</el-tag>
                    </template>
                </el-table-column>

                <!-- 文件类型列 -->
                <el-table-column prop="type" label="文件类型" align="center">
                    <template #default="{ row }">
                        <el-tag type="success">{{ row.type }}</el-tag>
                    </template>
                </el-table-column>

                <!-- 操作列 -->
                <el-table-column label="操作" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="handleDownload(row)">
                            <el-icon>
                                <Download />
                            </el-icon>
                            下载
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
    </span>
</template>

<script setup>
import { Document, Download } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { ref, watch } from 'vue';

// 定义props
const props = defineProps({
    url: {
        type: String,
        default: ''
    }
})

// 响应式数据
const show = ref(false)
const files = ref([])

// 解析URL字符串为文件数组
const parseUrls = window.parseUrls

// 监听url变化
watch(() => props.url, (newUrl) => {
    files.value = parseUrls(newUrl)
}, { immediate: true })

// 显示文件查看弹窗
const showImport = () => {
    if (!props.url) {
        ElMessage.warning('请先完成资源上传!')
        return
    }
    show.value = true
}

// 关闭弹窗
const handleClose = () => {
    show.value = false
}

// 处理文件下载
const handleDownload = (file) => {
    window.open(file.url, '_blank')
}
</script>

<style scoped>
/* 文件链接容器样式 */
.file-link-container {
    margin: 0 10px;
}

/* 文件查看弹窗样式 */
.file-dialog {
    height: 800px;
}

/* 表格内标签样式 */
:deep(.el-tag) {
    margin: 2px;
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
</style>