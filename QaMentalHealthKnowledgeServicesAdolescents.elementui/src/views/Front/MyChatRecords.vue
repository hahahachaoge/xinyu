<template>
    <div class="my-records">
        <div class="records-header">
            <h3>我的问答记录</h3>
        </div>
        <div class="record-list">
            <div class="record-item" v-for="item in records" :key="item.id" @click="goChat(item.id)">
                <div class="record-icon">
                    <svg viewBox="0 0 20 20" fill="#6483df" width="18" height="18"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7z" clip-rule="evenodd"/></svg>
                </div>
                <div class="record-info">
                    <h4>{{ item.sessionTitle }}</h4>
                    <p>{{ item.messageCount || 0 }} 条消息 · {{ (item.lastMessageTime || '').substring(0, 16) }}</p>
                </div>
            </div>
        </div>
        <div class="empty-tip" v-if="records.length === 0 && !loading">
            <p>暂无问答记录</p>
            <router-link to="/front/aiChat">从一个问题开始 →</router-link>
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

const loadRecords = async () => {
    if (!commonStore.token) return;
    loading.value = true;
    const { data, success } = await post('/aiChat/mySessions', { page: 1, limit: 100 });
    if (success) records.value = data.items || [];
    loading.value = false;
};

const goChat = (sessionId) => {
    router.push('/front/aiChat?sessionId=' + sessionId);
};

onMounted(() => { loadRecords(); });
</script>

<style scoped lang="scss">
.my-records { background: #fff; border-radius: 8px; padding: 20px; }
.records-header { margin-bottom: 16px; }
.records-header h3 { font-size: 16px; color: #203e76; }
.record-item { display: flex; align-items: center; gap: 12px; padding: 14px 0; border-bottom: 1px solid #f8faff; cursor: pointer; }
.record-item:hover { background: #f8faff; }
.record-item:last-child { border-bottom: none; }
.record-icon { width: 36px; height: 36px; border-radius: 8px; background: #edf2ff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.record-info h4 { font-size: 14px; color: #52699a; margin-bottom: 2px; }
.record-info p { font-size: 12px; color: #8191b1; }
.empty-tip { text-align: center; padding: 40px 0; color: #8191b1; }
</style>
