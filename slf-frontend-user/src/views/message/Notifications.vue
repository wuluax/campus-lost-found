<template>
  <div class="min-h-screen bg-[#f8f9fc] pb-6">

    <div class="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-gray-100">
      <NavBar title="通知中心">
        <template #right>
          <span
            v-if="hasUnread"
            class="text-xs text-blue-500 font-medium active:opacity-60"
            @click="handleMarkAllRead"
          >
            全部已读
          </span>
        </template>
      </NavBar>
    </div>

    <div v-if="loading && notifications.length === 0" class="flex justify-center items-center h-[60vh]">
      <van-loading type="spinner" color="#ccc" vertical>加载中...</van-loading>
    </div>

    <div v-else-if="notifications.length === 0" class="flex flex-col items-center justify-center h-[60vh]">
      <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
        <i class="i-ph:bell-simple text-3xl text-gray-300" />
      </div>
      <p class="text-gray-400 text-sm">暂无通知</p>
    </div>

    <div v-else class="px-4 pt-3 space-y-2">
      <div
        v-for="n in notifications"
        :key="n.id"
        class="flex items-start gap-3 p-3 bg-white rounded-2xl border shadow-sm transition-all duration-200 cursor-pointer active:scale-[0.99]"
        :class="n.isRead === 0 ? 'border-blue-100' : 'border-gray-100'"
        @click="handleClick(n)"
      >
        <div
          class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5"
          :class="iconBgClass(n.type)"
        >
          <i :class="iconClass(n.type)" class="text-lg" />
        </div>

        <div class="flex-1 min-w-0">
          <div class="flex items-center justify-between mb-1">
            <span class="text-[14px] font-bold text-gray-900 truncate flex items-center gap-1.5">
              {{ n.title }}
              <span v-if="n.isRead === 0" class="w-2 h-2 bg-blue-500 rounded-full inline-block flex-shrink-0" />
            </span>
            <span class="text-[11px] text-gray-400 flex-shrink-0 ml-2">
              {{ formatTime(n.createTime) }}
            </span>
          </div>
          <div v-if="n.content" class="text-sm text-gray-500 line-clamp-2">
            {{ n.content }}
          </div>
          <div v-if="n.senderNickname" class="text-[11px] text-gray-400 mt-1">
            来自 {{ n.senderNickname }}
          </div>
        </div>
      </div>

      <div v-if="hasMore" class="flex justify-center py-4">
        <span
          class="text-xs text-gray-400 bg-gray-100 px-4 py-1.5 rounded-full cursor-pointer active:bg-gray-200"
          @click="loadMore"
        >
          加载更多
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { showSuccessToast } from 'vant'
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '@/api/notification'
import NavBar from '@/layout/NavBar.vue'

const router = useRouter()

const notifications = ref([])
const loading = ref(true)
const pageNo = ref(1)
const total = ref(0)
const hasMore = computed(() => notifications.value.length < total.value)
const hasUnread = computed(() => notifications.value.some(n => n.isRead === 0))

const loadList = async (reset = true) => {
  if (reset) {
    pageNo.value = 1
    loading.value = true
  }
  try {
    const res = await getNotifications({ pageNo: pageNo.value, pageSize: 20 })
    if (reset) {
      notifications.value = res?.list || []
    } else {
      notifications.value.push(...(res?.list || []))
    }
    total.value = res?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  pageNo.value++
  loadList(false)
}

const handleClick = async (n) => {
  if (n.isRead === 0) {
    try {
      await markNotificationRead(n.id)
      n.isRead = 1
    } catch (e) {}
  }
  if (n.type === 'clue' || n.type === 'favorite') {
    if (n.relatedId) router.push(`/items/${n.relatedId}`)
  } else if (n.type === 'message') {
    if (n.senderId) router.push(`/message/chat/${n.senderId}`)
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllNotificationsRead()
    notifications.value.forEach(n => { n.isRead = 1 })
    showSuccessToast('已全部标为已读')
  } catch (e) {}
}

const iconClass = (type) => ({
  clue: 'i-ph:chat-circle-text-fill',
  favorite: 'i-ph:star-fill',
  message: 'i-ph:chat-circle-dots-fill',
  system: 'i-ph:megaphone-fill',
})[type] || 'i-ph:bell-fill'

const iconBgClass = (type) => ({
  clue: 'bg-blue-50 text-blue-500',
  favorite: 'bg-yellow-50 text-yellow-500',
  message: 'bg-green-50 text-green-500',
  system: 'bg-purple-50 text-purple-500',
})[type] || 'bg-gray-50 text-gray-500'

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60 * 1000) return '刚刚'
  if (diff < 60 * 60 * 1000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 24 * 60 * 60 * 1000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 7 * 24 * 60 * 60 * 1000) return Math.floor(diff / 86400000) + '天前'
  return `${d.getMonth() + 1}/${d.getDate()}`
}

onMounted(() => loadList())
</script>
