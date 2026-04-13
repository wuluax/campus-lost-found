// 布局组件
const Layout = () => import('@/layout/Tabbar.vue')

// 命名导出路由数组
export const routes = [
  // 基础路由
  {
    path: '/',
    name: 'Root',
    redirect: '/home',
    meta: { title: 'Root' },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/Register.vue'),
    meta: { title: '注册' },
  },

  // 首页
  {
    path: '/home',
    name: 'Home',
    redirect: '/home/index',
    component: Layout,
    meta: { title: '首页', icon: 'i-tabler:home-filled', showInTabbar: true },
    children: [
      {
        path: 'index',
        name: 'HomePage',
        component: () => import('@/views/home/index.vue'),
        meta: { keepAlive: false },
      },
    ],
  },

  // 功能
  {
    path: '/feature',
    name: 'Feature',
    redirect: '/feature/index',
    component: Layout,
    meta: { title: '功能', icon: 'i-solar:box-bold  ', showInTabbar: true },
    children: [
      {
        path: 'index',
        name: 'FeaturePage',
        component: () => import('@/views/feature/index.vue'),
        meta: { keepAlive: false },
      },
    ],
  },

  // 消息
  {
    path: '/message',
    name: 'Message',
    redirect: '/message/index',
    component: Layout,
    meta: { title: '消息', icon: 'i-ph:chat-circle-dots-fill', showInTabbar: true },
    children: [
      {
        path: 'index',
        name: 'MessagePage',
        component: () => import('@/views/message/index.vue'),
        meta: { keepAlive: false },
      },
    ],
  },

  // 我的
  {
    path: '/my',
    name: 'My',
    redirect: '/my/index',
    component: Layout,
    meta: { title: '我的', icon: 'i-simple-icons:docsify', showInTabbar: true },
    children: [
      {
        path: 'index',
        name: 'MyPage',
        component: () => import('@/views/my/index.vue'),
        meta: { keepAlive: false },
      },
    ],
  },
  
        {
        path: '/my/userinfo',
        name: 'UserInfo',
        component: () => import('@/views/my/UserInfo.vue'),
        meta: { title: '编辑个人信息', innerPage: true },
      },
  {
    path: '/my/publish',
    name: 'MyPublish',
    component: () => import('@/views/my/MyPublish.vue'),
    meta: { title: '我的发布', innerPage: true },
  },
  {
    path: '/my/favorite',
    name: 'MyFavorite',
    component: () => import('@/views/my/MyFavorite.vue'),
    meta: { title: '我的收藏', innerPage: true },
  },
  // 内页

  // 发布页面
  {
    path: '/feature/publish-lost',
    name: 'PublishLost',
    component: () => import('@/views/feature/Publish.vue'),
    meta: { title: '发布失物', innerPage: true },
  },
  {
    path: '/feature/publish-found',
    name: 'PublishFound',
    component: () => import('@/views/feature/Publish.vue'),
    meta: { title: '发布招领', innerPage: true },
  },
  // 物品详情页
  {
    path: '/items/:id',
    name: 'ItemDetail',
    component: () => import('@/views/home/itemdetail.vue'),
    meta: { title: '物品详情', innerPage: true },
  },
  
  {
    path: '/feature/ai',
    name: 'AI',
    component: () => import('@/views/feature/AI.vue'),
    meta: { title: 'AI智能助手', innerPage: true },
  },
  // 通知中心
  {
    path: '/message/notifications',
    name: 'Notifications',
    component: () => import('@/views/message/Notifications.vue'),
    meta: { title: '通知中心', innerPage: true },
  },
  // 私信聊天页
  {
    path: '/message/chat/:userId',
    name: 'Chat',
    component: () => import('@/views/message/Chat.vue'),
    meta: { title: '聊天', innerPage: true },
  },
]
