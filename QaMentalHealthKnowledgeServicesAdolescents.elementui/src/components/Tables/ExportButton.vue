<template>
    <el-button class="margin-left-xs" type="success" size="mini" @click="handleExport">
        <el-icon>
            <Download />
        </el-icon>导 出
    </el-button>
</template>

<script setup>
import { Download } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 定义props
const props = defineProps({
    exportUrl: {
        type: String,
        required: true
    },
    where: {
        type: Object,
        default: () => ({})
    }
});

// 处理导出
const handleExport = () => {
    try {

        const query = JSON.stringify(props.where)
        // 构建完整的导出URL
        const exportUrl = `${import.meta.env.VITE_API_BASE_URL}${props.exportUrl}?query=${query}`;

        // 创建隐藏的下载链接
        const link = document.createElement('a');
        link.href = exportUrl;
        link.download = '';
        link.style.display = 'none';

        // 添加到DOM并触发下载
        document.body.appendChild(link);
        link.click();

        // 清理
        document.body.removeChild(link);

        ElMessage.success('导出成功');
    } catch (error) {
        console.error('导出失败:', error);
        ElMessage.error('导出失败');
    }
};
</script>

<style scoped></style>
