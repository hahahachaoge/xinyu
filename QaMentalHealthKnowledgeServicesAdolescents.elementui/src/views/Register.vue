<template>
    <AuthLayout title="创建账号" subtitle="加入心屿，开启属于你的成长旅程" eyebrow="从认识自己开始，遇见更多可能" compact>
                    <el-form ref="registerForm" :model="formData" label-width="70px" label-position="top" :rules="rules" class="register-form" @submit.prevent="registerBtn">
                        <el-row :gutter="16">
                            <el-col :span="12">
                                <el-form-item label="账号" prop="userName">
                                    <el-input v-model="formData.userName" placeholder="请输入账号" :prefix-icon="User" size="large" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="密码" prop="passWord">
                                    <el-input v-model="formData.passWord" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" size="large" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row :gutter="16">
                            <el-col :span="12">
                                <el-form-item label="姓名" prop="nickName">
                                    <el-input v-model="formData.nickName" placeholder="请输入姓名" size="large" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="联系方式" prop="phoneNumber">
                                    <el-input v-model="formData.phoneNumber" placeholder="请输入手机号" :prefix-icon="Phone" size="large" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="formData.email" placeholder="请输入邮箱" :prefix-icon="Message" size="large" />
                        </el-form-item>
                        <el-form-item label="验证码" prop="code">
                            <div class="code-container">
                                <el-input v-model="formData.code" placeholder="请输入验证码" size="large" />
                                <div class="code-image"><ValidCode ref="validCodeRef" :content-width="128" :content-height="46" /></div>
                                <button type="button" class="captcha-refresh" aria-label="刷新验证码" @click="validCodeRef?.refreshCode()"><Refresh /></button>
                            </div>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" class="auth-button" size="large" native-type="submit">注册，开启成长 <span aria-hidden="true">→</span></el-button>
                        </el-form-item>
                    </el-form>

                    <div class="auth-options">
                        <div class="login-link">
                            <span>已有账号？</span>
                            <router-link :to="{ path: '/login' }"><span class="link-text">去登录</span></router-link>
                        </div>
                        <div class="forget-link">
                            <router-link :to="{ path: '/forgetPassword' }"><span class="link-text">忘记密码</span></router-link>
                        </div>
                    </div>
    </AuthLayout>
</template>

<script setup>
import AuthLayout from '@/components/AuthLayout.vue'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { post } from '@/api/http';
import { ElMessage } from 'element-plus';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import ValidCode from '../components/Identifyingcode/ImageCode.vue';

const router = useRouter();
const registerForm = ref(null);
const validCodeRef = ref(null);

const formData = reactive({ userName: '', passWord: '', phoneNumber: '', roleType: '2', email: '', nickName: '', code: '' });

const rules = reactive({
    userName: [{ required: true, message: '请输入账号', trigger: 'blur' }, { min: 3, max: 20, message: '账号长度应在3到20个字符之间', trigger: 'blur' }],
    passWord: [{ required: true, message: '请输入密码', trigger: 'blur' }, { validator: (r, v, cb) => { if (!v || !/^.{8,16}$/.test(v)) cb(new Error('请输入8-16位密码')); else cb(); }, trigger: 'blur' }],
    email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { validator: (r, v, cb) => { if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(v)) cb(new Error('邮箱格式不正确')); else cb(); }, trigger: 'blur' }],
    nickName: [{ required: true, message: '请输入姓名', trigger: 'blur' }, { min: 2, max: 20, message: '姓名长度应在2到20个字符之间', trigger: 'blur' }],
    phoneNumber: [{ required: true, message: '请输入联系方式', trigger: 'blur' }, { validator: (r, v, cb) => { if (!/^1[0-9]\d{9}$/.test(v)) cb(new Error('请输入正确的手机号')); else cb(); }, trigger: 'blur' }],
    code: [{ required: true, message: '请输入验证码', trigger: 'blur' }, { validator: (r, v, cb) => { if (v !== validCodeRef.value.getCode()) cb(new Error('请输入正确的验证码')); else cb(); }, trigger: 'blur' }]
});

const registerBtn = () => {
    registerForm.value.validate(async (valid) => {
        if (valid) {
            try {
                const { success } = await post('/sysUser/register', formData);
                if (success) { ElMessage.success('注册成功'); router.push('/login'); }
            } catch { validCodeRef.value.refreshCode(); }
        } else { ElMessage.error('注册验证不通过'); validCodeRef.value.refreshCode(); }
    });
};
</script>
