<template>
  <div class="h-[100dvh] overflow-hidden bg-[#F2F4F7] flex flex-col relative">

    <div class="fixed top-0 left-0 w-full z-50 bg-[#F2F4F7]/80 backdrop-blur-xl border-b border-gray-200/50">
      <NavBar :title="otherUser.nickname || '聊天'" />
    </div>

    <div
      ref="chatContainer"
      class="flex-1 overflow-y-auto px-4 pt-[60px] pb-[80px] space-y-6 scroll-smooth"
    >
      <!-- 加载更多 -->
      <div v-if="hasMore" class="flex justify-center pt-2">
        <span
          class="text-xs text-gray-400 bg-gray-200/50 px-3 py-1 rounded-full cursor-pointer active:bg-gray-300/50"
          @click="loadMore"
        >
          加载更多
        </span>
      </div>

      <div v-for="msg in displayMessages" :key="msg.id || msg._localId" class="flex flex-col animate-fadeIn">

        <div v-if="shouldShowTime(msg)" class="flex justify-center mb-4 mt-2">
          <span class="text-[11px] text-gray-400 bg-gray-200/50 px-2.5 py-1 rounded-md font-medium">
            {{ formatMsgTime(msg.createTime) }}
          </span>
        </div>

        <div :class="['flex w-full gap-3', isMe(msg) ? 'flex-row-reverse' : 'flex-row']">
          <div class="flex-shrink-0">
            <van-image
              round
              width="40px"
              height="40px"
              :src="isMe(msg) ? (myAvatar || '/cat.jpeg') : (otherUser.avatar || '/cat.jpeg')"
              class="border border-white/50 shadow-sm"
            />
          </div>

          <div :class="['max-w-[75%] flex flex-col', isMe(msg) ? 'items-end' : 'items-start']">
            <div
              class="px-4 py-3 text-[15px] leading-relaxed break-all shadow-sm"
              :class="isMe(msg)
                ? 'bg-blue-600 text-white rounded-2xl rounded-tr-sm'
                : 'bg-white text-gray-800 rounded-2xl rounded-tl-sm border border-gray-100'"
            >
              {{ msg.content }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="fixed bottom-0 left-0 w-full bg-white/95 backdrop-blur-md border-t border-gray-100 z-50 safe-area-pb">
      <div class="px-4 py-3 flex items-end gap-3">
        <div class="flex-1 bg-gray-100 rounded-[20px] px-4 py-2 min-h-[40px] flex flex-col justify-center transition-all focus-within:bg-white focus-within:ring-2 focus-within:ring-blue-100 focus-within:border-blue-200 border border-transparent">
          <textarea
            v-model="inputText"
            rows="1"
            class="w-full bg-transparent border-none outline-none text-[15px] text-gray-800 placeholder-gray-400 resize-none leading-6 max-h-24 py-0.5"
            placeholder="输入消息..."
            maxlength="500"
            @keydown.enter.exact.prevent="handleSend"
          ></textarea>
        </div>

        <button
          class="w-10 h-10 rounded-full flex items-center justify-center transition-all flex-shrink-0 mb-0.5 shadow-sm"
          :class="inputText.trim()
            ? 'bg-blue-600 text-white shadow-blue-200 hover:bg-blue-700 transform hover:scale-105'
            : 'bg-gray-100 text-gray-300 cursor-not-allowed'"
          @click="handleSend"
          :disabled="!inputText.trim()"
        >
          <i class="i-ph:paper-plane-right-fill text-lg translate-x-[-1px] translate-y-[1px]" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { showFailToast } from 'vant'
import { getChatHistory, markAsRead, sendMessage } from '@/api/message'
import { connectChat, onChatMessage, sendWsMessage } from '@/utils/websocket'
import { useUserStore } from '@/stores/user'
import { formatMonthDayTime } from '@/utils/date'
import NavBar from '@/layout/NavBar.vue'

const route = useRoute()
const userStore = useUserStore()

const otherUserId = computed(() => Number(route.params.userId))
const messages = ref([])
const inputText = ref('')
const chatContainer = ref(null)
const pageNo = ref(1)
const pageSize = 30
const total = ref(0)
const otherUser = ref({ nickname: '', avatar: '' })
const hasMore = computed(() => messages.value.length < total.value)
const myAvatar = computed(() => userStore.userInfo?.avatar)

const isMe = (msg) => msg.senderId === userStore.userId

// 消息按时间正序展示（API 返回倒序）
const displayMessages = computed(() => [...messages.value].sort((a, b) => new Date(a.createTime) - new Date(b.createTime)))

const shouldShowTime = (msg) => {
  const list = displayMessages.value
  const idx = list.indexOf(msg)
  if (idx === 0) return true
  const prev = list[idx - 1]
  return new Date(msg.createTime) - new Date(prev.createTime) > 5 * 60 * 1000
}

const formatMsgTime = (time) => {
  return formatMonthDayTime(time)
}

const scrollToBottom = (smooth = true) => {
  nextTick(() => {
    const container = chatContainer.value
    if (container) {
      container.scrollTo({ top: container.scrollHeight, behavior: smooth ? 'smooth' : 'auto' })
    }
  })
}

const loadMessages = async () => {
  try {
    const res = await getChatHistory(otherUserId.value, { pageNo: pageNo.value, pageSize })
    total.value = res.total
    const list = res.list || []
    if (pageNo.value === 1) {
      messages.value = list
    } else {
      // 加载更多时合并旧消息（去重）
      const existIds = new Set(messages.value.map(m => m.id))
      const newMsgs = list.filter(m => !existIds.has(m.id))
      messages.value = [...newMsgs, ...messages.value]
    }

    // 从聊天记录获取对方信息
    if (list.length > 0) {
      const otherMsg = list.find(m => m.senderId !== userStore.userId)
      if (otherMsg) {
        otherUser.value = { nickname: otherMsg.senderNickname, avatar: otherMsg.senderAvatar }
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const loadMore = async () => {
  pageNo.value++
  await loadMessages()
}

const handleSend = async () => {
  const content = inputText.value.trim()
  if (!content) return

  // 先通过 WebSocket 发送
  sendWsMessage({ receiverId: otherUserId.value, content })

  inputText.value = ''
}

// 接收 WebSocket 消息
onChatMessage((msg) => {
  // 只处理当前聊天对象的消息
  if (msg.senderId === otherUserId.value || msg.senderId === userStore.userId) {
    // 避免重复
    if (!messages.value.find(m => m.id === msg.id)) {
      messages.value.push(msg)
      scrollToBottom(true)
    }
    // 标记已读
    if (msg.senderId === otherUserId.value) {
      markAsRead(otherUserId.value).catch(() => {})
    }
  }
})

onMounted(async () => {
  // 建立 WebSocket 连接
  if (userStore.token) {
    connectChat(userStore.token)
  }
  await loadMessages()
  scrollToBottom(false)
  // 标记消息已读
  markAsRead(otherUserId.value).catch(() => {})
})
</script>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.scroll-smooth::-webkit-scrollbar { display: none; }
.scroll-smooth { -ms-overflow-style: none; scrollbar-width: none; }
</style>
