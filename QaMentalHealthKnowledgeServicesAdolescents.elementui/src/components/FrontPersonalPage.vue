<template>
    <section class="personal-page">
        <div class="breadcrumb"><router-link to="/front/home">首页</router-link><span>/</span><span>{{ title }}</span></div>
        <FrontPageIntro eyebrow="留一点时间，给自己" :title="title" :description="description" :image="readingImage" />
        <div class="personal-page-grid">
            <div class="personal-page-content"><slot /></div>
            <aside class="personal-companion">
                <img :src="cornerImage" alt="窗边的书本、花朵与茶杯" />
                <div class="personal-companion-copy"><span>YOUR LITTLE SPACE</span><h2>慢慢来，<br />成长自有节奏。</h2><p>收藏一点知识，记录一些感受，<br />也给自己留一点温柔的空间。</p><router-link to="/front/articleList">读一点心理知识 →</router-link></div>
            </aside>
        </div>
    </section>
</template>
<script setup>
import FrontPageIntro from './FrontPageIntro.vue';
import readingImage from '@/assets/article-reading-hero.png';
import cornerImage from '@/assets/chat-calm-corner.png';
defineProps({ title: String, description: String });
</script>
<style scoped lang="scss">
.personal-page { padding-bottom: 48px; }
.breadcrumb { display: flex; align-items: center; gap: 12px; color: #7d8caf; }.breadcrumb a { color: #536d9f; text-decoration: none; }
.personal-page-grid { display: grid; grid-template-columns: minmax(0, 1fr) 300px; gap: 28px; align-items: start; }
.personal-page-content { min-width: 0; }
.personal-companion { overflow: hidden; border-radius: 20px; background: #eef0ff; border: 1px solid #e0e6f8; }
.personal-companion > img { width: 100%; height: 240px; object-fit: cover; display: block; }
.personal-companion-copy { padding: 26px; background: linear-gradient(130deg, #f0edff, #f7faff); }
.personal-companion-copy > span { color: #8e9bbb; font-size: 10px; letter-spacing: 2px; }
.personal-companion-copy h2 { font: 600 28px/1.6 'SimSun', serif; color: #203f7b; margin: 10px 0; }
.personal-companion-copy p { font-size: 13px; line-height: 1.9; color: #7b8aaa; margin-bottom: 22px; }
.personal-companion-copy a { color: #5577cd; text-decoration: none; font-size: 13px; }
@media(max-width: 1000px) { .personal-page-grid { grid-template-columns: minmax(0, 1fr) 250px; gap: 20px; } }
@media(max-width: 760px) { .personal-page-grid { grid-template-columns: 1fr; }.personal-companion { display: grid; grid-template-columns: 35% 1fr; }.personal-companion > img { height: 100%; }.personal-companion-copy { padding: 20px; }.personal-companion-copy h2 { font-size: 22px; } }
</style>
