<template>
  <div class="h-[100dvh] overflow-hidden bg-[#F2F4F7] flex flex-col relative">
    
    <div class="fixed top-0 left-0 w-full z-50 bg-[#F2F4F7]/80 backdrop-blur-xl border-b border-gray-200/50">
      <NavBar title="智能助手" />
    </div>

    <div 
      ref="chatContainer" 
      class="flex-1 overflow-y-auto px-4 pt-[60px] pb-[90px] space-y-6 scroll-smooth"
    >
      <div v-for="msg in history" :key="msg.id || msg._localId" class="flex flex-col animate-fadeIn">
        
        <div v-if="shouldShowTime(msg)" class="flex justify-center mb-4 mt-2">
          <span class="text-[11px] text-gray-400 bg-gray-200/50 px-2.5 py-1 rounded-md font-medium">
            {{ formatMonthDayTime(msg.createTime) }}
          </span>
        </div>

        <div :class="['flex w-full gap-3', isMe(msg) ? 'flex-row-reverse' : 'flex-row']">
          
          <div class="flex-shrink-0">
             <van-image
              round
              width="40px"
              height="40px"
              :src="isMe(msg) ? (myAvatar || '/cat.jpeg') : (aiAvatar)"
              class="border border-white/50 shadow-sm"
            />
          </div>

          <div :class="['max-w-[75%] flex flex-col', isMe(msg) ? 'items-end' : 'items-start']">
            
            <div 
              v-if="showBubble(msg)"
              class="px-4 py-3 text-[15px] leading-relaxed break-all shadow-sm"
              :class="isMe(msg) 
                ? 'bg-blue-600 text-white rounded-2xl rounded-tr-sm' 
                : 'bg-white text-gray-800 rounded-2xl rounded-tl-sm border border-gray-100'"
            >
              <div v-if="msg.content">{{ msg.content }}</div>
              
              <van-image 
                v-if="msg.imageUrl" 
                :src="msg.imageUrl" 
                width="100%" 
                fit="cover" 
                class="rounded-lg mt-2 bg-black/5"
                @load="handleImageLoad"
              />
            </div>

            <div 
              v-if="msg.itemId" 
              @click="goItem(msg.itemId)" 
              class="mt-2 w-64 bg-white rounded-xl p-2.5 shadow-sm border border-gray-100 active:scale-[0.98] transition-transform cursor-pointer overflow-hidden relative"
            > 
              <div class="absolute top-0 right-0 w-16 h-16 bg-blue-50 rounded-bl-full -mr-8 -mt-8 z-0"></div>
              
              <div class="flex gap-3 relative z-10">
                <div class="relative w-[60px] h-[60px] flex-shrink-0">
                  <van-image 
                    :src="msg._item?.item?.imageUrl || '/school.jpg'" 
                    width="60" 
                    height="60" 
                    fit="cover" 
                    class="rounded-lg bg-gray-50"
                  />
                  <div
                    v-if="msg._score !== null && msg._score !== undefined"
                    class="absolute bottom-0 right-0 bg-black/60 text-white text-[9px] px-1 rounded-tl-md backdrop-blur-sm"
                  >
                    {{ msg._score }}%
                  </div>
                </div>
                <div class="flex-1 min-w-0 flex flex-col justify-between py-0.5">
                   <div class="text-[13px] font-bold text-gray-800 line-clamp-2 leading-tight">
                     {{ msg._item?.item?.description || '物品详情加载中...' }}
                   </div>
                   <div class="flex items-center justify-between mt-1">
                     <span class="text-[10px] text-blue-500 bg-blue-50 px-1.5 py-0.5 rounded">点击查看</span>
                     <i class="i-ph:caret-right-bold text-gray-300 text-xs" />
                   </div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>

      <div v-if="isAiThinking" class="flex w-full gap-3 animate-pulse-soft">
        <van-image round width="40px" height="40px" :src="aiAvatar" class="flex-shrink-0 border border-white/50" />
        <div class="bg-white text-gray-500 rounded-2xl rounded-tl-sm shadow-sm border border-gray-100 px-4 py-3 flex items-center h-[42px]">
          <div class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>

    <div class="fixed bottom-0 left-0 w-full bg-white/95 backdrop-blur-md border-t border-gray-100 z-50 safe-area-pb">
      <div class="px-4 py-3 flex items-end gap-3">
        
        <div class="flex gap-2 mb-1">
           <van-uploader :max-count="1" :max-size="2 * 1024 * 1024" @oversize="onOversize" :after-read="afterRead" accept="image/*">
            <div class="w-9 h-9 rounded-full bg-gray-100 text-gray-500 flex items-center justify-center active:bg-gray-200 transition-colors">
              <i class="i-ph:image text-xl" />
            </div>
          </van-uploader>
          
          <button 
            class="w-9 h-9 rounded-full bg-gray-100 text-gray-500 flex items-center justify-center active:bg-gray-200 transition-colors disabled:opacity-50"
            @click="openItemPicker" 
            :disabled="isAiThinking"
          >
            <i class="i-ph:sparkle text-xl" />
          </button>
        </div>

        <div class="flex-1 bg-gray-100 rounded-[20px] px-4 py-2 min-h-[40px] flex flex-col justify-center transition-all focus-within:bg-white focus-within:ring-2 focus-within:ring-blue-100 focus-within:border-blue-200 border border-transparent">
          
          <div v-if="attachedImageUrl" class="mb-2 relative w-fit group">
            <van-image :src="attachedImageUrl" width="50" height="50" fit="cover" class="rounded-lg border border-gray-200 block"/>
            <div 
              class="absolute -top-2 -right-2 bg-gray-800 text-white rounded-full w-5 h-5 flex items-center justify-center cursor-pointer shadow-md"
              @click="removeImage"
            >
              <i class="i-ph:x text-xs" />
            </div>
          </div>

          <textarea
            v-model="inputText"
            rows="1"
            class="w-full bg-transparent border-none outline-none text-[15px] text-gray-800 placeholder-gray-400 resize-none leading-6 max-h-24 py-0.5"
            placeholder="描述线索..."
            maxlength="300"
            @keydown.enter.prevent="handleSend"
          ></textarea>
        </div>

        <button 
          class="w-10 h-10 rounded-full flex items-center justify-center transition-all flex-shrink-0 mb-0.5 shadow-sm"
          :class="(inputText.trim() || attachedImageUrl) && !isAiThinking
            ? 'bg-blue-600 text-white shadow-blue-200 hover:bg-blue-700 transform hover:scale-105' 
            : 'bg-gray-100 text-gray-300 cursor-not-allowed'"
          @click="handleSend"
          :disabled="!(inputText.trim() || attachedImageUrl) || isAiThinking"
        >
          <i class="i-ph:paper-plane-right-fill text-lg translate-x-[-1px] translate-y-[1px]" />
        </button>
      </div>
    </div>

    <van-popup 
      v-model:show="showItemPicker" 
      position="bottom" 
      round 
      class="max-h-[70vh] flex flex-col"
      closeable
    >
      <div class="px-5 pt-5 pb-2">
        <div class="text-center text-[17px] font-bold text-gray-900">选择我的物品</div>
        <div class="text-center text-xs text-gray-400 mt-1">AI 将基于此物品特征进行全库搜索</div>
      </div>

      <div class="px-4 mt-2">
        <van-tabs v-model:active="matchTab" shrink type="card" class="custom-card-tabs">
          <van-tab title="我丢失的" name="lost" />
          <van-tab title="我拾到的" name="found" />
        </van-tabs>
      </div>

      <div class="flex-1 overflow-y-auto p-4 min-h-[300px]">
        <div v-if="loadingMyItems" class="py-10 text-center">
           <van-loading type="spinner" color="#1989fa" size="24px" />
        </div>
        
        <div v-else-if="myItems.length === 0" class="flex flex-col items-center justify-center py-10 text-gray-400">
           <i class="i-ph:package text-4xl mb-2 opacity-50" />
           <span class="text-sm">暂无相关记录</span>
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="it in myItems"
            :key="it.item.id"
            class="relative"
          >
            <ItemCard
              :item="it.item"
              :category-name="getCategoryName(it.item.categoryId)"
              :location-name="getLocationName(it.item.locationId)"
              @click="selectMyItem(it.item.id)"
              :class="selectedMyItemId === it.item.id 
                ? 'ring-2 ring-blue-500/20 border border-blue-500' 
                : 'border border-gray-100'"
            />
            <i
              v-if="selectedMyItemId === it.item.id"
              class="i-ph:check-circle-fill text-blue-500 absolute top-2 right-2 text-lg"
            />
          </div>
        </div>
      </div>

      <div class="p-4 border-t border-gray-100 safe-area-pb">
        <van-button 
          block 
          round 
          type="primary" 
          class="!h-[44px] !bg-blue-600 !border-blue-600 !font-bold shadow-lg shadow-blue-500/20"
          :disabled="!selectedMyItemId || isAiThinking" 
          :loading="sendingMatch" 
          @click="sendSelectedItemForMatch"
        >
          发送并开始匹配
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { getAiHistory, sendAiMessage } from '@/api/ai'
import { getItemDetail, getMyItems } from '@/api/item'
import { uploadAiImage } from '@/api/common'
import { showFailToast } from 'vant'
import { useDictMap } from '@/hooks/useDictMap'
import { formatMonthDayTime } from '@/utils/date'

const router = useRouter()
const userStore = useUserStore()

const history = ref([])
const inputText = ref('')
const chatContainer = ref(null)
const attachedImageUrl = ref('')
const isAiThinking = ref(false)
const dictLoaded = ref(false)

const showItemPicker = ref(false)
const matchTab = ref('lost')
const loadingMyItems = ref(false)
const myItems = ref([])
const selectedMyItemId = ref(null)
const sendingMatch = ref(false)
const {
  loadDicts: loadDictOptions,
  getCategoryName,
  getLocationName,
} = useDictMap()

const myAvatar = computed(() => userStore.userInfo?.avatar)
const aiAvatar = '/logo.svg' // 建议换一个更正式的AI图标

const isMe = (msg) => msg.role === '0'
const currentMatchType = computed(() => (matchTab.value === 'lost' ? 0 : 1))
const showBubble = (msg) => {
  if (!msg) return false
  if (msg.itemId && msg._score !== undefined && msg._score !== null && !msg.imageUrl) {
    return false
  }
  return Boolean(msg.content || msg.imageUrl)
}

const shouldShowTime = (msg) => {
  const idx = history.value.indexOf(msg)
  if (idx === 0) return true
  const prev = history.value[idx - 1]
  const diff = new Date(msg.createTime) - new Date(prev.createTime)
  return diff > 5 * 60 * 1000
}

const scrollToBottom = (smooth = true) => {
  nextTick(() => {
    const container = chatContainer.value
    if (container) {
      container.scrollTo({
        top: container.scrollHeight,
        behavior: smooth ? 'smooth' : 'auto'
      })
    }
  })
}

watch(() => history.value, (newVal, oldVal) => {
  if (newVal.length > 0) {
    const isInitial = !oldVal || oldVal.length === 0
    scrollToBottom(!isInitial)
  }
}, { deep: true })

watch(isAiThinking, (val) => {
  if (val) scrollToBottom(true)
})

const handleImageLoad = () => scrollToBottom(true)

const loadHistory = async () => {
  try {
    const res = await getAiHistory()
    history.value = res || []
    for (const msg of history.value) {
      parseItemScore(msg)
      if (msg.itemId) {
        try {
          const detail = await getItemDetail(msg.itemId)
          msg._item = detail
        } catch (e) {}
      }
    }
  } catch (error) {
    console.error(error)
  }
}

watch(matchTab, () => {
  selectedMyItemId.value = null
  myItems.value = []
  if (showItemPicker.value) loadMyItems()
})

const loadMyItems = async () => {
  try {
    loadingMyItems.value = true
    const res = await getMyItems({ pageNo: 1, pageSize: 50, type: currentMatchType.value })
    myItems.value = res?.list || []
  } catch (e) {
    myItems.value = []
  } finally {
    loadingMyItems.value = false
  }
}

const loadDictsOnce = async () => {
  if (dictLoaded.value) return
  try {
    await loadDictOptions()
    dictLoaded.value = true
  } catch (e) {
    dictLoaded.value = false
  }
}

const openItemPicker = async () => {
  if (isAiThinking.value) return
  showItemPicker.value = true
  selectedMyItemId.value = null
  await loadDictsOnce()
  await loadMyItems()
}

const selectMyItem = (id) => {
  selectedMyItemId.value = id
}

const isTimeoutError = (error) => {
  const msg = String(error?.message || '').toLowerCase()
  return error?.code === 'ECONNABORTED' || msg.includes('timeout')
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

const appendAiMessages = async (aiMsgList) => {
  const msgs = Array.isArray(aiMsgList) ? aiMsgList : (aiMsgList ? [aiMsgList] : [])
  for (const msg of msgs) {
    parseItemScore(msg)
    if (msg?.itemId) {
      try {
        const detail = await getItemDetail(msg.itemId)
        msg._item = detail
      } catch (e) {}
    }
    history.value.push(msg)
  }
}

const recoverAfterTimeout = async () => {
  showFailToast('AI处理较慢，正在同步结果...')
  await sleep(2000)
  await loadHistory()
}

const sendSelectedItemForMatch = async () => {
  if (!selectedMyItemId.value || isAiThinking.value) return
  try {
    sendingMatch.value = true
    const localUserMsg = {
      _localId: Date.now(),
      role: '0',
      content: '请帮我匹配这个物品',
      itemId: selectedMyItemId.value,
      createTime: new Date().toISOString(),
    }
    history.value.push(localUserMsg)

    try {
      const detail = await getItemDetail(selectedMyItemId.value)
      localUserMsg._item = detail
    } catch (e) {}

    showItemPicker.value = false
    isAiThinking.value = true

    const aiMsgList = await sendAiMessage({ itemId: selectedMyItemId.value, content: '请帮我匹配这个物品' })
    await appendAiMessages(aiMsgList)
  } catch (error) {
    console.error(error)
    if (isTimeoutError(error)) {
      await recoverAfterTimeout()
      return
    }
    showFailToast('AI 响应失败')
  } finally {
    isAiThinking.value = false
    sendingMatch.value = false
  }
}

const handleSend = async () => {
  const content = inputText.value.trim()
  if ((!content && !attachedImageUrl.value) || isAiThinking.value) return

  const localUserMsg = {
    _localId: Date.now(),
    role: '0',
    content: content,
    imageUrl: attachedImageUrl.value,
    createTime: new Date().toISOString()
  }
  history.value.push(localUserMsg)
  
  const sentContent = inputText.value 
  const sentImage = attachedImageUrl.value
  inputText.value = ''
  attachedImageUrl.value = ''

  isAiThinking.value = true

  try {
    const aiMsgList = await sendAiMessage({ content: sentContent, imageUrl: sentImage })
    await appendAiMessages(aiMsgList)
  } catch (error) {
    console.error(error)
    if (isTimeoutError(error)) {
      await recoverAfterTimeout()
      return
    }
    showFailToast('AI 响应失败')
  } finally {
    isAiThinking.value = false
  }
}

const onOversize = () => showFailToast('图片大小不能超过2MB')

const afterRead = async (file) => {
  try {
    if (!file.file) return
    const url = await uploadAiImage(file.file)
    attachedImageUrl.value = url
  } catch (e) {
    showFailToast('图片上传失败')
  }
}

const removeImage = () => { attachedImageUrl.value = '' }

onMounted(() => { loadHistory() })

const goItem = (id) => {
  if (!id) return
  router.push(`/items/${id}`)
}

const parseItemScore = (msg) => {
  if (!msg || msg.role !== '1' || !msg.itemId || !msg.content) return
  const c = String(msg.content).trim()
  if (!c.startsWith('{') || !c.endsWith('}')) return
  try {
    const obj = JSON.parse(c)
    if (obj && obj.score !== undefined && obj.score !== null) {
      msg._score = Number(obj.score)
    }
  } catch (e) {}
}

</script>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
}
.typing-indicator span {
  display: block;
  width: 5px;
  height: 5px;
  background-color: #9CA3AF;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}
.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }
@keyframes typing {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 隐藏滚动条但保留滚动功能 */
.scroll-smooth::-webkit-scrollbar {
  display: none;
}
.scroll-smooth {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* 针对 Vant Tabs 的自定义，去掉默认的红/蓝色调，改用极简风 */
:deep(.custom-card-tabs .van-tabs__nav--card) {
  border: none;
  background-color: #f3f4f6; /* gray-100 */
  border-radius: 8px;
}
:deep(.custom-card-tabs .van-tab--card) {
  border: none;
  background-color: transparent;
  color: #6b7280;
  border-radius: 6px;
  margin: 2px;
}
:deep(.custom-card-tabs .van-tab--card.van-tab--active) {
  background-color: #fff;
  color: #111827;
  font-weight: bold;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
</style>
