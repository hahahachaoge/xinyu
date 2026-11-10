<template>
    <div class="upload-files-wrap">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" list-type="text" :show-file-list="true" :on-success="handleUploadSuccess"
            :on-remove="handleRemove" :file-list="fileList" :limit="limit" :multiple="true" :accept="accept">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">请上传资源文件<span style="padding-left:10px;color:rgb(237, 95, 24)"><span
                        v-if="accept">
                        最大可上传大小不超过1GB,支持:{{ accept }}类型的文件</span></span>
            </div>
        </el-upload>
    </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
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
        default: 1,
    },
    accept: {
        type: String,
        default: ''
    }
});

// 定义emit
const emit = defineEmits(['update:modelValue']);

// 响应式数据
const uploadUrl = import.meta.env.VITE_API_BASE_URL + "/File/batchUpload";
const fileList = ref([]);

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
    if (newVal) {
        fileList.value = newVal.split(",").map(x => {
            return {
                url: x,
                name: getFileNameByPath(x),
                status: "success"
            }
        });
    }
}, { immediate: true });

// 文件列表转换方法
const FileListConvert = (files) => {
    let list = [];
    if (Array.isArray(files)) {
        files.filter(x => x.status == "success").forEach((item) => {
            if (item.response != null) {
                list = [...list, { name: "", url: item.response.data[0].url }];
            } else {
                list = [...list, item];
            }
        });
    }
    return list;
};

// 文件上传成功处理
const handleUploadSuccess = (response, file, fileList) => {
    let fs = FileListConvert(fileList);
    let url = fs.length > 0 ? fs.map(x => x.url).join(",") : "";
    emit('update:modelValue', url);
};

// 文件移除处理
const handleRemove = (file, fileList) => {
    let fs = FileListConvert(fileList);
    let url = fs.length > 0 ? fs.map(x => x.url).join(",") : "";
    emit('update:modelValue', url);
};
</script>

<style scoped>
/* 上传组件容器样式 */
.upload-files-wrap {
    width: 100%;
}

.el-upload__tip {
    margin-left: 10px;
}
</style>