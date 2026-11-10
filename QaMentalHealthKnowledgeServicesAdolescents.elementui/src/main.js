import "./styles/index.scss";
import { createPinia } from "pinia";
import { createPersistedState } from "pinia-plugin-persistedstate";
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

const app = createApp(App);
// 关闭开发环境下的警告信息
app.config.warnHandler = () => {};
app.use(router);

const pinia = createPinia();
pinia.use(createPersistedState());
app.use(pinia);

app.mount("#app");
