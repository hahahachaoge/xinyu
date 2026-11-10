<template>
    <AuthLayout title="找回密码" subtitle="请联系管理员核验身份后重置密码" eyebrow="别着急，我们陪你找回连接" compact>
        <el-alert title="当前暂不支持在线找回密码" type="info" :closable="false"
            description="请联系学校老师或网站管理员，核验身份后协助重置。已登录用户可以在个人中心修改密码。" show-icon />
        <div class="auth-options">
            <router-link to="/login">返回登录</router-link>
            <router-link to="/register">注册账号</router-link>
        </div>
    </AuthLayout>
</template>

<script setup>
import AuthLayout from '@/components/AuthLayout.vue';
</script>
