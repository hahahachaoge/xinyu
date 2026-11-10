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
                    <el-form-item label="测评量表">
                        <SigleSelect url="/scaleInfo/list" class="search-input" columnName="scaleName" :clearable="true" columnValue="id" v-model="searchForm.scaleInfoId" />
                    </el-form-item>
                    <el-form-item label="完成时间" class="search-input">
                        <el-date-picker v-model="searchForm.finishTimeRange" type="datetimerange" start-placeholder="开始完成时间" end-placeholder="结束完成时间" value-format="YYYY-MM-DD HH:mm:ss" />
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 详情弹窗 -->
        <el-dialog title="测评详情" v-model="detailShow" width="700px" :close-on-click-modal="true">
            <div class="detail-dialog" v-if="detailData">
                <!-- 基本信息 -->
                <div class="detail-section">
                    <div class="detail-row">
                        <span class="detail-label">用户</span>
                        <span class="detail-value">{{ detailData.userDto?.nickName || detailData.userDto?.userName || '-' }}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">量表名称</span>
                        <span class="detail-value">{{ detailData.scaleInfoDto?.scaleName || '-' }}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">总分</span>
                        <span class="detail-value detail-score">{{ detailData.totalScore }} 分</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">结果标签</span>
                        <el-tag :type="getResultTagType(detailData.resultLabel)" size="small">{{ detailData.resultLabel || '-' }}</el-tag>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">完成时间</span>
                        <span class="detail-value">{{ detailData.finishTime || '-' }}</span>
                    </div>
                </div>
                <!-- 结果解读 -->
                <div class="detail-section" v-if="detailData.resultInterpretation">
                    <div class="detail-section-title">结果解读</div>
                    <div class="detail-interpretation">{{ detailData.resultInterpretation }}</div>
                </div>
                <!-- 作答详情 -->
                <div class="detail-section">
                    <div class="detail-section-title">作答详情</div>
                    <div class="answer-list" v-if="answerDetails.length > 0">
                        <div class="answer-item" v-for="(item, idx) in answerDetails" :key="idx">
                            <div class="answer-question">
                                <span class="answer-index">{{ idx + 1 }}</span>
                                <span class="answer-title">{{ item.questionTitle }}</span>
                            </div>
                            <div class="answer-options">
                                <span class="answer-option" v-for="opt in item.options" :key="opt.label"
                                    :class="{ selected: opt.score === item.selectedScore }">
                                    {{ opt.label }}（{{ opt.score }}分）
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="answer-empty" v-else>暂无作答详情数据</div>
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
                <el-table-column label="量表名称" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.scaleInfoDto?.scaleName }}</template>
                </el-table-column>
                <el-table-column label="总分" min-width="80">
                    <template #default="{ row }">{{ row.totalScore }}</template>
                </el-table-column>
                <el-table-column label="结果标签" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="getResultTagType(row.resultLabel)" size="small">{{ row.resultLabel || '-' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="完成时间" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.finishTime }}</template>
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
const detailData = ref(null);
const answerDetails = ref([]);

const getResultTagType = (label) => {
    if (!label) return 'info';
    if (label.includes('正常') || label.includes('良好')) return 'success';
    if (label.includes('轻度') || label.includes('偏高') || label.includes('一般')) return 'warning';
    return 'danger';
};

const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/assessmentRecord/list', { page: pagination.page, limit: pagination.limit, ...searchForm });
        if (success) { tableData.value = data.items || []; pagination.total = data.totalCount || 0; }
    } finally { loading.value = false; }
};

const handleCurrentChange = (page) => { pagination.page = page; fetchData(); };
const handleSizeChange = (size) => { pagination.limit = size; pagination.page = 1; fetchData(); };
const handleSelectionChange = (rows) => { selectedRows.value = rows; };
const searchClick = () => { pagination.page = 1; fetchData(); };
const resetClick = () => { Object.keys(searchForm).forEach(key => { searchForm[key] = undefined; }); pagination.page = 1; fetchData(); };

const showDetail = async (row) => {
    detailData.value = row;
    answerDetails.value = [];
    detailShow.value = true;

    // 解析作答JSON
    let answers = [];
    try {
        if (row.answerJson) {
            answers = typeof row.answerJson === 'string' ? JSON.parse(row.answerJson) : row.answerJson;
        }
    } catch { answers = []; }

    // 加载量表题目
    if (row.scaleInfoId) {
        const { data, success } = await post('/scaleQuestion/list', { page: 1, limit: 100, scaleInfoId: row.scaleInfoId });
        if (success) {
            const questions = (data.items || []).sort((a, b) => (a.questionIndex || 0) - (b.questionIndex || 0));
            answerDetails.value = questions.map((q, idx) => {
                let options = [];
                try { options = typeof q.optionJson === 'string' ? JSON.parse(q.optionJson) : (q.optionJson || []); } catch { options = []; }
                const answer = answers.find(a => a.questionIndex === q.questionIndex) || answers[idx];
                return {
                    questionTitle: q.questionTitle,
                    options,
                    selectedScore: answer ? answer.selectedScore : null
                };
            });
        }
    }
};

const showDeleteModal = async (id) => {
    try {
        await ElMessageBox.confirm('确认删除该记录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/assessmentRecord/deleteById', { id });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

const batchDelete = async () => {
    const ids = selectedRows.value.map(x => x.id);
    if (ids.length === 0) { ElMessage.warning('请先选择要删除的数据'); return; }
    try {
        await ElMessageBox.confirm('确认删除所选的记录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/assessmentRecord/batchDeleteByIds', { ids });
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

/* 详情弹窗 */
.detail-dialog { }
.detail-section { margin-bottom: 20px; }
.detail-section-title { font-size: 15px; font-weight: 600; color: #1E293B; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #F1F5F9; }
.detail-row { display: flex; align-items: center; gap: 12px; padding: 8px 0; border-bottom: 1px solid #F8FAFC; }
.detail-label { font-size: 13px; color: #94A3B8; width: 80px; flex-shrink: 0; }
.detail-value { font-size: 14px; color: #334155; }
.detail-score { font-size: 20px; font-weight: 700; color: #5B9BD5; }
.detail-interpretation { font-size: 14px; color: #334155; line-height: 1.7; padding: 12px; background: #F8FAFC; border-radius: 8px; }

/* 作答详情 */
.answer-list { }
.answer-item { padding: 14px 0; border-bottom: 1px solid #F1F5F9; }
.answer-item:last-child { border-bottom: none; }
.answer-question { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 10px; }
.answer-index { width: 24px; height: 24px; border-radius: 50%; background: #EBF3FB; color: #5B9BD5; font-size: 12px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.answer-title { font-size: 14px; color: #1E293B; line-height: 1.5; }
.answer-options { display: flex; flex-wrap: wrap; gap: 8px; padding-left: 34px; }
.answer-option { font-size: 13px; color: #64748B; padding: 4px 12px; border-radius: 6px; background: #F8FAFC; border: 1px solid #E2E8F0; }
.answer-option.selected { background: #EBF3FB; border-color: #5B9BD5; color: #5B9BD5; font-weight: 500; }
.answer-empty { text-align: center; padding: 20px 0; color: #94A3B8; font-size: 13px; }
</style>