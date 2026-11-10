<template>
    <footer class="home-footer" :class="{ 'reading-footer': botanical }"><div class="footer-inner"><router-link class="footer-brand" to="/front/home"><WellbeingIcon name="leaf" /><strong>心屿</strong><span>陪伴每一份成长中的心情</span></router-link><nav aria-label="网站说明"><template v-for="(item, index) in infoItems" :key="item.title"><span v-if="index" class="divider">|</span><button @click="selectedInfo = item">{{ item.title }}</button></template></nav><span v-if="botanical" class="footer-note">在更好的自己路上，<br />我们一起慢慢来 ♡</span><p>本平台提供心理健康知识支持，不替代专业诊断与治疗。</p></div></footer>
    <el-dialog :model-value="!!selectedInfo" :title="selectedInfo?.title" @close="selectedInfo = null" width="min(520px, 92vw)"><p class="info-content">{{ selectedInfo?.content }}</p><template #footer><el-button type="primary" @click="selectedInfo = null">我知道了</el-button></template></el-dialog>
</template>
<script setup>
import { ref } from 'vue';
import WellbeingIcon from '@/components/WellbeingIcon.vue';
defineProps({ botanical: { type: Boolean, default: false } });
const selectedInfo = ref(null);
const infoItems = [
    { title: '关于我们', content: '心屿是面向青少年的心理健康知识服务平台，通过知识问答、科普阅读和心理测评，帮助你认识情绪、理解关系、探索成长。' },
    { title: '使用说明', content: '在首页输入困惑，进入知识问答后确认发送；也可以选择主题查找文章，或通过成长专题入口进行心理测评。问答中的参考来源可以点击查看。' },
    { title: '隐私政策', content: '提问时请避免填写身份证号、联系方式、详细住址，以及能够识别自己或他人的私人信息。已登录用户可以在问答页查看与删除自己的对话记录。' },
    { title: '意见反馈', content: '你可以在每条问答回复下选择“满意”或“不满意”，反馈回答的帮助程度。遇到持续的心理困扰，请联系信任的成年人或专业人员获得支持。' }
];
</script>
<style scoped lang="scss">
.home-footer { border-top: 1px solid #e4ebfa; color: #7788b0; padding: 23px 0 27px; background: #f8fbff; }.footer-inner { width: var(--content-width); margin: 0 auto; display: flex; align-items: center; justify-content: space-between; gap: 25px; }.footer-brand { color: #173b7b; text-decoration: none; display: flex; align-items: center; gap: 8px; white-space: nowrap; }.footer-brand svg { width: 33px; height: 33px; color: #6584df; }.footer-brand strong { font: 600 24px 'SimSun', serif; }.footer-brand span { color: #8391b3; font-size: 11px; margin-left: 10px; }nav { display: flex; align-items: center; gap: 15px; white-space: nowrap; }nav button { font-size: 11px; color: #7586ae; cursor: pointer; }nav button:hover { color: #3d62bc; }.divider { font-size: 10px; color: #b6c4e5; }.footer-inner > p { max-width: 290px; border-left: 1px solid #c6d1ed; padding-left: 25px; font-size: 10px; line-height: 1.8; word-break: normal; }.info-content { line-height: 1.9; }
@media (max-width: 1200px) { .footer-inner { gap: 16px; }.footer-brand span { font-size: 9px; margin-left: 3px; }nav { gap: 10px; }nav button { font-size: 10px; }.footer-inner > p { font-size: 9px; max-width: 245px; padding-left: 16px; } }
@media (max-width: 760px) { .footer-inner { flex-wrap: wrap; gap: 17px; }.footer-brand { width: 100%; }.footer-brand span { font-size: 11px; }nav { width: 100%; justify-content: flex-start; gap: 17px; }nav button { font-size: 11px; }.footer-inner > p { padding-left: 0; border: 0; max-width: none; font-size: 10px; }.home-footer { padding-top: 20px; } }
</style>

<style scoped lang="scss">
.reading-footer { border: 0; position: relative; padding: 40px 0 17px; background: linear-gradient(#ffffffb8, #ffffffb0), url('@/assets/article-botanical-landscape.png') center 75% / cover; }
.reading-footer .footer-inner { display: grid; grid-template-columns: 1fr 1.2fr 1fr; align-items: start; gap: 20px; }
.reading-footer .footer-brand { display: grid; grid-template-columns: 44px auto; justify-content: start; gap: 5px 12px; }.reading-footer .footer-brand svg { width: 43px; height: 43px; grid-row: 1 / 3; }.reading-footer .footer-brand strong { font-size: 27px; }.reading-footer .footer-brand span { grid-column: 2; margin: 0; font-size: 13px; letter-spacing: 1px; }
.reading-footer nav { padding-top: 13px; justify-content: center; gap: 18px; }.reading-footer nav button { font-size: 12px; }
.reading-footer .footer-inner > p { grid-column: 1 / -1; max-width: none; padding: 0; border: 0; text-align: center; font-size: 11px; color: #a1aac0; margin-top: 12px; }
.footer-note { justify-self: end; font: 17px/1.7 'KaiTi', 'STKaiti', serif; color: #9da6c8; letter-spacing: 1px; transform: rotate(-7deg); padding-right: 8px; }
@media (max-width: 1050px) { .reading-footer .footer-brand span { font-size: 11px; }.reading-footer nav { gap: 12px; }.reading-footer nav button { font-size: 11px; }.footer-note { font-size: 14px; } }
@media (max-width: 760px) { .reading-footer { padding-top: 25px; }.reading-footer .footer-inner { grid-template-columns: 1fr 1fr; gap: 20px 12px; }.reading-footer .footer-brand { width: auto; }.reading-footer .footer-brand strong { font-size: 25px; }.reading-footer .footer-brand span { font-size: 10px; }.reading-footer nav { grid-row: 2; grid-column: 1 / -1; padding-top: 0; justify-content: flex-start; gap: 20px; }.reading-footer .footer-inner > p { font-size: 9px; margin-top: 0; }.footer-note { font-size: 13px; } }
</style>
