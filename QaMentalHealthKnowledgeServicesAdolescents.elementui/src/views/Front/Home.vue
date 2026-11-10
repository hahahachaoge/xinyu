<template>
    <div class="home-page">
        <section class="hero-section" aria-labelledby="home-title">
            <img class="hero-art" :src="heroImage" alt="阳光花园里，两位少年一起阅读、倾听彼此的心事" fetchpriority="high" />
            <div class="hero-copy">
                <p class="eyebrow">循证知识 · 温暖陪伴</p>
                <h1 id="home-title">每一种心情，<br />都值得被认真倾听</h1>
                <p class="hero-description">从一个问题开始，认识情绪，也更了解自己。</p>
                <form class="hero-search" @submit.prevent="startChat(question)"><WellbeingIcon name="search" /><input v-model="question" aria-label="最近有什么让你困扰的事" placeholder="最近有什么让你困扰的事？" maxlength="2000" /><button type="submit">开始提问 <WellbeingIcon name="arrow" /></button></form>
                <div class="quick-questions" aria-label="试试这些问题"><button v-for="item in quickQuestions" :key="item" @click="startChat(item)">{{ item }}</button></div>
                <div class="trust-row"><span><WellbeingIcon name="book" />基于专业知识库</span><span><WellbeingIcon name="document" />回答附参考来源</span><span><WellbeingIcon name="shield" />尊重个人隐私</span></div>
            </div>
            <div class="hero-note" aria-hidden="true"><WellbeingIcon name="leaf" /><p>慢慢来，<br />成长自有节奏。</p></div>
        </section>

        <section class="topics-section" aria-labelledby="topics-title">
            <div class="section-header"><div><h2 id="topics-title">陪你读懂成长的每一面</h2><p>从你关心的话题开始探索</p></div><router-link class="section-more" to="/front/articleList">探索全部 <WellbeingIcon name="arrow" /></router-link></div>
            <div class="topic-grid"><router-link v-for="topic in topics" :key="topic.title" :to="topicLink(topic)" class="topic-card" :class="topic.tone"><img :src="topic.image" alt="" loading="lazy" /><div class="topic-copy"><h3>{{ topic.title }}</h3><p>{{ topic.description[0] }}<br />{{ topic.description[1] }}</p><span class="round-arrow"><WellbeingIcon name="arrow" /></span></div></router-link></div>
        </section>

        <section class="reading-section" aria-labelledby="reading-title">
            <div class="section-header"><div><h2 id="reading-title">给此刻的你，一点温柔的解答</h2><p>用易懂的知识，认识真实的自己</p></div><router-link class="section-more" to="/front/articleList">更多心理知识 <WellbeingIcon name="arrow" /></router-link></div>
            <el-skeleton v-if="loadingArticles" :rows="5" animated class="reading-skeleton" />
            <div v-else class="article-grid"><router-link v-for="(item, index) in displayedArticles" :key="item.id || item.title" :to="articleLink(item)" class="article-card"><div class="article-cover"><img :src="item.coverImage || articleImages[index]" :alt="articleAlts[index]" loading="lazy" /><span class="article-category" :class="{ lavender: index === 2 }"><WellbeingIcon name="shield" />{{ item.articleTypeDto?.typeName || articleDefaults[index].category }}</span></div><div class="article-body"><h3>{{ item.title }}</h3><p>{{ articleDescription(item, index) }}</p><span class="read-link">阅读知识 <WellbeingIcon name="arrow" /></span></div></router-link></div>
            <p v-if="articleLoadFailed" class="section-status">精选阅读方向已为你准备好，完整知识内容可稍后重试。<button @click="loadArticles">重新加载</button></p>
        </section>

        <section class="answer-section" aria-labelledby="answer-title">
            <div class="answer-guide"><h2 id="answer-title">一个问题，也可以是改变的开始</h2><p class="section-description">有依据的回答，让理解更进一步</p><ol class="answer-steps"><li v-for="step in answerSteps" :key="step.title"><span class="step-icon"><WellbeingIcon :name="step.icon" /></span><div><h3>{{ step.title }}</h3><p>{{ step.description }}</p></div></li></ol></div>
            <div class="answer-demo"><span class="demo-label">问答示例</span><div class="demo-question"><span class="demo-avatar"><UserFilled /></span><p>最近学习压力很大，我可以怎么调整？</p></div><div class="demo-answer"><WellbeingIcon name="leaf" /><p>先试着把任务拆成小步骤，为休息留出时间。<br />当压力持续影响生活时，可以向信任的成年人或专业人员寻求支持。</p></div><div class="demo-bottom"><div class="source-tags"><span>参考知识：</span><span class="source-tag">压力管理</span><span class="source-tag">情绪调节</span></div><button class="primary-button" @click="startChat('最近学习压力很大，我可以怎么调整？')">试着问一个问题 <WellbeingIcon name="arrow" /></button></div></div>
        </section>

        <section class="growth-section" aria-labelledby="growth-title"><img :src="calmImage" alt="窗边的书、茶杯与花朵，留一点时间给自己" loading="lazy" /><div class="growth-copy"><h2 id="growth-title">成长没有标准答案，你可以慢慢来</h2><h3>在变化中，找到自己的节奏</h3><p>了解自己、理解关系，也为每一次小小的进步留一点空间。</p><router-link class="primary-button" to="/front/scaleList">探索成长专题 <WellbeingIcon name="arrow" /></router-link></div></section>
        <section class="support-strip"><WellbeingIcon class="support-leaf" name="leaf" /><h2>你不必独自面对所有情绪</h2><p>当你需要更多帮助时，可以联系信任的成年人，或寻求专业心理支持。</p><button @click="supportDialog = true">了解求助方式 <WellbeingIcon name="arrow" /></button></section>
        <el-dialog v-model="supportDialog" title="你可以获得更多支持" width="min(520px, 92vw)" class="home-info-dialog"><p>如果困扰持续影响睡眠、学习或日常生活，可以先联系信任的家人、老师或学校心理老师，也可以到正规医疗机构的心理科寻求专业支持。</p><p>把最近的感受、持续时间和希望得到的帮助写下来，可以帮助你开始沟通。</p><template #footer><el-button type="primary" @click="supportDialog = false">我知道了</el-button></template></el-dialog>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { UserFilled } from '@element-plus/icons-vue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import defaultHeroImage from '@/assets/home-garden-hero.png';
import emotionImage from '@/assets/home-topic-emotion.png';
import connectionImage from '@/assets/home-topic-connection.png';
import selfImage from '@/assets/home-topic-self.png';
import walkingImage from '@/assets/home-friends-walking.png';
import calmImage from '@/assets/chat-calm-corner.png';
import readingImage from '@/assets/wellbeing-reading.png';

const router = useRouter();
const question = ref('');
const supportDialog = ref(false);
const articleList = ref([]);
const articleTypes = ref([]);
const bannerList = ref([]);
const heroImage = computed(() => bannerList.value[0]?.imageUrl || defaultHeroImage);
const loadingArticles = ref(true);
const articleLoadFailed = ref(false);
const quickQuestions = ['考试前总是紧张怎么办？', '如何与父母好好沟通？', '情绪低落时可以做些什么？'];
const topics = [
    { title: '情绪与压力', keywords: ['情绪', '压力'], tone: 'blue', image: emotionImage, description: ['认识情绪，学会调节，', '在起伏中找到平衡。'] },
    { title: '学习与成长', keywords: ['学业', '学习'], tone: 'cream', image: calmImage, description: ['面对挑战，积累善意，', '走向更喜欢的自己。'] },
    { title: '人际与沟通', keywords: ['人际', '沟通'], tone: 'lilac', image: connectionImage, description: ['理解他人，也表达自己，', '建立更自在的关系。'] },
    { title: '自我与认知', keywords: ['自我', '认知'], tone: 'pink', image: selfImage, description: ['探索发现，接纳不完美，', '看见独特而真实的自己。'] }
];
const articleDefaults = [
    { title: '考试前的紧张，可以怎样缓解？', category: '学习与成长', description: '了解紧张情绪的常见表现，尝试一些实用的方法，让自己在考场前更从容。', keyword: '考试' },
    { title: '情绪低落时，如何照顾自己？', category: '情绪与压力', description: '当情绪低落时，从日常生活、思维方式和社会支持等方面，寻找适合自己的调节方法。', keyword: '情绪' },
    { title: '把心里话，慢慢说出来', category: '人际与沟通', description: '学会用更合适的方式表达自己的想法，也更好地理解他人，让关系变得更轻松。', keyword: '沟通' }
];
const articleImages = [calmImage, readingImage, walkingImage];
const articleAlts = ['阳光落在窗边的书本和茶杯上', '女孩在安静的窗边阅读，照顾自己的心情', '两位少年在树荫下并肩同行'];
const displayedArticles = computed(() => articleList.value.length ? articleList.value.slice(0, 3) : articleDefaults);
const articleDescription = (item, index) => {
    const summary = item.summary || item.description || item.articleDesc;
    if (summary) return String(summary).replace(/<[^>]*>/g, '').slice(0, 78);
    if (!item.id) return articleDefaults[index].description;
    return `从${item.articleTypeDto?.typeName || '心理健康'}的日常话题出发，读一点实用知识，多一点对自己和他人的理解。`;
};
const articleLink = (item) => item.id ? { path: '/front/articleDetail', query: { id: item.id } } : { path: '/front/articleList', query: { title: item.keyword } };
const topicLink = (topic) => {
    const type = articleTypes.value.find(item => topic.keywords.some(keyword => item.typeName?.includes(keyword)));
    return { path: '/front/articleList', query: type ? { typeId: type.id } : { title: topic.keywords[0] } };
};
const answerSteps = [
    { title: '说出你的困惑', description: '用自己的话描述正在经历的困扰', icon: 'people' },
    { title: '查找相关知识', description: '基于专业的心理知识库进行检索', icon: 'search' },
    { title: '结合参考来源理解答案', description: '提供清晰的参考来源，帮助你进一步了解', icon: 'document' }
];
const startChat = (text) => router.push({ path: '/front/aiChat', query: text.trim() ? { question: text.trim() } : {} });
const loadArticles = async () => {
    loadingArticles.value = true;
    articleLoadFailed.value = false;
    try {
        const { success, data } = await post('/article/list', { page: 1, limit: 3 });
        if (!success) throw new Error('文章加载失败');
        articleList.value = Array.isArray(data?.items) ? data.items : [];
    } catch { articleLoadFailed.value = true; }
    finally { loadingArticles.value = false; }
};
onMounted(() => { Promise.allSettled([loadArticles(), post('/banner/list', { page: 1, limit: 100 }).then(({ success, data }) => { if (success) bannerList.value = (data?.items || []).slice().sort((a, b) => a.id - b.id); }), post('/articleType/list', { page: 1, limit: 100 }).then(({ success, data }) => { if (success) articleTypes.value = data?.items || []; })]); });
</script>

<style scoped lang="scss" src="./home-design.scss"></style>
