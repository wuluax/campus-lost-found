<template>
  <div class="min-h-screen bg-[#f8f9fc] pb-24">
    <!-- 顶部个人信息 -->
    <div class="bg-gradient-to-br from-blue-500 to-blue-600 pt-10 pb-16 px-5 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-40 h-40 bg-white/10 rounded-full -mr-16 -mt-16" />
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/5 rounded-full -ml-10 -mb-10" />

      <div class="flex items-center gap-4 relative z-10">
        <van-image
          round width="64" height="64" fit="cover"
          :src="userInfo.avatar || '/cat.jpeg'"
          class="border-3 border-white/30 shadow-lg flex-shrink-0"
        />
        <div class="flex-1 min-w-0">
          <h2 class="text-xl font-bold text-white m-0 truncate">
            {{ userInfo.nickname || '未设置昵称' }}
          </h2>
          <div class="text-white/60 text-sm mt-1">
            {{ userInfo.studentNo ? '学号 ' + userInfo.studentNo : userInfo.phone || '' }}
          </div>
        </div>
      </div>
    </div>

    <div class="px-4 -mt-8 relative z-10 space-y-3">

      <!-- 数据统计 -->
      <div class="bg-white rounded-2xl shadow-sm p-4">
        <div class="grid grid-cols-3 divide-x divide-gray-100">
          <div class="flex flex-col items-center py-1 cursor-pointer active:opacity-60" @click="router.push('/my/publish')">
            <span class="text-xl font-bold text-gray-900">{{ stats.published }}</span>
            <span class="text-[11px] text-gray-400 mt-1">发布</span>
          </div>
          <div class="flex flex-col items-center py-1 cursor-pointer active:opacity-60" @click="router.push('/my/favorite')">
            <span class="text-xl font-bold text-gray-900">{{ stats.favorited }}</span>
            <span class="text-[11px] text-gray-400 mt-1">收藏</span>
          </div>
          <div class="flex flex-col items-center py-1 cursor-pointer active:opacity-60" @click="router.push('/my/publish')">
            <span class="text-xl font-bold text-gray-900">{{ stats.resolved }}</span>
            <span class="text-[11px] text-gray-400 mt-1">已找到</span>
          </div>
        </div>
      </div>

      <!-- 菜单列表 -->
      <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div
          v-for="item in menuItems"
          :key="item.path"
          class="flex items-center gap-3 px-4 py-4 active:bg-gray-50 transition-colors cursor-pointer border-b border-gray-50 last:border-0"
          @click="router.push(item.path)"
        >
          <div class="w-9 h-9 rounded-xl flex items-center justify-center" :class="item.iconBg">
            <i :class="item.icon" class="text-lg" />
          </div>
          <span class="flex-1 text-[15px] font-medium text-gray-800">{{ item.label }}</span>
          <i class="i-ph:caret-right-bold text-gray-300 text-sm" />
        </div>
      </div>

      <!-- 退出登录 -->
      <div
        class="bg-white rounded-2xl shadow-sm overflow-hidden cursor-pointer active:bg-gray-50 transition-colors"
        @click="showLogoutAction = true"
      >
        <div class="flex items-center justify-center py-4 gap-2">
          <i class="i-ph:sign-out-bold text-red-400 text-lg" />
          <span class="text-red-500 font-medium text-[15px]">退出登录</span>
        </div>
      </div>
    </div>

    <van-action-sheet
      v-model:show="showLogoutAction"
      :actions="logoutActions"
      cancel-text="取消"
      close-on-click-action
      round
    />
  </div>
</template>

<script setup>
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { getMyItemsCount } from '@/api/item'
import { getFavorites } from '@/api/favorite'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const router = useRouter()

const stats = ref({ published: 0, favorited: 0, resolved: 0 })

const menuItems = [
  { label: '个人信息', path: '/my/userinfo', icon: 'i-ph:user-circle-fill text-blue-500', iconBg: 'bg-blue-50' },
  { label: '我的发布', path: '/my/publish', icon: 'i-ph:package-fill text-emerald-500', iconBg: 'bg-emerald-50' },
  { label: '我的收藏', path: '/my/favorite', icon: 'i-ph:star-fill text-yellow-500', iconBg: 'bg-yellow-50' },
]

const showLogoutAction = ref(false)
const logoutActions = [
  {
    name: '确认退出',
    color: '#ef4444',
    callback: () => {
      userStore.logout()
      router.replace('/login')
      showToast('已退出')
    },
  },
]

const loadStats = async () => {
  // 三个计数并行请求：总发布数、已完成数（status=2）、收藏数
  const [publishedCount, resolvedCount, favResult] = await Promise.allSettled([
    getMyItemsCount(),
    getMyItemsCount({ status: 2 }),
    getFavorites({ pageNo: 1, pageSize: 1 }),
  ])
  if (publishedCount.status === 'fulfilled') {
    stats.value.published = publishedCount.value || 0
  }
  if (resolvedCount.status === 'fulfilled') {
    stats.value.resolved = resolvedCount.value || 0
  }
  if (favResult.status === 'fulfilled') {
    stats.value.favorited = favResult.value?.total || 0
  }
}

onMounted(() => {
  loadStats()
})
</script>
