<template>
    <AuthLayout title="找回密码" subtitle="填写注册信息，重新连接属于你的成长空间" eyebrow="别着急，我们陪你找回连接" compact>
                    <el-form ref="forgetForm" :model="formData" label-width="70px" label-position="top" :rules="rules" class="forget-form" @submit.prevent="handleSubmit">
                        <el-form-item label="账号" prop="userName">
                            <el-input v-model="formData.userName" placeholder="请输入账号" :prefix-icon="User" size="large" />
                        </el-form-item>
                        <el-row :gutter="16">
                            <el-col :span="12">
                                <el-form-item label="邮箱" prop="email">
                                    <el-input v-model="formData.email" placeholder="请输入邮箱" :prefix-icon="Message" size="large" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="联系方式" prop="phoneNumber">
                                    <el-input v-model="formData.phoneNumber" placeholder="请输入手机号" :prefix-icon="Phone" size="large" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="新密码" prop="passWord">
                            <el-input v-model="formData.passWord" type="password" show-password placeholder="请输入新密码" :prefix-icon="Lock" size="large" />
                        </el-form-item>
                        <el-form-item label="验证码" prop="code">
                            <div class="code-container">
                                <el-input v-model="formData.code" placeholder="请输入验证码" size="large" />
                                <div class="code-image"><ValidCode ref="validCodeRef" :content-width="128" :content-height="46" /></div>
                                <button type="button" class="captcha-refresh" aria-label="刷新验证码" @click="validCodeRef?.refreshCode()"><Refresh /></button>
                            </div>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" class="auth-button" size="large" native-type="submit">重设密码 <span aria-hidden="true">→</span></el-button>
                        </el-form-item>
                    </el-form>

                    <div class="auth-options">
                        <div class="login-link">
                            <span>想起密码了？</span>
                            <router-link :to="{ path: '/login' }"><span class="link-text">去登录</span></router-link>
                        </div>
                        <div class="register-link">
                            <router-link :to="{ path: '/register' }"><span class="link-text">注册账号</span></router-link>
                        </div>
                    </div>
    </AuthLayout>
</template>

<script setup>
import AuthLayout from '@/components/AuthLayout.vue'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { post } from '@/api/http';
import ValidCode from '@/components/Identifyingcode/ImageCode.vue';
import { ElMessage } from 'element-plus';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const forgetForm = ref(null);
const validCodeRef = ref(null);

const formData = reactive({ userName: '', passWord: '', email: '', phoneNumber: '', code: '' });

const rules = {
    userName: [{ required: true, message: '请输入账号', trigger: 'blur' }, { min: 3, max: 20, message: '账号长度应在3到20个字符之间', trigger: 'blur' }],
    passWord: [{ required: true, message: '请输入密码', trigger: 'blur' }, { validator: (r, v, cb) => { if (!v || !/^.{8,16}$/.test(v)) cb(new Error('请输入8-16位密码')); else cb(); }, trigger: 'blur' }],
    email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { validator: (r, v, cb) => { if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(v)) cb(new Error('请输入正确邮箱')); else cb(); }, trigger: 'blur' }],
    phoneNumber: [{ required: true, message: '请输入联系方式', trigger: 'blur' }, { validator: (r, v, cb) => { if (!/^1[0-9]\d{9}$/.test(v)) cb(new Error('请输入正确的手机号')); else cb(); }, trigger: 'blur' }],
    code: [{ required: true, message: '请输入验证码', trigger: 'blur' }, { validator: (r, v, cb) => { if (v !== validCodeRef.value.getCode()) cb(new Error('请输入正确的验证码')); else cb(); }, trigger: 'blur' }]
};

const handleSubmit = async () => {
    if (!forgetForm.value) return;
    await forgetForm.value.validate(async (valid) => {
        if (valid) {
            try {
                const res = await post('/sysUser/forgetPassword', formData);
                if (res.success) { ElMessage.success('修改密码成功'); router.push('/login'); }
                else { validCodeRef.value.refreshCode(); }
            } catch { validCodeRef.value.refreshCode(); }
        } else { ElMessage.error('验证不通过'); validCodeRef.value.refreshCode(); }
    });
};
</script>
