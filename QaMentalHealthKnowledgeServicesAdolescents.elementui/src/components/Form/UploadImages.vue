<template>
    <div class="upload-files-wrap">
        <!-- 图片上传组件 -->
        <el-upload :action="uploadUrl" list-type="picture-card" :show-file-list="true" :on-success="handleUploadSuccess"
            :on-remove="handleRemove" :on-preview="handlePreview" :file-list="fileList"
            accept=".jpg,.png,.jpeg,.jfif,.webp" :limit="limit" :multiple="limit > 1">
            <el-icon>
                <Plus />
            </el-icon>
        </el-upload>
        <!-- 图片预览 -->
        <el-image-viewer v-if="previewVisible" :url-list="previewList" :initial-index="previewIndex" teleported
            @close="previewVisible = false" />
    </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue';
import { ref, watch } from 'vue';
import { post } from '@/api/http';
// 定义props
const props = defineProps({
    modelValue: {
        type: [Number, String],
        default: ''
    },
    limit: {
        type: Number,
        default: 1,
    },
})

// 定义emit
const emit = defineEmits(['update:modelValue'])

// 响应式数据
const uploadUrl = import.meta.env.VITE_API_BASE_URL + "/File/batchUpload"
const fileList = ref([])
const previewVisible = ref(false)
const previewList = ref([])
const previewIndex = ref(0)

// 图片预览
const handlePreview = (file) => {
    previewList.value = fileList.value.map(f => f.url)
    previewIndex.value = fileList.value.findIndex(f => f.url === file.url) || 0
    previewVisible.value = true
}

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
    if (newVal) {
        fileList.value = newVal.split(",").map(x => {
            return {
                url: x,
                name: getFileNameByPath(x),
                status: "success"
            }
        })
    }
}, { immediate: true })

// 文件列表转换方法
const FileListConvert = (files) => {
    let list = []
    if (Array.isArray(files)) {
        files.filter(x => x.status == "success").forEach((item) => {
            if (item.response != null && item.response.data) {
                // batchUpload 可能返回多个文件，逐个添加
                item.response.data.forEach(f => {
                    list.push({ name: f.url, url: f.url })
                })
            } else if (item.url) {
                list.push(item)
            }
        })
    }
    return list
}

// 同步 emit url
const emitUrls = (files) => {
    let fs = FileListConvert(files)
    let url = fs.length > 0 ? fs.map(x => x.url).join(",") : ""
    emit('update:modelValue', url)
}

// 文件上传成功处理：等所有文件都上传完再 emit
const handleUploadSuccess = (response, file, currentFiles) => {
    // 检查是否所有文件都已上传完成
    const allDone = currentFiles.every(f => f.status === 'success')
    if (allDone) {
        emitUrls(currentFiles)
    }
}

// 文件移除处理
const handleRemove = (file, currentFiles) => {
    emitUrls(currentFiles)
}
</script>

<style scoped>
/* 上传组件容器样式 */
.upload-files-wrap {
    width: 100%;
}

/* 上传按钮样式 */
:deep(.el-upload--picture-card) {
    background-color: transparent !important;
}

/* 上传图标样式 */
:deep(.el-upload--picture-card .el-icon) {
    font-size: 28px;
    color: #8c939d;
}
</style>