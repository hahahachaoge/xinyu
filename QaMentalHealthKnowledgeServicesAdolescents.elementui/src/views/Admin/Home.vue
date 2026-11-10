<template></template>
<script setup>
import { useCommonStore } from "@/store";
import { computed, onBeforeMount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
const router = useRouter();
const route = useRoute();
const commonStore = useCommonStore();
const token = computed(() => commonStore.token);
const userInfo = computed(() => commonStore.userInfo);
const roleType = computed(() => commonStore.roleType);
const userId = computed(() => commonStore.userId);
onBeforeMount(async () => {
    router.push("/admin/userList");
})
</script>
<style scoped lang="scss"></style>