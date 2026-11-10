<template>
    <section class="front-page-intro">
        <div class="intro-copy">
            <p class="intro-eyebrow">{{ eyebrow }}</p>
            <h1>{{ title }}</h1>
            <p class="intro-description">{{ description }}</p>
            <slot />
        </div>
        <img :src="image" alt="" class="intro-art" />
    </section>
</template>

<script setup>
import readingIllustration from '@/assets/article-reading-hero.png';

defineProps({
    eyebrow: String,
    title: String,
    description: String,
    image: { type: String, default: readingIllustration }
});
</script>

<style scoped lang="scss">
.front-page-intro { display: grid; grid-template-columns: 1fr 34%; background: #edf2ff; border: 1px solid #dfe7f9; border-radius: 22px; overflow: hidden; margin-bottom: 28px; min-height: 214px; }
.intro-copy { align-self: center; padding: 28px 36px; }
.intro-eyebrow { font-size: 11px; letter-spacing: 2px; color: #7486ac; margin-bottom: 10px; }
h1 { color: #203e76; font-size: clamp(24px, 2.2vw, 32px); font-weight: 600; line-height: 1.5; letter-spacing: 1px; }
.intro-description { color: #7486ac; font-size: 13px; line-height: 1.9; margin-top: 12px; max-width: 640px; }
.intro-art { width: 100%; height: 100%; max-height: 250px; object-fit: cover; object-position: center; mask-image: linear-gradient(90deg, transparent, #000 20%); }
@media (max-width: 760px) {
    .front-page-intro { grid-template-columns: 1fr 28%; min-height: 190px; border-radius: 16px; margin-bottom: 20px; }
    .intro-copy { padding: 22px 20px; } .intro-description { font-size: 12px; }
    .intro-art { max-height: none; } h1 { font-size: 23px; }
}
@media (max-width: 420px) { .front-page-intro { grid-template-columns: 1fr; } .intro-art { display: none; } }
</style>
