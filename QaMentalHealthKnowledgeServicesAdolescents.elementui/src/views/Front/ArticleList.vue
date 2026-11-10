<template>
    <div class="article-list-page">
        <section class="reading-hero" aria-labelledby="reading-hero-title">
            <img class="reading-hero-art" :src="heroImage" alt="女孩在阳光与花朵环绕的窗边安静阅读" fetchpriority="high" />
            <div class="hero-copy">
                <nav class="reading-breadcrumb" aria-label="当前位置"><router-link to="/front/home">首页</router-link><span>/</span><span aria-current="page">科普专区</span></nav>
                <p class="hero-eyebrow">认识情绪 · 发现自己</p>
                <h1 id="reading-hero-title">读一点知识，多一点理解</h1>
                <p class="hero-description">关于情绪、学习和人际关系，慢慢找到新的视角。<br />让每一次阅读，都成为理解自己的小小起点。</p>
            </div>
            <div class="reading-note" aria-hidden="true">阅读，<br />也是一种自我照顾<span>♥</span></div>
        </section>

        <section class="reading-library" aria-labelledby="library-title">
            <div class="filter-bar">
                <div class="filter-tabs" role="group" aria-label="文章分类">
                    <button class="category-tab" :class="{ active: currentType === null }" :aria-pressed="currentType === null" @click="changeType(null)">全部</button>
                    <button v-for="type in typeList" :key="type.id" class="category-tab" :class="{ active: currentType === type.id }" :aria-pressed="currentType === type.id" @click="changeType(type.id)">{{ type.typeName }}</button>
                    <button v-if="typeLoadError" class="retry-types" @click="loadTypes">重新加载分类</button>
                </div>
                <form class="article-search" role="search" @submit.prevent="applySearch"><WellbeingIcon name="search" /><input v-model="searchText" placeholder="搜索你关心的话题…" aria-label="搜索文章标题" maxlength="100" /><button v-if="searchText" type="button" class="clear-search" aria-label="清除搜索" @click="clearSearch">×</button><button class="sr-only" type="submit">搜索</button></form>
            </div>

            <div class="library-heading"><h2 id="library-title">发现适合你的心理知识</h2><div class="sort-tabs" role="group" aria-label="文章排序"><button :class="{ active: sortBy === 'latest' }" :aria-pressed="sortBy === 'latest'" @click="changeSort('latest')">最新发布</button><span aria-hidden="true">|</span><button :class="{ active: sortBy === 'recommended' }" :aria-pressed="sortBy === 'recommended'" @click="changeSort('recommended')">推荐阅读</button></div></div>

            <div class="article-grid" v-if="loading" aria-label="文章加载中" aria-busy="true"><div v-for="index in pageSize" :key="index" class="article-skeleton"><el-skeleton animated><template #template><el-skeleton-item variant="image" class="skeleton-cover" /><div class="skeleton-copy"><el-skeleton-item variant="text" style="width: 25%" /><el-skeleton-item variant="h3" /><el-skeleton-item variant="text" /><el-skeleton-item variant="text" style="width: 75%" /></div></template></el-skeleton></div></div>
            <div v-else-if="loadError" class="library-empty" role="status"><WellbeingIcon name="book" /><h3>文章暂时加载失败</h3><p>请稍后重试，我们会继续为你准备有用的知识。</p><button class="outline-button" @click="loadList">重新加载</button></div>
            <div v-else-if="!list.length" class="library-empty" role="status"><WellbeingIcon name="search" /><h3>暂时没有找到相关文章</h3><p>试试其他关键词，或看看全部心理知识。</p><button class="outline-button" @click="resetFilters">查看全部文章</button></div>
            <div v-else class="article-grid" :aria-busy="loading">
                <router-link v-for="item in list" :key="item.id" :to="{ path: '/front/articleDetail', query: { id: item.id } }" class="article-card">
                    <div class="article-cover"><img :src="coverFor(item)" :alt="item.title" loading="lazy" @error="useFallbackImage($event, item)" /></div>
                    <div class="article-body"><span class="article-category">{{ item.articleTypeDto?.typeName || '心理科普' }}</span><h3>{{ item.title }}</h3><p>{{ summaryFor(item) }}</p><span class="read-link">阅读文章 <WellbeingIcon name="arrow" /></span></div>
                </router-link>
            </div>

            <nav v-if="!loadError && totalCount > 0" class="article-pagination" aria-label="文章分页"><el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="totalCount" :pager-count="5" layout="prev, pager, next" :disabled="loading" @current-change="changePage" /></nav>
            <div class="reading-cta"><div class="cta-copy"><h2>读过之后，还有想问的？</h2><p>带着你的问题，继续探索新的理解。</p></div><router-link to="/front/aiChat">去智能问答 <WellbeingIcon name="arrow" /></router-link></div>
        </section>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import heroImage from '@/assets/article-reading-hero.png';
import readingImage from '@/assets/wellbeing-reading.png';
import deskImage from '@/assets/chat-calm-corner.png';
import friendsImage from '@/assets/chat-garden-hero.png';
import familyImage from '@/assets/article-family-conversation.png';
import growthImage from '@/assets/article-self-growth.png';

const route = useRoute();
const router = useRouter();
const pageSize = 6;
const list = ref([]);
const typeList = ref([]);
const currentType = ref(null);
const searchText = ref('');
const appliedSearch = ref('');
const currentPage = ref(1);
const totalCount = ref(0);
const sortBy = ref('latest');
const loading = ref(true);
const loadError = ref(false);
const typeLoadError = ref(false);
let listRequestId = 0;
let isDisposed = false;

const readQuery = () => {
    const typeId = Number(route.query.typeId);
    currentType.value = Number.isInteger(typeId) && typeId > 0 ? typeId : null;
    searchText.value = typeof route.query.title === 'string' ? route.query.title.slice(0, 100) : '';
    appliedSearch.value = searchText.value.trim();
    const page = Number(route.query.page);
    currentPage.value = Number.isInteger(page) && page > 0 ? page : 1;
    sortBy.value = route.query.sort === 'recommended' ? 'recommended' : 'latest';
};
readQuery();

const syncQuery = () => {
    const query = {};
    if (currentType.value) query.typeId = String(currentType.value);
    if (appliedSearch.value) query.title = appliedSearch.value;
    if (currentPage.value > 1) query.page = String(currentPage.value);
    if (sortBy.value === 'recommended') query.sort = 'recommended';
    return router.replace({ path: '/front/articleList', query });
};

const loadTypes = async () => {
    typeLoadError.value = false;
    const { success, data } = await post('/articleType/list', { page: 1, limit: 100 });
    if (isDisposed) return;
    if (success && Array.isArray(data?.items)) typeList.value = data.items;
    else typeLoadError.value = true;
};

const loadList = async () => {
    const requestId = ++listRequestId;
    loading.value = true;
    loadError.value = false;
    try {
        const { data, success } = await post('/article/list', {
            page: currentPage.value,
            limit: pageSize,
            title: appliedSearch.value || undefined,
            articleTypeId: currentType.value || undefined,
            sortItem: { fieldName: sortBy.value === 'recommended' ? 'view_count' : 'publish_time', isAsc: false }
        });
        if (requestId !== listRequestId || isDisposed) return;
        if (!success) throw new Error('文章加载失败');
        list.value = Array.isArray(data?.items) ? data.items : [];
        totalCount.value = Number(data?.totalCount) || 0;
        const lastPage = Math.max(1, Math.ceil(totalCount.value / pageSize));
        if (currentPage.value > lastPage) { currentPage.value = lastPage; await syncQuery(); }
    } catch {
        if (requestId !== listRequestId || isDisposed) return;
        list.value = [];
        totalCount.value = 0;
        loadError.value = true;
    } finally {
        if (requestId === listRequestId && !isDisposed) loading.value = false;
    }
};

const changeType = (typeId) => { if (currentType.value === typeId) return; currentType.value = typeId; currentPage.value = 1; syncQuery(); };
const changeSort = (value) => { if (sortBy.value === value) return; sortBy.value = value; currentPage.value = 1; syncQuery(); };
const applySearch = () => { appliedSearch.value = searchText.value.trim(); currentPage.value = 1; syncQuery(); };
const clearSearch = () => { searchText.value = ''; applySearch(); };
const resetFilters = () => { currentType.value = null; searchText.value = ''; appliedSearch.value = ''; currentPage.value = 1; syncQuery(); };
const changePage = async () => { await syncQuery(); await nextTick(); document.getElementById('library-title')?.scrollIntoView({ block: 'start', behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth' }); };

const categoryOf = (item) => item.articleTypeDto?.typeName || '';
const illustrationFor = (item) => {
    const category = categoryOf(item);
    if (/家庭|亲子/.test(category)) return familyImage;
    if (/学业|学习|压力/.test(category)) return deskImage;
    if (/人际|沟通|交往/.test(category)) return friendsImage;
    if (/自我|成长|认知/.test(category)) return growthImage;
    if (/睡眠|低落|休息/.test(item.title || '')) return deskImage;
    return readingImage;
};
const coverFor = (item) => item.coverImage?.trim() || illustrationFor(item);
const useFallbackImage = (event, item) => { if (event.target.dataset.fallback) return; event.target.dataset.fallback = 'true'; event.target.src = illustrationFor(item); };
const summaryFor = (item) => {
    const content = item.summary || item.description || item.articleContent || '';
    const text = new DOMParser().parseFromString(content, 'text/html');
    text.querySelectorAll('script, style').forEach(node => node.remove());
    const summary = (text.body.textContent || '').replace(/\s+/g, ' ').trim();
    return summary || '从日常的感受出发，用一点心理知识，慢慢理解自己与身边的人。';
};

watch(() => route.fullPath, () => { readQuery(); loadList(); });
onMounted(() => { loadTypes(); loadList(); });
onBeforeUnmount(() => { isDisposed = true; listRequestId++; });
</script>

<style scoped lang="scss" src="./article-list-design.scss"></style>
