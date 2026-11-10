<template>
    <div style="margin: 15px 0px;">
        <div v-if="loading"><el-skeleton :rows="12" animated /></div>
        <div v-else>
            <el-empty v-if="options.length == 0" description="看官，数据正在维护中"></el-empty>
            <div v-else class="box">
                <slot name="content" :data="options"></slot>
            </div>
        </div>
        <div class="custom-pagination">
            <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.page" :page-size="pagination.limit" layout="total, prev, pager, next, jumper"
                :total="pagination.total">
            </el-pagination>
        </div>
    </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { post } from '@/api/http';
// 定义props
const props = defineProps({
    url: {
        type: String,
        default: ""
    },
    where: {
        type: [Number, String, Object],
        default: ""
    },
    limit: {
        type: Number,
        default: 10
    }
})

// 响应式数据
const loading = ref(false)
const options = ref([])
const where_ = ref({})
const pagination = reactive({
    limit: 10,
    page: 1,
    total: 0,
    pageSizes: [10, 20, 50, 100]
})

// 初始化数据
const InitData = () => {
    pagination.limit = props.limit
    pagination.page = 1
    pagination.total = 0
    where_.value = {}
}

// 重新加载数据
const Reload = async (where) => {
    pagination.page = 1
    pagination.total = 0
    let searchWhere = {}
    if (where) {
        searchWhere = Object.assign(searchWhere, where)
    }
    await FetchDataList(searchWhere)
}

// 获取数据列表
const FetchDataList = async (searchWhere) => {
    loading.value = true
    try {
        const { Data: { Items, TotalCount } } = await post(props.url, {
            Limit: pagination.limit,
            Size: pagination.limit,
            Page: pagination.page,
            ...where_.value,
            ...searchWhere
        })
        options.value = Items
        pagination.total = TotalCount
    } finally {
        loading.value = false
    }
}

// 页码改变
const handleCurrentChange = (event) => {
    pagination.page = event
    FetchDataList()
}

// 每页条数改变
const handleSizeChange = (event) => {
    pagination.limit = event
    pagination.page = 1
    FetchDataList()
}

// 组件挂载时初始化
onMounted(() => {
    pagination.limit = props.limit
    where_.value = props.where
    FetchDataList()
})

// 暴露方法给父组件
defineExpose({
    Reload,
    InitData
})
</script>

<style scoped>
/* 容器样式 */
.box {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    box-sizing: border-box;
}

/* 项目样式 */
.item {
    width: 100%;
    padding: 5px;
    box-sizing: border-box;
}

/* 文本截断样式 */
.content-cut {
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-all;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 4;
    line-height: 1.2em;
}

/* 卡片样式 */
.card {
    box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
    height: 100%;
    border-radius: 10px;
}

/* 分页器样式 */
.custom-pagination {
    margin-top: 20px;
    padding-bottom: 20px;
    display: flex;
    flex-direction: row-reverse;
}
</style>
