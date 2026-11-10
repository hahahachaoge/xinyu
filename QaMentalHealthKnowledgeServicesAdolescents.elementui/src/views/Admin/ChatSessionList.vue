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
                    <el-form-item label="用户">
                        <SigleSelect url="/sysUser/list" class="search-input" columnName="userName" :clearable="true" columnValue="id" v-model="searchForm.userId" />
                    </el-form-item>
                    <el-form-item label="最后消息时间" class="search-input">
                        <el-date-picker v-model="searchForm.lastMessageTimeRange" type="datetimerange" start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 对话详情弹窗 -->
        <el-dialog :title="'对话详情 - ' + (detailSession?.sessionTitle || '')" v-model="detailShow" width="700px" :close-on-click-modal="true">
            <div class="chat-detail" v-loading="detailLoading">
                <div class="chat-detail-info">
                    <span>用户：{{ detailSession?.userDto?.nickName || detailSession?.userDto?.userName || '-' }}</span>
                    <span>消息数：{{ detailSession?.messageCount || 0 }}</span>
                    <span>最后消息：{{ detailSession?.lastMessageTime || '-' }}</span>
                </div>
                <div class="chat-detail-messages">
                    <div class="chat-msg" :class="msg.senderRole === 1 ? 'msg-user' : 'msg-ai'" v-for="msg in detailMessages" :key="msg.id">
                        <div class="msg-avatar">
                            <svg v-if="msg.senderRole === 2" viewBox="0 0 24 24" fill="#5B9BD5" width="20" height="20"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/></svg>
                            <svg v-else viewBox="0 0 24 24" fill="#8C7EC8" width="20" height="20"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                        </div>
                        <div class="msg-body">
                            <div class="msg-role">{{ msg.senderRole === 1 ? '用户' : 'AI助手' }}</div>
                            <div class="msg-bubble" :class="msg.senderRole === 1 ? 'bubble-user' : 'bubble-ai'">{{ msg.messageContent }}</div>
                            <div class="msg-time">{{ msg.creationTime }}</div>
                        </div>
                    </div>
                    <div class="chat-detail-empty" v-if="detailMessages.length === 0 && !detailLoading">暂无消息记录</div>
                </div>
            </div>
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
                <el-table-column label="会话标题" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.sessionTitle }}</template>
                </el-table-column>
                <el-table-column label="消息数量" min-width="90">
                    <template #default="{ row }">
                        <el-tag size="small" type="info">{{ row.messageCount || 0 }}条</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="最后消息时间" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.lastMessageTime }}</template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="default" @click="showDetail(row)">查看详情</el-button>
                        <el-button type="danger" size="default" @click="showDeleteModal(row.id)">
                            <el-icon><Delete /></el-icon>删 除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-box">
                <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]" :page-size="pagination.limit"
                    layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" />
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
const detailShow = ref(false);
const detailSession = ref(null);
const detailMessages = ref([]);
const detailLoading = ref(false);

const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/chatSession/list', { page: pagination.page, limit: pagination.limit, ...searchForm });
        if (success) { tableData.value = data.items || []; pagination.total = data.totalCount || 0; }
    } finally { loading.value = false; }
};

const handleCurrentChange = (page) => { pagination.page = page; fetchData(); };
const handleSizeChange = (size) => { pagination.limit = size; pagination.page = 1; fetchData(); };
const handleSelectionChange = (rows) => { selectedRows.value = rows; };
const searchClick = () => { pagination.page = 1; fetchData(); };
const resetClick = () => { Object.keys(searchForm).forEach(key => { searchForm[key] = undefined; }); pagination.page = 1; fetchData(); };

const showDetail = async (row) => {
    detailSession.value = row;
    detailMessages.value = [];
    detailShow.value = true;
    detailLoading.value = true;
    const { data, success } = await post('/aiChat/getMessages', { id: row.id });
    if (success && data) detailMessages.value = Array.isArray(data) ? data : [];
    detailLoading.value = false;
};

const showDeleteModal = async (id) => {
    try {
        await ElMessageBox.confirm('确认删除该会话吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/chatSession/deleteById', { id });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

const batchDelete = async () => {
    const ids = selectedRows.value.map(x => x.id);
    if (ids.length === 0) { ElMessage.warning('请先选择要删除的数据'); return; }
    try {
        await ElMessageBox.confirm('确认删除所选的会话吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/chatSession/batchDeleteByIds', { ids });
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

/* 对话详情弹窗 */
.chat-detail { }
.chat-detail-info { display: flex; gap: 20px; padding: 12px 16px; background: #F8FAFC; border-radius: 8px; margin-bottom: 16px; font-size: 13px; color: #64748B; }
.chat-detail-messages { max-height: 500px; overflow-y: auto; padding: 0 4px; }
.chat-detail-empty { text-align: center; padding: 40px 0; color: #94A3B8; font-size: 13px; }

.chat-msg { display: flex; gap: 10px; margin-bottom: 16px; }
.chat-msg.msg-user { flex-direction: row-reverse; }
.msg-avatar { flex-shrink: 0; width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.msg-user .msg-avatar { background: #F3F0FB; }
.msg-ai .msg-avatar { background: #EBF3FB; }
.msg-body { max-width: 75%; }
.msg-role { font-size: 12px; color: #94A3B8; margin-bottom: 4px; }
.msg-user .msg-role { text-align: right; }
.msg-bubble { padding: 10px 14px; border-radius: 10px; font-size: 14px; line-height: 1.7; word-break: break-all; white-space: pre-wrap; }
.bubble-user { background: #5B9BD5; color: #fff; border-top-right-radius: 4px; }
.bubble-ai { background: #F8FAFC; color: #334155; border: 1px solid #F1F5F9; border-top-left-radius: 4px; }
.msg-time { font-size: 11px; color: #CBD5E1; margin-top: 4px; }
.msg-user .msg-time { text-align: right; }
</style>