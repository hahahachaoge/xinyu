<template>
    <el-footer class="footer">
        <div class="footer-inner">
            <div class="footer-top">
                <div class="footer-brand">
                    <svg class="footer-logo" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M16 4C9.373 4 4 9.373 4 16s5.373 12 12 12 12-5.373 12-12S22.627 4 16 4zm0 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S6 21.523 6 16 10.477 6 16 6z" fill="#397b68" opacity="0.8"/>
                        <path d="M16 10a2 2 0 100 4 2 2 0 000-4zm-1 6v6h2v-6h-2z" fill="#397b68" opacity="0.8"/>
                    </svg>
                    <span class="footer-name">青少年心理健康知识服务平台</span>
                </div>
                <div class="footer-links">
                    <span class="footer-link">关于我们</span>
                    <span class="footer-divider">|</span>
                    <span class="footer-link">隐私政策</span>
                    <span class="footer-divider">|</span>
                    <span class="footer-link">使用条款</span>
                    <span class="footer-divider">|</span>
                    <span class="footer-link">联系我们</span>
                </div>
            </div>
            <div class="footer-bottom">
                <span>© 2026 青少年心理健康知识服务平台 · 基于RAG技术的智能问答系统</span>
                <span class="footer-hotline">心理援助热线：400-161-9995</span>
            </div>
        </div>
    </el-footer>
</template>

<script setup>
</script>

<style scoped lang="scss">
.footer {
    background-color: #28463a;
    padding: 0;
    height: auto !important;
}
.footer-inner {
    width: var(--content-width);
    margin: 0 auto;
    padding: 30px 0 20px;
}
.footer-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 20px;
    border-bottom: 1px solid #405c4c;
}
.footer-brand {
    display: flex;
    align-items: center;
    gap: 10px;
}
.footer-logo {
    width: 28px;
    height: 28px;
}
.footer-name {
    color: #bacbbd;
    font-size: 14px;
    font-weight: 500;
}
.footer-links {
    display: flex;
    align-items: center;
    gap: 12px;
}
.footer-link {
    color: #7c8f81;
    font-size: 13px;
    cursor: pointer;
    transition: color 0.2s;
}
.footer-link:hover {
    color: #397b68;
}
.footer-divider {
    color: #475569;
    font-size: 12px;
}
.footer-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 16px;
    color: #657c6d;
    font-size: 12px;
}
.footer-hotline {
    color: #397b68;
    font-weight: 500;
}
.footer { background: #edf3ed; border-top: 1px solid #dbe4d9; }
.footer-top { border-color: #dbe4d9; }
.footer-name { color: #28463a; }
.footer-link, .footer-bottom { color: #657c6d; }
.footer-divider { color: #a0b5a4; }
</style>
