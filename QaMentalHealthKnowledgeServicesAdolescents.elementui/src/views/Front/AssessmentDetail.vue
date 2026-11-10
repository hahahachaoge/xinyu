<template>
    <div class="assessment-detail-page assessment-record-design">
        <section class="record-hero" aria-labelledby="record-page-title"><p>看见此刻 · 理解自己</p><h1 id="record-page-title">每一次自我了解，都是成长的开始</h1><span>把结果当作一份观察，慢慢找到适合自己的节奏。</span></section>
        <nav class="record-breadcrumb" aria-label="当前位置"><router-link to="/front/home">首页</router-link><span>/</span><router-link to="/front/myAssessments">我的测评</router-link><span>/</span><span aria-current="page">测评详情</span></nav>
        <div v-if="loading" class="record-loading" aria-label="测评记录加载中" aria-busy="true"><el-skeleton :rows="12" animated /></div>
        <div v-else-if="loadError" class="record-error" role="status"><WellbeingIcon name="document" /><h2>{{ loadError === 'missing' ? '暂时找不到这份测评记录' : '测评记录暂时加载失败' }}</h2><p>可以稍后重试，或返回我的测评查看其他记录。</p><button v-if="loadError !== 'missing'" @click="loadData">重新加载</button><router-link to="/front/myAssessments">返回我的测评</router-link></div>
        <div v-else class="record-columns">
            <div class="record-main">
                <section class="record-summary" aria-labelledby="record-scale-title"><div class="summary-content"><span class="record-badge">测评结果</span><h2 id="record-scale-title">{{ record.scaleInfoDto?.scaleName || '测评量表' }}</h2><p class="summary-description">{{ record.scaleInfoDto?.scaleDesc || '关注此刻的感受，给自己一点慢慢了解的时间。' }}</p><div class="completion-row"><div class="completed-indicator"><span><WellbeingIcon name="leaf" /></span><strong>测评已完成</strong></div><div class="record-score"><strong>{{ record.totalScore ?? '—' }}</strong><span>分<small>测评总分</small></span></div></div><div class="record-status"><span v-if="record.resultLabel">{{ record.resultLabel }}</span><time v-if="record.finishTime" :datetime="record.finishTime">{{ record.finishTime }}</time></div></div><div class="record-summary-meta"><span><WellbeingIcon name="document" />{{ record.scaleInfoDto?.scaleTypeFormat || '心理测评' }}</span><span><List />共 {{ questionCount }} 题</span><a href="#record-answer-review" @click.prevent="jumpToReview"><WellbeingIcon name="search" />作答回顾</a></div></section>
                <p class="record-context-note"><InfoFilled />分数需要结合量表说明和个人感受理解。</p>

                <section class="record-interpretation record-panel" aria-labelledby="interpretation-title"><h2 id="interpretation-title"><WellbeingIcon name="book" />如何理解这份结果</h2><p v-if="record.resultInterpretation" class="saved-interpretation">{{ record.resultInterpretation }}</p><p v-else>这份测评是一段自我观察的起点。可以回顾近期的日常安排、休息情况，以及哪些事情让你感到压力。</p><p>一次测评不能完整定义你的状态，也不需要用分数给自己贴标签。</p><blockquote><span aria-hidden="true">“</span>比起急着评价自己，先听见自己的感受。</blockquote></section>

                <section class="record-review record-panel" id="record-answer-review" aria-labelledby="review-title"><h2 id="review-title"><Tickets />作答回顾</h2><p class="review-description">以下为这份测评记录对应的作答。</p><p v-if="reviewLoadError" class="review-message" role="status">题目暂时加载失败。<button @click="loadReview">重新加载作答回顾</button></p><p v-else-if="review.invalid" class="review-message">历史作答记录暂时无法解析，已保存的分数与解读仍可查看。</p><div v-else-if="review.rows.length" class="record-answer-list"><article v-for="(row, index) in review.rows" :key="row.id" class="record-answer-row"><div class="record-answer-line"><span class="record-answer-index">{{ String(index + 1).padStart(2, '0') }}</span><h3>{{ row.questionTitle }}</h3><span class="record-choice" :class="{ unavailable: row.status !== 'recorded' }"><small>你的选择：</small>{{ row.selectedLabel }}<Check v-if="row.status === 'recorded'" aria-hidden="true" /></span></div><details v-if="row.options.length" class="record-option-details"><summary>查看全部选项</summary><ul><li v-for="(option, optionIndex) in row.options" :key="optionIndex" :class="{ 'was-selected': row.status === 'recorded' && row.selectedLabel === option.label }"><span>{{ option.label }}</span><small>{{ option.score }}分</small></li></ul></details></article></div><p v-else class="review-message">暂无可回顾的题目与作答记录。</p><div class="record-reference-note"><InfoFilled />测评结果仅供自我了解，不替代专业诊断与治疗。</div><div class="record-actions"><router-link to="/front/myAssessments"><Back />返回我的测评</router-link><router-link v-if="validScaleId" :to="{ path: '/front/scaleTest', query: { id: validScaleId } }" class="record-retake">重新测评 <Right /></router-link><router-link v-else to="/front/scaleList" class="record-retake">查看测评列表 <Right /></router-link></div></section>
            </div>

            <aside class="record-sidebar"><section class="record-companion"><img :src="companionImage" alt="女孩在窗边书写，为自己的感受留一点时间" /><p>你不需要<br />一次解决<br />所有问题<span>—</span></p></section><section class="record-support-card"><h2><WellbeingIcon name="leaf" />给此刻的你，一点支持</h2><ul><li><span class="support-icon"><WellbeingIcon name="document" /></span><div><h3>把任务拆小</h3><p>从今天可以做到的一小步开始。</p></div></li><li><span class="support-icon"><Moon /></span><div><h3>给休息留白</h3><p>为睡眠、活动与放松留出时间。</p></div></li><li><span class="support-icon"><WellbeingIcon name="people" /></span><div><h3>让支持靠近</h3><p>和信任的人聊聊自己的感受。</p></div></li></ul></section><section class="record-knowledge-card"><span>心理知识</span><h2>{{ knowledgeTitle }}</h2><p>从更多角度认识自己，找到适合自己的应对方式。</p><router-link :to="{ path: '/front/articleList', query: { title: knowledgeKeyword } }">阅读相关知识 <Right /></router-link></section><section class="record-chat-card"><h2><ChatDotRound />还有想说的吗？</h2><p>带着你的问题，继续探索新的理解。</p><router-link to="/front/aiChat">去智能问答 <Right /></router-link></section><p class="record-sidebar-note"><InfoFilled />当困扰持续影响生活时，可以向心理老师或专业人员寻求支持。</p></aside>
        </div>
        <section class="record-closing"><p>成长没有标准答案，你可以慢慢来。</p><span>在更好的时光里<br />遇见更好的自己。</span></section>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { buildAssessmentReview } from '@/utils/assessmentReview';
import companionImage from '@/assets/test-writing-companion.png';
import { Back, ChatDotRound, Check, InfoFilled, List, Moon, Right, Tickets } from '@element-plus/icons-vue';
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const loading = ref(true);
const loadError = ref('');
const record = ref({});
const questions = ref([]);
const reviewLoadError = ref(false);
let loadEpoch = 0;
let reviewEpoch = 0;
let disposed = false;
const review = computed(() => buildAssessmentReview(questions.value, record.value.answerJson));
const questionCount = computed(() => record.value.scaleInfoDto?.questionCount ?? questions.value.length);
const validScaleId = computed(() => { const id = Number(record.value.scaleInfoId); return Number.isInteger(id) && id > 0 ? id : null; });
const knowledgeKeyword = computed(() => { const topic = `${record.value.scaleInfoDto?.scaleName || ''} ${record.value.scaleInfoDto?.scaleTypeFormat || ''}`; if (/学业|学习|压力/.test(topic)) return '压力'; if (/人际|关系|社交/.test(topic)) return '人际'; return '情绪'; });
const knowledgeTitle = computed(() => ({ 压力: '继续了解学习与压力', 人际: '继续了解关系与沟通', 情绪: '继续了解情绪与自我' })[knowledgeKeyword.value]);
const isCurrent = (epoch, id) => !disposed && epoch === loadEpoch && id === Number(route.query.id);
const jumpToReview = async () => { await nextTick(); document.getElementById('record-answer-review')?.scrollIntoView({ block: 'start', behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth' }); };
const loadReview = async () => {
    const epoch = loadEpoch, id = Number(route.query.id), request = ++reviewEpoch, scaleId = validScaleId.value;
    reviewLoadError.value = false;
    if (!scaleId) return;
    try {
        const result = await post('/scaleQuestion/list', { page: 1, limit: 100, scaleInfoId: scaleId });
        if (!isCurrent(epoch, id) || request !== reviewEpoch) return;
        if (!result.success) throw new Error('题目加载失败');
        questions.value = Array.isArray(result.data?.items) ? result.data.items : [];
    } catch { if (isCurrent(epoch, id) && request === reviewEpoch) reviewLoadError.value = true; }
};
const loadData = async () => {
    const id = Number(route.query.id), epoch = ++loadEpoch;
    reviewEpoch++; loading.value = true; loadError.value = ''; reviewLoadError.value = false;
    record.value = {}; questions.value = [];
    if (!Number.isInteger(id) || id <= 0) { loadError.value = 'missing'; loading.value = false; return; }
    try {
        const result = await post('/assessmentRecord/findById', { id });
        if (!isCurrent(epoch, id)) return;
        if (!result.success) throw new Error('记录加载失败');
        if (!result.data?.id) { loadError.value = 'missing'; return; }
        record.value = result.data;
        await loadReview();
    } catch { if (isCurrent(epoch, id)) loadError.value = 'failed'; }
    finally { if (isCurrent(epoch, id)) loading.value = false; }
};
watch(() => route.query.id, loadData, { immediate: true });
onBeforeUnmount(() => { disposed = true; loadEpoch++; reviewEpoch++; });
</script>

<style scoped lang="scss" src="@/views/Front/assessment-detail-design.scss"></style>
