<template>
    <div class="upload-video-container">
        <!-- 上传区域 -->
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :file-list="fileList" :on-success="handleUploadSuccess" :on-remove="handleRemove"
            :before-upload="beforeUpload" :on-exceed="handleExceed" :on-error="handleError" :limit="limit"
            :multiple="multiple" :accept="accept" class="video-uploader">
            <!-- 上传按钮 -->
            <div v-if="videoList.length < limit" class="upload-trigger">
                <el-icon>
                    <Upload />
                </el-icon>
                <div class="upload-text">点击上传视频</div>
            </div>

            <!-- 提示信息 -->
            <template #tip>
                <div class="el-upload__tip">
                    <span>支持格式: {{ accept || '.mp4, .avi, .mov, .wmv, .flv, .mkv' }}</span>
                    <span class="size-tip">文件大小不超过{{ maxSize }}MB</span>
                </div>
            </template>
        </el-upload>

        <!-- 视频列表展示 -->
        <div class="video-list" v-if="videoList.length > 0">
            <div v-for="(item, index) in videoList" :key="index" class="video-item">
                <!-- 视频封面 -->
                <div class="video-cover" @click="previewVideo(item)">
                    <video v-if="item.url" class="cover-image" :src="item.url"></video>
                    <img v-else src="@/assets/视频封面.png" class="cover-image" alt="视频封面">
                    <div class="play-icon">
                        <el-icon>
                            <VideoPlay />
                        </el-icon>
                    </div>
                </div>

                <!-- 视频信息 -->
                <div class="video-info">
                    <div class="video-name">{{ item.name || '视频文件' }}</div>
                    <div class="video-actions">
                        <el-button type="primary" link @click="previewVideo(item)">
                            <el-icon>
                                <View />
                            </el-icon> 预览
                        </el-button>
                        <el-button type="primary" link @click="removeVideo(index)">
                            <el-icon>
                                <Delete />
                            </el-icon> 删除
                        </el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 视频预览对话框 -->
        <el-dialog v-model="previewVisible" title="视频预览" width="800px" class="preview-dialog" append-to-body>
            <div class="video-preview-wrapper">
                <video v-if="previewUrl" controls autoplay class="preview-video" :src="previewUrl"></video>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { Delete, Upload, VideoPlay, View } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import { post } from '@/api/http';
import { useCommonStore } from '@/store';
const commonStore = useCommonStore();
const uploadHeaders = computed(() => ({ Authorization: commonStore.token || '' }));
// 定义props
const props = defineProps({
    modelValue: {
        type: [Number, String],
        default: ''
    },
    limit: {
        type: Number,
        default: 2,
    },
    accept: {
        type: String,
        default: '.mp4, .avi, .mov, .wmv, .flv, .mkv'
    },
    multiple: {
        type: Boolean,
        default: false
    },
    maxSize: {
        type: Number,
        default: 1024 // 默认1GB
    }
})

// 定义emit
const emit = defineEmits(['update:modelValue', 'change'])

// 响应式数据
const uploadUrl = ref(import.meta.env.VITE_API_BASE_URL + "/File/batchUpload")
const videoList = ref([]) // 使用videoList替代fileList
const fileList = ref([]) // 用于同步el-upload的文件列表
const previewVisible = ref(false)
const previewUrl = ref("")
const uploadLoading = ref(false)

// 初始化视频列表
onMounted(() => {
    if (props.modelValue) {
        const urls = props.modelValue.split(",")
        videoList.value = urls.map(x => {
            return {
                url: x,
                name: getFileNameByPath(x),
                status: "success"
            }
        })
        // 同步到fileList
        fileList.value = videoList.value
    }
})

// 方法定义
const beforeUpload = (file) => {
    // 验证文件类型
    const isValidType = props.accept.split(',').some(type => {
        return file.name.toLowerCase().endsWith(type.trim())
    })

    if (!isValidType) {
        ElMessage.error('请上传正确的视频格式!')
        return false
    }

    // 验证文件大小 (MB)
    const isLessThanMaxSize = file.size / 1024 / 1024 < props.maxSize
    if (!isLessThanMaxSize) {
        ElMessage.error(`视频大小不能超过 ${props.maxSize}MB!`)
        return false
    }

    uploadLoading.value = true
    return true
}

const previewVideo = (item) => {
    if (!item.url) {
        ElMessage.warning('视频地址不存在')
        return
    }
    previewUrl.value = item.url
    previewVisible.value = true
}

const handleExceed = () => {
    ElMessage.warning(`最多只能上传 ${props.limit} 个视频!`)
}

const handleError = () => {
    uploadLoading.value = false
    ElMessage.error('视频上传失败，请重试!')
}

const removeVideo = (index) => {
    videoList.value.splice(index, 1)
    fileList.value = [...videoList.value] // 同步到fileList
    updateValue()
}

const updateValue = () => {
    const url = videoList.value.length > 0 ? videoList.value.map(x => x.url).join(",") : ""
    emit('update:modelValue', url)
    emit('change', url)
}

const handleUploadSuccess = (response, file) => {
    uploadLoading.value = false

    if (response && response.success && response.data && response.data.length > 0) {
        // 添加到视频列表
        const newVideo = {
            name: file.name,
            url: response.data[0].url,
            status: "success"
        }
        videoList.value.push(newVideo)
        fileList.value = [...videoList.value] // 同步到fileList

        updateValue()
        ElMessage.success('视频上传成功!')
    } else {
        ElMessage.error('视频上传失败，请重试!')
    }
}

const handleRemove = (file) => {
    const index = videoList.value.findIndex(item => item.name === file.name)
    if (index !== -1) {
        videoList.value.splice(index, 1)
        fileList.value = [...videoList.value] // 同步到fileList
        updateValue()
    }
}
</script>

<style scoped>
/* 上传容器 */
.upload-video-container {
    width: 100%;
    margin-bottom: 20px;
}

/* 上传区域样式 */
.video-uploader {
    width: 100%;
}

/* 上传触发器样式 */
.upload-trigger {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 180px;
    height: 120px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    background-color: #fafafa;
    transition: border-color 0.3s;
}

.upload-trigger:hover {
    border-color: #409EFF;
}

.upload-trigger .el-icon {
    font-size: 28px;
    color: #8c939d;
    margin-bottom: 10px;
}

.upload-text {
    font-size: 14px;
    color: #606266;
}

/* 提示信息样式 */
.el-upload__tip {
    line-height: 1.5;
    margin-top: 10px;
    color: #606266;
    font-size: 12px;
}

.size-tip {
    margin-left: 10px;
    color: #ED5F18;
}

/* 视频列表样式 */
.video-list {
    margin-top: 20px;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
}

.video-item {
    display: flex;
    flex-direction: column;
    padding: 10px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    background-color: #f5f7fa;
    transition: all 0.3s;
}

.video-item:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 视频封面样式 */
.video-cover {
    position: relative;
    width: 100%;
    height: 180px;
    margin-bottom: 10px;
    border-radius: 4px;
    overflow: hidden;
    cursor: pointer;
}

.cover-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.play-icon {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.3);
    transition: background-color 0.3s;
}

.play-icon .el-icon {
    font-size: 40px;
    color: #fff;
    opacity: 0.8;
    transition: all 0.3s;
}

.video-cover:hover .play-icon {
    background-color: rgba(0, 0, 0, 0.5);
}

.video-cover:hover .play-icon .el-icon {
    transform: scale(1.1);
    opacity: 1;
}

/* 视频信息样式 */
.video-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.video-name {
    font-size: 14px;
    color: #303133;
    margin-bottom: 10px;
    word-break: break-all;
    line-height: 1.4;
}

.video-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.video-actions .el-button {
    padding: 0;
}

/* 预览对话框样式 */
.preview-dialog :deep(.el-dialog__body) {
    padding: 0;
}

.video-preview-wrapper {
    background-color: #000;
    padding: 10px;
    border-radius: 4px;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
}

.preview-video {
    width: 100%;
    max-height: 600px;
    object-fit: contain;
    background-color: #000;
}
</style>