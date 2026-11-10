<template>
    <div class="article-detail-page article-reading-design">
        <nav class="article-breadcrumb" aria-label="当前位置"><router-link to="/front/home">首页</router-link><span>/</span><router-link to="/front/articleList">科普专区</router-link><span>/</span><span aria-current="page">文章详情</span></nav>
        <div v-if="loading" class="article-loading" aria-label="文章加载中" aria-busy="true"><el-skeleton :rows="12" animated /></div>
        <div v-else-if="loadError" class="article-error" role="status"><WellbeingIcon name="book" /><h1>{{ loadError === 'missing' ? '这篇文章暂时找不到了' : '文章暂时加载失败' }}</h1><p>可以稍后重试，或返回科普专区看看其他知识。</p><button v-if="loadError !== 'missing'" @click="loadPage">重新加载</button><router-link to="/front/articleList">返回科普专区</router-link></div>
        <div v-else class="reading-columns">
            <div class="reading-main">
                <article class="reading-paper">
                    <span class="reading-category">{{ article.articleTypeDto?.typeName || '心理科普' }}</span>
                    <h1>{{ article.title }}</h1><p class="article-subtitle">{{ reading.guide }}</p>
                    <div class="article-byline"><el-avatar :size="35" :src="publisherAvatar"><WellbeingIcon name="leaf" /></el-avatar><span>{{ article.publisherDto?.nickName || '管理员' }}</span><time :datetime="article.publishTime">{{ (article.publishTime || '').slice(0, 10) }}</time><button class="favorite-button" :class="{ selected: isFavorited }" :aria-pressed="isFavorited" :disabled="favoriteBusy" @click="toggleFavorite"><Star /><span>{{ favoriteBusy ? '处理中…' : isFavorited ? '已收藏' : '收藏文章' }}</span></button></div>
                    <img class="article-main-cover" :src="articleIllustration(article)" :alt="article.title" />
                    <div class="reading-guide"><WellbeingIcon name="book" /><div><h2>阅读导语</h2><p>{{ reading.guide || '带着一点好奇，慢慢读懂自己的感受。' }}</p></div></div>
                    <div class="reading-content" ref="contentRef" v-html="reading.html"></div>
                    <blockquote class="reading-encouragement"><WellbeingIcon name="leaf" /><p>你不需要立刻变得积极，先理解此刻的自己。</p></blockquote>
                    <div class="article-footnote"><div><span>{{ article.articleTypeDto?.typeName || '心理知识' }}</span><span>自我关怀</span><span>成长支持</span></div><p>本文用于心理健康知识科普，不替代专业诊断与治疗。</p></div>
                </article>

                <section class="reading-comments" aria-labelledby="comments-title"><h2 id="comments-title">交流与留言</h2><p class="comments-description">分享你的思考，也尊重彼此的感受。</p>
                    <form class="comment-composer" @submit.prevent="submitComment"><el-input ref="commentInput" v-model="commentText" type="textarea" :rows="3" :maxlength="2000" :placeholder="replyTarget ? '回复 ' + replyTarget.nickName + '…' : '读完之后，有什么想说的？'" aria-label="留言内容" resize="none" :disabled="commentBusy" /><div class="comment-composer-actions"><span v-if="replyTarget" class="reply-hint">回复 {{ replyTarget.nickName }} <button type="button" @click="replyTarget = null">取消</button></span><button class="comment-submit" type="submit" :disabled="!commentText.trim() || commentBusy">{{ commentBusy ? '正在发表…' : replyTarget ? '发表回复' : '发表评论' }}</button></div></form>
                    <div class="comments-heading"><span>留言 {{ allComments.length ? `(${allComments.length})` : '' }}</span><button v-if="commentLoadError" @click="reloadComments">留言加载失败，点击重试</button></div>
                    <p v-if="!allComments.length && !commentLoadError" class="comments-empty">还没有留言，分享一点阅读后的感受吧。</p>
                    <div v-for="comment in topComments" :key="comment.id" class="reader-comment"><el-avatar :size="38" :src="avatarOf(comment)">{{ readerInitial(comment) }}</el-avatar><div class="reader-comment-body"><div class="comment-byline"><strong>{{ comment.userDto?.nickName || '读者' }}</strong><time>{{ comment.creationTime }}</time></div><p>{{ comment.commentContent }}</p><button class="reply-button" @click="setReply(comment)">回复</button><div v-if="getChildren(comment.id).length" class="reader-replies"><div v-for="reply in getChildren(comment.id)" :key="reply.id" class="reader-reply"><el-avatar :size="28" :src="avatarOf(reply)">{{ readerInitial(reply) }}</el-avatar><div class="reader-comment-body"><div class="comment-byline"><strong>{{ reply.userDto?.nickName || '读者' }}</strong><span v-if="reply.replyToUserDto?.nickName">回复 {{ reply.replyToUserDto.nickName }}</span><time>{{ reply.creationTime }}</time></div><p>{{ reply.commentContent }}</p><button class="reply-button" @click="setReply(reply, comment)">回复</button></div></div></div></div></div>
                </section>
            </div>

            <aside class="reading-sidebar">
                <section class="reading-promo"><img :src="companionImage" alt="花朵与书本陪伴着安静的阅读时光" /><div><p class="promo-eyebrow">给成长，一点新的启发</p><h2>读懂情绪，<br />也读懂自己</h2><p>有些困惑，可以慢慢说出来。</p><router-link to="/front/aiChat">去智能问答 <WellbeingIcon name="arrow" /></router-link></div><span class="promo-note" aria-hidden="true">向内看<br />也向前走</span></section>
                <nav v-if="reading.toc.length" class="reading-toc" aria-label="本文目录"><h2>本文目录</h2><a v-for="entry in reading.toc" :key="entry.id" :href="'#' + entry.id" :class="{ active: activeSection === entry.id }" :aria-current="activeSection === entry.id ? 'location' : undefined" @click.prevent="jumpToSection(entry.id)"><span>{{ entry.number }}</span><span>{{ entry.title }}</span></a></nav>
                <section class="reading-recommendations"><h2>猜你想读</h2><router-link v-for="item in recommendList" :key="item.id" :to="{ path: '/front/articleDetail', query: { id: item.id } }" class="recommended-reading"><img :src="articleIllustration(item)" :alt="item.title" loading="lazy" /><span class="reading-category">{{ item.articleTypeDto?.typeName || '心理科普' }}</span><h3>{{ item.title }}</h3><p>{{ articleExcerpt(item, 65) }}</p></router-link><p v-if="!recommendList.length" class="recommend-empty">{{ recommendError ? '推荐暂时加载失败' : '更多知识正在准备中' }}</p><button v-if="recommendError" class="recommend-retry" @click="reloadRecommendations">重新加载推荐</button></section>
                <button class="reading-support" @click="supportDialog = true"><span>每一种心情，<br />都值得被倾听</span><small>了解求助方式 →</small></button>
            </aside>
        </div>
        <div class="reading-return"><router-link to="/front/articleList"><Back />返回科普专区</router-link><router-link to="/front/articleList">继续探索，发现更多成长的启发 <WellbeingIcon name="arrow" /></router-link></div>
        <el-dialog v-model="supportDialog" title="你可以获得更多支持" width="min(500px, 92vw)"><p class="support-description">如果困扰持续影响学习、睡眠或日常生活，可以向信任的家人、老师或学校心理老师倾诉，也可以到正规医疗机构寻求专业心理支持。</p><template #footer><el-button type="primary" @click="supportDialog = false">我知道了</el-button></template></el-dialog>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { useCommonStore } from '@/store';
import { articleExcerpt, articleIllustration, buildArticleReading } from '@/utils/articleReading';
import companionImage from '@/assets/article-detail-companion.png';
import { Back, Star } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const commonStore = useCommonStore();
const route = useRoute();
const router = useRouter();
const article = ref({});
const loading = ref(true);
const loadError = ref('');
const isFavorited = ref(false);
const favoriteBusy = ref(false);
const commentBusy = ref(false);
const commentText = ref('');
const allComments = ref([]);
const commentLoadError = ref(false);
const replyTarget = ref(null);
const recommendList = ref([]);
const recommendError = ref(false);
const supportDialog = ref(false);
const activeSection = ref('');
const contentRef = ref(null);
const commentInput = ref(null);
let pageEpoch = 0;
let disposed = false;
let sectionObserver;
const reading = computed(() => buildArticleReading(article.value));
const publisherAvatar = computed(() => article.value.publisherDto?.imageUrls?.split(',')[0] || '');
const topComments = computed(() => allComments.value.filter(comment => !comment.parentId));
const getChildren = (parentId) => allComments.value.filter(comment => comment.parentId === parentId);
const avatarOf = (comment) => comment.userDto?.imageUrls?.split(',')[0] || '';
const readerInitial = (comment) => (comment.userDto?.nickName || '读者').slice(0, 1);
const currentArticleId = () => Number(route.query.id);
const pageIsCurrent = (epoch, id) => !disposed && epoch === pageEpoch && id === currentArticleId();

const jumpToSection = (id) => { activeSection.value = id; document.getElementById(id)?.scrollIntoView({ block: 'start', behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth' }); };
const observeSections = async () => {
    sectionObserver?.disconnect();
    await nextTick();
    activeSection.value = reading.value.toc[0]?.id || '';
    if (!contentRef.value || !('IntersectionObserver' in window)) return;
    sectionObserver = new IntersectionObserver(entries => {
        const visible = entries.filter(entry => entry.isIntersecting).sort((a, b) => a.boundingClientRect.top - b.boundingClientRect.top);
        if (visible.length) activeSection.value = visible[0].target.id;
    }, { rootMargin: '-160px 0px -55% 0px' });
    contentRef.value.querySelectorAll('[data-reading-heading]').forEach(node => sectionObserver.observe(node));
};

const reloadComments = async () => {
    const id = currentArticleId(), epoch = pageEpoch;
    const result = await post('/articleComment/list', { page: 1, limit: 500, articleId: id });
    if (!pageIsCurrent(epoch, id)) return;
    commentLoadError.value = !result.success;
    if (result.success) allComments.value = result.data?.items || [];
};
const reloadRecommendations = async () => {
    const id = currentArticleId(), epoch = pageEpoch;
    const result = await post('/article/recommend', { userId: commonStore.userId || 0, currentArticleId: id });
    if (!pageIsCurrent(epoch, id)) return;
    recommendError.value = !result.success;
    if (result.success) recommendList.value = (Array.isArray(result.data) ? result.data : []).filter(item => item.id !== id).slice(0, 3);
};
const loadPage = async () => {
    const id = currentArticleId(), epoch = ++pageEpoch;
    sectionObserver?.disconnect();
    loading.value = true; loadError.value = ''; article.value = {}; isFavorited.value = false;
    allComments.value = []; recommendList.value = []; commentText.value = ''; replyTarget.value = null;
    favoriteBusy.value = false; commentBusy.value = false; commentLoadError.value = false; recommendError.value = false;
    if (!Number.isInteger(id) || id <= 0) { loadError.value = 'missing'; loading.value = false; return; }
    try {
        const result = await post('/article/findById', { id });
        if (!pageIsCurrent(epoch, id)) return;
        if (!result.success) throw new Error('文章加载失败');
        if (!result.data?.id) { loadError.value = 'missing'; return; }
        article.value = result.data;
        post('/article/view', { id });
        await Promise.allSettled([reloadComments(), reloadRecommendations(), commonStore.token ? post('/favorite/checkFavorited', { favoriteType: 1, targetId: id }).then(result => { if (pageIsCurrent(epoch, id) && result.success) isFavorited.value = !!result.data; }) : Promise.resolve()]);
    } catch { if (pageIsCurrent(epoch, id)) loadError.value = 'failed'; }
    finally { if (pageIsCurrent(epoch, id)) { loading.value = false; if (!loadError.value) observeSections(); } }
};
const requireLogin = () => { if (commonStore.token) return true; ElMessage.warning({ message: '请先登录', duration: 1500 }); router.push('/login'); return false; };
const toggleFavorite = async () => {
    if (favoriteBusy.value || !requireLogin()) return;
    const id = currentArticleId(), epoch = pageEpoch;
    favoriteBusy.value = true;
    try { const result = await post('/favorite/toggle', { favoriteType: 1, targetId: id }); if (pageIsCurrent(epoch, id) && result.success) { isFavorited.value = !isFavorited.value; ElMessage.success({ message: isFavorited.value ? '已收藏' : '已取消收藏', duration: 1500 }); } }
    finally { if (pageIsCurrent(epoch, id)) favoriteBusy.value = false; }
};
const setReply = async (comment, parent) => { replyTarget.value = { nickName: comment.userDto?.nickName || '读者', userId: comment.userId, parentId: parent?.id || comment.id }; await nextTick(); commentInput.value?.focus(); };
const submitComment = async () => {
    if (!commentText.value.trim() || commentBusy.value || !requireLogin()) return;
    const id = currentArticleId(), epoch = pageEpoch;
    commentBusy.value = true;
    try {
        const result = await post('/articleComment/addOrEdit', { articleId: id, userId: commonStore.userId, commentContent: commentText.value.trim(), parentId: replyTarget.value?.parentId || null, replyToUserId: replyTarget.value?.userId || null });
        if (pageIsCurrent(epoch, id) && result.success) { ElMessage.success({ message: replyTarget.value ? '回复成功' : '评论成功', duration: 1500 }); commentText.value = ''; replyTarget.value = null; await reloadComments(); }
    } finally { if (pageIsCurrent(epoch, id)) commentBusy.value = false; }
};
watch(() => route.query.id, loadPage, { immediate: true });
onBeforeUnmount(() => { disposed = true; pageEpoch++; sectionObserver?.disconnect(); });
</script>

<style scoped lang="scss" src="./article-detail-design.scss"></style>
