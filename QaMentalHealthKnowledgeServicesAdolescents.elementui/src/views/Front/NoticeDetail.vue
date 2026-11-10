<template>
    <div class="notice-detail-page">
        <div class="breadcrumb">
            <span class="breadcrumb-label">您当前的位置:</span>
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/front/home' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>公告详情</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="detail-card">
            <el-tag size="small" effect="plain">{{ notice.noticeTypeDto?.typeName || '公告' }}</el-tag>
            <h1 class="detail-title">{{ notice.title }}</h1>
            <div class="detail-meta">
                <span>
                    <svg viewBox="0 0 16 16" fill="#8191b1" width="14" height="14"><path d="M8 8a3 3 0 100-6 3 3 0 000 6zm-5 6s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H3z"/></svg>
                    {{ notice.publisherDto?.nickName || '管理员' }}
                </span>
                <span>
                    <svg viewBox="0 0 16 16" fill="#8191b1" width="14" height="14"><path d="M4 .5a.5.5 0 00-1 0V1H2a2 2 0 00-2 2v1h16V3a2 2 0 00-2-2h-1V.5a.5.5 0 00-1 0V1H4V.5zM16 14V5H0v9a2 2 0 002 2h12a2 2 0 002-2z"/></svg>
                    {{ notice.publishTime }}
                </span>
            </div>
            <div class="detail-content" v-html="notice.noticeContent"></div>
            <div class="detail-actions">
                <el-button @click="$router.push('/front/home')">返回首页</el-button>
                <el-button :type="isFavorited ? 'warning' : 'default'" @click="toggleFavorite" v-if="commonStore.token">
                    {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from '@/store';
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const commonStore = useCommonStore();
const route = useRoute();
const notice = ref({});
const isFavorited = ref(false);

const loadNotice = async () => {
    const id = route.query.id;
    if (!id) return;
    const { data, success } = await post('/notice/findById', { id: Number(id) });
    if (success && data) notice.value = data;
};

const checkFavorited = async () => {
    if (!commonStore.token) return;
    const id = route.query.id;
    if (!id) return;
    const { data, success } = await post('/favorite/checkFavorited', { favoriteType: 2, targetId: Number(id) });
    if (success) isFavorited.value = !!data;
};

const toggleFavorite = async () => {
    const id = route.query.id;
    const { success } = await post('/favorite/toggle', { favoriteType: 2, targetId: Number(id) });
    if (success) {
        isFavorited.value = !isFavorited.value;
        ElMessage.success({ message: isFavorited.value ? '已收藏' : '已取消收藏', duration: 1500 });
    }
};

onMounted(() => { loadNotice(); checkFavorited(); });
</script>

<style scoped lang="scss">
.notice-detail-page { padding-bottom: 40px; max-width: 800px; margin: 0 auto; }
.detail-card { background: #fff; border-radius: 12px; padding: 32px; border: 1px solid #e5ebf8; }
.detail-title { font-size: 22px; color: #203e76; margin: 16px 0; line-height: 1.4; }
.detail-meta { display: flex; gap: 20px; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #e5ebf8; }
.detail-meta span { display: flex; align-items: center; gap: 4px; font-size: 13px; color: #8191b1; }
.detail-content { font-size: 15px; color: #52699a; line-height: 1.8; }
.detail-content :deep(p) { margin-bottom: 16px; }
.detail-actions { margin-top: 24px; padding-top: 16px; border-top: 1px solid #e5ebf8; display: flex; gap: 12px; }
</style>