<template>
    <div class="scale-test-page assessment-answer-design">
        <section class="answer-hero" aria-labelledby="answer-page-title"><p>听见内心 · 看见成长</p><h1 id="answer-page-title">给自己一点时间，听听内心的声音</h1><span>没有标准答案，按真实感受作答就好。</span></section>
        <div class="answer-workspace">
            <nav class="answer-breadcrumb" aria-label="当前位置"><router-link to="/front/home">首页</router-link><span>/</span><router-link to="/front/scaleList">心理测评</router-link><span>/</span><span aria-current="page">{{ result ? '测评结果' : '在线测评' }}</span></nav>
            <div v-if="loading" class="answer-loading" aria-busy="true" aria-label="测评题目加载中"><el-skeleton :rows="10" animated /></div>
            <div v-else-if="loadError" class="answer-error" role="status"><WellbeingIcon name="document" /><h2>{{ loadError === 'missing' ? '暂时找不到这份测评' : '测评题目暂时加载失败' }}</h2><p>可以稍后重试，或先看看其他适合你的测评。</p><button v-if="loadError !== 'missing'" @click="loadData">重新加载</button><router-link to="/front/scaleList">返回测评列表</router-link></div>
            <div v-else class="answer-columns">
                <section v-if="!result" class="answer-paper" aria-labelledby="assessment-title">
                    <header class="answer-paper-header"><span class="answer-category">{{ scaleInfo.scaleTypeFormat || '心理测评' }}</span><h2 id="assessment-title">{{ scaleInfo.scaleName }}</h2><p>{{ scaleInfo.scaleDesc }}</p></header>
                    <div class="answer-progress"><label for="assessment-progress">答题进度</label><progress id="assessment-progress" :value="answeredCount" :max="questions.length">{{ progressPercent }}%</progress><span>第 {{ currentIndex + 1 }} 题 / 共 {{ questions.length }} 题</span></div>
                    <nav class="question-navigation" aria-label="选择题目"><button v-for="(question, index) in questions" :key="question.id" :class="{ current: currentIndex === index, answered: hasQuestionAnswer(questions, answers, index) }" :aria-current="currentIndex === index ? 'step' : undefined" :aria-label="`第 ${index + 1} 题，${hasQuestionAnswer(questions, answers, index) ? '已作答' : '未作答'}`" :disabled="submitting" @click="goToQuestion(index)">{{ index + 1 }}</button></nav>
                    <div class="answer-question"><span class="answer-category question-kind">{{ currentQuestion.questionTypeFormat || '单选' }}题</span><h3 ref="questionHeading" tabindex="-1" aria-live="polite">{{ currentQuestion.questionTitle }}</h3><p>请选择最符合你真实感受的一项。</p>
                        <fieldset class="answer-options" :disabled="submitting"><legend class="answer-sr-only">{{ currentQuestion.questionTitle }}</legend><label v-for="(option, index) in currentOptions" :key="`${currentQuestion.id}-${index}`" class="answer-option" :class="{ selected: answers[currentIndex] === index }"><input v-model="answers[currentIndex]" type="radio" :name="'assessment-question-' + currentQuestion.id" :value="index" /><span class="option-letter">{{ String.fromCharCode(65 + index) }}.</span><span class="answer-option-label">{{ option.label }}</span><CircleCheckFilled v-if="answers[currentIndex] === index" class="answer-selected-mark" /></label></fieldset>
                    </div>
                    <div class="answer-actions"><button class="previous-answer" :disabled="currentIndex === 0 || submitting" @click="goToQuestion(currentIndex - 1)"><Back />上一题</button><button v-if="currentIndex < questions.length - 1" class="next-answer" :disabled="!hasQuestionAnswer(questions, answers, currentIndex) || submitting" @click="goToQuestion(currentIndex + 1)">下一题 <Right /></button><button v-else class="next-answer" :disabled="answeredCount !== questions.length || submitting" @click="submitTest">{{ submitting ? '正在提交…' : '提交测评' }}<Right v-if="!submitting" /></button></div>
                    <p v-if="submitError" class="answer-submit-error" role="alert">{{ submitError }} 你的作答仍保留在当前页面，可再次提交。</p>
                    <p v-if="currentIndex === questions.length - 1 && answeredCount < questions.length" class="answer-incomplete">还有 {{ questions.length - answeredCount }} 题未作答，可点击上方题号补充。</p>
                    <p class="answer-privacy"><WellbeingIcon name="shield" />测评用于自我了解，结果仅供参考。</p>
                </section>
                <section v-else class="answer-outcome" aria-labelledby="outcome-title"><span class="outcome-icon"><CircleCheckFilled /></span><p class="outcome-eyebrow">谢谢你认真倾听自己</p><h2 id="outcome-title">测评完成</h2><p class="outcome-scale">{{ scaleInfo.scaleName }}</p><div class="outcome-score">{{ result.totalScore }}<span>分</span></div><span class="outcome-label">{{ result.resultLabel }}</span><p class="outcome-interpretation">{{ result.resultInterpretation }}</p><div v-if="result.suggestion" class="outcome-suggestion"><h3>给你的一点建议</h3><p>{{ result.suggestion }}</p></div><p class="outcome-disclaimer">结果仅供自我了解，不替代专业诊断与治疗。</p><div class="outcome-actions"><router-link to="/front/scaleList">返回测评列表</router-link><button @click="retakeTest">重新测评</button><router-link to="/front/aiChat">聊聊我的感受 <Right /></router-link></div></section>
                <aside class="answer-sidebar"><div class="answer-companion"><img :src="companionImage" alt="女孩在阳光与花朵环绕的窗边认真书写" /><span>慢慢来，<br />按自己的节奏。<small>♡</small></span></div><section class="answer-reminders"><h2><WellbeingIcon name="leaf" />作答小提醒</h2><ul><li><Headset />选择安静、舒适的环境</li><li><WellbeingIcon name="document" />根据近期的真实感受回答</li><li><WellbeingIcon name="heart" />不必反复揣测，尊重第一感受。</li></ul></section><div class="answer-support"><WellbeingIcon name="leaf" /><p>如有持续困扰，可向心理老师或专业人员寻求支持。</p></div></aside>
            </div>
            <div class="answer-bottom"><router-link to="/front/scaleList"><Back />返回测评列表</router-link><p><WellbeingIcon name="book" />每一次认真倾听自己，都是成长的一小步。</p></div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from '@/store';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { answeredQuestionCount, hasQuestionAnswer, parseQuestionOptions } from '@/utils/assessmentAnswers';
import companionImage from '@/assets/test-writing-companion.png';
import { Back, CircleCheckFilled, Headset, Right } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const commonStore = useCommonStore();
const route = useRoute();
const router = useRouter();
const loading = ref(true);
const loadError = ref('');
const submitting = ref(false);
const submitError = ref('');
const currentIndex = ref(0);
const answers = ref({});
const result = ref(null);
const scaleInfo = ref({});
const questions = ref([]);
const questionHeading = ref(null);
let loadEpoch = 0;
let disposed = false;
const currentQuestion = computed(() => questions.value[currentIndex.value] || {});
const currentOptions = computed(() => parseQuestionOptions(currentQuestion.value));
const answeredCount = computed(() => answeredQuestionCount(questions.value, answers.value));
const progressPercent = computed(() => questions.value.length ? Math.round(answeredCount.value / questions.value.length * 100) : 0);
const isCurrent = (epoch, id) => !disposed && epoch === loadEpoch && id === Number(route.query.id);
const resetAnswers = () => { currentIndex.value = 0; answers.value = {}; result.value = null; submitError.value = ''; };
const loadData = async () => {
    const epoch = ++loadEpoch, id = Number(route.query.id);
    loading.value = true; loadError.value = ''; submitting.value = false; resetAnswers();
    scaleInfo.value = {}; questions.value = [];
    if (!Number.isInteger(id) || id <= 0) { loadError.value = 'missing'; loading.value = false; return; }
    try {
        const [info, questionList] = await Promise.all([
            post('/scaleInfo/findById', { id }),
            post('/scaleQuestion/list', { page: 1, limit: 100, scaleInfoId: id })
        ]);
        if (!isCurrent(epoch, id)) return;
        if (!info.success || !questionList.success) throw new Error('加载失败');
        if (!info.data?.id) { loadError.value = 'missing'; return; }
        const items = Array.isArray(questionList.data?.items) ? [...questionList.data.items] : [];
        items.sort((a, b) => (a.questionIndex || 0) - (b.questionIndex || 0));
        if (!items.length || items.some(item => !parseQuestionOptions(item).length)) throw new Error('测评配置不完整');
        scaleInfo.value = info.data; questions.value = items;
    } catch { if (isCurrent(epoch, id)) loadError.value = 'failed'; }
    finally { if (isCurrent(epoch, id)) loading.value = false; }
};
const goToQuestion = async (index) => { if (submitting.value || index < 0 || index >= questions.value.length) return; currentIndex.value = index; await nextTick(); questionHeading.value?.focus({ preventScroll: true }); };
const submitTest = async () => {
    if (submitting.value || result.value) return;
    const firstMissing = questions.value.findIndex((_, index) => !hasQuestionAnswer(questions.value, answers.value, index));
    if (firstMissing >= 0) { goToQuestion(firstMissing); ElMessage.warning('请完成所有题目后再提交'); return; }
    if (!commonStore.token) { ElMessage.warning({ message: '请先登录', duration: 1500 }); router.push('/login'); return; }
    const epoch = loadEpoch, id = Number(route.query.id);
    submitting.value = true; submitError.value = '';
    try {
        const saved = await post('/assessmentRecord/submit', {
            scaleInfoId: id,
            answers: questions.value.map((question, index) => ({ questionId: question.id, optionIndex: answers.value[index] }))
        });
        if (!isCurrent(epoch, id)) return;
        if (!saved.success) { submitError.value = '测评结果暂时未能保存，请稍后重试。'; return; }
        result.value = saved.data;
        await nextTick(); document.getElementById('outcome-title')?.scrollIntoView({ block: 'center', behavior: 'auto' });
    } catch (error) { if (isCurrent(epoch, id)) submitError.value = error.message || '提交失败，请稍后重试。'; }
    finally { if (isCurrent(epoch, id)) submitting.value = false; }
};
const retakeTest = async () => { resetAnswers(); await nextTick(); questionHeading.value?.focus({ preventScroll: true }); };
watch(() => route.query.id, loadData, { immediate: true });
onBeforeUnmount(() => { disposed = true; loadEpoch++; });
</script>

<style scoped lang="scss" src="./scale-test-design.scss"></style>
