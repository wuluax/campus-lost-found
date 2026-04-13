// 路由配置文件：集中管理页面路由与导航守卫
// 使用 Vue Router v4（与 Vue 3 配套），官方文档：https://router.vuejs.org/
import { createRouter, createWebHistory } from 'vue-router'

// 视图组件按需导入（保持清晰的结构）
import Login from '../views/Login.vue'
import MainLayout from '../layout/Index.vue'
import UserManagement from '../views/User.vue'
import ItemManagement from '../views/Item.vue'
import ClueManagement from '../views/Clue.vue'
import CategoryManagement from '../views/Category.vue'
import LocationManagement from '../views/Location.vue'
import MessageManagement from '../views/Message.vue'
import NotificationManagement from '../views/Notification.vue'

const isLoggedIn = () => !!localStorage.getItem('ADMIN_ACCESS_TOKEN')

// 路由表：
const routes = [
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/users',
    children: [
      {
        path: 'users',
        name: 'users',
        component: UserManagement,
        meta: { title: '用户管理' },
      },
      {
        path: 'items',
        name: 'items',
        component: ItemManagement,
        meta: { title: '物品管理' },
      },
      {
        path: 'clues',
        name: 'clues',
        component: ClueManagement,
        meta: { title: '线索管理' },
      },
      {
        path: 'categories',
        name: 'categories',
        component: CategoryManagement,
        meta: { title: '分类管理' },
      },
      {
        path: 'locations',
        name: 'locations',
        component: LocationManagement,
        meta: { title: '地点管理' },
      },
      {
        path: 'messages',
        name: 'messages',
        component: MessageManagement,
        meta: { title: '私信管理' },
      },
      {
        path: 'notifications',
        name: 'notifications',
        component: NotificationManagement,
        meta: { title: '通知管理' },
      },
    ],
  },
]

// 创建路由实例：使用 Vite 的基础路径作为 history 基础
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 导航守卫：未登录时访问受保护页面自动跳转到 /login
router.beforeEach((to) => {
  if (to.path === '/login' && isLoggedIn()) {
    return '/'
  }
  if (to.path !== '/login' && !isLoggedIn()) {
    return '/login'
  }
})

export default router
