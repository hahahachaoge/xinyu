<template>
    <main class="auth-layout" :class="{ 'auth-layout-compact': compact }">
        <section class="auth-brand" aria-label="平台介绍">
            <div class="brand-area">
                <div class="brand-identity">
                    <span class="brand-mark">
                        <svg viewBox="0 0 60 50" fill="none" aria-hidden="true"><path d="M29 46C22 26 38 10 58 4c0 21-13 33-29 42Z" fill="#a5a9e4"/><path d="M27 46C9 44 4 31 6 18c15 2 23 12 21 28Z" fill="#758ee0"/></svg>
                    </span>
                    <div><strong>心屿</strong><span>青少年心理健康知识服务</span></div>
                </div>
                <div class="brand-copy">
                    <p class="brand-eyebrow"><span></span>让每一份心事，都被温柔听见</p>
                    <h1>在这里，<br />读懂自己，<br /><em>自在成长。</em></h1>
                    <p class="brand-desc">探索心理健康知识，找到内心的答案。<br />陪你走过青春的困惑，也看见成长的光。</p>
                </div>
                <img class="hero-illustration" :src="mentalHealthHero" alt="青少年在花园里阅读、交流，彼此倾听与陪伴" />
                <p class="brand-note">慢慢来，<br />成长自有节奏。</p>
                <div class="feature-cards" aria-label="平台服务">
                    <div><ChatDotRound /><span>智能问答</span></div>
                    <div><CircleCheck /><span>心理测评</span></div>
                    <div><Reading /><span>知识科普</span></div>
                </div>
            </div>
        </section>
        <section class="auth-panel" :aria-label="title">
            <router-link class="auth-back" to="/front/home">← 返回首页</router-link>
            <div class="auth-content">
                <header class="auth-header">
                    <span class="welcome-label">{{ eyebrow }}</span>
                    <h2>{{ title }}<span aria-hidden="true">✦</span></h2>
                    <p>{{ subtitle }}</p>
                </header>
                <slot />
                <div class="auth-gentle-note"><span aria-hidden="true">✧</span><p>给心情一点呼吸的空间，也给自己一点温柔。</p></div>
            </div>
            <footer class="auth-footer"><span>© 2026 心屿 · 青少年心理健康知识服务</span><div><button @click="info = '隐私政策'">隐私政策</button><span>/</span><button @click="info = '使用说明'">使用说明</button></div></footer>
        </section>
    </main>
    <el-dialog :model-value="!!info" :title="info" width="min(480px, 92vw)" @close="info = ''"><p style="line-height: 1.9; color: #60749b">{{ info === '隐私政策' ? '请妥善保管账号信息，不向他人透露密码。提问或留言时，请避免提供能够识别自己或他人的私人信息。' : '登录后可以使用知识问答、阅读科普内容与进行心理测评。忘记密码时，请使用注册账号、邮箱和手机号找回。平台内容不替代专业诊断与治疗。' }}</p><template #footer><el-button @click="info = ''">我知道了</el-button></template></el-dialog>
</template>

<script setup>
import mentalHealthHero from '@/assets/chat-garden-hero.png'
import { ref } from 'vue'
import { ChatDotRound, CircleCheck, Reading } from '@element-plus/icons-vue'

defineProps({
    title: { type: String, required: true },
    subtitle: { type: String, required: true },
    eyebrow: { type: String, default: '很高兴，在这里遇见你' },
    compact: { type: Boolean, default: false }
})
const info = ref('')
</script>

<style scoped lang="scss" src="@/styles/auth-design.scss"></style>
