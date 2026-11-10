<template>
    <div class="comment-manage">
        <div class="comment-layout">
            <!-- 左侧：文章列表 -->
            <div class="article-sidebar">
                <div class="sidebar-header">
                    <h3>文章列表</h3>
                    <el-input v-model="articleKeyword" placeholder="搜索文章..." clearable size="small" prefix-icon="Search" @input="loadArticles" />
                </div>
                <div class="article-list" v-loading="articleLoading">
                    <div class="article-item" v-for="item in articleList" :key="item.id"
                        :class="{ active: selectedArticleId === item.id }" @click="selectArticle(item)">
                        <div class="article-item-title">{{ item.title }}</div>
                        <div class="article-item-meta">
                            <span>{{ item.articleTypeDto?.typeName || '' }}</span>
                            <span>{{ (item.publishTime || '').substring(0, 10) }}</span>
                        </div>
                    </div>
                    <div class="sidebar-empty" v-if="articleList.length === 0 && !articleLoading">暂无文章</div>
                </div>
            </div>

            <!-- 右侧：评论区域 -->
            <div class="comment-main">
                <div class="comment-main-empty" v-if="!selectedArticleId">
                    <svg viewBox="0 0 64 64" fill="none" width="64" height="64"><rect x="12" y="8" width="40" height="48" rx="6" fill="#F1F5F9"/><rect x="20" y="20" width="24" height="4" rx="2" fill="#CBD5E1"/><rect x="20" y="28" width="18" height="4" rx="2" fill="#CBD5E1"/><rect x="20" y="36" width="20" height="4" rx="2" fill="#CBD5E1"/></svg>
                    <p>请从左侧选择一篇文章查看评论</p>
                </div>
                <template v-else>
                    <div class="comment-main-header">
                        <h3>{{ selectedArticle?.title }}</h3>
                        <span class="comment-count">共 {{ allComments.length }} 条评论</span>
                    </div>
                    <div class="comment-main-body" v-loading="commentLoading">
                        <div class="comment-empty" v-if="topComments.length === 0 && !commentLoading">该文章暂无评论</div>
                        <!-- 顶级评论 -->
                        <div class="comment-thread" v-for="c in topComments" :key="c.id">
                            <div class="comment-card">
                                <div class="comment-card-left">
                                    <el-image v-if="c.userDto?.imageUrls" :src="c.userDto.imageUrls.split(',')[0]" class="comment-avatar" fit="cover" />
                                    <el-avatar v-else :size="40" class="comment-avatar">{{ (c.userDto?.nickName || '?').substring(0, 1) }}</el-avatar>
                                </div>
                                <div class="comment-card-right">
                                    <div class="comment-card-header">
                                        <span class="comment-nick">{{ c.userDto?.nickName || c.userDto?.userName || '匿名' }}</span>
                                        <span class="comment-time">{{ c.creationTime }}</span>
                                        <el-button type="danger" size="small" link class="comment-del" @click="deleteComment(c.id)">删除</el-button>
                                    </div>
                                    <div class="comment-card-content">{{ c.commentContent }}</div>
                                </div>
                            </div>
                            <!-- 子评论 -->
                            <div class="reply-list" v-if="getChildren(c.id).length > 0">
                                <div class="reply-card" v-for="r in getChildren(c.id)" :key="r.id">
                                    <div class="reply-card-left">
                                        <el-image v-if="r.userDto?.imageUrls" :src="r.userDto.imageUrls.split(',')[0]" class="reply-avatar" fit="cover" />
                                        <el-avatar v-else :size="32" class="reply-avatar">{{ (r.userDto?.nickName || '?').substring(0, 1) }}</el-avatar>
                                    </div>
                                    <div class="reply-card-right">
                                        <div class="reply-card-header">
                                            <span class="reply-nick">{{ r.userDto?.nickName || '匿名' }}</span>
                                            <template v-if="r.replyToUserDto?.nickName">
                                                <span class="reply-arrow">回复</span>
                                                <span class="reply-target">{{ r.replyToUserDto.nickName }}</span>
                                            </template>
                                            <span class="comment-time">{{ r.creationTime }}</span>
                                            <el-button type="danger" size="small" link class="comment-del" @click="deleteComment(r.id)">删除</el-button>
                                        </div>
                                        <div class="reply-card-content">{{ r.commentContent }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { Delete, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';

const articleKeyword = ref('');
const articleList = ref([]);
const articleLoading = ref(false);
const selectedArticleId = ref(null);
const selectedArticle = ref(null);
const allComments = ref([]);
const commentLoading = ref(false);

const topComments = computed(() => allComments.value.filter(c => !c.parentId));
const getChildren = (parentId) => allComments.value.filter(c => c.parentId === parentId);

const loadArticles = async () => {
    articleLoading.value = true;
    const params = { page: 1, limit: 100 };
    if (articleKeyword.value) params.title = articleKeyword.value;
    const { data, success } = await post('/article/list', params);
    if (success) articleList.value = data.items || [];
    articleLoading.value = false;
};

const selectArticle = (item) => {
    selectedArticleId.value = item.id;
    selectedArticle.value = item;
    loadComments();
};

const loadComments = async () => {
    if (!selectedArticleId.value) return;
    commentLoading.value = true;
    const { data, success } = await post('/articleComment/list', { page: 1, limit: 500, articleId: selectedArticleId.value });
    if (success) allComments.value = data.items || [];
    commentLoading.value = false;
};

const deleteComment = async (id) => {
    try {
        await ElMessageBox.confirm('确认删除该评论吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' });
        const { success } = await post('/articleComment/deleteById', { id });
        if (success) { loadComments(); ElMessage.success('删除成功'); }
    } catch { ElMessage.warning('用户放弃了操作'); }
};

onMounted(() => { loadArticles(); });
</script>

<style scoped lang="scss">
.comment-manage { height: calc(100vh - 180px); }
.comment-layout { display: flex; gap: 16px; height: 100%; }

/* 左侧文章列表 */
.article-sidebar {
    width: 300px; flex-shrink: 0; background: #fff; border-radius: 10px;
    border: 1px solid #F1F5F9; display: flex; flex-direction: column; overflow: hidden;
}
.sidebar-header { padding: 16px; border-bottom: 1px solid #F1F5F9; }
.sidebar-header h3 { font-size: 15px; color: #1E293B; margin: 0 0 10px; }
.article-list { flex: 1; overflow-y: auto; padding: 8px; }
.article-item {
    padding: 12px 14px; border-radius: 8px; cursor: pointer; margin-bottom: 4px; transition: all 0.2s;
}
.article-item:hover { background: #F8FAFC; }
.article-item.active { background: #EBF3FB; border-left: 3px solid #5B9BD5; }
.article-item-title { font-size: 14px; color: #1E293B; font-weight: 500; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.article-item-meta { display: flex; justify-content: space-between; margin-top: 6px; font-size: 12px; color: #94A3B8; }
.sidebar-empty { text-align: center; padding: 40px 0; color: #94A3B8; font-size: 13px; }

/* 右侧评论区 */
.comment-main {
    flex: 1; background: #fff; border-radius: 10px; border: 1px solid #F1F5F9;
    display: flex; flex-direction: column; overflow: hidden;
}
.comment-main-empty { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #94A3B8; font-size: 14px; gap: 12px; }
.comment-main-header { padding: 16px 20px; border-bottom: 1px solid #F1F5F9; display: flex; align-items: center; justify-content: space-between; }
.comment-main-header h3 { font-size: 15px; color: #1E293B; margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }
.comment-count { font-size: 13px; color: #94A3B8; flex-shrink: 0; margin-left: 12px; }
.comment-main-body { flex: 1; overflow-y: auto; padding: 16px 20px; }
.comment-empty { text-align: center; padding: 40px 0; color: #94A3B8; font-size: 13px; }

/* 评论卡片 */
.comment-thread { margin-bottom: 20px; }
.comment-card { display: flex; gap: 12px; }
.comment-card-left { flex-shrink: 0; }
.comment-avatar { width: 40px; height: 40px; border-radius: 50%; }
.comment-card-right { flex: 1; min-width: 0; }
.comment-card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; flex-wrap: wrap; }
.comment-nick { font-size: 14px; font-weight: 500; color: #1E293B; }
.comment-time { font-size: 12px; color: #94A3B8; }
.comment-del { margin-left: auto; }
.comment-card-content { font-size: 14px; color: #334155; line-height: 1.7; }

/* 回复列表 */
.reply-list { margin-left: 52px; padding: 12px 16px; background: #F8FAFC; border-radius: 8px; margin-top: 10px; }
.reply-card { display: flex; gap: 10px; padding: 10px 0; border-bottom: 1px solid #F1F5F9; }
.reply-card:last-child { border-bottom: none; padding-bottom: 0; }
.reply-card:first-child { padding-top: 0; }
.reply-card-left { flex-shrink: 0; }
.reply-avatar { width: 32px; height: 32px; border-radius: 50%; }
.reply-card-right { flex: 1; min-width: 0; }
.reply-card-header { display: flex; align-items: center; gap: 6px; margin-bottom: 3px; flex-wrap: wrap; }
.reply-nick { font-size: 13px; font-weight: 500; color: #334155; }
.reply-arrow { font-size: 12px; color: #94A3B8; }
.reply-target { font-size: 13px; font-weight: 500; color: #5B9BD5; }
.reply-card-content { font-size: 13px; color: #64748B; line-height: 1.6; }
</style>