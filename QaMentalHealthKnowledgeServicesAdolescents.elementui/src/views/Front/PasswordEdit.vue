<template>
    <div>

        <el-card>
            <template #header>
                <div class="clearfix">
                    <h2>修改密码</h2>
                </div>
            </template>
            <div>
                <el-form ref="editModalForm" :model="formData" label-width="80px" size="default" :rules="rules">
                    <el-form-item label="原始密码" prop="orginPassword">
                        <el-input type="password" v-model.trim="formData.orginPassword" placeholder="请输入原始密码"
                            show-password></el-input>
                    </el-form-item>

                    <el-form-item label="新密码" prop="newPassword">
                        <el-input type="password" v-model.trim="formData.newPassword" placeholder="请输入新密码"
                            show-password></el-input>
                    </el-form-item>

                    <el-form-item label="确认密码" prop="passWord">
                        <el-input type="password" v-model.trim="formData.passWord" placeholder="请再次输入新密码"
                            show-password></el-input>
                    </el-form-item>
                </el-form>
                <div class="button-container">
                    <el-button type="primary" size="default" @click="addOrEdit">保存新密码</el-button>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from "@/store"
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

// 获取store和router实例
const commonStore = useCommonStore()
const router = useRouter()

// 表单数据
const formData = reactive({})
formData.id = commonStore.userId

// 表单校验规则
const rules = {
    orginPassword: [
        { required: true, message: '该项为必填项', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {

                var reg = /^.{8,16}$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入8-16位密码'));
                } else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ],
    passWord: [
        { required: true, message: '该项为必填项', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {

                var reg = /^.{8,16}$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入8-16位密码'));
                } else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ]
}

// 表单引用
const editModalForm = ref(null)

// 创建或修改方法
const addOrEdit = async () => {
    if (!editModalForm.value) return

    await editModalForm.value.validate(async (valid) => {
        if (valid) {
            // 判断新密码和确认密码是否一致
            if (formData.newPassword !== formData.passWord) {
                ElMessage.error("新密码和确认密码不一致")
                return false
            }
            const { success } = await post("/sysUser/changePassword", formData)
            if (success) {
                ElMessage.success("修改成功!")
                await commonStore.logout()
                router.push("/login")
            }
        } else {

            return false
        }
    })
}
</script>

<style scoped lang="scss">
/* 卡片样式 */
.margin-top-sm {
    margin-top: 20px;
}

/* 清除浮动 */
.clearfix::after {
    content: '';
    display: table;
    clear: both;
}

/* 按钮容器样式 */
.button-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}
</style>
