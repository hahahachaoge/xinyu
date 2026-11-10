import vue from "@vitejs/plugin-vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import path from "path";
import { defineConfig } from "vite";

const elementPlusResolver = ElementPlusResolver({ importStyle: false });
const elementPlusIconNames = new Set(Object.keys(ElementPlusIconsVue));

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [elementPlusResolver],
      dts: false,
    }),
    Components({
      dirs: ["src/components"],
      resolvers: [
        elementPlusResolver,
        // 原来全局注册的图标也按模板实际使用情况导入。
        (name) => {
          if (elementPlusIconNames.has(name)) {
            return { name, from: "@element-plus/icons-vue" };
          }
        },
      ],
      dts: false,
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: "modern-compiler",
      },
    },
  },

  server: {
    port: 8000,
    strictPort: true,
    open: {
      app: {
        name: "microsoft-edge",
      },
    },
  },
});
