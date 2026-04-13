import 'virtual:uno.css'
import 'vant/es/toast/style'
import 'vant/es/dialog/style'
import 'vant/es/notify/style'
import 'vant/es/image-preview/style'
import '@unocss/reset/tailwind-compat.css'
import 'virtual:svg-icons-register'
import { createPinia } from 'pinia'
import App from './App.vue'
import router, { setupRouter } from './router'
import { Lazyload } from 'vant'

async function bootstrap() {
  const app = createApp(App)
  
  // 创建并注册Pinia
  const pinia = createPinia()
  app.use(pinia)
  setupRouter(app)
  await router.isReady()
  app.use(Lazyload)
  app.mount('#app', true)
}



void bootstrap()
