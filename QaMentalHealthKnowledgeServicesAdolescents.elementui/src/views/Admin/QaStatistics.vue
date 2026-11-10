<template>
    <div class="statistics-page">
        <!-- 卡片汇总 -->
        <div class="stat-cards">
            <div class="stat-card" style="border-left: 4px solid #6483df;">
                <div class="card-value">{{ stats.totalSessions || 0 }}</div>
                <div class="card-label">对话总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #6BBF73;">
                <div class="card-value">{{ stats.totalMessages || 0 }}</div>
                <div class="card-label">消息总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #F0A75B;">
                <div class="card-value">{{ stats.totalFeedbacks || 0 }}</div>
                <div class="card-label">反馈总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #E8726D;">
                <div class="card-value">{{ stats.satisfactionRate || 0 }}%</div>
                <div class="card-label">用户满意率</div>
            </div>
        </div>
        <!-- 图表区域 -->
        <div class="chart-row">
            <div class="chart-box">
                <h3>近7天问答量趋势</h3>
                <div ref="qaTrendRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>消息角色分布</h3>
                <div ref="roleDistRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box">
                <h3>反馈评分分布</h3>
                <div ref="ratingDistRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>热门问题TOP10</h3>
                <div ref="hotQuestionsRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box full-width">
                <h3>问答关键词词云</h3>
                <div ref="wordCloudRef" class="chart" style="height: 400px;"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import * as echarts from 'echarts';
import 'echarts-wordcloud';
import { onMounted, ref } from 'vue';

const stats = ref({});
const qaTrendRef = ref(null);
const roleDistRef = ref(null);
const ratingDistRef = ref(null);
const hotQuestionsRef = ref(null);
const wordCloudRef = ref(null);

const loadData = async () => {
    const { data, success } = await post('/statistics/qa', {});
    if (success && data) {
        stats.value = data;
        renderCharts(data);
    }
    // 加载词云
    const { data: wcData, success: wcSuccess } = await post('/statistics/qaWordCloud', {});
    if (wcSuccess && wcData) renderWordCloud(wcData);
};

const renderCharts = (data) => {
    // 1. 问答量趋势
    const trend = data.qaTrend || [];
    echarts.init(qaTrendRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 30, bottom: 30 },
        xAxis: { type: 'category', data: trend.map(i => i.date.substring(5)), axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        series: [{
            data: trend.map(i => i.count), type: 'line', smooth: true,
            areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(91,155,213,0.3)' }, { offset: 1, color: 'rgba(91,155,213,0.02)' }]) },
            lineStyle: { color: '#6483df', width: 3 }, itemStyle: { color: '#6483df' }
        }]
    });

    // 2. 消息角色分布
    const roleDist = data.roleDist || [];
    echarts.init(roleDistRef.value).setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 10, textStyle: { color: '#8191b1' } },
        color: ['#6483df', '#6BBF73'],
        series: [{ type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'], label: { formatter: '{b}\n{c}条', color: '#52699a' }, data: roleDist }]
    });

    // 3. 评分分布
    const ratingDist = data.ratingDist || [];
    const ratingLabels = ['1分(很差)', '2分(较差)', '3分(一般)', '4分(满意)', '5分(很满意)'];
    const ratingColors = ['#E8726D', '#F0A75B', '#F0D75B', '#6BBF73', '#6483df'];
    echarts.init(ratingDistRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 80, right: 20, top: 30, bottom: 30 },
        xAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'category', data: ratingLabels, axisLabel: { color: '#8191b1', fontSize: 12 } },
        series: [{ type: 'bar', data: ratingDist.map((r, i) => ({ value: r.count, itemStyle: { color: ratingColors[i], borderRadius: [0, 4, 4, 0] } })), barWidth: 20 }]
    });

    // 4. 热门问题TOP10
    const hot = (data.hotQuestions || []).reverse();
    echarts.init(hotQuestionsRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 180, right: 30, top: 10, bottom: 10 },
        xAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'category', data: hot.map(h => h.question && h.question.length > 16 ? h.question.substring(0, 16) + '...' : h.question), axisLabel: { color: '#52699a', fontSize: 12 } },
        series: [{ type: 'bar', data: hot.map(h => h.count), barWidth: 16, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#6483df' }, { offset: 1, color: '#a8b9ef' }]), borderRadius: [0, 4, 4, 0] } }]
    });
};

const renderWordCloud = (wcData) => {
    if (!wcData || wcData.length === 0) return;
    echarts.init(wordCloudRef.value).setOption({
        tooltip: { show: true, formatter: (p) => p.name + ': ' + p.value },
        series: [{
            type: 'wordCloud', shape: 'circle', left: 'center', top: 'center', width: '90%', height: '90%',
            sizeRange: [14, 60], rotationRange: [-30, 30], rotationStep: 10, gridSize: 8,
            textStyle: { fontFamily: 'sans-serif', fontWeight: 'bold', color: () => {
                const colors = ['#6483df', '#6BBF73', '#F0A75B', '#E8726D', '#9b8bd5', '#4d6dc4', '#4CAF50', '#FF9800'];
                return colors[Math.floor(Math.random() * colors.length)];
            }},
            data: wcData
        }]
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
.chart-box.full-width { grid-column: 1 / -1; }
.chart-box h3 { font-size: 15px; color: #203e76; margin: 0 0 12px; }
.chart { height: 300px; }
</style>