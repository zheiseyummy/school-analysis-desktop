import { createApp } from "vue";
import App from "./App.vue";
import router from "@/router";
import { setupStore } from "@/store";
import { setupElIcons, setupI18n } from "@/plugins";

// 本地SVG图标
import "virtual:svg-icons-register";

// 样式
// import "element-plus/dist/index.css";
import "element-plus/theme-chalk/dark/css-vars.css";
import "@/styles/index.scss";
import "uno.css";
import "animate.css";
import "viewerjs/dist/viewer.css";
import VueViewer from "v-viewer";

const app = createApp(App);
// 全局注册 状态管理(store)
setupStore(app);
// 全局注册Element-plus图标
setupElIcons(app);
// 国际化
setupI18n(app);
app.use(VueViewer, {
  defaultOptions: {
    zIndex: 9999,
  },
});
app.use(router).mount("#app");
