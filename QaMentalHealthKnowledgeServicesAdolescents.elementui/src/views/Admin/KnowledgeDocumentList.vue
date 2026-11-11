<template>
    <div>
        <el-alert v-if="indexAudit" :type="indexAudit.healthy ? 'success' : 'warning'" :closable="false"
            :title="`向量索引：${indexAudit.matchedCount}/${indexAudit.chunkCount} 个分块匹配，${indexAudit.vectorCount} 条真实向量`"
            :description="indexAudit.healthy ? `索引一致；待处理文档 ${indexAudit.pendingDocumentIds.length} 篇。` : '索引不一致时，可对已有分块重建向量；缺少分块的文档需要重新上传原文件。'" show-icon style="margin-bottom:16px" />
        <el-button type="warning" @click="rebuildAll" style="margin-bottom:16px">重建全部分块向量</el-button>
        <el-button @click="refreshIndex" :loading="auditLoading" style="margin-bottom:16px">刷新索引检查</el-button>
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
                    <el-form-item label="文档标题">
                        <el-input v-model.trim="searchForm.docTitle" placeholder="请输入文档标题" clearable
                            class="search-input" />
                    </el-form-item>
                    <el-form-item label="知识分类">
                        <SigleSelect url="/knowledgeType/list" class="search-input" columnName="typeName"
                            :clearable="true" columnValue="id" v-model="searchForm.knowledgeTypeId" />
                    </el-form-item>
                    <el-form-item label="向量化状态">
                        <SigleSelect url="/select/vectorStatusTypeEnum" class="search-input" columnName="name"
                            :clearable="true" columnValue="code" v-model="searchForm.vectorStatusType" />
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 上传/编辑对话框 -->
        <el-dialog :title="formData.id ? '修改知识文档' : '上传知识文档'" v-model="editorShow" width="50%" :lock-scroll="true"
            :close-on-click-modal="false">
            <el-form v-if="editorShow" ref="editModalForm" :rules="editModalFormRules" :model="formData"
                label-width="120px" size="default">
                <el-row :gutter="10" class="edit-from-body">
                    <el-col :span="24">
                        <el-form-item label="上传文件" prop="fileUrl">
                            <UploadFiles :limit="1" v-model="formData.fileUrl" />
                            <div class="form-tip">支持 PDF、TXT、Markdown 格式，上传后自动AI解析填充信息</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="parseLoading">
                        <div class="parse-loading">
                            <el-icon class="is-loading">
                                <Loading />
                            </el-icon>
                            <span>AI正在解析文件内容，自动提取标题、分类和描述...</span>
                        </div>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="文档标题" prop="docTitle">
                            <el-input v-model="formData.docTitle" placeholder="请输入文档标题（上传文件后自动填充）" clearable />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="知识分类" prop="knowledgeTypeId">
                            <SigleSelect url="/knowledgeType/list" columnName="typeName" columnValue="id"
                                v-model="formData.knowledgeTypeId" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="文档描述" prop="docDesc">
                            <el-input type="textarea" :rows="4" v-model="formData.docDesc"
                                placeholder="请输入文档描述（上传文件后自动填充）" />
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

        <!-- 文档详情对话框 -->
        <el-dialog title="文档详情" v-model="detailShow" width="65%" :lock-scroll="true">
            <div v-if="detailData.id" class="detail-container">
                <div class="detail-header">
                    <div class="detail-title">{{ detailData.docTitle }}</div>
                    <el-tag :type="getStatusTagType(detailData.vectorStatusType)" size="default">
                        {{ detailData.vectorStatusTypeFormat }}
                    </el-tag>
                </div>
                <div class="detail-meta">
                    <span>分类：{{ detailData.knowledgeTypeDto?.typeName || '-' }}</span>
                    <span>格式：{{ detailData.fileFormat || '-' }}</span>
                    <span>大小：{{ detailData.fileSize || '-' }}</span>
                    <span>分块数：{{ detailData.chunkCount || 0 }}</span>
                    <span>上传人：{{ detailData.uploaderDto?.userName || '-' }}</span>
                    <span>上传时间：{{ detailData.creationTime || '-' }}</span>
                    <span v-if="detailData.vectorTime">向量化时间：{{ detailData.vectorTime }}</span>
                </div>
                <div class="detail-desc" v-if="detailData.docDesc">
                    <div class="detail-label">文档描述</div>
                    <div class="detail-text">{{ detailData.docDesc }}</div>
                </div>
                <div class="detail-desc">
                    <strong>文档标注的外部出处</strong>
                    <p v-for="source in documentSources" :key="source.url">
                        <a :href="source.url" target="_blank" rel="noopener noreferrer">{{ source.title }} ↗</a>
                    </p>
                    <p v-if="!documentSources.length">尚未标注可核对的外部出处。</p>
                </div>
                <!-- 分块信息 -->
                <div class="detail-chunks" v-if="chunkList.length > 0">
                    <div class="detail-label">分块信息（共 {{ chunkList.length }} 块）</div>
                    <div class="chunk-list">
                        <div class="chunk-item" v-for="chunk in chunkList" :key="chunk.id">
                            <div class="chunk-header">
                                <span class="chunk-index">#{{ chunk.chunkIndex }}</span>
                                <span class="chunk-token">{{ chunk.tokenCount }} 字符</span>
                                <span class="chunk-vector" v-if="chunk.vectorId">
                                    <el-tag type="success" size="small">已向量化</el-tag>
                                </span>
                            </div>
                            <div class="chunk-content">{{ chunk.chunkContent }}</div>
                        </div>
                    </div>
                </div>
                <el-empty v-else-if="detailData.vectorStatusType === 3" description="暂无分块数据" />
            </div>
        </el-dialog>

        <!-- 数据表格 -->
        <el-card class="box-card">
            <div class="table-header">
                <el-button type="primary" size="default" @click="showUploadModal()">
                    <el-icon>
                        <Edit />
                    </el-icon>上传文档
                </el-button>
                <el-button type="danger" size="default" @click="batchDelete">
                    <el-icon>
                        <Delete />
                    </el-icon>批量删除
                </el-button>
            </div>
            <el-table v-loading="loading" :data="tableData" stripe style="width: 100%"
                @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column label="文档标题" min-width="180" show-overflow-tooltip>
                    <template #default="{ row }">
                        <el-button type="primary" link @click="showDetail(row.id)">{{ row.docTitle }}</el-button>
                    </template>
                </el-table-column>
                <el-table-column label="知识分类" min-width="120" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.knowledgeTypeDto?.typeName || '-' }}</template>
                </el-table-column>
                <el-table-column label="文件格式" min-width="90">
                    <template #default="{ row }">
                        <el-tag v-if="row.fileFormat" size="small" type="info">{{ row.fileFormat }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="文件大小" min-width="90">
                    <template #default="{ row }">{{ row.fileSize || '-' }}</template>
                </el-table-column>
                <el-table-column label="分块数" min-width="80">
                    <template #default="{ row }">{{ row.chunkCount || 0 }}</template>
                </el-table-column>
                <el-table-column label="向量化状态" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="getStatusTagType(row.vectorStatusType)" size="default">
                            {{ row.vectorStatusTypeFormat }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="上传人" min-width="100" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.uploaderDto?.userName || '-' }}</template>
                </el-table-column>
                <el-table-column label="上传时间" min-width="160" show-overflow-tooltip>
                    <template #default="{ row }">{{ row.creationTime }}</template>
                </el-table-column>
                <el-table-column label="操作" width="530" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="default" @click="showDetail(row.id)">详情</el-button>
                        <el-button @click="showEditModal(row.id)" :disabled="row.vectorStatusType === 2">编辑</el-button>
                        <el-button type="warning" size="default" @click="handleReVectorize(row)"
                            :disabled="row.vectorStatusType === 2">
                            重新向量化
                        </el-button>
                        <el-button v-if="row.chunkCount > 0" @click="rebuildExisting(row)" :disabled="row.vectorStatusType === 2">重建分块向量</el-button>
                        <el-button type="danger" size="default" @click="showDeleteModal(row.id)">删除</el-button>
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
import { useCommonStore } from "@/store";
import { Delete, Edit, Loading, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, reactive, ref, computed, watch } from 'vue';

const commonStore = useCommonStore();
const userId = computed(() => commonStore.userId);

const searchForm = reactive({});
const formData = reactive({
    docTitle: '',
    knowledgeTypeId: null,
    docDesc: '',
    fileUrl: '',
    uploaderId: null
});
const editorShow = ref(false);
const originalFileUrl = ref('');
const editModalForm = ref(null);
const detailShow = ref(false);
const detailData = ref({});
const documentSources = ref([]);
const chunkList = ref([]);
const detailAiLoading = ref(false);
const parseLoading = ref(false);
const tableData = ref([]);
const loading = ref(false);
const indexAudit = ref(null);
const auditLoading = ref(false);
const refreshIndex = async () => {
    auditLoading.value = true;
    try { const result = await post('/knowledgeDocument/indexAudit', {}); if(result.success) indexAudit.value = result.data; }
    finally { auditLoading.value = false; }
};
const rebuildAll = async () => {
    try {
        await ElMessageBox.confirm('对所有已有分块顺序重建向量？这会调用嵌入服务；缺少原文件及分块的文档会跳过。', '重建全部向量', { type:'warning' });
        const result = await post('/knowledgeDocument/rebuildAllVectors', {});
        if (result.success) { ElMessage.success('已开始顺序重建，请稍后刷新索引检查'); fetchData(); }
    } catch { }
};
const rebuildExisting = async (row) => {
    try {
        await ElMessageBox.confirm(`基于「${row.docTitle}」已有分块重新生成向量？这会调用嵌入服务。`, '重建向量', { type:'warning' });
        const result = await post('/knowledgeDocument/rebuildVectors', { id: row.id });
        if(result.success) { ElMessage.success('已开始重建，请稍后刷新索引检查'); fetchData(); }
    } catch { }
};
const selectedRows = ref([]);

const pagination = reactive({ page: 1, limit: 10, total: 0 });

const editModalFormRules = reactive({
    docTitle: [{ required: true, message: '请输入文档标题', trigger: 'blur' }],
    knowledgeTypeId: [{ required: true, message: '请选择知识分类', trigger: 'blur' }],
    fileUrl: [{ required: true, message: '请上传文件', trigger: 'blur' }],
});

const getStatusTagType = (status) => {
    const map = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' };
    return map[status] || 'info';
};

const fetchData = async () => {
    loading.value = true;
    try {
        const { data, success } = await post('/knowledgeDocument/list', {
            page: pagination.page, limit: pagination.limit, ...searchForm
        });
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

// 打开上传弹窗（新增）
const showUploadModal = () => {
    Object.keys(formData).forEach(key => delete formData[key]);
    Object.assign(formData, { docTitle: '', knowledgeTypeId: null, docDesc: '', fileUrl: '', uploaderId: userId.value });
    editorShow.value = true;
};

// 文件上传完成后自动AI解析
const onFileUploaded = async (val) => {
    if (!formData.fileUrl || formData.id) return; // 仅新增时自动解析
    parseLoading.value = true;
    try {
        const { data, success } = await post('/knowledgeDocument/parseFile', { fileUrl: formData.fileUrl });
        if (success && data) {
            if (data.title && !formData.docTitle) formData.docTitle = data.title;
            if (data.categoryId) formData.knowledgeTypeId = data.categoryId;
            if (data.description) formData.docDesc = data.description;
            ElMessage.success({ message: 'AI解析完成，已自动填充信息', duration: 2000 });
        }
    } catch (e) {
        // 解析失败不影响上传
    } finally {
        parseLoading.value = false;
    }
};

// 监听fileUrl变化，新增时自动触发AI解析
watch(() => formData.fileUrl, (newVal, oldVal) => {
    if (newVal && newVal !== oldVal && !formData.id && editorShow.value) {
        onFileUploaded();
    }
});

// 打开编辑弹窗
const showEditModal = async (id) => {
    Object.keys(formData).forEach(key => delete formData[key]);
    if (id) {
        const { data, success } = await post('/knowledgeDocument/findById', { id });
        if (success && data) {
            Object.assign(formData, data);
            formData.fileUrl = data.fileUrl || '';
            originalFileUrl.value = formData.fileUrl;
        }
    }
    editorShow.value = true;
};

// 提交表单
const addOrEditForm = async () => {
    if (!editModalForm.value) return;
    await editModalForm.value.validate(async valid => {
        if (valid) {
            // 新增时使用 uploadDocument 接口（自动触发向量化）
            const url = formData.id ? '/knowledgeDocument/addOrEdit' : '/knowledgeDocument/uploadDocument';
            if (!formData.uploaderId) formData.uploaderId = userId.value;
            const { success } = await post(url, formData);
            if (success) {
                if (formData.id && formData.fileUrl !== originalFileUrl.value) {
                    const indexed = await post('/knowledgeDocument/reVectorize', { id: formData.id });
                    if (!indexed.success) ElMessage.warning('文件已保存，请点击重新向量化重试');
                }
                editorShow.value = false;
                fetchData();
                ElMessage.success(formData.id ? '修改成功' : '上传成功，已开始向量化处理');
            }
        }
    });
};

// 查看详情
const showDetail = async (id) => {
    const { data, success } = await post('/knowledgeDocument/findById', { id });
    if (success && data) {
        detailData.value = data;
        documentSources.value = [];
        const sources = await post('/knowledgeDocument/sources', { id });
        if (sources.success) documentSources.value = sources.data || [];
        // 加载分块数据
        const chunkRes = await post('/documentChunk/list', { knowledgeDocumentId: id, limit: 1000, page: 1 });
        if (chunkRes.success) {
            chunkList.value = chunkRes.data.items || [];
        }
        detailShow.value = true;
    }
};

// 重新向量化
const handleReVectorize = async (row) => {
    try {
        await ElMessageBox.confirm(`确认对「${row.docTitle}」重新执行向量化处理吗？`, '提示', {
            confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        });
        const { success } = await post('/knowledgeDocument/reVectorize', { id: row.id });
        if (success) {
            ElMessage.success('已开始重新向量化处理');
            fetchData();
        }
    } catch { }
};

// AI汇总（表格行）
const handleAiSummary = async (row) => {
    if (!row.fileUrl) { ElMessage.warning('该文档未上传文件，无法AI汇总'); return; }
    row._aiLoading = true;
    try {
        const { data, success } = await post('/knowledgeDocument/aiSummary', { id: row.id });
        if (success && data) {
            row.docDesc = data;
            ElMessage.success({ message: 'AI汇总完成', duration: 1500 });
        }
    } finally { row._aiLoading = false; }
};

// AI汇总（详情弹窗）
const handleAiSummaryDetail = async () => {
    if (!detailData.value.fileUrl) { ElMessage.warning('该文档未上传文件，无法AI汇总'); return; }
    detailAiLoading.value = true;
    try {
        const { data, success } = await post('/knowledgeDocument/aiSummary', { id: detailData.value.id });
        if (success && data) {
            detailData.value.docDesc = data;
            ElMessage.success({ message: 'AI汇总完成', duration: 1500 });
        }
    } finally { detailAiLoading.value = false; }
};

const searchClick = () => { pagination.page = 1; fetchData(); };
const resetClick = () => {
    Object.keys(searchForm).forEach(key => { searchForm[key] = undefined; });
    pagination.page = 1;
    fetchData();
};

const showDeleteModal = async (id) => {
    try {
        await ElMessageBox.confirm('确认删除该文档及其关联的向量数据吗？', '提示', {
            confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        });
        const { success } = await post('/knowledgeDocument/deleteById', { id });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { }
};

const batchDelete = async () => {
    const ids = selectedRows.value.map(x => x.id);
    if (ids.length === 0) { ElMessage.warning('请先选择要删除的数据'); return; }
    try {
        await ElMessageBox.confirm('确认删除所选文档及其关联数据吗？', '提示', {
            confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        });
        const { success } = await post('/knowledgeDocument/batchDeleteByIds', { ids });
        if (success) { fetchData(); ElMessage.success('删除成功'); }
    } catch { }
};

onMounted(() => { fetchData(); refreshIndex(); });
</script>

<style scoped lang="scss">
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

.form-tip {
    font-size: 12px;
    color: #94A3B8;
    margin-top: 4px;
}

.parse-loading {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: #EBF3FB;
    border-radius: 8px;
    margin-bottom: 16px;
    font-size: 13px;
    color: #5B9BD5;
}

/* 详情弹窗 */
.detail-container {
    padding: 0 10px;
}

.detail-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
}

.detail-title {
    font-size: 18px;
    font-weight: 600;
    color: #1E293B;
}

.detail-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 20px;
    font-size: 13px;
    color: #64748B;
    padding: 12px 16px;
    background: #F8FAFC;
    border-radius: 8px;
    margin-bottom: 16px;
}

.detail-label {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
    margin-bottom: 8px;
}

.detail-text {
    font-size: 13px;
    color: #475569;
    line-height: 1.6;
    margin-bottom: 16px;
}

.detail-desc {
    margin-bottom: 16px;
}

/* 分块列表 */
.chunk-list {
    max-height: 400px;
    overflow-y: auto;
}

.chunk-item {
    border: 1px solid #E2E8F0;
    border-radius: 8px;
    padding: 12px 16px;
    margin-bottom: 10px;
}

.chunk-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
    font-size: 12px;
    color: #94A3B8;
}

.chunk-index {
    font-weight: 600;
    color: #5B9BD5;
}

.chunk-content {
    font-size: 13px;
    color: #475569;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>
