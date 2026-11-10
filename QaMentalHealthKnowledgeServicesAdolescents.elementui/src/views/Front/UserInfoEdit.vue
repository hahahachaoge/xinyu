<template>
    <div>
        <el-card>
            <template #header><h2>基本资料</h2><p class="form-description">完善你的信息，按自己的节奏记录成长。</p></template>
            <div class="margin-top-sm">
                <el-form ref="editModalForm" v-if="editShow" :model="formData" label-width="80px" size="default"
                    :rules="rules">
                    <el-form-item label="账号" prop="userName">
                        <el-input v-model="formData.userName" clearable :disabled="true"></el-input>
                    </el-form-item>

                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="formData.email" clearable> </el-input>
                    </el-form-item>
                    <el-form-item label="名称" prop="nickName">
                        <el-input v-model="formData.nickName" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="手机号码" prop="phoneNumber">
                        <el-input v-model="formData.phoneNumber" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="头像" prop="imageUrls">
                        <UploadImages v-model="formData.imageUrls"></UploadImages>
                    </el-form-item>


                    <el-form-item>
                        <el-button type="primary" size="default" @click="addOrEdit">保存资料</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { post } from '@/api/http';
import { useCommonStore } from "@/store";
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';

// 获取store
const commonStore = useCommonStore()

// 表单显示状态
const editShow = ref(false)

// 表单数据
const formData = reactive({})

// 表单引用
const editModalForm = ref(null)

// 表单验证规则
const rules = {
    userName: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 3, max: 20, message: '账号长度应在3到20个字符之间', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                var reg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入正确邮箱'));
                }
                else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ],
    nickName: [
        { required: true, message: '请输入名称', trigger: 'blur' },
    ],
    phoneNumber: [
        { required: true, message: '请输入手机号码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                var reg = /^1[123456789]\d{9}$/;
                if (!value || !reg.test(value)) {
                    callback(new Error('请输入正确的手机号'));
                }
                else {
                    callback();
                }
            }, trigger: 'blur'
        },
    ],


}

// 获取账号信息
const showEditModal = async () => {
    const { data } = await post("/sysUser/findById", { id: commonStore.userId })
    Object.assign(formData, data)
    editShow.value = true
}

// 创建或者修改
const addOrEdit = async () => {
    if (!editModalForm.value) return

    await editModalForm.value.validate(async valid => {
        if (valid) {
            const { success } = await post("/sysUser/addOrEdit", formData)
            if (success) {
                ElMessage.success("修改成功!")
                commonStore.getInfo()
            }
        }
    })
}

// 页面加载时获取数据
onMounted(() => {
    showEditModal()
})
</script>

<style scoped lang="scss"></style>
