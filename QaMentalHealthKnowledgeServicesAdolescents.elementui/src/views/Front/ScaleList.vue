<template>
    <div class="scale-list-page assessment-list-design">
        <section class="assessment-hero" aria-labelledby="assessment-page-title">
            <img class="assessment-hero-art" :src="heroImage" alt="女孩在阳光洒落的窗边书写，静静整理自己的感受" fetchpriority="high" />
            <div class="assessment-hero-copy">
                <nav class="assessment-breadcrumb" aria-label="当前位置"><router-link to="/front/home">首页</router-link><span>/</span><span aria-current="page">心理测评</span></nav>
                <p class="assessment-eyebrow">听见内心 · 看见成长</p>
                <h1 id="assessment-page-title">给自己一点时间，<br />了解此刻的状态</h1>
                <p class="assessment-intro">选择适合你的心理测评，探索自己的成长节奏。</p>
                <p class="assessment-disclaimer">测评结果仅供参考，如需专业帮助请咨询心理老师。</p>
            </div>
        </section>

        <ol class="assessment-steps" aria-label="测评流程">
            <li><span class="step-icon"><WellbeingIcon name="document" /></span><span class="step-number">01</span><span>选择测评</span></li>
            <li><span class="step-icon"><EditPen /></span><span class="step-number">02</span><span>按真实感受作答</span></li>
            <li><span class="step-icon"><Histogram /></span><span class="step-number">03</span><span>查看结果与建议</span></li>
        </ol>

        <section class="assessment-library" aria-labelledby="assessment-library-title" :aria-busy="loading">
            <div class="assessment-heading"><h2 id="assessment-library-title">从你关心的状态开始</h2><p>没有标准答案，只需如实表达此刻的感受。</p></div>
            <div v-if="loading" class="assessment-cards" aria-label="量表加载中"><div class="assessment-skeleton" v-for="index in 3" :key="index"><el-skeleton animated><template #template><div class="skeleton-row"><el-skeleton-item variant="image" class="skeleton-image" /><div class="skeleton-text"><el-skeleton-item variant="h3" style="width: 65%" /><el-skeleton-item variant="text" /><el-skeleton-item variant="text" style="width: 75%" /></div></div></template></el-skeleton></div></div>
            <div v-else-if="loadError" class="assessment-empty" role="status"><WellbeingIcon name="document" /><h3>测评量表暂时加载失败</h3><p>请稍后重试，给自己一点慢慢探索的时间。</p><button @click="loadList">重新加载</button></div>
            <div v-else-if="!scaleList.length" class="assessment-empty" role="status"><WellbeingIcon name="leaf" /><h3>新的测评正在准备中</h3><p>也可以先读一点心理知识，或聊聊此刻的感受。</p><router-link to="/front/articleList">探索心理知识 <WellbeingIcon name="arrow" /></router-link></div>
            <div v-else class="assessment-cards">
                <article class="assessment-card" v-for="item in scaleList" :key="item.id" :aria-labelledby="'scale-title-' + item.id">
                    <img class="assessment-card-image" :src="imageFor(item)" :alt="imageAltFor(item)" loading="lazy" />
                    <div class="assessment-card-copy"><h3 :id="'scale-title-' + item.id">{{ item.scaleName }}</h3><p>{{ item.scaleDesc || '给自己一点时间，了解最近的感受与状态。' }}</p><div class="assessment-tags"><span v-if="item.scaleTypeFormat">{{ item.scaleTypeFormat }}</span><span v-if="item.targetGroup">{{ item.targetGroup }}</span><span v-if="item.questionCount != null">{{ item.questionCount }}题</span></div></div>
                    <router-link class="assessment-start" :to="{ path: '/front/scaleTest', query: { id: item.id } }" :aria-label="'开始测评：' + item.scaleName">开始测评 <WellbeingIcon name="arrow" /></router-link>
                </article>
            </div>
        </section>

        <section class="assessment-reminders" aria-labelledby="assessment-reminder-title"><img :src="reminderImage" alt="沙漏、书本与窗边白花，营造安静舒适的空间" loading="lazy" /><div class="reminders-copy"><h2 id="assessment-reminder-title">开始之前，给你几个小提醒</h2><ul><li><House /><span>在安静、舒适的环境中作答</span></li><li><Checked /><span>按近期的真实感受选择答案</span></li><li><WellbeingIcon name="heart" /><span>测评帮助自我了解，不给自己贴标签。</span></li></ul></div></section>
        <section class="assessment-chat-cta"><div><h2>测评之后，还有想聊的吗？</h2><p>结合心理健康知识，继续理解自己的感受。</p></div><router-link to="/front/aiChat">去智能问答 <WellbeingIcon name="arrow" /></router-link></section>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { Checked, EditPen, Histogram, House } from '@element-plus/icons-vue';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import heroImage from '@/assets/scale-writing-hero.png';
import reflectionImage from '@/assets/scale-reflection.png';
import reminderImage from '@/assets/scale-reminder.png';
import studyImage from '@/assets/chat-calm-corner.png';
import relationshipImage from '@/assets/chat-garden-hero.png';

const scaleList = ref([]);
const loading = ref(true);
const loadError = ref(false);
let isDisposed = false;
let requestId = 0;
const imageFor = (item) => {
    const topic = `${item.scaleName || ''} ${item.scaleTypeFormat || ''}`;
    if (/人际|社交|关系/.test(topic)) return relationshipImage;
    if (/学业|学习|压力/.test(topic)) return studyImage;
    return reflectionImage;
};
const imageAltFor = (item) => {
    const image = imageFor(item);
    if (image === studyImage) return '阳光下的书本和茶杯，为学习留一点从容';
    if (image === relationshipImage) return '两位少年在花园中交流，倾听彼此的感受';
    return '女孩在窗边安静休息，留意此刻的心情';
};
const loadList = async () => {
    const currentRequest = ++requestId;
    loading.value = true;
    loadError.value = false;
    try {
        const { data, success } = await post('/scaleInfo/list', { page: 1, limit: 100 });
        if (isDisposed || currentRequest !== requestId) return;
        if (!success) throw new Error('量表加载失败');
        scaleList.value = Array.isArray(data?.items) ? data.items : [];
    } catch {
        if (isDisposed || currentRequest !== requestId) return;
        scaleList.value = [];
        loadError.value = true;
    } finally {
        if (!isDisposed && currentRequest === requestId) loading.value = false;
    }
};
onMounted(loadList);
onBeforeUnmount(() => { isDisposed = true; requestId++; });
</script>

<style scoped lang="scss" src="./scale-list-design.scss"></style>
