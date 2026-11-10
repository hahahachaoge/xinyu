<template>
    <div class="my-records">
        <div class="records-header">
            <h3>我的测评记录</h3>
        </div>
        <div class="record-list">
            <div class="record-item" v-for="item in records" :key="item.id">
                <div class="record-icon" :style="{ backgroundColor: getColor(item.totalScore).bg }">
                    <svg viewBox="0 0 20 20" :fill="getColor(item.totalScore).icon" width="18" height="18"><path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/><path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/></svg>
                </div>
                <div class="record-info">
                    <h4>{{ item.scaleInfoDto?.scaleName || '测评量表' }}</h4>
                    <p>得分：{{ item.totalScore }}分 · {{ item.resultLabel }} · {{ (item.finishTime || '').substring(0, 16) }}</p>
                </div>
                <el-button type="primary" size="small" @click="goDetail(item.id)">查看详情</el-button>
            </div>
        </div>
        <div class="empty-tip" v-if="records.length === 0 && !loading">
            <p>暂无测评记录</p>
            <router-link to="/front/scaleList">开启一次自我探索 →</router-link>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from '@/store';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const commonStore = useCommonStore();
const router = useRouter();
const records = ref([]);
const loading = ref(false);

const getColor = (score) => {
    if (score == null) return { bg: '#e5ebf8', icon: '#8191b1' };
    if (score <= 4) return { bg: '#EDF7EE', icon: '#6BBF73' };
    if (score <= 9) return { bg: '#FEF3E2', icon: '#F0A75B' };
    return { bg: '#FEF2F2', icon: '#E8726D' };
};

const goDetail = (id) => {
    router.push('/front/assessmentDetail?id=' + id);
};

const loadRecords = async () => {
    if (!commonStore.userId) return;
    loading.value = true;
    const { data, success } = await post('/assessmentRecord/list', { page: 1, limit: 100, userId: commonStore.userId });
    if (success) records.value = data.items || [];
    loading.value = false;
};

onMounted(() => { loadRecords(); });
</script>

<style scoped lang="scss">
.my-records { background: #fff; border-radius: 8px; padding: 20px; }
.records-header { margin-bottom: 16px; }
.records-header h3 { font-size: 16px; color: #203e76; }
.record-item { display: flex; align-items: center; gap: 12px; padding: 14px 0; border-bottom: 1px solid #f8faff; }
.record-item:last-child { border-bottom: none; }
.record-icon { width: 36px; height: 36px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.record-info h4 { font-size: 14px; color: #52699a; margin-bottom: 2px; }
.record-info p { font-size: 12px; color: #8191b1; }
.empty-tip { text-align: center; padding: 40px 0; color: #8191b1; }
</style>
