<template>
  <div class="min-h-screen bg-[#f8f9fc] pb-24">
    <!-- 顶部 -->
    <div class="bg-gradient-to-br from-blue-500 to-blue-600 pt-10 pb-14 px-5 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-40 h-40 bg-white/10 rounded-full -mr-16 -mt-16" />
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/5 rounded-full -ml-10 -mb-10" />
      <h1 class="text-[22px] font-bold text-white m-0 relative z-10">消息</h1>
      <p class="text-white/70 text-sm mt-1 relative z-10">私信与通知</p>
    </div>

    <div class="px-4 -mt-8 relative z-10 space-y-2">

      <!-- 通知中心入口 -->
      <div
        class="flex items-center gap-3 p-3.5 bg-white rounded-2xl shadow-sm active:scale-[0.99] transition-all duration-200 cursor-pointer"
        @click="router.push('/message/notifications')"
      >
        <div class="relative flex-shrink-0">
          <div class="w-12 h-12 rounded-full bg-gradient-to-br from-amber-400 to-orange-500 flex items-center justify-center shadow-sm shadow-orange-200">
            <i class="i-ph:bell-ringing-fill text-[22px] text-white" />
          </div>
          <div
            v-if="notificationUnread > 0"
            class="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] font-bold min-w-[18px] h-[18px] rounded-full flex items-center justify-center px-1 shadow-sm shadow-red-200"
          >
            {{ notificationUnread > 99 ? '99+' : notificationUnread }}
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <div class="text-[15px] font-bold text-gray-900">通知中心</div>
          <div class="text-[13px] text-gray-400 truncate mt-0.5">
            {{ latestNotifText }}
          </div>
        </div>
        <i class="i-ph:caret-right-bold text-gray-300 text-sm flex-shrink-0" />
      </div>

      <!-- 私信标题 -->
      <div class="px-1 pt-3 pb-1">
        <span class="text-xs font-bold text-gray-400 tracking-wider">私信</span>
      </div>

      <!-- 私信列表 -->
      <div v-if="chatLoading" class="flex justify-center items-center h-[30vh]">
        <van-loading type="spinner" color="#ccc" vertical>加载中...</van-loading>
      </div>

      <template v-else-if="conversations.length > 0">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          class="flex items-center gap-3 p-3 bg-white rounded-2xl shadow-sm active:scale-[0.99] transition-all duration-200 cursor-pointer"
          @click="goChat(conv.otherUserId)"
        >
          <div class="relative flex-shrink-0">
            <van-image
              round width="48" height="48" fit="cover"
              :src="conv.otherUserAvatar || '/cat.jpeg'"
              class="border-2 border-white shadow-sm"
            />
            <div
              v-if="conv.unreadCount > 0"
              class="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] font-bold min-w-[18px] h-[18px] rounded-full flex items-center justify-center px-1"
            >
              {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between mb-1">
              <span class="text-[15px] font-bold text-gray-900 truncate">
                {{ conv.otherUserNickname || '未知用户' }}
              </span>
              <span class="text-[11px] text-gray-400 flex-shrink-0 ml-2">
                {{ formatTime(conv.lastMessageTime) }}
              </span>
            </div>
            <div class="text-[13px] text-gray-500 truncate">
              {{ conv.lastMessage || '暂无消息' }}
            </div>
          </div>
        </div>
      </template>

      <div v-else class="flex flex-col items-center justify-center py-16">
        <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-3">
          <i class="i-ph:chat-dots text-3xl text-gray-300" />
        </div>
        <p class="text-gray-400 text-sm">暂无私信</p>
        <p class="text-gray-300 text-xs mt-1">在物品详情页点击聊天即可发起私信</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getConversations } from '@/api/message'
import { getNotificationUnreadCount, getNotifications } from '@/api/notification'
import { connectChat, onChatMessage } from '@/utils/websocket'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const conversations = ref([])
const chatLoading = ref(true)
const notificationUnread = ref(0)
const latestNotifText = ref('线索、收藏等通知')

const loadConversations = async () => {
  chatLoading.value = true
  try {
    conversations.value = await getConversations() || []
  } catch (e) {
    console.error(e)
  } finally {
    chatLoading.value = false
  }
}

const loadNotifInfo = async () => {
  try {
    notificationUnread.value = await getNotificationUnreadCount() || 0
    const res = await getNotifications({ pageNo: 1, pageSize: 1 })
    const list = res?.list || []
    if (list.length > 0) {
      latestNotifText.value = list[0].title || '线索、收藏等通知'
    }
  } catch (e) {}
}

const goChat = (otherUserId) => {
  router.push(`/message/chat/${otherUserId}`)
}

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

onChatMessage((data) => {
  if (data.pushType === 'notification') {
    notificationUnread.value++
    latestNotifText.value = data.notification?.title || '你收到了新通知'
    return
  }
  loadConversations()
})

onMounted(() => {
  loadConversations()
  loadNotifInfo()
  if (userStore.token) {
    connectChat(userStore.token)
  }
})

onActivated(() => {
  loadConversations()
  loadNotifInfo()
})
</script>
