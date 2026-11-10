<template>
    <div class="admin-layout">
        <!-- 顶部导航栏 -->
        <el-header class="header">
            <div class="header-left">
                <div class="logo" @click="router.push('/admin')">
                    <div class="logo-icon"><svg viewBox="0 0 60 50" width="40" height="38" fill="none" aria-hidden="true"><path d="M29 46C22 26 38 10 58 4c0 21-13 33-29 42Z" fill="#9a9cdd"/><path d="M27 46C9 44 4 31 6 18c15 2 23 12 21 28Z" fill="#6483df"/><path d="M15 17C4 18 0 11 0 3c10 0 16 5 15 14Z" fill="#90a4e5"/><path d="M29 45l13-23M27 44L15 27" stroke="white" stroke-opacity=".6"/></svg></div>
                    <div class="logo-content">
                        <h1 class="system-title">心屿 <span>管理控制台</span></h1>
                        <p class="system-subtitle">青少年心理健康知识服务</p>
                    </div>
                </div>
            </div>
            <div class="header-right">
                <el-dropdown>
                    <div class="user-info">
                        <el-avatar :size="34" :src="userInfo.imageUrls || defaultAvatar"
                            style="background-color: #EBF3FB;" />
                        <div class="user-details">
                            <span class="username">{{ userInfo.userName }}</span>
                            <span class="role">{{ roleType }}</span>
                        </div>
                        <svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" width="14" height="14">
                            <path d="M4 6l4 4 4-4" stroke="#9CA3AF" stroke-width="1.5" stroke-linecap="round"
                                stroke-linejoin="round" />
                        </svg>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="handleUserInfo">
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="16"
                                    height="16" style="margin-right: 8px;">
                                    <path d="M10 10a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z" stroke="#6B7280"
                                        stroke-width="1.5" />
                                    <path d="M3 17.5c0-3.5 3-5.5 7-5.5s7 2 7 5.5" stroke="#6B7280" stroke-width="1.5"
                                        stroke-linecap="round" />
                                </svg>
                                个人信息
                            </el-dropdown-item>
                            <el-dropdown-item @click="handlePasswordEdit">
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="16"
                                    height="16" style="margin-right: 8px;">
                                    <rect x="3" y="9" width="14" height="9" rx="2" stroke="#6B7280"
                                        stroke-width="1.5" />
                                    <path d="M7 9V6a3 3 0 0 1 6 0v3" stroke="#6B7280" stroke-width="1.5"
                                        stroke-linecap="round" />
                                </svg>
                                修改密码
                            </el-dropdown-item>
                            <el-dropdown-item @click="handleLogout">
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="16"
                                    height="16" style="margin-right: 8px;">
                                    <path d="M7 17H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1h3" stroke="#6B7280"
                                        stroke-width="1.5" stroke-linecap="round" />
                                    <path d="M14 13l3-3-3-3" stroke="#6B7280" stroke-width="1.5"
                                        stroke-linecap="round" stroke-linejoin="round" />
                                    <path d="M17 10H8" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" />
                                </svg>
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <!-- 主体内容区 -->
        <el-container class="main-container">
            <!-- 侧边栏 -->
            <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
                <!-- 折叠按钮 -->
                <div class="collapse-btn" @click="isCollapse = !isCollapse">
                    <svg v-if="!isCollapse" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg"
                        width="18" height="18">
                        <path d="M15 4L9 10l6 6" stroke="#8C9BAB" stroke-width="1.5" stroke-linecap="round"
                            stroke-linejoin="round" />
                    </svg>
                    <svg v-else viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                        height="18">
                        <path d="M7 4l6 6-6 6" stroke="#8C9BAB" stroke-width="1.5" stroke-linecap="round"
                            stroke-linejoin="round" />
                    </svg>
                </div>

                <el-scrollbar>
                    <el-menu :default-active="route.path" :router="true" class="sidebar-menu" :collapse="isCollapse"
                        background-color="transparent" text-color="#61749b" active-text-color="#456ac7">

                        <!-- 系统管理 -->
                        <el-sub-menu index="system">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                                    height="18" class="menu-svg-icon">
                                    <path d="M10 13a3 3 0 1 0 0-6 3 3 0 0 0 0 6z" stroke="currentColor"
                                        stroke-width="1.5" />
                                    <path d="M17.4 12.3a1.2 1.2 0 0 0 .2-1.3l-.7-1.2a1.2 1.2 0 0 0-1.1-.6h-.3a7 7 0 0 0-.6-.4l-.1-.3a1.2 1.2 0 0 0-.4-1.2l-1-1a1.2 1.2 0 0 0-1.3-.2l-.3.1a7 7 0 0 0-.5-.3V5.6a1.2 1.2 0 0 0-.7-1.1L9.4 4a1.2 1.2 0 0 0-1.3.2l-.2.2a7 7 0 0 0-.5 0l-.2-.2a1.2 1.2 0 0 0-1.2-.4l-1.3.4a1.2 1.2 0 0 0-.7 1l0 .3a7 7 0 0 0-.4.4l-.3 0a1.2 1.2 0 0 0-1 .7L2 7.8a1.2 1.2 0 0 0 .2 1.3l.2.2a7 7 0 0 0 0 .5l-.2.2a1.2 1.2 0 0 0-.2 1.3l.5 1.2a1.2 1.2 0 0 0 1 .7l.3 0a7 7 0 0 0 .4.4l0 .3a1.2 1.2 0 0 0 .7 1l1.3.4a1.2 1.2 0 0 0 1.2-.4l.2-.2a7 7 0 0 0 .5 0l.2.2a1.2 1.2 0 0 0 1.3.2l1.2-.5a1.2 1.2 0 0 0 .7-1l0-.3a7 7 0 0 0 .5-.3l.3.1a1.2 1.2 0 0 0 1.3-.2l1-1a1.2 1.2 0 0 0 .4-1.2l-.1-.3a7 7 0 0 0 .3-.5h.3a1.2 1.2 0 0 0 1.1-.6l.5-1.2z"
                                        stroke="currentColor" stroke-width="1.2" />
                                </svg>
                                <span>系统管理</span>
                            </template>
                            <el-menu-item index="/admin/userList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <circle cx="9" cy="6" r="3" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M3 16c0-3 2.5-5 6-5s6 2 6 5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
                                </svg>
                                账号管理
                            </el-menu-item>
                        </el-sub-menu>

                        <!-- 内容管理分组 -->
                        <el-sub-menu index="content">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                                    height="18" class="menu-svg-icon">
                                    <path d="M3 5h14M3 10h14M3 15h8" stroke="currentColor" stroke-width="1.5"
                                        stroke-linecap="round" />
                                </svg>
                                <span>内容管理</span>
                            </template>
                            <el-menu-item index="/Admin/articleList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <rect x="2" y="2" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M5 6h8M5 9h8M5 12h5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" />
                                </svg>
                                科普文章
                            </el-menu-item>
                            <el-menu-item index="/Admin/articleCommentList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M15 11a1.5 1.5 0 0 1-1.5 1.5H6L3 15V4.5A1.5 1.5 0 0 1 4.5 3h9A1.5 1.5 0 0 1 15 4.5z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                文章评论
                            </el-menu-item>
                            <el-menu-item index="/Admin/articleTypeList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M2 5h5v5H2zM11 5h5v5h-5zM2 13h5v3H2zM11 13h5v3h-5z" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                文章分类
                            </el-menu-item>
                            <el-menu-item index="/Admin/bannerList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <rect x="2" y="4" width="14" height="10" rx="2" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M2 11l4-3 3 2 3-2 4 3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round" />
                                    <circle cx="6" cy="7.5" r="1.2" stroke="currentColor" stroke-width="1.1" />
                                </svg>
                                轮播图
                            </el-menu-item>
                            <el-menu-item index="/Admin/noticeList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M4 3h10a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H9l-3 2.5V14H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M6 7h6M6 10h4" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" />
                                </svg>
                                公告信息
                            </el-menu-item>
                            <el-menu-item index="/Admin/noticeTypeList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M3 4h4v4H3zM11 4h4v4h-4zM3 11h4v4H3zM11 11h4v4h-4z" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                公告分类
                            </el-menu-item>
                        </el-sub-menu>

                        <!-- 知识库管理分组 -->
                        <el-sub-menu index="knowledge">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                                    height="18" class="menu-svg-icon">
                                    <path d="M2 4h5.5a3 3 0 0 1 3 3v10.5a2 2 0 0 0-2-2H2z" stroke="currentColor"
                                        stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                                    <path d="M18 4h-5.5a3 3 0 0 0-3 3v10.5a2 2 0 0 1 2-2H18z" stroke="currentColor"
                                        stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                <span>知识库管理</span>
                            </template>
                            <el-menu-item index="/Admin/knowledgeDocumentList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M4 2h7l4 4v10a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round" />
                                    <path d="M11 2v4h4" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                知识文档
                            </el-menu-item>
                            <el-menu-item index="/Admin/knowledgeTypeList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M8 2H3a1 1 0 0 0-1 1v4.5a1 1 0 0 0 1 1h5a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1zM15 9.5H10a1 1 0 0 0-1 1V15a1 1 0 0 0 1 1h5a1 1 0 0 0 1-1v-4.5a1 1 0 0 0-1-1zM15 2H10a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h5a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1zM8 10H3a1 1 0 0 0-1 1v4a1 1 0 0 0 1 1h5a1 1 0 0 0 1-1v-4a1 1 0 0 0-1-1z" stroke="currentColor" stroke-width="1.3" />
                                </svg>
                                知识分类
                            </el-menu-item>
                            <el-menu-item index="/Admin/documentChunkList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <rect x="2" y="2" width="14" height="4" rx="1" stroke="currentColor" stroke-width="1.4" />
                                    <rect x="2" y="8" width="14" height="4" rx="1" stroke="currentColor" stroke-width="1.4" />
                                    <rect x="2" y="14" width="8" height="2.5" rx="1" stroke="currentColor" stroke-width="1.3" />
                                </svg>
                                文档分块
                            </el-menu-item>
                        </el-sub-menu>

                        <!-- 智能问答分组 -->
                        <el-sub-menu index="qa">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                                    height="18" class="menu-svg-icon">
                                    <path d="M17 13a2 2 0 0 1-2 2H7l-4 3.5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"
                                        stroke="currentColor" stroke-width="1.5" stroke-linecap="round"
                                        stroke-linejoin="round" />
                                    <circle cx="7.5" cy="9" r="0.8" fill="currentColor" />
                                    <circle cx="10.5" cy="9" r="0.8" fill="currentColor" />
                                    <circle cx="13.5" cy="9" r="0.8" fill="currentColor" />
                                </svg>
                                <span>智能问答</span>
                            </template>
                            <el-menu-item index="/Admin/chatSessionList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <rect x="2" y="3" width="14" height="10" rx="2" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M6 15h6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
                                    <path d="M9 13v2" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
                                </svg>
                                对话会话
                            </el-menu-item>
                            <el-menu-item index="/Admin/hotQuestionList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M9 2C6.5 4.5 5 7 5 9.5a4 4 0 0 0 8 0C13 7 11.5 4.5 9 2z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
                                    <path d="M9 10.5a1.5 1.5 0 0 0 1.5-1.5c0-1-1.5-2.5-1.5-2.5S7.5 8 7.5 9A1.5 1.5 0 0 0 9 10.5z" stroke="currentColor" stroke-width="1.2" />
                                </svg>
                                热门问题
                            </el-menu-item>
                            <el-menu-item index="/Admin/qaFeedbackList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M5 9l3 3 5-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                                    <circle cx="9" cy="9" r="7" stroke="currentColor" stroke-width="1.4" />
                                </svg>
                                问答反馈
                            </el-menu-item>
                        </el-sub-menu>

                        <!-- 心理测评分组 -->
                        <el-sub-menu index="assessment">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                                    height="18" class="menu-svg-icon">
                                    <rect x="3" y="2" width="14" height="16" rx="2" stroke="currentColor"
                                        stroke-width="1.5" />
                                    <path d="M7 7h6M7 10h6M7 13h4" stroke="currentColor" stroke-width="1.3"
                                        stroke-linecap="round" />
                                </svg>
                                <span>心理测评</span>
                            </template>
                            <el-menu-item index="/Admin/scaleInfoList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <rect x="2" y="2" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M5 9h8M9 5v8" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" />
                                </svg>
                                测评量表
                            </el-menu-item>
                            <el-menu-item index="/Admin/assessmentRecordList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M6 2v2M12 2v2" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
                                    <rect x="2" y="3" width="14" height="13" rx="2" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M2 7h14" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M6 10l2 2 4-4" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                测评记录
                            </el-menu-item>
                        </el-sub-menu>

                        <!-- 用户互动 -->
                        <el-sub-menu index="interaction">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18"
                                    height="18" class="menu-svg-icon">
                                    <path d="M12 14.5c3 0 5-1.5 5-4s-2-4-5-4-5 1.5-5 4c0 1 .4 2 1.2 2.7L7 15l2.5-.8c.8.2 1.6.3 2.5.3z"
                                        stroke="currentColor" stroke-width="1.5" stroke-linecap="round"
                                        stroke-linejoin="round" />
                                    <path d="M8 6.5c-3 0-5 1.5-5 4 0 1 .4 2 1.2 2.7L3 15l2.5-.8" stroke="currentColor"
                                        stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                <span>用户互动</span>
                            </template>
                            <el-menu-item index="/Admin/favoriteList">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M9 15 C9 15 2 10.5 2 6.5 C2 4 4 2.5 6 2.5 C7.5 2.5 8.5 3.5 9 4.5 C9.5 3.5 10.5 2.5 12 2.5 C14 2.5 16 4 16 6.5 C16 10.5 9 15 9 15Z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                收藏管理
                            </el-menu-item>
                        </el-sub-menu>

                        <!-- 数据统计 -->
                        <el-sub-menu index="statistics">
                            <template #title>
                                <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg" width="18" height="18" class="menu-svg-icon">
                                    <path d="M3 17V7l4-4 4 3 4-4 2 2v13H3z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                                    <path d="M3 17h14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
                                </svg>
                                <span>数据统计</span>
                            </template>
                            <el-menu-item index="/admin/userStatistics">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <circle cx="9" cy="6" r="3" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M3 16c0-3 2.5-5 6-5s6 2 6 5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
                                </svg>
                                用户统计
                            </el-menu-item>
                            <el-menu-item index="/admin/qaStatistics">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M14 11a1.5 1.5 0 0 1-1.5 1.5h-6L4 15V5.5A1.5 1.5 0 0 1 5.5 4h7A1.5 1.5 0 0 1 14 5.5z" stroke="currentColor" stroke-width="1.4" />
                                </svg>
                                问答统计
                            </el-menu-item>
                            <el-menu-item index="/admin/knowledgeStatistics">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <path d="M2 4h5.5a2 2 0 0 1 2 2v9a1.5 1.5 0 0 0-1.5-1.5H2z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
                                    <path d="M16 4h-5.5a2 2 0 0 0-2 2v9a1.5 1.5 0 0 1 1.5-1.5H16z" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                                知识库统计
                            </el-menu-item>
                            <el-menu-item index="/admin/assessmentStatistics">
                                <svg viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" width="15" height="15" class="sub-menu-icon">
                                    <rect x="2" y="2" width="14" height="14" rx="2" stroke="currentColor" stroke-width="1.4" />
                                    <path d="M5 9h8M9 5v8" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" />
                                </svg>
                                测评统计
                            </el-menu-item>
                        </el-sub-menu>

                    </el-menu>
                </el-scrollbar>
            </el-aside>

            <!-- 主要内容区 -->
            <el-main class="main-content">
                <!-- 面包屑导航 -->
                <div class="breadcrumb-bar">
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index" :to="item.path">
                            {{ item.title }}
                        </el-breadcrumb-item>
                    </el-breadcrumb>
                    <span class="back-link" @click="goBack">
                        <svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" width="14"
                            height="14">
                            <path d="M10 12L6 8l4-4" stroke="#5B9BD5" stroke-width="1.5" stroke-linecap="round"
                                stroke-linejoin="round" />
                        </svg>
                        返回
                    </span>
                </div>
                <section class="admin-page-intro" aria-labelledby="admin-page-title">
                    <div><p class="admin-eyebrow">心屿 · 管理工作台</p><h2 id="admin-page-title">{{ route.meta.title || '管理控制台' }}</h2><p>让知识有温度，让每一份成长被认真看见。</p></div>
                    <img :src="adminLandscape" alt="" />
                </section>
                <router-view></router-view>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>
import defaultAvatar from '@/assets/默认头像.png';
import adminLandscape from '@/assets/article-botanical-landscape.png';
import { useCommonStore } from '@/store';
import { computed, onMounted, onBeforeUnmount, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const commonStore = useCommonStore();
const router = useRouter();
const route = useRoute();
onMounted(() => document.body.classList.add('admin-theme'));
onBeforeUnmount(() => document.body.classList.remove('admin-theme'));

const token = computed(() => commonStore.token)
const userInfo = computed(() => commonStore.userInfo)
const roleType = computed(() => commonStore.roleType)
const userId = computed(() => commonStore.userId)

const isCollapse = ref(false)
const breadcrumbList = ref([])

watch(
    () => route.matched,
    (matched) => {
        if (!isHome(matched[0])) {
            breadcrumbList.value = [
                { title: '控制台', path: '/admin' },
                ...matched.map(item => ({
                    title: item.meta.title || item.name,
                    path: item.path
                }))
            ]
        } else {
            breadcrumbList.value = matched.map(item => ({
                title: item.meta.title || item.name,
                path: item.path
            }))
        }
    },
    { immediate: true }
)

function isHome(route) {
    return route.path === "/admin";
}
function handleLogout() {
    commonStore.logout();
    router.push("/login");
}
function goBack() {
    router.back();
}
function handleUserInfo() {
    router.push("/admin/userPerson");
}
function handlePasswordEdit() {
    router.push("/admin/passwordEdit");
}
</script>

<style scoped lang="scss">
/* ===== 整体布局 ===== */
.admin-layout {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background-color: #F1F5F9;
}

/* ===== 顶部导航栏 ===== */
.header {
    background-color: #ffffff;
    border-bottom: 1px solid #E2E8F0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    height: 60px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
    z-index: 10;
}

.header-left {
    display: flex;
    align-items: center;
}

.logo {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    padding: 4px 0;
}

.logo-icon {
    flex-shrink: 0;
}

.logo-content {
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.system-title {
    font-size: 16px;
    font-weight: 700;
    color: #1E293B;
    margin: 0;
    line-height: 1.3;
}

.system-subtitle {
    font-size: 11px;
    color: #94A3B8;
    margin: 0;
    line-height: 1.3;
}

/* 右侧用户信息 */
.header-right {
    display: flex;
    align-items: center;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 6px 10px;
    border-radius: 8px;
    transition: background-color 0.2s;
}

.user-info:hover {
    background-color: #F8FAFC;
}

.user-details {
    display: flex;
    flex-direction: column;
    line-height: 1.2;
}

.username {
    font-size: 13px;
    color: #1E293B;
    font-weight: 500;
}

.role {
    font-size: 11px;
    color: #94A3B8;
}

:deep(.el-dropdown-menu__item) {
    display: flex;
    align-items: center;
    font-size: 13px;
    padding: 8px 16px;
}

/* ===== 主体容器 ===== */
.main-container {
    flex: 1;
    overflow: hidden;
}

/* ===== 侧边栏 ===== */
.sidebar {
    background-color: #1E293B;
    height: 100%;
    transition: width 0.3s;
    position: relative;
    overflow: visible;
}

.collapse-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    cursor: pointer;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    transition: background-color 0.2s;
}

.collapse-btn:hover {
    background-color: rgba(255, 255, 255, 0.05);
}

/* 菜单样式 */
.sidebar-menu {
    border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
    width: 220px;
}

/* SVG 图标在菜单中的样式 */
.menu-svg-icon {
    margin-right: 8px;
    flex-shrink: 0;
}

:deep(.el-sub-menu__title) {
    font-size: 13px;
    height: 46px;
    line-height: 46px;
    display: flex;
    align-items: center;
}

:deep(.el-menu-item) {
    font-size: 13px;
    height: 42px;
    line-height: 42px;
}

:deep(.el-sub-menu .el-menu-item) {
    padding-left: 52px !important;
    min-width: auto;
}

/* 激活菜单项样式 */
:deep(.el-menu-item.is-active) {
    background-color: #5B9BD5 !important;
    color: #ffffff !important;
    border-radius: 0;
}

:deep(.el-sub-menu__title:hover) {
    background-color: rgba(255, 255, 255, 0.05) !important;
}

:deep(.el-menu-item:hover) {
    background-color: rgba(255, 255, 255, 0.05) !important;
}

/* ===== 主内容区 ===== */
.main-content {
    background-color: #F1F5F9;
    padding: 20px;
    height: calc(100vh - 60px);
    overflow-y: auto;
    box-sizing: border-box;
}

/* 面包屑 */
.breadcrumb-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    padding: 12px 16px;
    background-color: #ffffff;
    border-radius: 10px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
}

.back-link {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    color: #5B9BD5;
    font-size: 13px;
    transition: color 0.2s;
}

.back-link:hover {
    color: #3A7CC0;
}
</style>

<style lang="scss" src="@/styles/admin-theme.scss"></style>
