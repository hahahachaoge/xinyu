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
            <div class="margin-top-xs">
                <el-form :inline="true" :model="searchForm" size="default">
                    <el-form-item label="账号">
                        <el-input v-model="searchForm.userName" class="search-input" placeholder="请输入账号"
                            clearable></el-input>
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-input v-model="searchForm.nickName" class="search-input" placeholder="请输入姓名"
                            clearable></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱">
                        <el-input v-model="searchForm.email" class="search-input" placeholder="请输入邮箱"
                            clearable></el-input>
                    </el-form-item>
                    <el-form-item label="手机号码">
                        <el-input v-model="searchForm.phoneNumber" class="search-input" placeholder="请输入手机号码"
                            clearable></el-input>
                    </el-form-item>
                    <el-form-item label="角色">
                        <SigleSelect v-model="searchForm.roleType" url="/select/roleType" columnName="name"
                            columnValue="code" columnLabel="label"></SigleSelect>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog :title="formData.id ? '修改账号' : '添加账号'" v-model="editorShow" width="50%" :lock-scroll="true"
            min-height="800px">
            <el-form v-if="editorShow" ref="editModalForm" :rules="editModalFormRules" :model="formData"
                label-width="140px" size="default">
                <el-row :gutter="10" class="edit-from-body">
                    <el-col :span="24">
                        <el-form-item label="账号" prop="userName" placeholder="请输入账号">
                            <el-input v-model.trim="formData.userName"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="!formData.id">
                        <el-form-item label="密码" prop="passWord">
                            <el-input type="password" v-model.trim="formData.passWord"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="角色" prop="roleType">
                            <SigleSelect :filterValue="[2]" v-model="formData.roleType" url="/select/roleType"
                                columnName="name" columnValue="code" columnLabel="label"></SigleSelect>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="邮箱" prop="email" placeholder="请输入邮箱">
                            <el-input v-model.trim="formData.email"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="联系方式" prop="phoneNumber" placeholder="请输入联系方式">
                            <el-input v-model.trim="formData.phoneNumber"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="姓名" prop="nickName" placeholder="请输入姓名">
                            <el-input v-model.trim="formData.nickName"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="图片">
                            <UploadImages :limit="1" v-model="formData.imageUrls"></UploadImages>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row type="flex" justify="end" align="bottom">
                    <el-form-item>
                        <el-button type="primary" size="default" plain @click="addOrEditForm()">确 定</el-button>
                        <el-button size="default" @click="editorShow = false">取 消</el-button>
                    </el-form-item>
                </el-row>
            </el-form>
        </el-dialog>

        <!-- 重置密码对话框 -->
        <el-dialog title="重置密码" v-model="resetPasswordShow" width="40%" :lock-scroll="true">
            <el-form v-if="resetPasswordShow" ref="resetPasswordForm" :rules="resetPasswordRules"
                :model="resetPasswordData" label-width="120px" size="default">
                <el-form-item label="新密码" prop="passWord">
                    <el-input type="password" v-model.trim="resetPasswordData.passWord" placeholder="请输入新密码"></el-input>
                </el-form-item>
                <el-row type="flex" justify="end" align="bottom">
                    <el-form-item>
                        <el-button type="primary" size="default" plain @click="confirmResetPassword()">确 定</el-button>
                        <el-button size="default" @click="resetPasswordShow = false">取 消</el-button>
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
                 <ExportButton exportUrl="/sysUser/export" :where="searchForm"></ExportButton>
            </div>
            <el-table v-loading="loading" :data="tableData"  stripe style="width: 100%">
           
                <el-table-column label="用户信息" min-width="220">
                    <template #default="{ row }">
                        <div class="user-cell">
                            <el-image v-if="row.imageUrls" :src="row.imageUrls.split(',')[0]"
                                class="user-cell-avatar" fit="cover"
                                :preview-src-list="row.imageUrls.split(',')" preview-teleported />
                            <el-avatar v-else :size="40" class="user-cell-avatar">
                                {{ (row.nickName || row.userName || '').substring(0, 1) }}
                            </el-avatar>
                            <div class="user-cell-info">
                                <span class="user-cell-name">{{ row.nickName }}</span>
                                <span class="user-cell-account">{{ row.userName }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="所属角色" show-overflow-tooltip>
                    <template #default="{ row }">
                        {{ row.roleTypeFormat }}
                    </template>
                </el-table-column>
                <el-table-column label="手机号" show-overflow-tooltip>
                    <template #default="{ row }">
                        {{ maskPhone(row.phoneNumber) }}
                    </template>
                </el-table-column>
                <el-table-column label="邮箱" width="150" show-overflow-tooltip>
                    <template #default="{ row }">
                        {{ maskEmail(row.email) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="300" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="default" class="margin-top-xs" @click="showEditModal(row.id)">
                            <el-icon>
                                <Edit />
                            </el-icon>修 改
                        </el-button>
                        <el-button type="primary" size="default" class="margin-top-xs" @click="resetPassword(row.id)">
                            <el-icon>
                                <Edit />
                            </el-icon>重置密码
                        </el-button>
                        <el-button type="danger" size="default" class="margin-top-xs" @click="showDeleteModal(row.id)">
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
import { maskEmail, maskPhone } from '@/utils/mask';
import { Delete, Edit, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';

const commonStore = useCommonStore();

// 搜索表单数据
const searchForm = reactive({});

// 编辑表单数据
const formData = reactive({});

// 编辑对话框显示状态
const editorShow = ref(false);

// 表单引用
const editModalForm = ref(null);

// 重置密码表单数据
const resetPasswordData = reactive({
    id: null,
    passWord: ''
});

// 重置密码对话框显示状态
const resetPasswordShow = ref(false);

// 重置密码表单引用
const resetPasswordForm = ref(null);

// 表格数据
const tableData = ref([]);
const loading = ref(false);

// 分页配置
const pagination = reactive({
    page: 1,
    limit: 10,
    total: 0
});

// 表单验证规则
const editModalFormRules = {
    userName: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
        { min: 3, max: 20, message: '账号长度应在3到20个字符之间', trigger: 'blur' }
    ],
    passWord: [
        {
            validator: (rule, value, callback) => {
                //如果是新增则必填
                if (!formData.id && !value) {
                    callback(new Error('该项为必填项'));
                    return;
                }
                var reg = /^.{8,16}$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入8-16位密码'));
                } else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ],
    email: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                var reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入正确的邮箱,必须包含@和.且@和.之间必须有内容'));
                } else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ],
    imageUrls: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
    ],
    nickName: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
    ],
    phoneNumber: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                var reg = /^1[123456789]\d{9}$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入正确的手机号,必须以1开头且长度为11位'));
                } else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ],

    roleType: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
    ],
};

// 重置密码表单验证规则
const resetPasswordRules = {
    passWord: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度应在6到20个字符之间', trigger: 'blur' }
    ],
};

// 获取列表数据
const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/sysUser/list', {
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

// 显示重置密码对话框
const resetPassword = (id) => {
    resetPasswordData.id = id;
    resetPasswordData.passWord = '';
    resetPasswordShow.value = true;
    // 清除表单验证状态
    if (resetPasswordForm.value) {
        resetPasswordForm.value.clearValidate();
    }
};

// 确认重置密码
const confirmResetPassword = async () => {
    if (!resetPasswordForm.value) return;

    await resetPasswordForm.value.validate(async valid => {
        if (valid) {
            try {
                const { success } = await post(`/sysUser/resetPassword`, resetPasswordData);
                if (success) {
                    resetPasswordShow.value = false;
                    ElMessage.success('重置密码成功');
                }
            } catch (error) {
                ElMessage.error('重置密码失败');
            }
        }
    });
};

// 显示编辑对话框
const showEditModal = async (id) => {
    const { data } = await post(`/sysUser/findById`, { id: id });
    Object.assign(formData, data);
    editorShow.value = true;
};

// 创建或编辑表单
const addOrEditForm = async () => {
    if (!editModalForm.value) return;

    await editModalForm.value.validate(async valid => {
        if (valid) {
            const { success } = await post(`/sysUser/addOrEdit`, formData);

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
        await ElMessageBox.confirm('确认删除该账号吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });

        const { success } = await post(`/sysUser/batchDeleteByIds`, { ids: [id] });
        if (success) {
            fetchData();
            ElMessage.success('删除成功');
        }
    }
    catch (error) {
        ElMessage.warning('用户放弃了操作');
    }
};

// 组件挂载时加载数据
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

/* 用户信息合并列 */
.user-cell {
    display: flex;
    align-items: center;
    gap: 12px;
}

.user-cell-avatar {
    width: 40px;
    height: 40px;
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
    font-size: 14px;
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
