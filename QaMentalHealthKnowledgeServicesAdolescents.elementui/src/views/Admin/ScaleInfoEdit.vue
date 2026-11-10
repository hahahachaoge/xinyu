<template>
    <div class="scale-edit-page">
        <!-- 量表基本信息 -->
        <el-card class="box-card">
            <template #header>
                <div class="card-title">
                    <svg viewBox="0 0 20 20" fill="#5B9BD5" width="18" height="18"><rect x="3" y="2" width="14" height="16" rx="2" stroke="#5B9BD5" stroke-width="1.5" fill="none"/><path d="M7 7h6M7 10h6M7 13h4" stroke="#5B9BD5" stroke-width="1.3" stroke-linecap="round"/></svg>
                    <span>{{ formData.id ? '编辑量表' : '新增量表' }}</span>
                </div>
            </template>
            <el-form ref="baseFormRef" :model="formData" :rules="baseRules" label-width="100px" size="default">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="量表名称" prop="scaleName">
                            <el-input v-model="formData.scaleName" placeholder="请输入量表名称" clearable />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="量表类型" prop="scaleType">
                            <SigleSelect url="/select/scaleTypeEnum" columnName="name" columnValue="code" v-model="formData.scaleType" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="适用人群" prop="targetGroup">
                            <el-input v-model="formData.targetGroup" placeholder="如：12-18岁青少年" clearable />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="量表描述" prop="scaleDesc">
                            <el-input type="textarea" :rows="3" v-model="formData.scaleDesc" placeholder="请输入量表描述" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </el-card>

        <!-- 题目与规则左右布局 -->
        <el-row :gutter="16">
            <!-- 左侧：测评题目 (60%) -->
            <el-col :span="14">
                <el-card class="box-card section-card">
                    <template #header>
                        <div class="card-title-row">
                            <div class="card-title">
                                <svg viewBox="0 0 20 20" fill="#6BBF73" width="18" height="18"><circle cx="10" cy="10" r="8" stroke="#6BBF73" stroke-width="1.5" fill="none"/><path d="M8 8a2 2 0 013.9.6c0 1.3-2 2-2 2" stroke="#6BBF73" stroke-width="1.3" stroke-linecap="round"/><circle cx="10" cy="13.5" r="0.6" fill="#6BBF73"/></svg>
                                <span>测评题目 ({{ questions.length }}题)</span>
                            </div>
                            <el-button type="primary" size="default" @click="addQuestion">
                                <svg viewBox="0 0 16 16" fill="currentColor" width="14" height="14"><path fill-rule="evenodd" d="M8 2a1 1 0 011 1v4h4a1 1 0 110 2H9v4a1 1 0 11-2 0V9H3a1 1 0 010-2h4V3a1 1 0 011-1z" clip-rule="evenodd"/></svg>
                                添加题目
                            </el-button>
                        </div>
                    </template>
                    <div class="section-scroll">
                        <div class="question-empty" v-if="questions.length === 0">
                            <svg viewBox="0 0 48 48" fill="none" width="48" height="48"><rect x="8" y="6" width="32" height="36" rx="4" fill="#F1F5F9"/><path d="M16 16h16M16 22h12M16 28h14" stroke="#CBD5E1" stroke-width="2" stroke-linecap="round"/></svg>
                            <p>暂无题目，点击上方按钮添加</p>
                        </div>
                        <div class="question-list" v-else>
                            <div class="question-card" v-for="(q, qIdx) in questions" :key="qIdx">
                                <div class="question-header">
                                    <span class="question-num">第 {{ qIdx + 1 }} 题</span>
                                    <div class="question-actions">
                                        <el-button size="small" :disabled="qIdx === 0" @click="moveQuestion(qIdx, -1)">
                                            <svg viewBox="0 0 16 16" fill="currentColor" width="12" height="12"><path d="M8 4l4 4H4z"/></svg>
                                        </el-button>
                                        <el-button size="small" :disabled="qIdx === questions.length - 1" @click="moveQuestion(qIdx, 1)">
                                            <svg viewBox="0 0 16 16" fill="currentColor" width="12" height="12"><path d="M8 12l4-4H4z"/></svg>
                                        </el-button>
                                        <el-button type="danger" size="small" @click="removeQuestion(qIdx)">删除</el-button>
                                    </div>
                                </div>
                                <el-form-item label="题目标题" class="question-title-input">
                                    <el-input v-model="q.questionTitle" placeholder="请输入题目内容" />
                                </el-form-item>
                                <el-form-item label="题目类型">
                                    <el-radio-group v-model="q.questionType">
                                        <el-radio :label="1">单选</el-radio>
                                        <el-radio :label="2">多选</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <div class="options-section">
                                    <div class="options-header">
                                        <span class="options-label">选项设置</span>
                                        <el-button size="small" @click="addOption(qIdx)">+ 添加选项</el-button>
                                    </div>
                                    <div class="option-row" v-for="(opt, oIdx) in q.options" :key="oIdx">
                                        <span class="option-letter">{{ String.fromCharCode(65 + oIdx) }}</span>
                                        <el-input v-model="opt.label" placeholder="选项内容" style="flex:1" />
                                        <el-input-number v-model="opt.score" :min="0" :max="100" placeholder="分值" style="width:100px" controls-position="right" />
                                        <span class="option-score-label">分</span>
                                        <el-button type="danger" size="small" circle :disabled="q.options.length <= 2" @click="removeOption(qIdx, oIdx)">
                                            <svg viewBox="0 0 16 16" fill="currentColor" width="12" height="12"><path d="M4 4l8 8M12 4l-8 8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧：评分规则 (40%) -->
            <el-col :span="10">
                <el-card class="box-card section-card">
                    <template #header>
                        <div class="card-title-row">
                            <div class="card-title">
                                <svg viewBox="0 0 20 20" fill="#F0A75B" width="18" height="18"><path d="M10 2l2.5 5h5.5l-4.5 3.5 2 5.5L10 13l-5.5 3 2-5.5L2 7h5.5z" stroke="#F0A75B" stroke-width="1.4" stroke-linejoin="round" fill="none"/></svg>
                                <span>评分规则 ({{ scoreRules.length }}条)</span>
                            </div>
                            <el-button type="primary" size="default" @click="addScoreRule">
                                <svg viewBox="0 0 16 16" fill="currentColor" width="14" height="14"><path fill-rule="evenodd" d="M8 2a1 1 0 011 1v4h4a1 1 0 110 2H9v4a1 1 0 11-2 0V9H3a1 1 0 010-2h4V3a1 1 0 011-1z" clip-rule="evenodd"/></svg>
                                添加规则
                            </el-button>
                        </div>
                    </template>
                    <div class="section-scroll">
                        <div class="rule-empty" v-if="scoreRules.length === 0">
                            <svg viewBox="0 0 48 48" fill="none" width="48" height="48"><path d="M24 8l5 10h11l-9 7 4 11L24 28l-11 8 4-11-9-7h11z" fill="#F1F5F9" stroke="#CBD5E1" stroke-width="2" stroke-linejoin="round"/></svg>
                            <p>暂无评分规则，点击上方按钮添加</p>
                        </div>
                        <div class="rule-list" v-else>
                            <div class="rule-card" v-for="(rule, rIdx) in scoreRules" :key="rIdx">
                                <div class="rule-header">
                                    <span class="rule-num">规则 {{ rIdx + 1 }}</span>
                                    <el-button type="danger" size="small" @click="removeScoreRule(rIdx)">删除</el-button>
                                </div>
                                <div class="rule-score-row">
                                    <div class="rule-field">
                                        <label>最低分</label>
                                        <el-input-number v-model="rule.minScore" :min="0" :max="999" style="width:100%" controls-position="right" size="small" />
                                    </div>
                                    <span class="rule-dash">—</span>
                                    <div class="rule-field">
                                        <label>最高分</label>
                                        <el-input-number v-model="rule.maxScore" :min="0" :max="999" style="width:100%" controls-position="right" size="small" />
                                    </div>
                                </div>
                                <div class="rule-form-item">
                                    <label>结果标签</label>
                                    <el-input v-model="rule.resultLabel" placeholder="如：正常、轻度焦虑" size="small" />
                                </div>
                                <div class="rule-form-item">
                                    <label>结果解读</label>
                                    <el-input type="textarea" :rows="2" v-model="rule.resultInterpretation" placeholder="对该分数区间的解读说明" size="small" />
                                </div>
                                <div class="rule-form-item">
                                    <label>建议</label>
                                    <el-input type="textarea" :rows="2" v-model="rule.suggestion" placeholder="针对该结果的建议" size="small" />
                                </div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 底部操作栏 -->
        <div class="bottom-bar">
            <el-button size="default" @click="goBack">取消返回</el-button>
            <el-button type="primary" size="default" :loading="saving" @click="saveAll">保存全部</el-button>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const baseFormRef = ref(null);
const saving = ref(false);

// 量表基本信息
const formData = reactive({
    id: undefined,
    scaleName: '',
    scaleDesc: '',
    scaleType: null,
    targetGroup: '',
    questionCount: 0
});

// 题目列表
const questions = ref([]);

// 评分规则列表
const scoreRules = ref([]);

const baseRules = {
    scaleName: [{ required: true, message: '请输入量表名称', trigger: 'blur' }],
    scaleType: [{ required: true, message: '请选择量表类型', trigger: 'change' }],
    scaleDesc: [{ required: true, message: '请输入量表描述', trigger: 'blur' }],
    targetGroup: [{ required: true, message: '请输入适用人群', trigger: 'blur' }],
};

// 加载量表详情（编辑模式）
const loadDetail = async (id) => {
    // 加载量表基本信息
    const { data, success } = await post('/scaleInfo/findById', { id });
    if (success && data) {
        Object.assign(formData, data);
    }
    // 加载题目列表
    const qRes = await post('/scaleQuestion/list', { scaleInfoId: id, page: 1, limit: 1000 });
    if (qRes.success && qRes.data) {
        const items = qRes.data.items || [];
        questions.value = items.map(q => ({
            id: q.id,
            questionIndex: q.questionIndex,
            questionTitle: q.questionTitle,
            questionType: q.questionType || 1,
            options: parseOptions(q.optionJson)
        })).sort((a, b) => a.questionIndex - b.questionIndex);
    }
    // 加载评分规则
    const rRes = await post('/scoreRule/list', { scaleInfoId: id, page: 1, limit: 1000 });
    if (rRes.success && rRes.data) {
        scoreRules.value = (rRes.data.items || []).map(r => ({
            id: r.id,
            minScore: r.minScore,
            maxScore: r.maxScore,
            resultLabel: r.resultLabel,
            resultInterpretation: r.resultInterpretation,
            suggestion: r.suggestion
        })).sort((a, b) => a.minScore - b.minScore);
    }
};

// 解析选项JSON
const parseOptions = (jsonStr) => {
    if (!jsonStr) return [{ label: '', score: 0 }, { label: '', score: 1 }];
    try {
        const arr = typeof jsonStr === 'string' ? JSON.parse(jsonStr) : jsonStr;
        return arr.map(o => ({ label: o.label || '', score: o.score ?? 0 }));
    } catch { return [{ label: '', score: 0 }, { label: '', score: 1 }]; }
};

// 添加题目
const addQuestion = () => {
    questions.value.push({
        id: undefined,
        questionIndex: questions.value.length + 1,
        questionTitle: '',
        questionType: 1,
        options: [
            { label: '从不', score: 0 },
            { label: '偶尔', score: 1 },
            { label: '经常', score: 2 },
            { label: '总是', score: 3 }
        ]
    });
};

// 移动题目
const moveQuestion = (idx, dir) => {
    const target = idx + dir;
    if (target < 0 || target >= questions.value.length) return;
    const temp = questions.value[idx];
    questions.value[idx] = questions.value[target];
    questions.value[target] = temp;
    questions.value = [...questions.value];
};

// 删除题目
const removeQuestion = (idx) => { questions.value.splice(idx, 1); };

// 添加选项
const addOption = (qIdx) => {
    questions.value[qIdx].options.push({ label: '', score: 0 });
};

// 删除选项
const removeOption = (qIdx, oIdx) => {
    if (questions.value[qIdx].options.length > 2) {
        questions.value[qIdx].options.splice(oIdx, 1);
    }
};

// 添加评分规则
const addScoreRule = () => {
    scoreRules.value.push({
        id: undefined, minScore: 0, maxScore: 0,
        resultLabel: '', resultInterpretation: '', suggestion: ''
    });
};

// 删除评分规则
const removeScoreRule = (idx) => { scoreRules.value.splice(idx, 1); };

// 保存全部
const saveAll = async () => {
    if (!baseFormRef.value) return;
    await baseFormRef.value.validate(async (valid) => {
        if (!valid) return;

        saving.value = true;
        try {
            // 1. 保存量表基本信息
            formData.questionCount = questions.value.length;
            const { data, success } = await post('/scaleInfo/addOrEdit', formData);
            if (!success) { ElMessage.error('保存量表信息失败'); return; }

            const scaleId = data.id || formData.id;

            // 2. 删除旧题目，重新保存
            if (formData.id) {
                // 获取旧题目ID列表并批量删除
                const oldQ = await post('/scaleQuestion/list', { scaleInfoId: scaleId, page: 1, limit: 1000 });
                if (oldQ.success && oldQ.data && oldQ.data.items) {
                    const oldIds = oldQ.data.items.map(q => q.id);
                    if (oldIds.length > 0) {
                        await post('/scaleQuestion/batchDeleteByIds', { ids: oldIds });
                    }
                }
            }
            // 逐条保存题目
            for (let i = 0; i < questions.value.length; i++) {
                const q = questions.value[i];
                await post('/scaleQuestion/addOrEdit', {
                    scaleInfoId: scaleId,
                    questionIndex: i + 1,
                    questionTitle: q.questionTitle,
                    questionType: q.questionType,
                    optionJson: JSON.stringify(q.options)
                });
            }

            // 3. 删除旧评分规则，重新保存
            if (formData.id) {
                const oldR = await post('/scoreRule/list', { scaleInfoId: scaleId, page: 1, limit: 1000 });
                if (oldR.success && oldR.data && oldR.data.items) {
                    const oldIds = oldR.data.items.map(r => r.id);
                    if (oldIds.length > 0) {
                        await post('/scoreRule/batchDeleteByIds', { ids: oldIds });
                    }
                }
            }
            // 逐条保存评分规则
            for (const rule of scoreRules.value) {
                await post('/scoreRule/addOrEdit', {
                    scaleInfoId: scaleId,
                    minScore: rule.minScore,
                    maxScore: rule.maxScore,
                    resultLabel: rule.resultLabel,
                    resultInterpretation: rule.resultInterpretation,
                    suggestion: rule.suggestion
                });
            }

            ElMessage.success('保存成功');
            router.push('/Admin/scaleInfoList');
        } catch (e) {
            ElMessage.error('保存失败');
        } finally {
            saving.value = false;
        }
    });
};

const goBack = () => { router.push('/Admin/scaleInfoList'); };

onMounted(() => {
    const id = route.query.id;
    if (id) {
        loadDetail(id);
    }
});
</script>

<style scoped lang="scss">
.scale-edit-page { padding-bottom: 80px; }
.box-card { margin-bottom: 16px; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: #1E293B; }
.card-title-row { display: flex; justify-content: space-between; align-items: center; }

/* 左右布局的卡片 */
.section-card {
    height: 100%;
    :deep(.el-card__body) { padding: 0; }
}
.section-scroll {
    max-height: calc(100vh - 340px);
    overflow-y: auto;
    padding: 16px;
}

/* 题目卡片 */
.question-empty, .rule-empty { text-align: center; padding: 40px 0; color: #94A3B8; font-size: 14px; }
.question-empty svg, .rule-empty svg { margin-bottom: 8px; }

.question-list { display: flex; flex-direction: column; gap: 14px; }
.question-card {
    border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px;
    background: #FAFCFF; transition: box-shadow 0.2s;
}
.question-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.question-header {
    display: flex; justify-content: space-between; align-items: center;
    margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #F1F5F9;
}
.question-num { font-size: 14px; font-weight: 600; color: #5B9BD5; }
.question-actions { display: flex; gap: 6px; }
.question-title-input { margin-bottom: 8px; }

/* 选项区域 */
.options-section { margin-top: 8px; padding: 12px; background: #fff; border-radius: 8px; border: 1px solid #F1F5F9; }
.options-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.options-label { font-size: 13px; color: #64748B; font-weight: 500; }
.option-row { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.option-row:last-child { margin-bottom: 0; }
.option-letter {
    width: 24px; height: 24px; border-radius: 50%; background: #EBF3FB; color: #5B9BD5;
    display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; flex-shrink: 0;
}
.option-score-label { font-size: 12px; color: #94A3B8; }

/* 评分规则 */
.rule-list { display: flex; flex-direction: column; gap: 14px; }
.rule-card {
    border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px;
    background: #FFFCF5; transition: box-shadow 0.2s;
}
.rule-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.rule-header {
    display: flex; justify-content: space-between; align-items: center;
    margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #FEF3E2;
}
.rule-num { font-size: 14px; font-weight: 600; color: #F0A75B; }
.rule-score-row {
    display: flex; align-items: flex-end; gap: 8px; margin-bottom: 10px;
}
.rule-dash { color: #94A3B8; font-size: 16px; padding-bottom: 6px; }
.rule-field { flex: 1; }
.rule-field label { display: block; font-size: 12px; color: #64748B; margin-bottom: 4px; }
.rule-form-item { margin-bottom: 10px; }
.rule-form-item:last-child { margin-bottom: 0; }
.rule-form-item label { display: block; font-size: 12px; color: #64748B; margin-bottom: 4px; }

/* 底部操作栏 */
.bottom-bar {
    position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
    background: #fff; border-top: 1px solid #E2E8F0; padding: 12px 24px;
    display: flex; justify-content: flex-end; gap: 12px;
    box-shadow: 0 -2px 8px rgba(0,0,0,0.04);
}
</style>
