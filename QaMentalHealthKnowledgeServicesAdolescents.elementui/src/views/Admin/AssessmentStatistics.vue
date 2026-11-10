<template>
    <div class="statistics-page">
        <!-- 卡片汇总 -->
        <div class="stat-cards">
            <div class="stat-card" style="border-left: 4px solid #6483df;">
                <div class="card-value">{{ stats.totalRecords || 0 }}</div>
                <div class="card-label">测评总次数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #6BBF73;">
                <div class="card-value">{{ stats.participantCount || 0 }}</div>
                <div class="card-label">参与用户数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #F0A75B;">
                <div class="card-value">{{ stats.totalScales || 0 }}</div>
                <div class="card-label">量表总数</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #9b8bd5;">
                <div class="card-value">{{ stats.avgScore || 0 }}</div>
                <div class="card-label">平均得分</div>
            </div>
            <div class="stat-card" style="border-left: 4px solid #E8726D;">
                <div class="card-value">{{ stats.totalQuestions || 0 }}</div>
                <div class="card-label">题目总数</div>
            </div>
        </div>
        <!-- 图表区域 -->
        <div class="chart-row">
            <div class="chart-box">
                <h3>各量表测评次数</h3>
                <div ref="scaleCountRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>测评结果分布</h3>
                <div ref="resultLabelRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box">
                <h3>近7天测评趋势</h3>
                <div ref="trendRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>分数段分布</h3>
                <div ref="scoreRangeRef" class="chart"></div>
            </div>
        </div>
        <div class="chart-row">
            <div class="chart-box">
                <h3>各量表平均分对比</h3>
                <div ref="radarRef" class="chart"></div>
            </div>
            <div class="chart-box">
                <h3>测评活跃用户TOP5</h3>
                <div ref="topUsersRef" class="chart"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import * as echarts from 'echarts';
import { onMounted, ref } from 'vue';

const stats = ref({});
const scaleCountRef = ref(null);
const resultLabelRef = ref(null);
const trendRef = ref(null);
const scoreRangeRef = ref(null);
const radarRef = ref(null);
const topUsersRef = ref(null);

const loadData = async () => {
    const { data, success } = await post('/assessmentStatistics/data', {});
    if (success && data) { stats.value = data; renderCharts(data); }
};

const renderCharts = (data) => {
    // 1. 各量表测评次数（柱状图）
    const sc = data.scaleCountDist || [];
    echarts.init(scaleCountRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 20, right: 20, top: 20, bottom: 40, containLabel: true },
        xAxis: { type: 'category', data: sc.map(i => i.name), axisLabel: { color: '#8191b1', fontSize: 11, rotate: sc.length > 3 ? 15 : 0 } },
        yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        series: [{ data: sc.map(i => i.count), type: 'bar', barWidth: 32, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#6483df' }, { offset: 1, color: '#a8b9ef' }]), borderRadius: [6, 6, 0, 0] } }]
    });

    // 2. 结果标签分布（饼图）
    const rl = data.resultLabelDist || [];
    const labelColors = { '正常': '#6BBF73', '轻度焦虑': '#F0A75B', '中重度焦虑': '#E8726D', '压力正常': '#6BBF73', '压力偏高': '#F0A75B', '压力过大': '#E8726D', '适应良好': '#6BBF73', '适应一般': '#F0A75B', '适应困难': '#E8726D' };
    echarts.init(resultLabelRef.value).setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}次 ({d}%)' },
        legend: { bottom: 10, textStyle: { color: '#8191b1', fontSize: 12 } },
        color: rl.map(i => labelColors[i.name] || '#6483df'),
        series: [{ type: 'pie', radius: ['35%', '60%'], center: ['50%', '42%'], label: { formatter: '{b}\n{d}%', color: '#52699a', fontSize: 12 }, data: rl }]
    });

    // 3. 近7天测评趋势（折线图+面积）
    const trend = data.assessTrend || [];
    echarts.init(trendRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: trend.map(i => i.date.substring(5)), axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        series: [{ data: trend.map(i => i.count), type: 'line', smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(140,126,200,0.3)' }, { offset: 1, color: 'rgba(140,126,200,0.02)' }]) }, lineStyle: { color: '#9b8bd5', width: 3 }, itemStyle: { color: '#9b8bd5' } }]
    });

    // 4. 分数段分布（柱状图）
    const sr = data.scoreRangeDist || [];
    echarts.init(scoreRangeRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 20, right: 20, top: 20, bottom: 40, containLabel: true },
        xAxis: { type: 'category', data: sr.map(i => i.name), axisLabel: { color: '#8191b1', fontSize: 11 } },
        yAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        series: [{ data: sr.map(i => ({ value: i.count, itemStyle: { color: i.color, borderRadius: [6, 6, 0, 0] } })), type: 'bar', barWidth: 40 }]
    });

    // 5. 各量表平均分对比（雷达图）
    const sa = data.scaleAvgScores || [];
    echarts.init(radarRef.value).setOption({
        tooltip: {},
        radar: { indicator: sa.map(i => ({ name: i.name, max: i.max || 15 })), shape: 'circle', splitArea: { areaStyle: { color: ['rgba(91,155,213,0.05)', 'rgba(91,155,213,0.1)'] } } },
        series: [{ type: 'radar', data: [{ value: sa.map(i => i.avg), name: '平均分', areaStyle: { color: 'rgba(140,126,200,0.25)' }, lineStyle: { color: '#9b8bd5', width: 2 }, itemStyle: { color: '#9b8bd5' } }] }]
    });

    // 6. 测评活跃用户TOP5（横向柱状图）
    const tu = (data.topUsers || []).reverse();
    echarts.init(topUsersRef.value).setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 100, right: 30, top: 10, bottom: 10 },
        xAxis: { type: 'value', minInterval: 1, axisLabel: { color: '#8191b1' } },
        yAxis: { type: 'category', data: tu.map(i => i.name), axisLabel: { color: '#52699a', fontSize: 12 } },
        series: [{ type: 'bar', data: tu.map(i => i.count), barWidth: 18, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#F0A75B' }, { offset: 1, color: '#F5C88A' }]), borderRadius: [0, 6, 6, 0] } }]
    });
};

onMounted(() => { loadData(); });
</script>

<style scoped lang="scss">
.statistics-page { }
.stat-cards { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; margin-bottom: 20px; }
.stat-card { background: #fff; border-radius: 10px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.card-value { font-size: 32px; font-weight: 700; color: #203e76; }
.card-label { font-size: 13px; color: #94A3B8; margin-top: 4px; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
.chart-box { background: #fff; border-radius: 10px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
.chart-box h3 { font-size: 15px; color: #203e76; margin: 0 0 12px; }
.chart { height: 300px; }
</style>