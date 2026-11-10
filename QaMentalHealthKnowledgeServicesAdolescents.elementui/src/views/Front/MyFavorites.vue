<template>
    <div class="my-records">
        <div class="records-header">
            <h3>我的收藏</h3>
        </div>
        <div class="record-list">
            <div class="record-item" v-for="item in favorites" :key="item.id">
                <div class="record-icon" :style="{ backgroundColor: item.favoriteType === 1 ? '#edf2ff' : '#EDF7EE' }">
                    <svg v-if="item.favoriteType === 1" viewBox="0 0 20 20" fill="#6483df" width="18" height="18"><path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd"/></svg>
                    <svg v-else viewBox="0 0 20 20" fill="#6BBF73" width="18" height="18"><path fill-rule="evenodd" d="M18 3a1 1 0 00-1.447-.894L8.763 6H5a3 3 0 000 6h.28l1.771 5.316A1 1 0 008 18h1a1 1 0 001-1v-4.382l6.553 3.276A1 1 0 0018 15V3z" clip-rule="evenodd"/></svg>
                </div>
                <div class="record-info">
                    <h4>{{ item._title || '未知' }}</h4>
                    <p>{{ item.favoriteType === 1 ? '科普文章' : '公告信息' }} · {{ (item.creationTime || '').substring(0, 16) }}</p>
                </div>
                <span class="cancel-btn" @click="cancelFavorite(item.id)">取消收藏</span>
            </div>
        </div>
        <div class="empty-tip" v-if="favorites.length === 0 && !loading">
            <p>暂无收藏内容</p>
            <router-link to="/front/articleList">发现值得收藏的心理知识 →</router-link>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from '@/store';
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';

const commonStore = useCommonStore();
const favorites = ref([]);
const loading = ref(false);

const loadFavorites = async () => {
    if (!commonStore.userId) return;
    loading.value = true;
    const { data, success } = await post('/favorite/list', { page: 1, limit: 100, userId: commonStore.userId });
    if (success) {
        const items = data.items || [];
        // 加载每个收藏对应的标题
        for (const item of items) {
            if (item.favoriteType === 1) {
                const r = await post('/article/findById', { id: item.targetId });
                item._title = r.success && r.data ? r.data.title : '文章已删除';
            } else if (item.favoriteType === 2) {
                const r = await post('/notice/findById', { id: item.targetId });
                item._title = r.success && r.data ? r.data.title : '公告已删除';
            }
        }
        favorites.value = items;
    }
    loading.value = false;
};

const cancelFavorite = async (id) => {
    const { success } = await post('/favorite/cancel', { id });
    if (success) {
        favorites.value = favorites.value.filter(f => f.id !== id);
        ElMessage.success({ message: '已取消收藏', duration: 1500 });
    }
};

onMounted(() => { loadFavorites(); });
</script>

<style scoped lang="scss">
.my-records { background: #fff; border-radius: 8px; padding: 20px; }
.records-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.records-header h3 { font-size: 16px; color: #203e76; }
.record-item { display: flex; align-items: center; gap: 12px; padding: 14px 0; border-bottom: 1px solid #f8faff; }
.record-item:last-child { border-bottom: none; }
.record-icon { width: 36px; height: 36px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.record-info { flex: 1; }
.record-info h4 { font-size: 14px; color: #52699a; margin-bottom: 2px; }
.record-info p { font-size: 12px; color: #8191b1; }
.cancel-btn { font-size: 12px; color: #E8726D; cursor: pointer; flex-shrink: 0; }
.cancel-btn:hover { text-decoration: underline; }
.empty-tip { text-align: center; padding: 40px 0; color: #8191b1; }
</style>
