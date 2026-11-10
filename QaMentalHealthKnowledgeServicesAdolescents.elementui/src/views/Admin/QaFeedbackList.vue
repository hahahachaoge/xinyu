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
                    <el-form-item label="用户">
                        <SigleSelect url="/sysUser/list" class="search-input" columnName="userName" :clearable="true" columnValue="id" v-model="searchForm.userId" />
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 查看消息弹窗 -->
        <el-dialog title="关联消息详情" v-model="msgDialogShow" width="600px" :close-on-click-modal="true">
            <div class="msg-detail" v-if="msgDetail">
                <div class="msg-item">
                    <span class="msg-label">发送角色</span>
                    <span class="msg-value">
                        <el-tag :type="msgDetail.senderRole === 1 ? '' : 'success'" size="small">{{ msgDetail.senderRole === 1 ? '用户' : 'AI' }}</el-tag>
                    </span>
                </div>
                <div class="msg-item">
                    <span class="msg-label">消息内容</span>
                    <div class="msg-content">{{ msgDetail.messageContent || '-' }}</div>
                </div>
                <div class="msg-item" v-if="msgDetail.retrievedChunkIds">
                    <span class="msg-label">检索分块</span>
                    <span class="msg-value">{{ msgDetail.retrievedChunkIds }}</span>
                </div>
                <div class="msg-item">
                    <span class="msg-label">发送时间</span>
                    <span class="msg-value">{{ msgDetail.creationTime || '-' }}</span>
                </div>
            </div>
            <div class="msg-empty" v-else>消息不存在或已被删除</div>
        </el-dialog>

        <!-- 数据表格 -->
        <el-card class="box-card">
            <div class="table-header">
                <el-button type="danger" size="default" @click="batchDelete">
                    <el-icon><Delete /></el-icon>批量删除
                </el-button>
            </div>
            <el-table v-loading="loading" :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="用户" min-width="180">
                    <template #default="{ row }">
                        <div class="user-cell" v-if="row.userDto">
                            <el-image v-if="row.userDto.imageUrls" :src="row.userDto.imageUrls.split(',')[0]" class="user-cell-avatar" fit="cover" :preview-src-list="row.userDto.imageUrls.split(',')" preview-teleported />
                            <el-avatar v-else :size="36" class="user-cell-avatar">{{ (row.userDto.nickName || row.userDto.userName || '').substring(0, 1) }}</el-avatar>
                            <div class="user-cell-info">
                                <span class="user-cell-name">{{ row.userDto.nickName || '-' }}</span>
                                <span class="user-cell-account">{{ row.userDto.userName }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="评分" min-width="120">
                    <template #default="{ row }">
                        <el-rate v-model="row.rating" disabled :max="5" :colors="['#E8726D','#F0A75B','#6BBF73']" />
                    </template>
                </el-table-column>
                <el-table-column label="反馈内容" min-width="200" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.feedbackContent || '-' }}</template>
                </el-table-column>
                <el-table-column label="反馈时间" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.creationTime }}</template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="default" @click="viewMessage(row.chatMessageId)">查看消息</el-button>
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
import { Delete, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';

const searchForm = reactive({});
const tableData = ref([]);
const loading = ref(false);
const selectedRows = ref([]);
const pagination = reactive({ page: 1, limit: 10, total: 0 });
const msgDialogShow = ref(false);
const msgDetail = ref(null);

const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/qaFeedback/list', { page: pagination.page, limit: pagination.limit, ...searchForm });
        if (success) { tableData.value = data.items || []; pagination.total = data.totalCount || 0; }
    } finally { loading.value = false; }
};

const handleCurrentChange = (page) => { pagination.page = page; fetchData(); };
const handleSizeChange = (size) => { pagination.limit = size; pagination.page = 1; fetchData(); };
const handleSelectionChange = (rows) => { selectedRows.value = rows; };
const searchClick = () => { pagination.page = 1; fetchData(); };
const resetClick = () => { Object.keys(searchForm).forEach(key => { searchForm[key] = undefined; }); pagination.page = 1; fetchData(); };

const viewMessage = async (chatMessageId) => {
    if (!chatMessageId) { ElMessage.warning({ message: '该反馈未关联消息', duration: 1500 }); return; }
    const { data, success } = await post('/chatMessage/findById', { id: chatMessageId });
    if (success && data) { msgDetail.value = data; } else { msgDetail.value = null; }
    msgDialogShow.value = true;
};

const showDeleteModal = async (id) => {
    try {
        await ElMessageBox.confirm('确认删除该反馈吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/qaFeedback/deleteById', { id });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

const batchDelete = async () => {
    const ids = selectedRows.value.map(x => x.id);
    if (ids.length === 0) { ElMessage.warning('请先选择要删除的数据'); return; }
    try {
        await ElMessageBox.confirm('确认删除所选的反馈吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/qaFeedback/batchDeleteByIds', { ids });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

onMounted(() => { fetchData(); });
</script>

<style scoped lang="scss">
.box-card { margin-bottom: 20px; }
.table-header { margin-bottom: 15px; }
.pagination-box { margin-top: 20px; display: flex; justify-content: flex-end; }
.user-cell { display: flex; align-items: center; gap: 10px; }
.user-cell-avatar { width: 36px; height: 36px; border-radius: 8px; flex-shrink: 0; }
.user-cell-info { display: flex; flex-direction: column; line-height: 1.4; min-width: 0; }
.user-cell-name { font-size: 13px; font-weight: 500; color: #1E293B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.user-cell-account { font-size: 12px; color: #94A3B8; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* 消息详情弹窗 */
.msg-detail { padding: 0 8px; }
.msg-item { display: flex; gap: 12px; padding: 12px 0; border-bottom: 1px solid #F1F5F9; }
.msg-item:last-child { border-bottom: none; }
.msg-label { font-size: 13px; color: #94A3B8; width: 80px; flex-shrink: 0; }
.msg-value { font-size: 14px; color: #334155; }
.msg-content { font-size: 14px; color: #334155; line-height: 1.7; white-space: pre-wrap; word-break: break-all; flex: 1; }
.msg-empty { text-align: center; padding: 30px 0; color: #94A3B8; font-size: 14px; }
</style>