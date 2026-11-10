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




                    <el-form-item label="所属量表">
                        <SigleSelect url="/scaleInfo/list" class="search-input" columnName="scaleName" :clearable="true" columnValue="id"
                            v-model="searchForm.scaleInfoId">
                        </SigleSelect>
                    </el-form-item>                


                    <el-form-item label="题目类型">
                        <SigleSelect url="/select/questionTypeEnum" class="search-input" columnName="name" :clearable="true" columnValue="code"
                            v-model="searchForm.questionType">
                        </SigleSelect>
                    </el-form-item>                
                </el-form>
            </div>
        </el-card>
<!-- 编辑对话框 -->
        <el-dialog :title="formData.id ? '修改测评题目' : '添加测评题目'" v-model="editorShow" width="50%" :lock-scroll="true"
            min-height="500px">
            <el-form v-if="editorShow" ref="editModalForm" :rules="editModalFormRules" :model="formData" label-width="140px"
                size="default">             
              <el-row :gutter="10" class="edit-from-body">

                    <el-col :span="24">
                        <el-form-item label="所属量表" prop="scaleInfoId">
                          <SigleSelect url="/scaleInfo/list" columnName="scaleName" columnValue="id"  v-model="formData.scaleInfoId" >
                          </SigleSelect>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="题目序号" prop="questionIndex">
                            <el-input-number  style="width:100%" v-model="formData.questionIndex"  placeholder="请输入题目序号"     :clearable="true"  :min="1" :max="1000000"></el-input-number>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="题目标题" prop="questionTitle">
                            <el-input type="text" v-model="formData.questionTitle"  placeholder="请输入题目标题"     :clearable="true"></el-input>
                        </el-form-item>
                    </el-col>


                    <el-col :span="24">
                        <el-form-item label="题目类型" prop="questionType">
                          <SigleSelect url="/select/questionTypeEnum" columnName="name" columnValue="code"  v-model="formData.questionType" >
                          </SigleSelect>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="选项与分值JSON" prop="optionJson">
                             <AIRichText v-model="formData.optionJson" ></AIRichText>
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
                <el-table-column label="量表名称" >
                    <template #default="{ row }">
                        {{ row.scaleInfoDto.scaleName}}
                    </template>
                </el-table-column>
                <el-table-column label="题目序号" >
                    <template #default="{ row }">
                        {{ row.questionIndex}}
                    </template>
                </el-table-column>
                <el-table-column label="题目标题" >
                    <template #default="{ row }">
                        {{ row.questionTitle}}
                    </template>
                </el-table-column>
                <el-table-column label="题目类型" >
                    <template #default="{ row }">
                        {{ row.questionTypeFormat}}
                    </template>
                </el-table-column>
                <el-table-column label="选项与分值JSON" min-width="140">
                    <template #default="{ row }">
                           <RichButton :content="row.optionJson"></RichButton>
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
"scaleInfoId":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"questionIndex":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"questionTitle":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"questionType":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
"optionJson":[
{ required: true, message: '该项为必填项', trigger: 'blur' },
              ],           
 });


// 获取列表数据
const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/scaleQuestion/list', {
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

    const { data } = await post(`/scaleQuestion/findById`, { id: id });
    
    Object.assign(formData, data);

    editorShow.value = true;

};

// 创建或编辑表单
const addOrEditForm = async () => {
    if (!editModalForm.value) return;

    await editModalForm.value.validate(async valid => {
        if (valid) {
            const { success } = await post(`/scaleQuestion/addOrEdit`, formData);

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
        const { success } = await post(`/scaleQuestion/deleteById`, { id: id });
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


            const { success } = await post(`/scaleQuestion/batchDeleteByIds`, { ids: ids });
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
</style>