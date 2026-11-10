<template>
    <div>
        <div id="aiEditor" :style="{ height: height }"></div>
    </div>
</template>

<script setup>
import { AiEditor, CustomAiModel } from "aieditor";
import "aieditor/dist/style.css";
import { nextTick, onMounted, ref, watch } from 'vue';

import { post, postUpload } from '@/api/http';

// 使用统一请求层接收完整JSON，避免编辑器HTTP客户端将网络分片当作完整响应。
class BackendEditorAiModel extends CustomAiModel {
    createAiClient(url, listener) {
        let stopped = false;
        const client = {
            start: async (payload) => {
                listener.onStart(client);
                try {
                    const result = await post(url, JSON.parse(payload));
                    if (!stopped && result.success) {
                        listener.onMessage({ role: 'assistant', content: result.data.content, index: 0, status: 2 });
                    }
                } finally {
                    client.stop();
                }
            },
            stop: () => {
                if (!stopped) {
                    stopped = true;
                    listener.onStop();
                }
            },
        };
        return client;
    }
}
// 定义props
const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    height: {
        type: String,
        default: '300px'
    }
})

// 定义emit
const emit = defineEmits(['update:modelValue'])

// 定义响应式数据
const uploadUrl = import.meta.env.VITE_API_BASE_URL + "/File/batchUpload"
const fileList = ref([])
let editor = null

// 初始化编辑器
const initEditor = () => {
    editor = new AiEditor({
        element: "#aiEditor",
        placeholder: "点击输入内容...",
        theme: "light",
        content: props.modelValue,
        contentIsMarkdown: false,
        contentRetention: false,
        contentRetentionKey: 'ai-editor-content',
        draggable: true,
        pasteAsText: false,
        image: {
            uploadUrl: uploadUrl,
            uploader: async (file, uploadUrl, headers, formName) => {
                const formData = new FormData()
                formData.append('file', file)

                let { success, data } = await postUpload(uploadUrl, formData)

                return {
                    "errorCode": 0,
                    "data": {
                        "src": data[0].url,
                        "alt": data[0].fileName,
                        "align": "center",
                        "width": "100%",
                        "height": "auto",
                        "class": "image-class",
                        "loading": true,
                        "data-src": data[0].url
                    }
                }
            },
        },
        onChange: (aiEditor) => {
            emit('update:modelValue', aiEditor.getHtml())
        },
        ai: {
            modelFactory: {
                create: (name, editor, config) => new BackendEditorAiModel(editor, config),
            },
            models: {
                custom: {
                    protocol: 'http',
                    url: '/editorAi/complete',
                    method: 'POST',
                    wrapPayload: (prompt) => JSON.stringify({ prompt }),
                }
            }
        }
    })
}

// 组件挂载后初始化编辑器
onMounted(() => {
    nextTick(() => {
        initEditor()
    })
})

// 监听modelValue变化
watch(() => props.modelValue, (newValue) => {
    if (editor && newValue !== editor.getHtml()) {
        editor.setContent(newValue)
    }
})
</script>

<style scoped>
/* 编辑器容器样式 */
#aiEditor {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
}
</style>
