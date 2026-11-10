<template>
    <el-select style="width: 100%;min-width: 155px;" :disabled="disabled" v-model="selectValue" filterable
        placeholder="请选择" :filterable="true" :loading="loading" :clearable="true" :remote="true" @change="handleChange">
        <el-option v-for="item in options" :key="item.value" :label="item.name" :value="item.value">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 14px">{{ item.label }}</span>
        </el-option>
    </el-select>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { post } from '@/api/http';
// 定义props
const props = defineProps({
    modelValue: {
        type: [String, Number],
        default: ''
    },
    url: {
        type: String,
        default: ''
    },
    columnName: {
        type: String,
        default: ''
    },
    columnValue: {
        type: String,
        default: ''
    },
    columnLabel: {
        type: String,
        default: ''
    },
    where: {
        type: Object,
        default: () => ({})
    },
    filterValue: {
        type: Array,
        default: () => []
    },
    disabled: {
        type: Boolean,
        default: false
    }
})

// 定义emit
const emit = defineEmits(['update:modelValue', 'change'])

// 响应式数据
const loading = ref(false)
const options = ref([])
const selectValue = ref('')
const where_ = ref({})
const searchWhere = ref({})

// 监听modelValue变化
watch(
    () => props.modelValue,
    (newVal) => {
        if (newVal && newVal.toString().length > 0) {
            selectValue.value = newVal.toString()
        } else {
            selectValue.value = newVal
        }
    },
    { immediate: true }
)

// 获取数据列表
const fetchDataList = async (searchWhere = {}) => {
    loading.value = true
    try {
        const { data: { items } } = await post(props.url, {
            limit: 999,
            ...where_.value,
            ...searchWhere
        })
        let filterValue = props.filterValue.map(x => x.toString())

        const dataList = items.filter(item =>
            !filterValue.includes(item[props.columnValue]?.toString())
        ).map(item => ({
            name: item[props.columnName],
            value: item[props.columnValue]?.toString(),
            label: item[props.columnLabel]?.toString(),
            orginRItem: item
        }))


        if (filterValue.some(x => x.toString() === selectValue.value?.toString())) {
            selectValue.value = ''
        }

        options.value = dataList
    } catch (error) {
        console.error('Failed to fetch data:', error)
    } finally {
        loading.value = false
    }
}

// 重新加载数据
const Reload = async (where) => {
    const newSearchWhere = where ? { ...where } : {}
    searchWhere.value = newSearchWhere
    await fetchDataList(newSearchWhere)
}

// 选择值变化处理
const handleChange = (value) => {
    emit('update:modelValue', value)
    const selectedItem = options.value.find(x => x.value === value)
    if (selectedItem) {
        emit('change', { Value: value, SelectItem: selectedItem.orginRItem })
    }
}

// 组件挂载时初始化
onMounted(() => {
    if (props.modelValue && props.modelValue.toString().length > 0) {
        selectValue.value = props.modelValue.toString()
    }
    where_.value = props.where
    fetchDataList()
})
// 暴露方法给父组件
defineExpose({
    Reload,

})
</script>

<style scoped></style>
