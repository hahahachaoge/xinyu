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




                    <el-form-item label="文章分类">
                        <SigleSelect url="/articleType/list" class="search-input" columnName="typeName" :clearable="true" columnValue="id"
                            v-model="searchForm.articleTypeId">
                        </SigleSelect>
                    </el-form-item>                


                    <el-form-item label="发布人">
                        <SigleSelect url="/sysUser/list" class="search-input" columnName="userName" :clearable="true" columnValue="id"
                            v-model="searchForm.publisherId">
                        </SigleSelect>
                    </el-form-item>                


                    <el-form-item label="发布时间" class="search-input">
                        <el-date-picker v-model="searchForm.publishTimeRange" type="datetimerange" start-placeholder="开始发布时间"
                            end-placeholder="结束发布时间" value-format="YYYY-MM-DD HH:mm:ss" >
                        </el-date-picker>                                       
                    </el-form-item>                
                </el-form>
            </div>
        </el-card>
<!-- 编辑对话框 -->
        <el-dialog :title="formData.id ? '修改科普文章' : '添加科普文章'" v-model="editorShow" width="50%" :lock-scroll="true"
            min-height="500px">
            <el-form v-if="editorShow" ref="editModalForm" :rules="editModalFormRules" :model="formData" label-width="140px"
                size="default">             
              <el-row :gutter="10" class="edit-from-body">

                    <el-col :span="24">
                        <el-form-item label="文章分类" prop="articleTypeId">
                          <SigleSelect url="/articleType/list" columnName="typeName" columnValue="id"  v-model="formData.articleTypeId" >
                          </SigleSelect>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="文章标题" prop="title">
                            <el-input type="text" v-model="formData.title"  placeholder="请输入文章标题"     :clearable="true"></el-input>
                        </el-form-item>
                    </el-col>


                    <el-col :span="24">
                        <el-form-item label="封面图片" prop="coverImage">
                           <UploadImages :limit="1" v-model="formData.coverImage"></UploadImages>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="文章内容" prop="articleContent">
                             <AIRichText v-model="formData.articleContent" ></AIRichText>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="发布时间" prop="publishTime">
                            <el-date-picker v-model="formData.publishTime" align="right" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss">
                            </el-date-picker>
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
                <el-button type="primary" size="default" @click="showEditModal()">
                    <el-icon>
                        <Edit />
                    </el-icon>新 增
                </el-button>
                <el-button type="danger" size="default" @click="batchDelete">
                    <el-icon>
                        <Delete />
                    </el-icon>批量删除
                </el-button>
            </div>
            <el-table v-loading="loading" :data="tableData" stripe style="width: 100%"@selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="分类名称" >
                    <template #default="{ row }">
                        {{ row.articleTypeDto.typeName}}
                    </template>
                </el-table-column>
                <el-table-column label="文章标题" >
                    <template #default="{ row }">
                        {{ row.title}}
                    </template>
                </el-table-column>
                <el-table-column label="封面图片" min-width="120">
                    <template #default="{ row }">
                        <el-image v-if="row.coverImage" :src="row.coverImage.split(',')[0]" 
                            style="width: 50px; height: 50px" fit="cover" :preview-src-list="row.coverImage.split(',')" preview-teleported>
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column label="文章内容" min-width="140">
                    <template #default="{ row }">
                           <RichButton :content="row.articleContent"></RichButton>
                    </template>
                </el-table-column>
                <el-table-column label="发布人" min-width="180">
                    <template #default="{ row }">
                        <div class="user-cell" v-if="row.publisherDto">
                            <el-image v-if="row.publisherDto.imageUrls" :src="row.publisherDto.imageUrls.split(',')[0]"
                                class="user-cell-avatar" fit="cover"
                                :preview-src-list="row.publisherDto.imageUrls.split(',')" preview-teleported />
                            <el-avatar v-else :size="36" class="user-cell-avatar">
                                {{ (row.publisherDto.nickName || row.publisherDto.userName || '').substring(0, 1) }}
                            </el-avatar>
                            <div class="user-cell-info">
                                <span class="user-cell-name">{{ row.publisherDto.nickName || '-' }}</span>
                                <span class="user-cell-account">{{ row.publisherDto.userName }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="发布时间" >
                    <template #default="{ row }">
                        {{ row.publishTime}}
                    </template>
                </el-table-column>
                <el-table-column label="浏览量" >
                    <template #default="{ row }">
                        {{ row.viewCount}}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="default" @click="showEditModal(row.id)">
                            <el-icon>
                                <Edit />
                            </el-icon>修 改
                        </el-button>
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
"articleTypeId":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"title":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"articleContent":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"publishTime":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
 });


// 获取列表数据
const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/article/list', {
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

    const { data } = await post(`/article/findById`, { id: id });
    
    Object.assign(formData, data);

    // 新增时自动设置发布人和浏览量
    if (!id) {
        formData.publisherId = commonStore.userId;
        formData.viewCount = 0;
        formData.coverImage = '';
    }

    editorShow.value = true;

};

// 创建或编辑表单
const addOrEditForm = async () => {
    if (!editModalForm.value) return;

    await editModalForm.value.validate(async valid => {
        if (valid) {
            const { success } = await post(`/article/addOrEdit`, formData);

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
        const { success } = await post(`/article/deleteById`, { id: id });
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


            const { success } = await post(`/article/batchDeleteByIds`, { ids: ids });
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