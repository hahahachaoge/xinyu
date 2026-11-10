<template>
    <div>
        <!-- 搜索表单卡片 -->
        <el-card class="box-card">
            <div slot="header" class="clearfix">
                <el-row>
                    <el-button type="primary" size="default" @click="searchClick">
                        <el-icon><Search /></el-icon>查询
                    </el-button>
                    <el-button type="warning" size="default" @click="resetClick">
                        <el-icon><Refresh /></el-icon>清空条件
                    </el-button>
                </el-row>
            </div>
            <div class="margin-top-sm">
                <el-form :inline="true" :model="searchForm" size="default">
                    <el-form-item label="关键词" prop="keyWord">
                        <el-input v-model.trim="searchForm.keyWord" placeholder="请输入关键词" :clearable="true"></el-input>
                    </el-form-item>
                    <el-form-item label="量表类型">
                        <SigleSelect url="/select/scaleTypeEnum" class="search-input" columnName="name" :clearable="true" columnValue="code" v-model="searchForm.scaleType"></SigleSelect>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 数据表格 -->
        <el-card class="box-card">
            <div class="table-header">
                <el-button type="primary" size="default" @click="goAdd">
                    <el-icon><Edit /></el-icon>新 增
                </el-button>
                <el-button type="danger" size="default" @click="batchDelete">
                    <el-icon><Delete /></el-icon>批量删除
                </el-button>
            </div>
            <el-table v-loading="loading" :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="量表名称" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.scaleName }}</template>
                </el-table-column>
                <el-table-column label="量表描述" min-width="200">
                    <template #default="{ row }">
                        <RichButton :content="row.scaleDesc"></RichButton>
                    </template>
                </el-table-column>
                <el-table-column label="量表类型" min-width="100">
                    <template #default="{ row }">{{ row.scaleTypeFormat }}</template>
                </el-table-column>
                <el-table-column label="适用人群" min-width="120" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.targetGroup }}</template>
                </el-table-column>
                <el-table-column label="题目数量" min-width="80">
                    <template #default="{ row }">{{ row.questionCount }}</template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="default" @click="goEdit(row.id)">
                            <el-icon><Edit /></el-icon>修 改
                        </el-button>
                        <el-button type="danger" size="default" @click="showDeleteModal(row.id)">
                            <el-icon><Delete /></el-icon>删 除
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
import { Delete, Edit, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, computed, reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const commonStore = useCommonStore();

const where = reactive({});
const searchForm = reactive({});
const tableData = ref([]);
const loading = ref(false);
const selectedRows = ref([]);
const pagination = reactive({ page: 1, limit: 10, total: 0 });

const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/scaleInfo/list', { page: pagination.page, limit: pagination.limit, ...searchForm, ...where });
        if (success) {
            tableData.value = data.items || [];
            pagination.total = data.totalCount || 0;
        }
    } finally {
        loading.value = false;
    }
};

const handleCurrentChange = (page) => { pagination.page = page; fetchData(); };
const handleSizeChange = (size) => { pagination.limit = size; pagination.page = 1; fetchData(); };
const handleSelectionChange = (rows) => { selectedRows.value = rows; };

// 跳转到新增页面
const goAdd = () => { router.push('/admin/scaleInfoEdit'); };

// 跳转到编辑页面
const goEdit = (id) => { router.push('/admin/scaleInfoEdit?id=' + id); };

const searchClick = () => { pagination.page = 1; fetchData(); };
const resetClick = () => { Object.keys(searchForm).forEach(key => { searchForm[key] = undefined; }); pagination.page = 1; fetchData(); };

const showDeleteModal = async (id) => {
    try {
        await ElMessageBox.confirm('确认删除该量表及其所有题目和评分规则吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/scaleInfo/deleteById', { id });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

const batchDelete = async () => {
    const ids = selectedRows.value.map(x => x.id);
    if (ids.length === 0) { ElMessage.warning('请先选择要删除的数据'); return; }
    try {
        await ElMessageBox.confirm('确认删除所选的量表吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/scaleInfo/batchDeleteByIds', { ids });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

onMounted(() => { fetchData(); });
</script>

<style scoped lang="scss">
.box-card { margin-bottom: 20px; }
.table-header { margin-bottom: 15px; }
.pagination-box { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
