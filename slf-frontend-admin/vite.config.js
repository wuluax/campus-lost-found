// Vite 项目配置文件
import { defineConfig } from 'vite'
// 官方 Vue 插件：支持 .vue 单文件组件、模板编译等特性
import vue from '@vitejs/plugin-vue'
// Unocss Vite 插件：按需生成原子化样式，提升开发效率与构建性能
import UnoCSS from '@unocss/vite'
// Node 路径工具：用于配置别名
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 配置文档：https://vite.dev/config/
export default defineConfig({
  // 挂载插件列表：启用 Vue 插件与 Unocss 插件
  // - vue(): 解析 .vue SFC
  // - UnoCSS(): 原子化样式方案，配置位于根目录 uno.config.js
  plugins: [vue(), UnoCSS()],
  // 解决构建时无法解析 '@/...' 别名的问题
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  // 本地开发服务器配置：通过代理转发到后端 Spring Boot，解决浏览器跨域限制
  // 说明：前端请求以 /api 起始，代理会去掉 /api 前缀并转发到 8080
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api'),
      },
    },
  },
})
