<template>
    <AuthLayout title="欢迎回来" subtitle="登录账号，开启今天的成长旅程" eyebrow="很高兴，在这里遇见你">
                    <el-form ref="loginForm" :model="formData" label-width="70px" label-position="top" :rules="rules"
                        class="login-form" @submit.prevent="loginBtn">
                        <el-form-item label="账号" prop="userName">
                            <el-input v-model="formData.userName" placeholder="请输入账号" :prefix-icon="User" size="large" />
                        </el-form-item>

                        <el-form-item label="密码" prop="passWord">
                            <el-input v-model="formData.passWord" type="password" show-password placeholder="请输入密码"
                                :prefix-icon="Lock" size="large" />
                        </el-form-item>

                        <el-form-item label="角色" prop="roleType">
                            <el-select v-model="formData.roleType" placeholder="请选择角色" style="width: 100%" size="large">
                                <el-option v-for="item in roleOptions" :key="item.code" :label="item.label"
                                    :value="item.code" />
                            </el-select>
                        </el-form-item>

                        <el-form-item label="验证码" prop="code">
                            <div class="code-container">
                                <el-input v-model="formData.code" placeholder="请输入验证码" size="large" />
                                <div class="code-image">
                                    <ImageCode ref="validCodeRef" :content-width="128" :content-height="46" />
                                </div>
                                <button type="button" class="captcha-refresh" aria-label="刷新验证码" @click="validCodeRef?.refreshCode()"><Refresh /></button>
                            </div>
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" class="auth-button" size="large" native-type="submit">
                                登录，开启探索 <span aria-hidden="true">→</span>
                            </el-button>
                        </el-form-item>
                    </el-form>

                    <div class="auth-options">
                        <div class="register-link">
                            <span>还没有账号？</span>
                            <router-link :to="{ path: '/register' }">
                                <span class="link-text">立即注册</span>
                            </router-link>
                        </div>
                        <div class="forget-link">
                            <router-link :to="{ path: '/forgetPassword' }">
                                <span class="link-text">忘记密码</span>
                            </router-link>
                        </div>
                    </div>
    </AuthLayout>
</template>

<script setup>
import AuthLayout from '@/components/AuthLayout.vue'
import { User, Lock } from '@element-plus/icons-vue'
import { post } from '@/api/http';
import ImageCode from '@/components/Identifyingcode/ImageCode.vue'
import { useCommonStore } from '@/store'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const commonStore = useCommonStore()
const loginForm = ref(null)
const validCodeRef = ref(null)

const formData = reactive({
    userName: '',
    passWord: '',
    roleType: '',
    code: ''
})

const roleOptions = ref([])

const rules = reactive({
    userName: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 3, max: 20, message: '账号长度应在3到20个字符之间', trigger: 'blur' }
    ],
    passWord: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    roleType: [
        { required: true, message: '请选择角色', trigger: 'blur' },
    ],
    code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                const identifyCode = validCodeRef.value.getCode()
                if (value !== identifyCode) {
                    callback(new Error('请输入正确的验证码'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ]
})

onMounted(() => {
    getRoleTypeApi()
})

const getRoleTypeApi = async () => {
    try {
        const { data: { items } } = await post('/select/roleType')
        roleOptions.value = items
    } catch (error) {
        console.error('获取角色类型失败', error)
    }
}

const loginBtn = () => {
    loginForm.value.validate(async (valid) => {
        if (valid) {
            try {
                const { success } = await commonStore.login(formData)
                if (success) {
                    ElMessage.success('登录成功')
                    router.push({ path: '/admin' })
                }
            } catch (error) {
                console.error('登录失败', error)
            }
        } else {
            ElMessage.error('登录验证不通过')
            return false
        }
    })
}
</script>
