<template>
    <div class="chat-page">
        <div class="chat-layout">
            <!-- 左侧会话列表 -->
            <div class="chat-sidebar">
                <button class="new-chat-btn" @click="newChat" :disabled="isStreaming"><WellbeingIcon name="plus" />新建对话</button>
                <div class="sidebar-header"><h3>对话记录</h3><button class="icon-button" aria-label="搜索对话" @click="showSessionSearch = !showSessionSearch"><WellbeingIcon name="search" /></button></div>
                <el-input v-if="showSessionSearch" class="session-search" v-model="sessionSearch" placeholder="搜索对话记录" clearable />
                <div class="session-list">
                    <div class="session-item" v-for="item in filteredSessions" :key="item.id"
                        :class="{ active: currentSessionId === item.id }" @click="!isStreaming && selectSession(item.id)">
                        <svg viewBox="0 0 20 20" fill="currentColor" width="16" height="16" class="session-icon"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7z" clip-rule="evenodd"/></svg>
                        <div class="session-info">
                            <span class="session-title">{{ item.sessionTitle }}</span>
                            <span class="session-time">{{ item.lastMessageTime }}</span>
                        </div>
                        <svg class="session-delete" viewBox="0 0 20 20" fill="#bacbbd" width="14" height="14" @click.stop="deleteSession(item.id)"><path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
                    </div>
                    <div class="empty-session" v-if="sessionList.length === 0 && !sessionLoading">
                        <WellbeingIcon name="book" /><p>还没有对话记录</p><span>从一个问题开始，慢慢了解自己</span>
                    </div>
                </div>
                <!-- 热门问题 -->
                <div class="hot-section">
                    <h4>热门问题</h4>
                    <button class="hot-item" v-for="(item, index) in displayHotQuestions" :key="item.id" @click="askHotQuestion(item.questionText)" :disabled="isStreaming"><WellbeingIcon :name="['school', 'people', 'heart'][index % 3]" /><span>{{ item.questionText }}</span><span class="hot-chevron">›</span></button>
                </div>
                <div class="sidebar-art"><p>每一种心情，<br />都值得被倾听<span>～</span></p></div>
            </div>
            <!-- 右侧对话区域 -->
            <div class="chat-main">
                <div class="chat-header">
                    <h3>{{ currentSession?.sessionTitle || '新的对话' }}</h3>
                    <span class="chat-tip">每一个小小的困惑，都值得认真对待</span><span class="header-dots" aria-hidden="true">•••</span>
                </div>
                <div class="chat-messages" ref="messagesRef">
                    <!-- 欢迎消息 -->
                    <section class="chat-welcome" v-if="messages.length === 0 && !isStreaming">
                        <img :src="conversationIllustration" alt="两位少年在花园里倾听与交流的水彩插画" />
                        <p class="welcome-eyebrow"><WellbeingIcon name="leaf" />给心情一点呼吸的空间 ♧</p>
                        <h1>你好，今天想聊些什么？</h1>
                        <p class="welcome-description">关于情绪、学习或人际关系，都可以从这里说起。<br />结合专业知识，陪你一起寻找新的思路。</p>
                        <div class="welcome-topics">
                            <button v-for="topic in welcomeTopics" :key="topic.title" @click="chooseTopic(topic.question)" :class="topic.tone"><WellbeingIcon :name="topic.icon" /><span class="topic-copy"><strong>{{ topic.title }}</strong><small>{{ topic.question }}</small></span><WellbeingIcon class="topic-arrow" name="arrow" /></button>
                        </div>
                        <div class="trust-row"><span><WellbeingIcon name="book" />基于专业知识库</span><span><WellbeingIcon name="document" />回答附参考来源</span><span><WellbeingIcon name="shield" />尊重个人隐私</span></div>
                    </section>
                    <div class="message-item" :class="msg.senderRole === 1 ? 'user' : 'system'" v-for="msg in messages" :key="msg.id">
                        <div class="message-avatar" :class="msg.senderRole === 1 ? 'user-avatar' : 'system-avatar'">
                            <svg v-if="msg.senderRole === 2" viewBox="0 0 24 24" fill="#397b68" width="22" height="22"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/></svg>
                            <svg v-else viewBox="0 0 24 24" fill="#a38b64" width="22" height="22"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                        </div>
                        <div class="message-content">
                            <div class="message-bubble" :class="msg.senderRole === 1 ? 'user-bubble' : 'system-bubble'">
                                <span v-if="msg.senderRole === 2" v-html="formatContent(msg.messageContent)"></span>
                                <span v-else>{{ msg.messageContent }}</span>
                            </div>
                            <!-- 知识溯源 -->
                            <div class="source-tags" v-if="msg.senderRole === 2 && msg.retrievedChunkIds">
                                <svg viewBox="0 0 16 16" fill="#397b68" width="12" height="12"><path d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L13.414 4A2 2 0 0114 5.414V12a2 2 0 01-2 2H6a2 2 0 01-2-2V4z"/></svg>
                                <span class="source-label">来源：</span>
                                <span class="source-chunk-tag" v-for="cid in parseChunkIds(msg.retrievedChunkIds)" :key="cid" @click="showChunkDetail(cid)">
                                    片段 #{{ cid }}
                                </span>
                            </div>
                            <p v-if="msg.senderRole === 2 && !msg.retrievedChunkIds" class="source-tags">本条回答未引用知识库资料</p>
                            <p v-if="msg.retrievalMode === 'keyword'" class="source-tags">本次参考资料通过关键词检索获得</p>
                            <!-- 反馈按钮 -->
                            <div class="feedback-btns" v-if="msg.senderRole === 2 && msg.id">
                                <span class="fb-btn" :class="{ 'fb-active': msg.feedback === 5 }" @click="submitFeedback(msg, 5)">
                                    <svg viewBox="0 0 20 20" fill="currentColor" width="14" height="14"><path d="M2 10.5a1.5 1.5 0 113 0v6a1.5 1.5 0 01-3 0v-6zM6 10.333v5.43a2 2 0 001.106 1.79l.05.025A4 4 0 008.943 18h5.416a2 2 0 001.962-1.608l1.2-6A2 2 0 0015.56 8H12V4a2 2 0 00-2-2 1 1 0 00-1 1v.667a4 4 0 01-.8 2.4L6.8 7.933a4 4 0 00-.8 2.4z"/></svg>
                                    满意
                                </span>
                                <span class="fb-btn" :class="{ 'fb-active-bad': msg.feedback === 1 }" @click="submitFeedback(msg, 1)">
                                    <svg viewBox="0 0 20 20" fill="currentColor" width="14" height="14"><path d="M18 9.5a1.5 1.5 0 11-3 0v-6a1.5 1.5 0 013 0v6zM14 9.667v-5.43a2 2 0 00-1.106-1.79l-.05-.025A4 4 0 0011.057 2H5.64a2 2 0 00-1.962 1.608l-1.2 6A2 2 0 004.44 12H8v4a2 2 0 002 2 1 1 0 001-1v-.667a4 4 0 01.8-2.4l1.4-1.866a4 4 0 00.8-2.4z"/></svg>
                                    不满意
                                </span>
                            </div>
                        </div>
                    </div>
                    <!-- 流式回复中 -->
                    <div class="message-item system" v-if="isStreaming">
                        <div class="message-avatar system-avatar">
                            <svg viewBox="0 0 24 24" fill="#397b68" width="22" height="22"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/></svg>
                        </div>
                        <div class="message-content">
                            <div class="message-bubble system-bubble">
                                <span v-html="formatContent(streamingContent)"></span>
                                <span class="typing-cursor" v-if="streamingContent">|</span>
                                <span class="typing-dots" v-else>
                                    <span>.</span><span>.</span><span>.</span>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="composer-wrap">
                    <div class="chat-input-area">
                        <el-input ref="inputRef" v-model="inputText" type="textarea" :rows="2" placeholder="把此刻的困惑，慢慢说给我听…" aria-label="输入想聊的问题" resize="none" @keydown.enter.exact.prevent="sendMessage" :disabled="isStreaming" />
                        <div class="composer-actions"><WellbeingIcon name="leaf" /><el-button type="primary" :disabled="!inputText.trim() || isStreaming" :loading="isStreaming" @click="sendMessage">{{ isStreaming ? '回复中…' : '发送 ↑' }}</el-button></div>
                    </div>
                    <div class="input-hint">Enter 发送 · Shift + Enter 换行</div>
                    <p class="chat-disclaimer">ⓘ 内容仅供知识参考，不能代替专业诊断与治疗。</p>
                </div>
            </div>
            <aside class="recommend-sidebar">
                <h3>也许你想了解</h3>
                <article class="reading-card" v-for="card in readingCards" :key="card.title">
                    <img :src="calmIllustration" :alt="card.alt" :class="card.imageClass" />
                    <h4>{{ card.title }}</h4><div class="reading-tags"><span v-for="tag in card.tags" :key="tag">{{ tag }}</span></div><p>{{ card.description }}</p>
                    <router-link :to="{ path: '/front/articleList', query: { title: card.keyword } }">阅读知识 <WellbeingIcon name="arrow" /></router-link>
                </article>
                <button class="support-card" @click="supportDialogShow = true"><h4>需要更多支持？</h4><p>当你需要时，总有更多<br />专业的帮助方式。</p><span>了解求助方式 →</span><WellbeingIcon name="leaf" /></button>
            </aside>
        </div>
        <el-dialog v-model="supportDialogShow" title="给自己多一点支持" width="min(480px, 92vw)">
            <p class="support-copy">如果困扰持续影响学习、睡眠或日常生活，可以向信任的家人、老师或学校心理老师倾诉，也可以到正规医疗机构的心理科寻求专业支持。</p>
            <p class="support-copy">可以先写下最近的感受和需要的帮助，再选择让自己感到安心的方式开口。</p>
            <template #footer><el-button type="primary" @click="supportDialogShow = false">我知道了</el-button></template>
        </el-dialog>
        <!-- 知识片段详情弹窗 -->
        <el-dialog class="front-dialog" title="知识溯源详情" v-model="chunkDialogShow" width="min(600px, 92vw)" :close-on-click-modal="true">
            <div class="chunk-detail" v-if="chunkDetail">
                <div class="chunk-doc-info" v-if="chunkDetail.docTitle">
                    <div class="chunk-doc-title">
                        <svg viewBox="0 0 16 16" fill="#397b68" width="16" height="16"><path d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L13.414 4A2 2 0 0114 5.414V12a2 2 0 01-2 2H6a2 2 0 01-2-2V4z"/></svg>
                        <span>{{ chunkDetail.docTitle }}</span>
                        <el-tag size="small" type="info" v-if="chunkDetail.fileFormat">{{ chunkDetail.fileFormat }}</el-tag>
                    </div>
                    <p class="chunk-doc-desc" v-if="chunkDetail.docDesc">{{ chunkDetail.docDesc }}</p>
                </div>
                <div class="chunk-meta">
                    <span>片段序号：第 {{ chunkDetail.chunkIndex }} 块</span>
                    <span>Token数：{{ chunkDetail.tokenCount || '-' }}</span>
                </div>
                <div class="chunk-content-box">
                    <div class="chunk-content-label">片段内容</div>
                    <div class="chunk-content-text">{{ chunkDetail.chunkContent }}</div>
                </div>
            </div>
            <div class="chunk-empty" v-else>该知识片段不存在或已被删除</div>
        </el-dialog>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import conversationIllustration from '@/assets/chat-garden-hero.png';
import calmIllustration from '@/assets/chat-calm-corner.png';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
import { useCommonStore } from '@/store';
import { ElMessage } from 'element-plus';
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import { consumeSseStream } from '@/utils/sse';
import { useRoute, useRouter } from 'vue-router';

const commonStore = useCommonStore();
const router = useRouter();
const route = useRoute();
const welcomeTopics = [
    { title: '学习压力', question: '学习压力大时，如何放松？', icon: 'leaf', tone: 'blue' },
    { title: '人际沟通', question: '怎样和朋友更好地沟通？', icon: 'people', tone: 'purple' },
    { title: '情绪调节', question: '情绪低落时可以做些什么？', icon: 'heart', tone: 'pink' }
];
const readingCards = [
    { title: '和压力相处的小练习', tags: ['学习与成长', '情绪与压力'], description: '从日常生活中找到放松的方法，让紧绷的心慢慢松下来。', keyword: '压力', alt: '花香与阳光里的笔记本和茶杯', imageClass: 'desk-image' },
    { title: '给情绪一点休息的时间', tags: ['情绪与压力', '自我关怀'], description: '允许自己偶尔停下来，在忙碌的生活中找到内心的平静。', keyword: '情绪', alt: '开满白花的窗边与安静的阅读角落', imageClass: 'window-image' }
];
const defaultHotQuestions = [
    { id: 'study', questionText: '考试前总是紧张怎么办？' },
    { id: 'family', questionText: '如何与父母好好沟通？' },
    { id: 'mood', questionText: '情绪低落时可以做些什么？' }
];
const displayHotQuestions = computed(() => hotQuestions.value.length ? hotQuestions.value.slice(0, 3) : defaultHotQuestions);
const sessionSearch = ref('');
const showSessionSearch = ref(false);
const supportDialogShow = ref(false);
const inputRef = ref(null);
const filteredSessions = computed(() => sessionList.value.filter(item => (item.sessionTitle || '').includes(sessionSearch.value)));
const chooseTopic = (question) => { inputText.value = question; inputRef.value?.focus(); };

const inputText = ref(typeof route.query.question === 'string' ? route.query.question.slice(0, 2000) : '');
const currentSessionId = ref(null);
const messagesRef = ref(null);
const sessionList = ref([]);
const hotQuestions = ref([]);
const messages = ref([]);
const isStreaming = ref(false);
const streamingContent = ref('');
const streamingChunkIds = ref('');
const sessionLoading = ref(false);
const streamingMode = ref('');
let streamController = null;
let disposed = false;
const chunkDialogShow = ref(false);
const chunkDetail = ref(null);

const currentSession = computed(() => sessionList.value.find(s => s.id === currentSessionId.value));

// 格式化AI回复内容（换行处理）
const formatContent = (content) => {
    if (!content) return '';
    return content.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\n/g, '<br/>');
};

// 滚动到底部
const scrollToBottom = async () => {
    await nextTick();
    if (messagesRef.value) {
        messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
};

// 加载会话列表
const loadSessions = async () => {
    sessionLoading.value = true;
    const { data, success } = await post('/aiChat/mySessions', { page: 1, limit: 100 });
    if (success) {
        sessionList.value = data.items || [];
    }
    sessionLoading.value = false;
};

// 加载热门问题
const loadHotQuestions = async () => {
    const { data, success } = await post('/aiChat/hotQuestions', {});
    if (success && data) {
        hotQuestions.value = Array.isArray(data) ? data : [];
    }
};

// 选择会话
const selectSession = async (id) => {
    if (isStreaming.value) return;
    router.replace({ query: { ...route.query, sessionId: id } });
    currentSessionId.value = id;
    const { data, success } = await post('/aiChat/getMessages', { id });
    if (success && data) {
        messages.value = Array.isArray(data) ? data.map(m => ({ ...m, feedback: null })) : [];
    }
    scrollToBottom();
};

// 新建对话
const newChat = () => {
    if (isStreaming.value) return;
    currentSessionId.value = null;
    router.replace({ query: { ...route.query, sessionId: undefined } });
    messages.value = [];
    streamingContent.value = '';
};

// 删除会话
const deleteSession = async (id) => {
    if (isStreaming.value) return;
    const { success } = await post('/aiChat/deleteSession', { id });
    if (success) {
        sessionList.value = sessionList.value.filter(s => s.id !== id);
        if (currentSessionId.value === id) {
            currentSessionId.value = null;
            messages.value = [];
        }
        ElMessage.success({ message: '已删除', duration: 1500 });
    }
};

// 点击热门问题
const askHotQuestion = (text) => {
    inputText.value = text;
    sendMessage();
};

// 发送消息
const sendMessage = async () => {
    if (!inputText.value.trim() || isStreaming.value) return;
    if (!commonStore.token) { ElMessage.warning('请先登录'); router.push('/login'); return; }
    const question = inputText.value.trim();
    if (question.length > 2000) { ElMessage.warning('问题不能超过 2000 字'); return; }
    isStreaming.value = true; inputText.value = ''; streamingContent.value = ''; streamingChunkIds.value = ''; streamingMode.value = '';
    let savedMessageId = null, tokenUsage = null;
    try {
        if (!currentSessionId.value) {
            const { data, success } = await post('/aiChat/createSession', { question });
            if (!success || !data?.id) throw new Error('创建会话失败');
            currentSessionId.value = data.id; sessionList.value.unshift(data);
            router.replace({ query: { ...route.query, sessionId: data.id } });
        }
        messages.value.push({ id: 'user_' + Date.now(), senderRole: 1, messageContent: question });
        scrollToBottom();
        streamController = new AbortController();
        const response = await fetch((import.meta.env.VITE_API_BASE_URL || '') + '/aiChat/chatStream', {
            method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: commonStore.token },
            body: JSON.stringify({ sessionId: currentSessionId.value, question }), signal: streamController.signal
        });
        if (response.status === 401) commonStore.logout();
        await consumeSseStream(response, event => {
            if (event.type === 'content') { streamingContent.value += event.content; scrollToBottom(); }
            if (event.type === 'chunks') { streamingChunkIds.value = event.chunkIds; streamingMode.value = event.retrievalMode; }
            if (event.type === 'saved') { savedMessageId = event.messageId; tokenUsage = event.tokenUsage; }
        });
        if (!savedMessageId || !streamingContent.value) throw new Error('回复未能保存，请重试');
        messages.value.push({ id: savedMessageId, senderRole: 2, messageContent: streamingContent.value,
            retrievedChunkIds: streamingChunkIds.value, tokenUsage, retrievalMode: streamingMode.value, feedback: null });
    } catch (error) {
        if (!disposed) {
            ElMessage.error(error.message || '请求失败，请重试'); inputText.value = question;
            if (currentSessionId.value) {
                const result = await post('/aiChat/getMessages', { id: currentSessionId.value });
                if (result.success) messages.value = result.data || [];
            }
        }
    } finally {
        streamController = null; isStreaming.value = false; streamingContent.value = ''; streamingChunkIds.value = '';
        if (!disposed) { scrollToBottom(); loadSessions(); }
    }
};

// 提交反馈
const submitFeedback = async (msg, rating) => {
    if (!commonStore.token) return;
    msg.feedback = rating;
    const { success } = await post('/aiChat/feedback', {
        chatMessageId: msg.id,
        rating: rating,
        feedbackContent: rating === 5 ? '满意' : '不满意'
    });
    if (success) {
        ElMessage.success({ message: '感谢你的反馈', duration: 1500 });
    }
};

// 解析chunkIds字符串为数组
const parseChunkIds = (chunkIds) => {
    if (!chunkIds) return [];
    return chunkIds.split(',').map(s => s.trim()).filter(s => s);
};

// 查看知识片段详情
const showChunkDetail = async (chunkId) => {
    chunkDetail.value = null;
    chunkDialogShow.value = true;
    const { data, success } = await post('/aiChat/getChunkDetails', { chunkIds: [Number(chunkId)] });
    if (success && data && data.length > 0) {
        chunkDetail.value = data[0];
    } else { ElMessage.warning('来源片段不存在或已移除'); chunkDialogShow.value = false; }
};

onMounted(async () => {
    if (commonStore.token) {
        await loadSessions();
        const id = Number(route.query.sessionId);
        if (sessionList.value.some(s => s.id === id)) await selectSession(id);
    }
    loadHotQuestions();
});
onBeforeUnmount(() => { disposed = true; streamController?.abort(); });
</script>

<style scoped lang="scss">
.chat-page { height: calc(100dvh - 112px); min-height: 780px; color: #203e76; --ink: #17386f; --muted: #8590b5; }
.chat-page button { font-family: inherit; cursor: pointer; }
.chat-page button:disabled { cursor: default; opacity: .55; }
.chat-layout { display: grid; grid-template-columns: minmax(220px, 20.7fr) minmax(0, 57fr) minmax(240px, 20.7fr); gap: 6px; height: 100%; }
.chat-sidebar, .chat-main, .recommend-sidebar { border: 1px solid #dfe8ff; border-radius: 17px; background: linear-gradient(140deg, #ffffffec, #f9fbffed); overflow: hidden; min-width: 0; }
.chat-sidebar { display: flex; flex-direction: column; padding: 21px 20px 0; }
.new-chat-btn { flex-shrink: 0; display: flex; justify-content: center; align-items: center; gap: 12px; width: 100%; height: 50px; color: white; background: linear-gradient(110deg, #426db1, #6b7fc8); border: 0; border-radius: 11px; font-size: 17px; letter-spacing: 1px; box-shadow: inset 0 1px 1px #ffffff50; }
.new-chat-btn svg { width: 24px; height: 24px; }
.sidebar-header { display: flex; justify-content: space-between; align-items: center; padding: 30px 0 12px; }
h3, h4 { font-family: 'Noto Serif SC', 'Songti SC', 'SimSun', serif; color: #112d62; font-weight: 600; }
.sidebar-header h3 { font-size: 18px; }
.icon-button { padding: 2px; border: 0; background: transparent; color: #4c5fb0; }
.icon-button svg { width: 22px; height: 22px; display: block; }
.session-search { margin-bottom: 10px; }
.session-list { min-height: 195px; max-height: 235px; overflow-y: auto; flex: 0 1 235px; }
.empty-session { padding: 30px 0 36px; text-align: center; }
.empty-session > svg { display: block; width: 88px; height: 75px; margin: 0 auto 17px; color: #7c83d0; stroke-width: .65; }
.empty-session p { font-size: 16px; margin-bottom: 7px; }
.empty-session > span { font-size: 13px; color: var(--muted); }
.session-item { display: flex; align-items: center; gap: 9px; padding: 12px 8px; border-radius: 8px; margin-bottom: 5px; cursor: pointer; }
.session-item:hover, .session-item.active { background: #eaf0ff; }
.session-info { flex: 1; min-width: 0; }.session-icon { color: #697bc0; flex-shrink: 0; }
.session-title { display: block; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }.session-time { color: var(--muted); font-size: 11px; }
.session-delete { opacity: .5; flex-shrink: 0; }.session-item:hover .session-delete { opacity: 1; }
.hot-section { padding: 26px 0 8px; border-top: 1px solid #e4e9f6; }
.hot-section h4 { font-size: 18px; margin-bottom: 11px; }
.hot-item { display: flex; width: 100%; align-items: center; gap: 16px; padding: 13px 0; border: 0; border-bottom: 1px solid #e6ebf8; background: transparent; text-align: left; font-size: 14px; color: #526696; line-height: 1.6; }
.hot-item:last-child { border-bottom: 0; }.hot-item:hover { color: #3757b8; }.hot-item svg { width: 27px; height: 27px; flex-shrink: 0; color: #4864bc; }.hot-chevron { margin-left: auto; font-size: 25px; color: #a0add0; }
.sidebar-art { flex: 1; min-height: 220px; margin: 0 -20px; background: linear-gradient(#fbfcff 0%, #fbfcff00 45%), url('@/assets/chat-calm-corner.png') 28% bottom / auto 115% no-repeat; position: relative; }
.sidebar-art p { position: relative; z-index: 1; padding: 28px 10px 0 18%; font-family: 'KaiTi', 'STKaiti', serif; color: #3c5087; font-size: 23px; letter-spacing: 3px; line-height: 1.8; transform: rotate(-8deg); text-shadow: 0 2px 8px white, 0 2px 8px white; }.sidebar-art p span { display: block; line-height: 1; padding-left: 32%; }
.chat-main { display: flex; flex-direction: column; }
.chat-header { display: flex; flex-shrink: 0; align-items: center; gap: 18px; padding: 17px 22px; min-height: 61px; box-sizing: border-box; }.chat-header h3 { font-size: 23px; white-space: nowrap; max-width: 55%; overflow: hidden; text-overflow: ellipsis; }.chat-tip { font-size: 12px; color: #8892b5; }.header-dots { margin-left: auto; letter-spacing: 3px; font-size: 13px; color: #4056a3; }
.chat-messages { padding: 0 12px; flex: 1; min-height: 0; overflow-y: auto; }
.chat-welcome { text-align: center; }.chat-welcome > img { width: 100%; height: clamp(255px, 34.5vh, 390px); object-fit: cover; object-position: center 43%; border-radius: 12px 12px 0 0; display: block; mask-image: linear-gradient(#000 0%, #000 78%, transparent 100%); }
.welcome-eyebrow { display: flex; justify-content: center; align-items: center; gap: 9px; color: #5369b4; font-family: 'KaiTi', 'STKaiti', serif; font-size: 19px; letter-spacing: 2px; position: relative; margin-top: -9px; }.welcome-eyebrow svg { width: 26px; height: 26px; }
.chat-welcome h1 { color: #1d3e75; font-family: 'Noto Serif SC', 'Songti SC', 'SimSun', serif; font-size: clamp(28px, 2.9vw, 46px); font-weight: 600; line-height: 1.4; margin-top: 8px; letter-spacing: 2px; }
.welcome-description { color: #7c88af; font-size: 15px; line-height: 1.65; margin: 10px 0 18px; }
.welcome-topics { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; padding: 0 16px; }
.welcome-topics button { min-height: 118px; position: relative; display: flex; align-items: flex-start; gap: 16px; padding: 26px 20px; text-align: left; border: 1px solid #eaf0ff; border-radius: 13px; background: #eaf2fe; color: #5378ca; transition: transform .2s, box-shadow .2s; }
.welcome-topics button:hover { transform: translateY(-3px); box-shadow: 0 6px 18px #5e6bb51a; }.welcome-topics button.purple { background: #f3effe; color: #8c55bf; border-color: #f0eafe; }.welcome-topics button.pink { background: #fdf0f7; color: #a24cae; border-color: #fae7f2; }
.welcome-topics button > svg { width: 34px; height: 34px; flex-shrink: 0; }.topic-copy { min-width: 0; }.topic-copy strong { display: block; color: #1e386a; font-family: 'SimSun', serif; font-size: 18px; font-weight: 600; margin-bottom: 8px; }.topic-copy small { font-size: 12px; color: #8390b8; line-height: 1.5; white-space: nowrap; }.welcome-topics button > svg.topic-arrow { position: absolute; width: 19px; height: 19px; right: 17px; bottom: 13px; }
.trust-row { display: flex; align-items: center; justify-content: center; gap: 0; margin: 24px 0 20px; font-size: 13px; color: #7181b0; }.trust-row span { display: flex; align-items: center; justify-content: center; gap: 9px; padding: 0 30px; border-right: 1px solid #bdcbed; }.trust-row span:last-child { border: 0; }.trust-row svg { width: 22px; height: 22px; color: #3d63bb; }
.composer-wrap { padding: 0 24px 12px; flex-shrink: 0; }.chat-input-area { padding: 12px 14px 12px 20px; border: 1px solid #9cb7ff; border-radius: 17px; background: #ffffffc7; box-shadow: 0 4px 18px #6784c412; }.chat-input-area:focus-within { border-color: #6486df; box-shadow: 0 0 0 3px #698aff12; }.chat-input-area :deep(.el-textarea__inner) { box-shadow: none; border: 0; background: transparent; padding: 5px 0; font: inherit; font-size: 15px; color: #3f5686; min-height: 40px !important; }.chat-input-area :deep(.el-textarea__inner::placeholder) { color: #a1adce; }.composer-actions { display: flex; justify-content: space-between; align-items: center; }.composer-actions > svg { width: 27px; height: 27px; color: #6789dc; }.composer-actions .el-button { border: 0; border-radius: 24px; min-width: 106px; height: 39px; background: linear-gradient(#7a98e6, #4469bc); color: white; }.composer-actions .el-button.is-disabled { opacity: .75; }.input-hint { text-align: right; font-size: 11px; color: #a0add0; padding-top: 6px; }.chat-disclaimer { text-align: center; font-size: 11px; color: #a0add0; margin-top: -1px; }
.recommend-sidebar { padding: 20px; display: flex; flex-direction: column; overflow-y: auto; }.recommend-sidebar > h3 { font-size: 20px; margin-bottom: 16px; letter-spacing: 1px; }.reading-card { padding-bottom: 14px; margin-bottom: 14px; border-bottom: 1px solid #e5eafa; }.reading-card > img { display: block; width: 100%; height: clamp(130px, 16.4vh, 182px); object-fit: cover; border-radius: 11px; }.reading-card > img.desk-image { object-position: 15% 80%; }.reading-card > img.window-image { object-position: 85% 35%; }.reading-card h4 { font-size: 19px; margin: 10px 0; }.reading-tags { display: flex; gap: 10px; flex-wrap: wrap; }.reading-tags span { padding: 4px 12px; background: #edf3ff; color: #657ba9; border: 1px solid #d1ddff; border-radius: 20px; font-size: 12px; }.reading-card p { font-size: 14px; color: #8996b8; line-height: 1.7; margin: 9px 0 10px; }.reading-card a { display: inline-flex; align-items: center; gap: 9px; color: #385caa; font-size: 14px; text-decoration: none; }.reading-card a:hover { text-decoration: underline; }.reading-card a svg { width: 20px; height: 20px; }
.support-card { position: relative; display: block; text-align: left; padding: 22px 17px; border: 0; border-radius: 12px; background: linear-gradient(120deg, #e9f1ff, #e6edfc); margin-top: auto; color: #5271a8; overflow: hidden; flex-shrink: 0; }.support-card h4 { font-size: 19px; position: relative; z-index: 1; }.support-card p { font-size: 13px; line-height: 1.7; margin: 6px 0 10px; }.support-card span { font-size: 13px; }.support-card > svg { position: absolute; width: 115px; height: 115px; right: -18px; bottom: -13px; color: #91a7d5; opacity: .6; }.support-copy { line-height: 1.9; margin: 0 0 14px; }
.message-item { display: flex; gap: 10px; padding: 12px; }.message-item.user { flex-direction: row-reverse; }.message-avatar { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 10px; background: #edf1fd; flex-shrink: 0; }.message-avatar svg { fill: #6382c5; }.message-content { max-width: 82%; }.message-bubble { padding: 12px 16px; border-radius: 12px; line-height: 1.8; font-size: 14px; overflow-wrap: anywhere; }.system-bubble { background: #eef3fc; }.user-bubble { background: #5779c4; color: white; }.source-tags, .feedback-btns { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 8px; font-size: 12px; color: #7a8bb6; }.source-chunk-tag, .fb-btn { cursor: pointer; padding: 3px 8px; background: #eef2fd; border-radius: 5px; }.fb-btn { display: flex; align-items: center; gap: 3px; }.fb-active { color: #365ba6; }.fb-active-bad { color: #bb6a8e; }.typing-cursor, .typing-dots { animation: blink 1s infinite; }@keyframes blink { 50% { opacity: .25; } }
.chunk-doc-info, .chunk-content-text { padding: 14px; border-radius: 10px; background: #f1f5fd; line-height: 1.8; }.chunk-doc-title { display: flex; gap: 8px; align-items: center; }.chunk-doc-desc { margin-top: 8px; }.chunk-meta { display: flex; gap: 20px; margin: 15px 0; }.chunk-content-label { margin-bottom: 8px; }.chunk-content-text { white-space: pre-wrap; overflow-wrap: anywhere; }
@media (min-height: 960px) { .chat-page { min-height: 850px; }.session-list { min-height: 205px; flex-basis: 255px; max-height: 255px; }.sidebar-header { padding-top: 32px; }.welcome-description { margin-bottom: 22px; }.trust-row { margin: 26px 0 24px; } }
@media (max-width: 1350px) { .chat-layout { grid-template-columns: 225px minmax(0, 1fr) 250px; }.chat-sidebar, .recommend-sidebar { padding-left: 16px; padding-right: 16px; }.sidebar-art { margin: 0 -16px; }.chat-header { gap: 10px; padding: 17px; }.chat-header h3 { font-size: 20px; }.chat-tip { font-size: 10px; }.welcome-topics { gap: 10px; padding: 0 8px; }.welcome-topics button { padding: 20px 12px; gap: 10px; }.topic-copy strong { font-size: 16px; }.topic-copy small { white-space: normal; font-size: 11px; }.welcome-topics button > svg { width: 28px; height: 28px; }.trust-row span { padding: 0 16px; font-size: 11px; }.reading-card h4 { font-size: 17px; }.reading-tags span { padding: 4px 9px; } }
@media (max-width: 1100px) { .chat-layout { grid-template-columns: 220px minmax(0, 1fr); gap: 10px; }.recommend-sidebar { display: none; }.chat-page { min-height: 760px; }.chat-welcome > img { height: 300px; } }
@media (max-width: 760px) { .chat-page { height: auto; min-height: 0; }.chat-layout { display: flex; flex-direction: column; gap: 12px; }.chat-sidebar { padding: 12px; }.new-chat-btn { height: 42px; font-size: 15px; }.sidebar-header { padding: 14px 0 8px; }.sidebar-header h3 { font-size: 16px; }.session-list { min-height: 0; max-height: 130px; flex-basis: auto; }.empty-session { padding: 0 0 10px; }.empty-session > svg, .empty-session > span, .sidebar-art, .hot-section { display: none; }.empty-session p { font-size: 12px; margin: 0; text-align: left; color: #8a98bb; }.chat-main { min-height: 740px; }.chat-header { padding: 16px; flex-wrap: wrap; }.chat-tip { font-size: 10px; }.chat-welcome > img { height: 235px; }.welcome-eyebrow { font-size: 15px; margin-top: -6px; }.chat-welcome h1 { font-size: 27px; letter-spacing: 0; }.welcome-description { font-size: 12px; }.welcome-topics { gap: 8px; padding: 0 2px; }.welcome-topics button { flex-direction: column; gap: 8px; padding: 14px 10px 26px; min-height: 135px; }.topic-copy strong { font-size: 15px; }.trust-row { margin: 20px 0; }.trust-row span { padding: 0 8px; font-size: 9px; gap: 4px; }.trust-row svg { width: 17px; height: 17px; }.composer-wrap { padding: 0 12px 13px; margin-top: 10px; }.chat-disclaimer { margin-top: 6px; font-size: 9px; }.input-hint { font-size: 9px; }.chat-messages { overflow: visible; }.recommend-sidebar { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; padding: 16px; }.recommend-sidebar > h3 { grid-column: 1 / -1; margin-bottom: 0; }.reading-card { margin-bottom: 0; }.reading-card h4 { font-size: 15px; }.reading-card p { font-size: 12px; }.reading-tags { gap: 5px; }.reading-tags span { font-size: 10px; padding: 3px 7px; }.support-card { grid-column: 1 / -1; }.message-item { padding: 10px 0; } }
</style>
