<template>
    <div class="statistics-page">
        <!-- 卡片汇总 -->
        <div class="stat-cards">
            <div class="stat-card" style="border-left: 4px solid #6483df;">
                <div class="card-value">{{ stats.totalDocs || 0 }}</div>
                <div class="card-label">知识文档总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #6BBF73;">
                <div class="card-value">{{ stats.vectorizedDocs || 0 }}</div>
                <div class="card-label">已向量化文档</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #F0A75B;">
                <div class="card-value">{{ stats.totalChunks || 0 }}</div>
                <div class="card-label">文档分块总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #9b8bd5;">
                <div class="card-value">{{ stats.totalTypes || 0 }}</div>
                <div class="card-label">知识分类数</div>
            </div>
        </div>
        <!-- 图表区域 -->
        <div class="chart-row">
            <div class="chart-box">
                <h3>各分类文档数量</h3>
                <div ref="typeDistRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>向量化状态分布</h3>
                <div ref="vectorStatusRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box">
                <h3>各分类分块数量</h3>
                <div ref="typeChunkRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>知识库概览</h3>
                <div ref="overviewRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box full-width">
                <h3>知识库内容词云</h3>
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
const typeDistRef = ref(null);
const vectorStatusRef = ref(null);
const typeChunkRef = ref(null);
const overviewRef = ref(null);
const wordCloudRef = ref(null);

const loadData = async () => {
    const { data, success } = await post('/statistics/knowledge', {});
    if (success && data) {
        stats.value = data;
        renderCharts(data);
    }
    const { data: wcData, success: wcSuccess } = await post('/statistics/knowledgeWordCloud', {});
    if (wcSuccess && wcData) renderWordCloud(wcData);
};

const renderCharts = (data) => {
    // 1. 各分类文档数量（柱状图）
    const typeDist = data.typeDist || [];
    echarts.init(typeDistRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 100, right: 20, top: 20, bottom: 20 },
        xAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'category', data: typeDist.map(t => t.name), axisLabel: { color: '#52699a', fontSize: 12 } },
        series: [{ type: 'bar', data: typeDist.map(t => t.count), barWidth: 22, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#6483df' }, { offset: 1, color: '#a8b9ef' }]), borderRadius: [0, 6, 6, 0] } }]
    });

    // 2. 向量化状态分布（饼图）
    const vectorDist = data.vectorStatusDist || [];
    const statusColors = { '待处理': '#94A3B8', '处理中': '#F0A75B', '已完成': '#6BBF73', '失败': '#E8726D' };
    echarts.init(vectorStatusRef.value).setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 10, textStyle: { color: '#8191b1' } },
        color: vectorDist.map(v => statusColors[v.name] || '#6483df'),
        series: [{ type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'], label: { formatter: '{b}\n{c}个', color: '#52699a' }, data: vectorDist }]
    });

    // 3. 各分类分块数量（柱状图）
    const chunkDist = data.typeChunkDist || [];
    echarts.init(typeChunkRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 100, right: 20, top: 20, bottom: 20 },
        xAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'category', data: chunkDist.map(t => t.name), axisLabel: { color: '#52699a', fontSize: 12 } },
        series: [{ type: 'bar', data: chunkDist.map(t => t.count), barWidth: 22, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#9b8bd5' }, { offset: 1, color: '#c6bdf0' }]), borderRadius: [0, 6, 6, 0] } }]
    });

    // 4. 知识库概览（仪表盘）
    const vectorRate = data.totalDocs > 0 ? Math.round((data.vectorizedDocs || 0) / data.totalDocs * 100) : 0;
    echarts.init(overviewRef.value).setOption({
        series: [{
            type: 'gauge', startAngle: 200, endAngle: -20, min: 0, max: 100,
            pointer: { show: false },
            progress: { show: true, width: 20, roundCap: true, itemStyle: { color: '#6BBF73' } },
            axisLine: { lineStyle: { width: 20, color: [[1, '#F1F5F9']] } },
            axisTick: { show: false }, splitLine: { show: false }, axisLabel: { show: false },
            title: { offsetCenter: [0, '30%'], fontSize: 14, color: '#8191b1' },
            detail: { valueAnimation: true, offsetCenter: [0, '-10%'], fontSize: 36, fontWeight: 700, color: '#203e76', formatter: '{value}%' },
            data: [{ value: vectorRate, name: '向量化完成率' }]
        }]
    });
};

const renderWordCloud = (wcData) => {
    if (!wcData || wcData.length === 0) return;
    echarts.init(wordCloudRef.value).setOption({
        tooltip: { show: true, formatter: (p) => p.name + ': ' + p.value },
        series: [{
            type: 'wordCloud', shape: 'circle', left: 'center', top: 'center', width: '90%', height: '90%',
            sizeRange: [14, 56], rotationRange: [-45, 45], rotationStep: 15, gridSize: 8,
            textStyle: { fontFamily: 'sans-serif', fontWeight: 'bold', color: () => {
                const colors = ['#6483df', '#6BBF73', '#F0A75B', '#E8726D', '#9b8bd5', '#4d6dc4', '#2196F3', '#009688'];
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