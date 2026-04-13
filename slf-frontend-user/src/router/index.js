import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { useUserStore } from '@/stores/user'
import { isNavigationFailure } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  strict: true,
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

export function setupRouter(app) {
  app.use(router)
  router.beforeEach(async (to) => {
    const userStore = useUserStore()
    
    const whitePathList = ['/login', '/register']
    if (!userStore.isLogin && !whitePathList.includes(to.path)) {
      return {
        path: '/login',
        query: { redirect: to.fullPath }
      }
    }
  })

  router.afterEach((to, _, failure) => {
    document.title = to?.meta?.title || document.title
    if (isNavigationFailure(failure)) {
      console.warn('failed navigation', failure)
    }
  })

  router.onError((error) => {
    console.error('路由错误', error)
  })
}

export default router
