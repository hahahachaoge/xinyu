<template>
    <div>

        <!-- 搜索表单卡片 -->
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <el-row>
                    <el-button type="primary" size="default" @click="searchClick">
                        <el-icon>
                            <Search />
                        </el-icon>查询
                    </el-button>
                    <el-button type="warning" size="default" @click="resetClick">
                        <el-icon>
                            <Refresh />
                        </el-icon>清空条件
                    </el-button>
                </el-row>
            </div>
            <div class="margin-top-sm">
                <el-form :inline="true" :model="searchForm" size="default">
   							<el-form-item label="关键词" prop="keyWord">
                        <el-input v-model.trim="searchForm.keyWord" placeholder="请输入关键词"
                            :clearable="true"></el-input>
                    </el-form-item>


                </el-form>
            </div>
        </el-card>
        <!-- 数据表格 -->
        <el-card class="box-card">
            <div class="table-header">
                <el-button type="danger" size="default" @click="batchDelete">
                    <el-icon>
                        <Delete />
                    </el-icon>批量删除
                </el-button>
            </div>
            <el-table v-loading="loading" :data="tableData" stripe style="width: 100%"@selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="分类名称" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">
                        {{ row.typeName}}
                    </template>
                </el-table-column>
                <el-table-column label="分类描述" min-width="300" show-overflow-tooltip>
                    <template #default="{ row }">
                        {{ row.typeDesc }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button type="danger" size="default" @click="showDeleteModal(row.id)">
                            <el-icon>
                                <Delete />
                            </el-icon>删 除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-box">
                <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]" :page-size="pagination.limit"
                    layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
                </el-pagination>
            </div>
        </el-card>
        
              
              
    </div>
</template>


<script setup>
import { post } from '@/api/http';
import { useCommonStore } from "@/store";

import { Delete, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {onMounted, reactive, ref } from 'vue';

const commonStore = useCommonStore();

const searchForm = reactive({});
const tableData = ref([]);
const loading = ref(false);
const selectedRows = ref([]);

const pagination = reactive({
    page: 1,
    limit: 10,
    total: 0
});


// 获取列表数据
const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/knowledgeType/list', {
            page: pagination.page,
            limit: pagination.limit,
            ...searchForm
        });
        if (success) {
            tableData.value = data.items || [];
            pagination.total = data.totalCount || 0;
        }
    } finally {
        loading.value = false;
    }
};

// 页码改变
const handleCurrentChange = (page) => {
    pagination.page = page;
    fetchData();
};

// 每页条数改变
const handleSizeChange = (size) => {
    pagination.limit = size;
    pagination.page = 1;
    fetchData();
};

// 选择行变化
const handleSelectionChange = (rows) => {
    selectedRows.value = rows;
};


// 搜索点击
const searchClick = () => {
    pagination.page = 1;
    fetchData();
};

// 重置搜索条件
const resetClick = () => {
    Object.keys(searchForm).forEach(key => {
        searchForm[key] = undefined;
    });
    pagination.page = 1;
    fetchData();
};

// 显示删除确认框
const showDeleteModal = async (id) => {

    try {
        await ElMessageBox.confirm('确认删除该信息吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        const { success } = await post(`/knowledgeType/deleteById`, { id: id });
        if (success) {
            fetchData();
            ElMessage.success('删除成功');
        }
    }
    catch (error) {
        ElMessage.warning('用户放弃了操作');
    }
};

// 批量删除
const batchDelete = async () => {
    const ids = selectedRows.value.map(x => x.id);
    if (ids.length > 0) {
        try {
            await ElMessageBox.confirm('确认删除所选的行数据吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })


            const { success } = await post(`/knowledgeType/batchDeleteByIds`, { ids: ids });
            if (success) {
                fetchData();
                ElMessage.success('删除成功');
            }
        }
        catch (error) {
            ElMessage.warning('用户放弃了操作');
        }
    }
    else {
        ElMessage.warning('请先选择要删除的数据');
    }
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped lang="scss">
/* 搜索表单样式 */
.box-card {
    margin-bottom: 20px;
}

.table-header {
    margin-bottom: 15px;
}

.pagination-box {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>