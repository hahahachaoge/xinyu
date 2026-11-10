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


                    <el-form-item label="用户">
                        <SigleSelect url="/sysUser/list" class="search-input" columnName="userName" :clearable="true" columnValue="id"
                            v-model="searchForm.userId">
                        </SigleSelect>
                    </el-form-item>                


                    <el-form-item label="收藏类型">
                        <SigleSelect url="/select/favoriteTypeEnum" class="search-input" columnName="name" :clearable="true" columnValue="code"
                            v-model="searchForm.favoriteType">
                        </SigleSelect>
                    </el-form-item>                
                </el-form>
            </div>
        </el-card>
<!-- 编辑对话框 -->
        <el-dialog :title="formData.id ? '修改收藏' : '添加收藏'" v-model="editorShow" width="50%" :lock-scroll="true"
            min-height="500px">
            <el-form v-if="editorShow" ref="editModalForm" :rules="editModalFormRules" :model="formData" label-width="140px"
                size="default">             
              <el-row :gutter="10" class="edit-from-body">

                    <el-col :span="24">
                        <el-form-item label="用户" prop="userId">
                          <SigleSelect url="/sysUser/list" columnName="userName" columnValue="id"  v-model="formData.userId" >
                          </SigleSelect>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="收藏类型" prop="favoriteType">
                          <SigleSelect url="/select/favoriteTypeEnum" columnName="name" columnValue="code"  v-model="formData.favoriteType" >
                          </SigleSelect>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="收藏目标" prop="targetId">
                            <el-input-number  style="width:100%" v-model="formData.targetId"  placeholder="请输入收藏目标"     :clearable="true"  :min="1" :max="1000000"></el-input-number>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row type="flex" justify="end" align="bottom">
                    <el-form-item>
                        <el-button size="default" type="primary" plain @click="addOrEditForm()">确 定</el-button>
                        <el-button size="default" @click="editorShow = false">取 消</el-button>
                    </el-form-item>
                </el-row>
            </el-form>
        </el-dialog>





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
                <el-table-column label="用户" min-width="180">
                    <template #default="{ row }">
                        <div class="user-cell" v-if="row.userDto">
                            <el-image v-if="row.userDto.imageUrls" :src="row.userDto.imageUrls.split(',')[0]"
                                class="user-cell-avatar" fit="cover"
                                :preview-src-list="row.userDto.imageUrls.split(',')" preview-teleported />
                            <el-avatar v-else :size="36" class="user-cell-avatar">
                                {{ (row.userDto.nickName || row.userDto.userName || '').substring(0, 1) }}
                            </el-avatar>
                            <div class="user-cell-info">
                                <span class="user-cell-name">{{ row.userDto.nickName || '-' }}</span>
                                <span class="user-cell-account">{{ row.userDto.userName }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="收藏类型" >
                    <template #default="{ row }">
                        {{ row.favoriteTypeFormat}}
                    </template>
                </el-table-column>
                <el-table-column label="收藏目标" >
                    <template #default="{ row }">
                        {{ row.targetId}}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
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

import { Delete, Edit, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {onMounted,onBeforeMount,nextTick, watch,computed, reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const commonStore = useCommonStore();
// 计算属性
const token = computed(() => commonStore.token)
const userInfo = computed(() => commonStore.userInfo)
const roleType = computed(() => commonStore.roleType)
const userId = computed(() => commonStore.userId)
  
//表格条件
const where = reactive({});
// 搜索表单数据
const searchForm = reactive({});

// 编辑表单数据
const formData = reactive({});

// 编辑对话框显示状态
const editorShow = ref(false);

// 表单引用
const editModalForm = ref(null);

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const selectedRows = ref([]);

// 分页配置
const pagination = reactive({
    page: 1,
    limit: 10,
    total: 0
});


// 表单验证规则
const editModalFormRules = reactive({
"userId":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"favoriteType":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"targetId":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
 });


// 获取列表数据
const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/favorite/list', {
            page: pagination.page,
            limit: pagination.limit,
            ...searchForm,
            ...where
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


// 显示编辑对话框
const showEditModal = async (id) => {

    const { data } = await post(`/favorite/findById`, { id: id });
    
    Object.assign(formData, data);

    editorShow.value = true;

};

// 创建或编辑表单
const addOrEditForm = async () => {
    if (!editModalForm.value) return;

    await editModalForm.value.validate(async valid => {
        if (valid) {
            const { success } = await post(`/favorite/addOrEdit`, formData);

            if (success) {
                editorShow.value = false;
                fetchData();
                ElMessage.success('操作成功');
            }
        }
    });
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
        const { success } = await post(`/favorite/deleteById`, { id: id });
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


            const { success } = await post(`/favorite/batchDeleteByIds`, { ids: ids });
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

onBeforeMount(() => {
   
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

.user-cell {
    display: flex;
    align-items: center;
    gap: 10px;
}

.user-cell-avatar {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    flex-shrink: 0;
}

.user-cell-info {
    display: flex;
    flex-direction: column;
    line-height: 1.4;
    min-width: 0;
}

.user-cell-name {
    font-size: 13px;
    font-weight: 500;
    color: #1E293B;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.user-cell-account {
    font-size: 12px;
    color: #94A3B8;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>