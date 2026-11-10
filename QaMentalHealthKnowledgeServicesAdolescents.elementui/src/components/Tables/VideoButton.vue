<template>
    <!-- 视频链接 -->
    <span class="video-link" @click="handleOpen">
        <el-icon>
            <VideoPlay />
        </el-icon>
        查看视频
    </span>

    <!-- 视频弹窗 -->
    <el-dialog v-model="dialogVisible" title="视频播放" width="80%" :before-close="handleClose" class="video-dialog"
        append-to-body>
        <!-- 视频列表容器 -->
        <el-tabs v-model="activeTab" class="video-tabs" @tab-change="handleTabChange">
            <el-tab-pane v-for="(videoUrl, index) in videoList" :key="index" :label="'视频 ' + (index + 1)" :name="index">
                <div class="video-item">
                    <video :ref="el => { if (el) videoRefs[index] = el }" :src="videoUrl" controls class="video-player"
                        preload="metadata"></video>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-dialog>
</template>

<script setup>
import { VideoPlay } from '@element-plus/icons-vue';
import { onMounted, ref, watch } from 'vue';

// 接收视频URL字符串
const props = defineProps({
    url: {
        type: String,
        required: true
    }
})

// 弹窗显示状态
const dialogVisible = ref(false)
// 视频列表
const videoList = ref([])
// 视频引用数组
const videoRefs = ref([])
// 当前激活的tab
const activeTab = ref(0)

// 处理打开弹窗
const handleOpen = () => {
    dialogVisible.value = true
}

// 处理关闭弹窗
const handleClose = () => {
    // 停止所有视频播放
    stopAllVideos()
    dialogVisible.value = false
}

// 处理标签页切换
const handleTabChange = (tabIndex) => {
    // 停止所有视频播放
    stopAllVideos()
}

// 停止所有视频播放
const stopAllVideos = () => {
    videoRefs.value.forEach(video => {
        if (video) {
            video.pause()
            video.currentTime = 0
        }
    })
}

// 监听URL变化，处理视频列表
watch(() => props.url, (newUrl) => {
    if (newUrl) {
        // 使用逗号分割URL字符串
        videoList.value = newUrl.split(',')
    }
}, { immediate: true })

// 组件挂载时初始化视频列表
onMounted(() => {
    if (props.url) {
        videoList.value = props.url.split(',')
    }
})
</script>

<style scoped>
/* 视频链接样式 */
.video-link {
    color: #409EFF;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    transition: color 0.3s;
}

.video-link:hover {
    color: #66b1ff;
}

/* 视频弹窗样式 */
.video-dialog :deep(.el-dialog__body) {
    padding: 20px;
}

/* 视频tabs样式 */
.video-tabs {
    width: 100%;
}

/* 单个视频项样式 */
.video-item {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    background: #f5f7fa;
    padding: 20px;
    border-radius: 8px;
}

/* 视频播放器样式 */
.video-player {
    width: 100%;
    max-width: 800px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    background: #000;
}

/* 弹窗标题样式 */
.video-dialog :deep(.el-dialog__header) {
    margin-right: 0;
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
}

.video-dialog :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
}
</style>