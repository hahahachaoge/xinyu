<template>
    <div class="not-found-container">
        <!-- 404页面内容 -->
        <div class="error-content">
            <h1 class="error-code">404</h1>
            <h2 class="error-title">页面不存在</h2>
            <p class="error-message">抱歉，您访问的页面不存在或已被移除。</p>
            <el-button type="primary" @click="goHome">返回首页</el-button>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NotFoundPage',
    methods: {
        goHome() {
            this.$router.push('/')
        }
    }
}
</script>

<style scoped lang="scss">
/* 404页面容器 */
.not-found-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    background-color: #f8f9fa;
}

/* 错误内容 */
.error-content {
    padding: 40px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 错误代码 */
.error-code {
    font-size: 3.75rem;
    font-weight: bold;
    color: #4b5563;
    margin-bottom: 1rem;
}

/* 错误标题 */
.error-title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #4b5563;
    margin-bottom: 1.5rem;
}

/* 错误消息 */
.error-message {
    color: #6b7280;
    margin-bottom: 2rem;
}
</style> 