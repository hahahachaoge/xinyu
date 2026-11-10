<template>
    <el-button class="margin-left-xs" type="success" size="mini" @click="handleExport" :loading="exporting">
        <el-icon>
            <Download />
        </el-icon>导 出
    </el-button>
</template>

<script setup>
import { Download } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { ref } from 'vue';
import service from '@/utils/axios';
const exporting = ref(false);

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
const handleExport = async () => {
    if (exporting.value) return;
    exporting.value = true;
    try {
        const response = await service.get(props.exportUrl, {
            params: { query: JSON.stringify(props.where) }, responseType: 'blob'
        });
        if (response.headers['content-type']?.includes('application/json')) {
            const result = JSON.parse(await response.data.text());
            throw new Error(result.msg || '导出失败');
        }
        const url = URL.createObjectURL(response.data);
        const link = document.createElement('a');
        link.href = url; link.download = '数据导出.xls'; link.style.display = 'none';
        document.body.appendChild(link); link.click(); link.remove();
        setTimeout(() => URL.revokeObjectURL(url), 1000);
        ElMessage.success('导出成功');
    } catch (error) {
        ElMessage.error(error.message || '导出失败');
    } finally { exporting.value = false; }
};

</script>

<style scoped></style>
