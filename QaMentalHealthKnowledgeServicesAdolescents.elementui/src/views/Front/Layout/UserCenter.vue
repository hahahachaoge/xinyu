<template>
    <div class="user-center">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
            <span class="breadcrumb-label">您当前的位置:</span>
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>个人中心</el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <!-- 用户信息卡片 -->
        <div class="user-card">
            <div class="user-banner" :style="{ backgroundImage: `url(${userInfo.backgroundImage || conversationIllustration})` }">
                <div class="user-avatar">
                    <img :src="userInfo.imageUrls || defaultAvatar" :alt="userInfo.name">
                </div>
            </div>
            <div class="user-info">
                <div class="user-basic">
                    <h2>{{ userInfo.nickName }}</h2>
                    <span class="role-tag">{{ userInfo.roleTypeFormat }}</span>
                </div>
                <p class="user-intro">{{ userInfo.introduction }}</p>
                <div class="user-details">
                    <div class="detail-item">
                        <svg class="detail-svg" viewBox="0 0 20 20" fill="currentColor"><path d="M10 10a4 4 0 100-8 4 4 0 000 8zm-6 8a6 6 0 1112 0H4z"/></svg>
                        <span>{{ userInfo.userName }}</span>
                    </div>
                    <div class="detail-item">
                        <svg class="detail-svg" viewBox="0 0 20 20" fill="currentColor"><path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/></svg>
                        <span>{{ maskPhone(userInfo.phoneNumber) }}</span>
                    </div>
                    <div class="detail-item">
                        <svg class="detail-svg" viewBox="0 0 20 20" fill="currentColor"><path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/><path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/></svg>
                        <span>{{ maskEmail(userInfo.email) }}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 功能菜单和内容区域 -->
        <div class="user-content">
            <div class="function-menu">
                <div class="menu-scroll">
                    <div class="menu-item" v-for="(item, index) in menuItems" :key="index"
                        :class="{ active: currentMenu === item.key }" @click="switchMenu(item.key)">
                        <span class="menu-icon" v-html="item.svg"></span>
                        <span>{{ item.name }}</span>
                    </div>
                </div>
            </div>

            <!-- 内容显示区域 -->
            <div class="content-area">
                <Suspense>
                    <template #default>
                        <component :is="currentComponent"></component>
                    </template>
                    <template #fallback>
                        <div class="loading">加载中...</div>
                    </template>
                </Suspense>
            </div>
        </div>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import conversationIllustration from '@/assets/wellbeing-conversation.png';
import defaultAvatar from '@/assets/默认头像.png'; // 导入默认头像图片
import { useCommonStore } from '@/store';
import { maskEmail, maskPhone } from '@/utils/mask';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 获取store和router实例
const commonStore = useCommonStore()
const router = useRouter()
const route = useRoute()

// 从store获取用户信息
const userInfo = computed(() => commonStore.userInfo)

// 菜单相关数据
const currentMenu = ref('UserInfoEdit')
const menuItems = [
    { name: '个人资料', svg: '<svg viewBox="0 0 20 20" fill="currentColor" width="18" height="18"><path d="M10 10a4 4 0 100-8 4 4 0 000 8zm-6 8a6 6 0 1112 0H4z"/></svg>', key: 'UserInfoEdit' },
    { name: '修改密码', svg: '<svg viewBox="0 0 20 20" fill="currentColor" width="18" height="18"><path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0zm-3 5a1.5 1.5 0 100 3 1.5 1.5 0 000-3z" clip-rule="evenodd"/></svg>', key: 'PasswordEdit' },
    { name: '我的问答', svg: '<svg viewBox="0 0 20 20" fill="currentColor" width="18" height="18"><path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zm-4 0H9v2h2V9z" clip-rule="evenodd"/></svg>', key: 'MyChatRecords' },
    { name: '我的测评', svg: '<svg viewBox="0 0 20 20" fill="currentColor" width="18" height="18"><path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/><path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/></svg>', key: 'MyAssessments' },
    { name: '我的收藏', svg: '<svg viewBox="0 0 20 20" fill="currentColor" width="18" height="18"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>', key: 'MyFavorites' },
]

// 预加载组件
const UserInfoEdit = defineAsyncComponent(() => import("@/views/Front/UserInfoEdit.vue"))
const PasswordEdit = defineAsyncComponent(() => import("@/views/Front/PasswordEdit.vue"))
const MyChatRecords = defineAsyncComponent(() => import("@/views/Front/MyChatRecords.vue"))
const MyAssessments = defineAsyncComponent(() => import("@/views/Front/MyAssessments.vue"))
const MyFavorites = defineAsyncComponent(() => import("@/views/Front/MyFavorites.vue"))

// 动态组件计算属性
const currentComponent = computed(() => {
    const componentMap = {
        UserInfoEdit,
        PasswordEdit,
        MyChatRecords,
        MyAssessments,
        MyFavorites,
    }
    return componentMap[currentMenu.value]
})

// 切换菜单方法
const switchMenu = (key) => {
    currentMenu.value = key
    router.replace({
        query: {
            currentMenu: key
        }
    })
}

// 组件挂载时设置当前菜单
onMounted(() => {
    currentMenu.value = route.query.currentMenu || 'UserInfoEdit'
})
</script>

<style scoped lang="scss">
/* 整体容器 */
.user-center {

    background-color: transparent;
    min-height: 70vh;
}


/* 用户信息卡片 */
.user-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

/* 用户背景横幅 */
.user-banner {
    height: 140px;
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center 42%;
    position: relative;
}

/* 用户头像 */
.user-avatar {
    position: absolute;
    bottom: -40px;
    left: 40px;

    img {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        border: 4px solid #fff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }
}

/* 用户信息区域 */
.user-info {
    padding: 50px 40px 30px;
}

/* 用户基本信息 */
.user-basic {
    display: flex;
    align-items: center;
    margin-bottom: 10px;

    h2 {
        margin: 0;
        margin-right: 10px;
    }
}



/* 角色标签 */
.role-tag {
    padding: 2px 10px;
    background: #397b68;
    color: #fff;
    border-radius: 12px;
    font-size: 12px;
}

/* 用户简介 */
.user-intro {
    color: #666;
    margin-bottom: 20px;
}

/* 用户详细信息 */
.user-details {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

.detail-item {
    display: flex;
    align-items: center;
    color: #666;

    .detail-svg {
        width: 16px;
        height: 16px;
        margin-right: 6px;
        flex-shrink: 0;
    }
}

/* 用户内容区域布局 */
.user-content {
    display: flex;
    gap: 20px;
    margin-top: 20px;
}

/* 功能菜单样式调整 */
.function-menu {
    flex: 0 0 250px;
    /* 固定宽度250px */
    background: #fff;
    border-radius: 8px;
    padding: 20px;
}

/* 菜单滚动区域改为垂直布局 */
.menu-scroll {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

/* 菜单项样式调整 */
.menu-item {
    display: flex;
    align-items: center;
    flex-direction: row;
    justify-content: flex-start;
    padding: 12px 20px;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;

    .menu-icon {
        margin-right: 10px;
        display: flex;
        align-items: center;
    }

    &:hover {
        background: #e8ede6;
    }

    &.active {
        background: var(--el-color-primary);
        color: #fff;
    }

    &.active:hover {
        background: var(--el-color-primary);
    }
}

/* 内容显示区域 */
.content-area {
    flex: 1;
    /* 占据剩余所有空间 */
    border-radius: 8px;
    min-height: 500px;

    overflow: hidden;

}

/* 加载状态样式 */
.loading {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
    color: #666;
    font-size: 16px;
}
</style>
