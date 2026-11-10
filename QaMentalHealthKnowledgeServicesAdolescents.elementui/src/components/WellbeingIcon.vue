<template>
    <svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="1.35" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <template v-if="name === 'leaf'"><path d="M16 28C15 14 22 5 29 4c1 12-5 19-13 20M16 28C15 17 10 11 3 11c0 9 5 14 12 13M16 25l9-15M14 22L7 15" /></template>
        <template v-else-if="name === 'people'"><circle cx="13" cy="10" r="5"/><path d="M3 28v-4a10 10 0 0120 0v4M23 6a5 5 0 010 10M26 20a8 8 0 014 8"/></template>
        <path v-else-if="name === 'heart'" d="M16 28L4 16C-5 6 9-3 16 8 23-3 37 6 28 16Z"/>
        <template v-else-if="name === 'book'"><path d="M16 8C12 4 5 4 2 6v21c5-2 10-1 14 2 4-3 9-4 14-2V6c-3-2-10-2-14 2v21M6 10c3-1 5 0 7 1M6 15c3-1 5 0 7 1M20 11c2-1 4-2 7-1M20 16c2-1 4-2 7-1"/></template>
        <template v-else-if="name === 'school'"><path d="M2 12l14-7 14 7-14 7ZM7 15v9c6 4 12 4 18 0v-9M30 13v11"/></template>
        <template v-else-if="name === 'shield'"><path d="M16 3l12 5v10c0 6-7 10-12 13C11 28 4 24 4 18V8ZM11 16l4 4 7-8"/></template>
        <template v-else-if="name === 'document'"><rect x="6" y="3" width="21" height="27" rx="3"/><path d="M11 10h11M11 16h11M11 22h7"/></template>
        <template v-else-if="name === 'search'"><circle cx="13" cy="13" r="9"/><path d="M20 20l9 9"/></template>
        <path v-else-if="name === 'arrow'" d="M4 16h24M21 9l7 7-7 7"/>
        <path v-else-if="name === 'plus'" d="M16 5v22M5 16h22"/>
    </svg>
</template>
<script setup>
defineProps({ name: { type: String, default: 'leaf' } });
</script>
