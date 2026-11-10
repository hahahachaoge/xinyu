<template>
    <div class="statistics-page">
        <!-- 卡片汇总 -->
        <div class="stat-cards">
            <div class="stat-card" style="border-left: 4px solid #6483df;">
                <div class="card-value">{{ stats.totalUsers || 0 }}</div>
                <div class="card-label">注册用户总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #6BBF73;">
                <div class="card-value">{{ stats.normalUsers || 0 }}</div>
                <div class="card-label">普通用户</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #F0A75B;">
                <div class="card-value">{{ stats.activeUsers || 0 }}</div>
                <div class="card-label">近30天活跃用户</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #9b8bd5;">
                <div class="card-value">{{ stats.adminUsers || 0 }}</div>
                <div class="card-label">管理员</div>
            </div>
        </div>
        <!-- 图表区域 -->
        <div class="chart-row">
            <div class="chart-box">
                <h3>近7天新增用户趋势</h3>
                <div ref="userTrendRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>近7天用户活跃趋势</h3>
                <div ref="activeTrendRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box">
                <h3>用户行为分布</h3>
                <div ref="behaviorRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>用户参与度统计</h3>
                <div ref="participationRef" class="chart"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import * as echarts from 'echarts';
import { onMounted, ref } from 'vue';

const stats = ref({});
const userTrendRef = ref(null);
const activeTrendRef = ref(null);
const behaviorRef = ref(null);
const participationRef = ref(null);

const loadData = async () => {
    const { data, success } = await post('/statistics/user', {});
    if (success && data) {
        stats.value = data;
        renderCharts(data);
    }
};

const renderCharts = (data) => {
    // 1. 新增用户趋势（折线图）
    const trend = data.userTrend || [];
    echarts.init(userTrendRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 30, bottom: 30 },
        xAxis: { type: 'category', data: trend.map(i => i.date.substring(5)), axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        series: [{ data: trend.map(i => i.count), type: 'line', smooth: true, areaStyle: { color: 'rgba(100,131,223,0.13)' }, lineStyle: { color: '#6483df', width: 3 }, itemStyle: { color: '#6483df' } }]
    });

    // 2. 活跃趋势（柱状图）
    const active = data.activeTrend || [];
    echarts.init(activeTrendRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 30, bottom: 30 },
        xAxis: { type: 'category', data: active.map(i => i.date.substring(5)), axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        series: [{ data: active.map(i => i.count), type: 'bar', barWidth: 24, itemStyle: { color: '#6BBF73', borderRadius: [4, 4, 0, 0] } }]
    });

    // 3. 行为分布（饼图）
    const behavior = data.behaviorDist || [];
    echarts.init(behaviorRef.value).setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 10, textStyle: { color: '#8191b1' } },
        color: ['#6483df', '#F0A75B', '#6BBF73', '#9b8bd5'],
        series: [{ type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'], label: { formatter: '{b}\n{d}%', color: '#52699a' }, data: behavior }]
    });

    // 4. 参与度统计（雷达图）
    echarts.init(participationRef.value).setOption({
        tooltip: {},
        radar: {
            indicator: [
                { name: '问答会话', max: Math.max(data.totalSessions || 1, 10) },
                { name: '心理测评', max: Math.max(data.totalAssessments || 1, 10) },
                { name: '收藏内容', max: Math.max(data.totalFavorites || 1, 10) },
                { name: '发表评论', max: Math.max(data.totalComments || 1, 10) }
            ],
            shape: 'circle',
            splitArea: { areaStyle: { color: ['rgba(91,155,213,0.05)', 'rgba(91,155,213,0.1)'] } }
        },
        series: [{ type: 'radar', data: [{ value: [data.totalSessions || 0, data.totalAssessments || 0, data.totalFavorites || 0, data.totalComments || 0], name: '用户参与度', areaStyle: { color: 'rgba(91,155,213,0.2)' }, lineStyle: { color: '#6483df' }, itemStyle: { color: '#6483df' } }] }]
    });
};

onMounted(() => { loadData(); });
</script>

<style scoped lang="scss">
.statistics-page { }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { background: #fff; border-radius: 10px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.card-value { font-size: 32px; font-weight: 700; color: #203e76; }
.card-label { font-size: 13px; color: #94A3B8; margin-top: 4px; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
.chart-box { background: #fff; border-radius: 10px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.chart-box h3 { font-size: 15px; color: #203e76; margin: 0 0 12px; }
.chart { height: 300px; }
</style>
