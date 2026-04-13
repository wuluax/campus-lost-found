import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import Unocss from 'unocss/vite'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { VantResolver as VantAutoResolver } from '@vant/auto-import-resolver'

export default defineConfig({
  define: {
    global: 'globalThis',
  },
  plugins: [
    vue(),
    Unocss(),

    // 自动导入
    AutoImport({
      imports: [
        'vue',          // 自动导入 ref、reactive、computed...
        'vue-router',   // 自动导入 useRouter、useRoute
      ],
      resolvers: [
        VantAutoResolver(), // 自动导入 Vant 相关方法（如 Toast 等）
      ],
      dirs: ['src/api', 'src/hooks', 'src/utils'],
      dts: false,
    }),

    //自动导入 Vant 组件
    Components({
      dirs: ['src/components', 'src/layout'],
      resolvers: [VantAutoResolver()],
      dts:false,
    }),

    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
      symbolId: 'icon-[name]',
    }),
  ],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  
  // 代理配置
  server: {
    host: '0.0.0.0',
    port: 5555,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      },
      '/ws': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
      }
    }
  }
})
