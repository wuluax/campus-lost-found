// 应用入口：创建 Vue 应用并注册全局插件与样式
import { createApp } from 'vue'
import App from './App.vue'

// 注册 Element Plus（最新版本），用于构建交互式 UI 组件
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css' // Element Plus 基础样式

// 注册路由：集中管理页面跳转与守卫
import router from './router'

// 引入 Unocss 原子化样式（需在 vite.config.js 中启用插件）
import 'uno.css'

// 引入全局 SCSS 样式（统一样式管理位置）
import './styles/index.scss'

createApp(App)
  .use(ElementPlus)
  .use(router)
  .mount('#app')

