<template>
    <div class="front-layout home-layout unified-front" :class="{ 'chat-design-layout': isBrandDesign, 'home-design-layout': currentPath === '/front/home', 'article-design-layout': ['/front/articleList', '/front/articleDetail', '/front/scaleList', '/front/scaleTest', '/front/assessmentDetail'].includes(currentPath), 'scale-design-layout': currentPath === '/front/scaleList', 'article-reading-layout': currentPath === '/front/articleDetail', 'test-answer-layout': currentPath === '/front/scaleTest', 'assessment-record-layout': currentPath === '/front/assessmentDetail' }">
        <el-container>
            <el-header class="front-header">
                <div class="header-container">
                    <!-- 左侧logo和系统名称 -->
                    <div class="left-section">
                        <svg v-if="isBrandDesign" class="brand-leaf" viewBox="0 0 60 50" fill="none" aria-hidden="true"><defs><linearGradient id="brand-petal" x1="5" y1="50" x2="60" y2="0" gradientUnits="userSpaceOnUse"><stop stop-color="#456cc8"/><stop offset=".5" stop-color="#8b8ed3"/><stop offset="1" stop-color="#daa8bb"/></linearGradient></defs><path d="M29 46C22 26 38 10 58 4c0 21-13 33-29 42Z" fill="url(#brand-petal)"/><path d="M27 46C9 44 4 31 6 18c15 2 23 12 21 28Z" fill="#5c7dd0"/><path d="M15 17C4 18 0 11 0 3c10 0 16 5 15 14Z" fill="#7f91da"/><path d="M29 45l13-23M27 44L15 27" stroke="white" stroke-opacity=".6"/></svg>
                        <svg v-else class="logo-icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <rect width="40" height="40" rx="10" fill="#397b68" fill-opacity="0.1"/>
                            <path d="M20 8C13.373 8 8 13.373 8 20s5.373 12 12 12 12-5.373 12-12S26.627 8 20 8zm0 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S10 25.523 10 20 14.477 10 20 10z" fill="#397b68"/>
                            <path d="M20 14a2 2 0 100 4 2 2 0 000-4zm-1 6v6h2v-6h-2z" fill="#397b68"/>
                            <path d="M15 18.5c0-.828.672-1.5 1.5-1.5s1.5.672 1.5 1.5v5a1.5 1.5 0 01-3 0v-5zm7 0c0-.828.672-1.5 1.5-1.5s1.5.672 1.5 1.5v5a1.5 1.5 0 01-3 0v-5z" fill="#397b68" opacity="0.5"/>
                        </svg>
                        <template v-if="isBrandDesign"><span class="chat-brand-name">心屿</span><span class="chat-brand-subtitle">青少年心理健康知识服务</span></template>
                        <span v-else class="system-name">青少年心理健康知识服务平台</span>
                    </div>

                    <!-- 中间菜单区域 -->
                    <div class="menu-section">
                        <div class="menu-item" :class="{ active: currentPath === '/front/home' }"
                            @click="toPath('/front/home')">
                            <svg class="menu-svg" viewBox="0 0 20 20" fill="currentColor"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/></svg>
                            首页
                        </div>
                        <div class="menu-item" :class="{ active: currentPath === '/front/aiChat' }"
                            @click="toPath('/front/aiChat')">
                            <svg class="menu-svg" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zm-4 0H9v2h2V9z" clip-rule="evenodd"/></svg>
                            智能问答
                        </div>
                        <div class="menu-item" :class="{ active: currentPath.startsWith('/front/article') }"
                            @click="toPath('/front/articleList')">
                            <svg class="menu-svg" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clip-rule="evenodd"/></svg>
                            科普专区
                        </div>
                        <div class="menu-item" :class="{ active: currentPath.startsWith('/front/scale') || currentPath === '/front/assessmentDetail' }"
                            @click="toPath('/front/scaleList')">
                            <svg class="menu-svg" viewBox="0 0 20 20" fill="currentColor"><path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/><path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/></svg>
                            心理测评
                        </div>
                    </div>

                    <!-- 右侧用户信息 -->
                    <div class="right-section">
                        <template v-if="!token">
                            <el-button type="text" @click="toRegister()">注册</el-button>
                            <el-button type="text" @click="toLogin()">登录</el-button>
                        </template>

                        <el-dropdown v-if="token" trigger="click" placement="bottom-end">
                            <button type="button" class="user-info" aria-label="个人菜单">
                                <el-avatar :size="32" :src="userInfo.imageUrls || defaultAvatar" />
                                <span class="username">{{ userInfo.nickName || userInfo.userName }}</span>
                                <el-icon class="personal-menu-arrow"><ArrowDown /></el-icon>
                            </button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item v-for="item in personalPages" :key="item.path" :icon="item.icon"
                                        :class="{ 'personal-menu-active': currentPath === item.path || (item.path === '/front/myAssessments' && currentPath === '/front/assessmentDetail') }"
                                        @click="toPath(item.path)">
                                        {{ item.title }}
                                    </el-dropdown-item>
                                    <el-dropdown-item divided @click="loginOut()">
                                        <el-icon><SwitchButton /></el-icon>
                                        <span>退出</span>
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </el-header>
            <div class="header-spacer"></div>
            <el-main class="main-container main-box">
                <router-view v-slot="{ Component }">
                    <FrontPersonalPage v-if="personalPage" :title="personalPage.title" :description="personalPage.description">
                        <component :is="Component" />
                    </FrontPersonalPage>
                    <component :is="Component" v-else />
                </router-view>
            </el-main>
            <footer v-if="currentPath === '/front/aiChat'" class="chat-design-footer"><span>心屿 · 陪伴每一份成长中的心情</span><span>隐私政策　 /　 使用说明</span></footer>
            <HomeDesignFooter v-else-if="currentPath === '/front/home'" />
            <HomeDesignFooter v-else-if="currentPath === '/front/articleList'" botanical />
            <HomeDesignFooter v-else-if="currentPath === '/front/scaleList'" />
            <HomeDesignFooter v-else-if="currentPath === '/front/scaleTest'" />
            <HomeDesignFooter v-else-if="currentPath === '/front/assessmentDetail'" />
            <HomeDesignFooter v-else botanical />
        </el-container>
    </div>
</template>

<script setup>
import defaultAvatar from '@/assets/默认头像.png';
import { useCommonStore } from '@/store';
import { ArrowDown, ChatDotRound, Document, Lock, Star, SwitchButton, User } from '@element-plus/icons-vue';
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import FrontPersonalPage from '@/components/FrontPersonalPage.vue';
import HomeDesignFooter from './HomeDesignFooter.vue';

const commonStore = useCommonStore();
const router = useRouter();
const route = useRoute();

const currentPath = computed(() => route.path);
const isBrandDesign = computed(() => true);
const token = computed(() => commonStore.token);
const userInfo = computed(() => commonStore.userInfo);
const personalPages = [
    { title: '个人资料', path: '/front/userInfo', icon: User },
    { title: '修改密码', path: '/front/passwordEdit', icon: Lock },
    { title: '我的问答', path: '/front/myChatRecords', icon: ChatDotRound },
    { title: '我的测评', path: '/front/myAssessments', icon: Document },
    { title: '我的收藏', path: '/front/myFavorites', icon: Star },
];
const personalDescriptions = {
    '/front/userInfo': '让这里更像你。完善个人资料，记录属于自己的成长旅程。',
    '/front/passwordEdit': '照顾自己的感受，也照顾账号的安全。',
    '/front/myChatRecords': '每一个认真提出的问题，都是了解自己的开始。',
    '/front/myAssessments': '回看每一次自我探索，慢慢理解成长中的变化。',
    '/front/myFavorites': '把触动你的知识与启发，留在随时可以找到的地方。',
};
const personalPage = computed(() => {
    const page = personalPages.find(item => item.path === route.path);
    return page ? { ...page, description: personalDescriptions[page.path] } : null;
});

const toLogin = () => router.push({ path: "/login" });
const toRegister = () => router.push({ path: "/register" });
const loginOut = async () => { await commonStore.logout(); router.push({ path: "/login" }); };
const toPath = (url) => router.push({ path: url });
</script>

<style scoped lang="scss">
.front-header { background-color: white; width: 100%; position: fixed; z-index: 1000; padding: 0; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.header-spacer { height: 60px; }
.home-layout { --content-width: min(92%, 1440px); background: #f7f8f3; }
.home-layout .front-header { box-shadow: none; border-bottom: 1px solid #e8ede6; }
.home-layout .main-container { padding-top: 28px !important; }
.home-layout .system-name { color: #28463a; }
.home-layout .menu-item.active, .home-layout .menu-item:hover, .home-layout .el-button--text:hover { color: #397b68; }
.home-layout .menu-item::before { background-color: #397b68; }
.home-layout .logo-icon path { fill: #397b68; }
.home-layout .logo-icon rect { fill: #397b68; }
.header-container {
    width: var(--content-width);
    height: 60px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.left-section {
    display: flex;
    align-items: center;
    gap: 12px;
}
.logo-icon {
    width: 40px;
    height: 40px;
}
.system-name {
    font-size: 16px;
    color: #28463a;
    font-weight: 600;
    letter-spacing: 0.5px;
}
.menu-section {
    display: flex;
    align-items: center;
    gap: 8px;
}
.menu-svg {
    width: 16px;
    height: 16px;
    margin-right: 4px;
}
.menu-item {
    font-size: 14px;
    color: #657c6d;
    cursor: pointer;
    padding: 0 16px;
    position: relative;
    transition: all 0.3s ease;
    height: 60px;
    line-height: 60px;
    display: flex;
    align-items: center;
}
.menu-item:hover {
    color: #397b68;
}
.menu-item::before {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: #397b68;
    transform: scaleX(0);
    transition: transform 0.3s ease;
}
.menu-item:hover::before {
    transform: scaleX(1);
}
.menu-item.active {
    color: #397b68;
    font-weight: 600;
}
.menu-item.active::before {
    transform: scaleX(1);
}
.right-section {
    display: flex;
    align-items: center;
    gap: 16px;
}
.user-info {
    border: 0;
    background: transparent;
    font: inherit;
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
    transition: all 0.3s ease;
}
.user-info:hover {
    background-color: #f5f7fa;
}
.user-info:focus-visible { outline: 2px solid #6483df; outline-offset: 2px; }
.personal-menu-arrow { color: #60739b; font-size: 12px; }
:deep(.personal-menu-active) { color: #456ac7; background: #eef3ff; }
.username {
    font-size: 14px;
    color: #333;
}
:deep(.el-dropdown-menu__item) {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
}
.el-button--text {
    font-size: 14px;
    color: #657c6d;
}
.el-button--text:hover {
    color: #397b68;
}
.main-container {
    position: relative;
    width: var(--content-width);
    margin: 0 auto;
}
.main-box {
    min-height: calc(100vh - 120px);
}
.el-main {
    padding: 20px 0px 0px 0px !important;
    overflow: hidden;
}
@media (max-width: 1100px) {
    .home-layout .system-name { max-width: 230px; font-size: 13px; }
    .home-layout .menu-item { padding: 0 10px; font-size: 13px; }
    .home-layout .left-section { gap: 8px; }
    .home-layout .right-section { gap: 8px; }
}
@media (max-width: 760px) {
    .home-layout .front-header { height: 112px; }
    .home-layout .header-spacer { height: 112px; }
    .home-layout .header-container { height: 112px; flex-wrap: wrap; align-content: center; row-gap: 8px; }
    .home-layout .menu-section { order: 3; width: 100%; justify-content: space-between; gap: 0; }
    .home-layout .menu-item { height: 40px; line-height: 40px; padding: 0 8px; font-size: 12px; }
    .home-layout .system-name { max-width: 195px; font-size: 11px; }
    .home-layout .logo-icon { width: 32px; height: 32px; }
    .home-layout .username { max-width: 64px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 12px; }
    .home-layout .right-section { gap: 0; }
    .home-layout .right-section .el-button { margin-left: 8px; font-size: 12px; }
    .home-layout .main-container { padding-top: 16px !important; }
}
@media (max-width: 380px) {
    .home-layout .system-name { max-width: 150px; }
    .home-layout .menu-svg { display: none; }
}
</style>

<style scoped lang="scss">
.chat-design-layout { --content-width: calc(100% - 56px); --el-color-primary: #5576cb; --el-text-color-primary: #203e76; --el-text-color-regular: #52699a; background: radial-gradient(ellipse at 50% 0%, #f7faffeb 40%, #e9f1ffb8), url('@/assets/chat-garden-hero.png') center / cover fixed; }
.chat-design-layout .front-header { background: #f3f7ffbd; backdrop-filter: blur(16px); border-color: #dae5ff; }
.chat-design-layout .header-container { position: relative; }.chat-design-layout .left-section { gap: 11px; }
.brand-leaf { width: 53px; height: 47px; }.chat-brand-name { font: 600 27px 'SimSun', serif; color: #132f63; white-space: nowrap; }.chat-brand-subtitle { color: #415b90; font-size: 13px; margin-left: 4px; white-space: nowrap; }
.chat-design-layout .menu-section { position: absolute; left: 50%; transform: translateX(-50%); gap: 30px; }.chat-design-layout .menu-item { color: #263f77; font-size: 16px; padding: 0 15px; }.chat-design-layout .menu-svg { display: none; }.chat-design-layout .menu-item.active { color: #244899; }.chat-design-layout .menu-item::before { height: 4px; border-radius: 6px 6px 0 0; background: #5c7dd7; }
.chat-design-layout .right-section { gap: 12px; }.chat-design-layout .right-section .el-button { color: #2d467f; font-size: 15px; }.chat-design-layout .right-section .el-button:last-child { border: 1px solid #7e97db; padding: 9px 22px; height: 37px; border-radius: 10px; margin-left: 0; }
.chat-design-layout .main-container { padding-top: 10px !important; min-height: 0; }.chat-design-footer { width: var(--content-width); margin: 0 auto; padding: 14px 17px; box-sizing: border-box; display: flex; justify-content: space-between; color: #637ba8; font-size: 12px; }
@media (max-width: 1350px) { .chat-design-layout { --content-width: calc(100% - 32px); }.chat-brand-subtitle { font-size: 11px; max-width: 135px; white-space: normal; line-height: 1.5; }.chat-design-layout .menu-section { gap: 12px; }.chat-design-layout .menu-item { font-size: 14px; padding: 0 12px; } }
@media (max-width: 1000px) { .chat-brand-subtitle { display: none; }.chat-design-layout .menu-section { gap: 4px; }.chat-design-layout .menu-item { padding: 0 10px; } }
@media (max-width: 760px) { .chat-design-layout { --content-width: calc(100% - 24px); }.brand-leaf { width: 36px; height: 36px; }.chat-brand-name { font-size: 23px; }.chat-design-layout .menu-section { position: static; transform: none; }.chat-design-layout .menu-item { font-size: 13px; }.chat-design-layout .right-section .el-button:last-child { height: 31px; padding: 6px 16px; }.chat-design-layout .main-container { padding-top: 12px !important; }.chat-design-footer { font-size: 10px; padding: 15px 0; gap: 10px; } }
</style>

<style lang="scss" src="@/styles/front-theme.scss"></style>

<style scoped lang="scss">
.personal-navigation {
    width: var(--content-width);
    margin: 0 auto;
    display: flex;
    justify-content: center;
    gap: 32px;
    height: 44px;
    overflow-x: auto;
    border-top: 1px solid #e5eaf4;
    box-sizing: border-box;
}
.personal-navigation a {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    padding: 0 12px;
    color: #60739b;
    font-size: 14px;
    text-decoration: none;
    border-bottom: 2px solid transparent;
}
.personal-navigation a:hover, .personal-navigation a.is-active {
    color: #456ac7;
    border-bottom-color: #6483df;
}
.personal-navigation a:focus-visible { outline: 2px solid #6483df; outline-offset: -3px; }
.front-layout.home-layout .front-header { height: 104px; }
.front-layout.home-layout .header-spacer { height: 104px; }
@media (max-width: 760px) {
    .front-layout.home-layout .front-header, .front-layout.home-layout .header-spacer { height: 156px; }
    .personal-navigation { justify-content: flex-start; gap: 8px; }
    .personal-navigation a { font-size: 12px; padding: 0 10px; }
}
</style>

<style scoped lang="scss">
.home-design-layout { --content-width: min(90%, 1440px); --el-color-primary: #6483df; background: #f8fbff; }
.home-design-layout .front-header { height: 72px; background: #f8fbff9c; border: 0; backdrop-filter: blur(10px); }
.home-design-layout .header-spacer { height: 72px; }.home-design-layout .header-container { height: 72px; }.home-design-layout .main-container { padding-top: 0 !important; overflow: visible; }
.home-design-layout .brand-leaf { width: 38px; height: 38px; }.home-design-layout .chat-brand-name { font-size: 28px; }.home-design-layout .chat-brand-subtitle { font-size: 12px; color: #8290b3; }
.home-design-layout .menu-section { gap: 16px; }.home-design-layout .menu-item { height: 72px; line-height: 72px; font-size: 14px; padding: 0 14px; }.home-design-layout .menu-item::before { bottom: 12px; left: 14px; width: calc(100% - 28px); height: 2px; border-radius: 0; }
.home-design-layout .right-section { gap: 7px; }.home-auth-link { color: #4c608e; font-size: 12px; cursor: pointer; }.home-auth-link:hover { color: #3b62c6; }.home-auth-divider { font-size: 12px; color: #8b9bbd; }
.front-layout.home-design-layout .front-header, .front-layout.home-design-layout .header-spacer { height: 116px; }
@media (max-width: 1200px) { .home-design-layout .menu-section { gap: 4px; left: 57%; }.home-design-layout .menu-item { font-size: 12px; padding: 0 12px; }.home-design-layout .chat-brand-subtitle { font-size: 10px; max-width: none; white-space: nowrap; }.home-design-layout .chat-brand-name { font-size: 25px; }.home-design-layout .left-section { gap: 8px; } }
@media (max-width: 1000px) { .home-design-layout .chat-brand-subtitle { display: none; }.home-design-layout .menu-section { left: 53%; } }
@media (max-width: 760px) { .home-design-layout { --content-width: calc(100% - 32px); }.home-design-layout .front-header, .home-design-layout .header-spacer, .home-design-layout .header-container { height: 104px; }.home-design-layout .header-container { row-gap: 4px; }.home-design-layout .menu-section { position: static; transform: none; }.home-design-layout .menu-item { height: 36px; line-height: 36px; font-size: 12px; padding: 0 10px; }.home-design-layout .menu-item::before { bottom: 0; left: 10px; width: calc(100% - 20px); }.home-design-layout .brand-leaf { width: 31px; height: 31px; }.home-design-layout .chat-brand-name { font-size: 24px; }.home-design-layout .main-container { padding-top: 0 !important; } }
</style>

<style scoped lang="scss">
@media (max-width: 760px) {
    .front-layout.home-design-layout .front-header, .front-layout.home-design-layout .header-spacer { height: 148px; }
}
</style>

<style scoped lang="scss">
.article-design-layout { --content-width: min(92.8%, 1440px); --el-color-primary: #6678cf; background: #fff; overflow-x: clip; }
.article-design-layout .front-header { background: #ffffffed; border-bottom: 0; backdrop-filter: blur(14px); }
.front-layout.article-design-layout .front-header, .front-layout.article-design-layout .header-spacer { height: 108px; }
.article-design-layout .header-container { height: 64px; }
.article-design-layout .brand-leaf { width: 43px; height: 43px; }.article-design-layout .chat-brand-name { font-size: 29px; }.article-design-layout .chat-brand-subtitle { font-size: 13px; color: #9099b3; }
.article-design-layout .menu-section { gap: 18px; left: 52%; }.article-design-layout .menu-item { font-size: 15px; height: 64px; line-height: 64px; padding: 0 12px; }.article-design-layout .menu-item::before { height: 2px; left: 5px; width: calc(100% - 10px); bottom: 5px; border-radius: 0; background: #5a68b9; }
.article-design-layout .right-section { gap: 16px; }.article-design-layout .right-section > .el-button { font-size: 14px; height: 37px; padding: 9px 25px; border-radius: 9px; margin-left: 0; }.article-design-layout .right-section > .el-button:first-child { background: #6977ca; color: white; border: 1px solid #6977ca; }.article-design-layout .right-section > .el-button:last-child { border-color: #8b96d9; color: #6373bb; }
.article-design-layout .main-container { padding-top: 0 !important; overflow: visible; min-height: 0; }
.article-design-layout .personal-navigation { border-color: #eef1f8; }
@media (max-width: 1200px) { .article-design-layout .chat-brand-subtitle { font-size: 11px; max-width: 165px; }.article-design-layout .menu-section { left: 54%; gap: 12px; }.article-design-layout .menu-item { font-size: 13px; padding: 0 10px; }.article-design-layout .right-section { gap: 10px; }.article-design-layout .right-section > .el-button { padding: 9px 20px; } }
@media (max-width: 1000px) { .article-design-layout .menu-section { left: 50%; gap: 8px; }.article-design-layout .chat-brand-subtitle { display: none; }.article-design-layout .personal-navigation { gap: 18px; } }
@media (max-width: 760px) { .article-design-layout { --content-width: calc(100% - 32px); }.front-layout.article-design-layout .front-header, .front-layout.article-design-layout .header-spacer { height: 156px; }.article-design-layout .header-container { height: 112px; }.article-design-layout .menu-section { position: static; transform: none; }.article-design-layout .menu-item { height: 40px; line-height: 40px; font-size: 12px; padding: 0 12px; }.article-design-layout .menu-item::before { bottom: 0; }.article-design-layout .brand-leaf { width: 35px; height: 35px; }.article-design-layout .chat-brand-name { font-size: 25px; }.article-design-layout .right-section { gap: 8px; }.article-design-layout .right-section > .el-button { font-size: 12px; padding: 8px 17px; height: 33px; }.article-design-layout .personal-navigation { gap: 8px; }.article-design-layout .main-container { padding-top: 0 !important; } }
</style>

<style lang="scss" src="@/styles/front-refresh.scss"></style>

<style scoped lang="scss">
.front-layout.scale-design-layout { --content-width: min(90%, 1440px); background: #fbfcff; }
.front-layout.scale-design-layout .main-container { padding-top: 0 !important; overflow: visible; }
.scale-design-layout :deep(.home-footer) { border: 0; background: linear-gradient(#ffffffc9, #ffffffc0), url('@/assets/article-botanical-landscape.png') center 75% / cover; padding: 21px 0 25px; }
@media (max-width: 760px) { .front-layout.scale-design-layout { --content-width: calc(100% - 32px); }.front-layout.scale-design-layout .main-container { padding-top: 0 !important; } }
</style>

<style scoped lang="scss">
.front-layout.article-reading-layout { --content-width: min(88%, 1400px); background: linear-gradient(#f7fbff45, #f5faffdd 850px), url('@/assets/article-detail-landscape.png') center top / 100% auto fixed no-repeat; }
.front-layout.article-reading-layout .main-container { padding-top: 22px !important; min-height: calc(100vh - 280px); }
.front-layout.article-reading-layout .front-header { background: linear-gradient(#f8fbffb5, #f8fbff60), url('@/assets/article-detail-landscape.png') center 20% / cover; border-bottom: 1px solid #e5edfc; }
.article-reading-layout .personal-navigation { background: #f0f5ffc9; width: 100%; padding: 0 6%; gap: 28px; border-color: #e7eefa; }
.article-reading-layout .personal-navigation svg { width: 17px; height: 17px; margin-right: 10px; }
.article-reading-layout :deep(.home-footer) { border: 0; background: linear-gradient(#f7fbff80, #f7fbffb0), url('@/assets/article-botanical-landscape.png') center 65% / cover; }
@media (max-width: 1000px) { .front-layout.article-reading-layout { --content-width: calc(100% - 40px); }.article-reading-layout .personal-navigation { gap: 12px; } }
@media (max-width: 760px) { .front-layout.article-reading-layout { --content-width: calc(100% - 24px); background-size: auto 700px; }.front-layout.article-reading-layout .main-container { padding-top: 16px !important; }.article-reading-layout .personal-navigation { padding: 0 12px; gap: 8px; } }
</style>

<style scoped lang="scss" src="@/styles/front-navigation.scss"></style>

<style scoped lang="scss">
.front-layout.home-layout.unified-front.test-answer-layout { --content-width: min(86.5%, 1380px); background: linear-gradient(#f4f9ff15, #f5faff55), url('@/assets/article-detail-landscape.png') center center / cover fixed; overflow-x: clip; }
.front-layout.test-answer-layout .main-container { padding-top: 0 !important; min-height: 0; overflow: visible; }
.test-answer-layout .personal-navigation svg { width: 19px; height: 19px; margin-right: 10px; }
.test-answer-layout :deep(.home-footer) { padding-top: 20px; background: #ffffffed; border: 0; }
@media (max-width: 1000px) { .front-layout.home-layout.unified-front.test-answer-layout { --content-width: calc(100% - 40px); } }
@media (max-width: 760px) { .front-layout.home-layout.unified-front.test-answer-layout { --content-width: calc(100% - 24px); }.front-layout.test-answer-layout .main-container { padding-top: 0 !important; }.test-answer-layout .personal-navigation svg { width: 16px; height: 16px; margin-right: 7px; } }
</style>

<style scoped lang="scss">
.front-layout.home-layout.unified-front.assessment-record-layout { --content-width: min(96%, 1440px); background: linear-gradient(#fff, #fbfcff); overflow-x: clip; }
.front-layout.assessment-record-layout .main-container { padding-top: 0 !important; overflow: visible; min-height: 0; }
.assessment-record-layout .personal-navigation svg { width: 17px; height: 17px; margin-right: 9px; }
.assessment-record-layout :deep(.home-footer) { background: #fff; border: 0; padding-top: 20px; }
@media (max-width: 760px) { .front-layout.home-layout.unified-front.assessment-record-layout { --content-width: calc(100% - 24px); }.front-layout.assessment-record-layout .main-container { padding-top: 0 !important; }.assessment-record-layout .personal-navigation svg { width: 16px; height: 16px; margin-right: 6px; } }
</style>
