<template>
  <div class="min-h-screen bg-[#F7F8FA] pb-[100px]">
    
    <div class="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-gray-100">
      <NavBar title="详情" />
    </div>

    <div v-if="loading" class="flex justify-center items-center h-[60vh]">
       <van-loading type="spinner" color="#ccc" vertical>加载中...</van-loading>
    </div>

    <div v-if="itemDetail.item && !loading" class="animate-fade-in space-y-3 pt-3">
      
      <div class="bg-white px-4 py-3 flex items-center justify-between">
         <div class="flex items-center gap-3">
            <van-image
              round
              width="40"
              height="40"
              fit="cover"
              :src="itemDetail.user?.avatar || '/cat.jpeg'"
              class="border border-gray-100"
            />
            <div>
               <div class="text-[15px] font-bold text-gray-900 leading-tight">
                 {{ itemDetail.user?.nickname || '匿名用户' }}
               </div>
               <div class="text-xs text-gray-400 mt-0.5">
                 {{ formatDateTime(itemDetail.item.createTime) }} 发布
               </div>
            </div>
         </div>
         
         <van-button 
            v-if="isOwner"
            round 
            size="small" 
            class="!h-7 !px-3 !bg-gray-100 !text-gray-600 !border-none !font-bold active:!bg-gray-200"
            @click="goEdit"
         >
            编辑
         </van-button>
      </div>

      <div class="bg-white px-4 py-4 pb-6">
         <div class="flex flex-wrap items-center gap-2 mb-4">
            <div 
              class="px-2 py-1 rounded-[4px] text-xs font-bold tracking-wide"
              :class="itemDetail.item.type === 0 
                ? 'bg-rose-50 text-rose-500' 
                : 'bg-emerald-50 text-emerald-500'"
            >
               {{ itemDetail.item.type === 0 ? '寻找失物' : '寻找失主' }}
            </div>
            <div class="px-2 py-1 rounded-[4px] bg-blue-50 text-blue-500 text-xs font-bold tracking-wide">
               {{ getCategoryName(itemDetail.item.categoryId) }}
            </div>
         </div>

         <div class="text-[16px] text-gray-800 leading-7 font-medium tracking-wide mb-4 whitespace-pre-wrap break-all">
            {{ itemDetail.item.description }}
         </div>

         <div v-if="itemDetail.item?.imageUrl" class="rounded-xl overflow-hidden border border-gray-100 bg-gray-50">
            <van-image
              :src="itemDetail.item.imageUrl"
              width="100%"
              fit="contain"
              class="max-h-[400px]"
              @click="previewImage()"
            />
         </div>
      </div>

      <van-cell-group :border="false" class="!my-0">
         <van-cell center class="!py-4">
            <template #icon>
               <i class="i-ph:map-pin-fill text-lg text-gray-400 mr-3" />
            </template>
            <template #title>
               <span class="text-gray-500 text-sm">地点</span>
            </template>
            <template #value>
               <span class="text-gray-900 font-bold text-sm">{{ getLocationName(itemDetail.item.locationId) }}</span>
            </template>
         </van-cell>

         <van-cell center class="!py-4">
            <template #icon>
               <i class="i-ph:clock-fill text-lg text-gray-400 mr-3" />
            </template>
            <template #title>
               <span class="text-gray-500 text-sm">丢/拾时间</span>
            </template>
            <template #value>
               <span class="text-gray-900 font-bold text-sm">{{ formatDateTime(itemDetail.item.itemTime) }}</span>
            </template>
         </van-cell>

         <van-cell center class="!py-4">
            <template #icon>
               <i class="i-ph:phone-fill text-lg text-gray-400 mr-3" />
            </template>
            <template #title>
               <span class="text-gray-500 text-sm">联系方式</span>
            </template>
            <template #value>
               <div class="flex items-center justify-end gap-2" >
                  <span class="text-gray-900 font-bold text-sm">{{ itemDetail.item.contact }}</span>
               </div>
            </template>
         </van-cell>
      </van-cell-group>

      <div class="bg-white pb-6">
         <div class="px-4 py-4 border-b border-gray-50 flex justify-between items-center">
            <div class="font-bold text-gray-900">
               线索留言 <span class="text-gray-400 font-normal text-xs ml-1">{{ clues.length }}</span>
            </div>
         </div>

        <div v-if="clues.length > 0" class="divide-y divide-gray-50">
           <div v-for="clue in clues" :key="clue.id" class="px-4 py-4">
              <div class="flex gap-3">
                 <van-image round width="32" height="32" :src="clue.user?.avatar || '/cat.jpeg'" class="flex-shrink-0 mt-0.5 border border-gray-100" />
                 <div class="flex-1">
                    <div class="flex items-center justify-between mb-1">
                       <span class="text-sm font-bold text-gray-700">{{ clue.user?.nickname || ('用户' + clue.userId) }}</span>
                       <span class="text-[10px] text-gray-400">{{ formatDateTime(clue.createTime) }}</span>
                    </div>
                    <div class="text-[14px] text-gray-600 leading-relaxed text-justify">
                       {{ clue.content }}
                    </div>
                    <div class="mt-2 flex justify-end items-center">
                       <span
                         v-if="clue.userId == currentUserId"
                         class="text-[10px] text-gray-400 bg-gray-50 px-2 py-0.5 rounded cursor-pointer hover:text-red-500 hover:bg-red-50 transition-colors"
                         @click="handleDeleteClue(clue.id)"
                       >
                         删除
                       </span>
                    </div>
                 </div>
              </div>
           </div>
        </div>

         <div v-else class="py-12 text-center">
            <div class="w-12 h-12 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-3">
               <i class="i-ph:chat-dots text-2xl text-gray-300" />
            </div>
            <p class="text-gray-400 text-xs">暂无相关线索</p>
         </div>
      </div>
    </div>

    <div class="fixed bottom-0 left-0 w-full bg-white/95 backdrop-blur-md border-t border-gray-100 px-4 py-2 z-40 flex items-center gap-3 safe-area-pb">
       
       <div 
         class="flex-1 bg-gray-100 rounded-full h-[40px] flex items-center px-4 cursor-text active:bg-gray-200 transition-colors group" 
         @click="showCluePopup = true"
       >
         <i class="i-ph:pencil-simple text-gray-400 mr-2 group-hover:text-gray-600" />
         <span class="text-sm text-gray-400 group-hover:text-gray-500">提供线索...</span>
       </div>
       
       <div
         v-if="!isOwner"
         class="w-[40px] h-[40px] rounded-full flex items-center justify-center active:bg-gray-50 transition-colors cursor-pointer"
         @click="goChat"
       >
         <i class="i-ph:chat-circle-dots text-2xl text-gray-600" />
       </div>

       <div
         class="w-[40px] h-[40px] rounded-full flex items-center justify-center active:bg-gray-50 transition-colors cursor-pointer"
         @click="toggleFavorite"
       >
         <i
           class="text-2xl transition-all"
           :class="isFavorited ? 'i-ph:star-fill text-yellow-400 scale-110' : 'i-ph:star text-gray-600'"
         />
       </div>
    </div>

    <van-popup v-model:show="showCluePopup" position="bottom" round class="p-4 safe-area-pb">
       <div class="flex justify-between items-center mb-4">
          <span class="text-[15px] font-bold text-gray-900">补充线索</span>
          <div class="p-1 -mr-2 active:opacity-50" @click="showCluePopup = false">
             <i class="i-ph:x-bold text-gray-400 text-lg"/>
          </div>
       </div>
       
       <div class="bg-gray-50 rounded-xl p-3 mb-4 border border-gray-100 focus-within:ring-1 focus-within:ring-blue-100 focus-within:border-blue-200 transition-all">
          <textarea
            v-model="clueContent"
            rows="3"
            class="w-full bg-transparent border-none outline-none text-sm text-gray-800 placeholder-gray-400 resize-none"
            placeholder="请详细描述线索（如发现的具体位置、时间、物品特征等）..."
            maxlength="300"
          ></textarea>
          <div class="text-right text-xs text-gray-300 mt-1">
             {{ clueContent.length }}/300
          </div>
       </div>

       <van-button 
         block 
         round 
         type="primary" 
         class="!h-[44px] !font-bold !bg-blue-600 !border-blue-600 shadow-blue-100 shadow-lg" 
         :loading="submittingClue" 
         @click="submitClue"
       >
          提交线索
       </van-button>
    </van-popup>

  </div>
</template>

<script setup>

import { showSuccessToast, showFailToast, showImagePreview, showConfirmDialog } from 'vant'
import { getItemDetail } from '@/api/item'
import { getItemClues, createClue, deleteClue as apiDeleteClue } from '@/api/clue'
import { useUserStore } from '@/stores/user' 
import NavBar from '@/layout/NavBar.vue'

import { useDictMap } from '@/hooks/useDictMap'
import { formatDateTime } from '@/utils/date'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const itemDetail = ref({ item: null, user: {} })
const clues = ref([])
const {
  loadDicts,
  getCategoryName,
  getLocationName,
} = useDictMap()
const clueContent = ref('')
const submittingClue = ref(false)
const loading = ref(true) 
const showCluePopup = ref(false)
const isFavorited = ref(false)

const currentUserId = computed(() => userStore.userId)
const isOwner = computed(() => itemDetail.value.item && itemDetail.value.item.userId === currentUserId.value)

const toggleFavorite = async () => {
    if (!currentUserId.value) { showFailToast('请先登录'); return }
    const id = route.params.id
    try {
        if (isFavorited.value) {
            await removeFavorite(id)
            isFavorited.value = false
            showSuccessToast('已取消收藏')
        } else {
            await addFavorite(id)
            isFavorited.value = true
            showSuccessToast('已收藏')
        }
    } catch (error) {}
}

const previewImage = () => {
  const url = itemDetail.value.item?.imageUrl
  if (url) {
    showImagePreview({ images: [url], closeable: true })
  }
}


const loadData = async () => {
    loading.value = true
    try {
        const [detail, cluesData] = await Promise.all([
            getItemDetail(route.params.id),
            getItemClues(route.params.id),
            loadDicts()
        ])
        
        itemDetail.value = detail
        clues.value = cluesData || []
        try { isFavorited.value = await checkFavorite(route.params.id) } catch (e) {}
        
    } catch (error) {
        console.error(error)
        showFailToast('数据加载失败')
    } finally {
        loading.value = false
    }
}

const submitClue = async () => {
    const content = clueContent.value
    if (!content || !content.trim()) {
        showFailToast('线索内容不能为空')
        return
    }
    try {
        submittingClue.value = true
        await createClue(route.params.id, { content: content.trim() })
        showSuccessToast('提交成功')
        clueContent.value = ''
        showCluePopup.value = false
        clues.value = await getItemClues(route.params.id)
    } catch (error) {
        showFailToast('提交失败')
    } finally {
        submittingClue.value = false
    }
}

const handleDeleteClue = async (clueId) => {
    try {
        await showConfirmDialog({
            title: '确定删除',
            message: '删除后不可恢复',
            confirmButtonText: '删除',
            cancelButtonText: '取消'
        })
    } catch {
        return
    }
    try {
        await apiDeleteClue(clueId)
        showSuccessToast('删除成功')
        clues.value = await getItemClues(route.params.id)
    } catch (error) {
        showFailToast('删除失败')
    }
}

const goEdit = () => {
  const id = route.params.id
  const type = itemDetail.value.item?.type
  const path = type === 0 ? '/feature/publish-lost' : '/feature/publish-found'
  router.push({ path, query: { editId: id } })
}

const goChat = () => {
  const publisherId = itemDetail.value.item?.userId
  if (!publisherId) return
  if (!currentUserId.value) { showFailToast('请先登录'); return }
  router.push(`/message/chat/${publisherId}`)
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.4s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 覆盖 Cell 样式以实现极简风 */
:deep(.van-cell) {
  padding-left: 20px;
  padding-right: 20px;
  background-color: #fff;
}
:deep(.van-cell:after) {
  left: 56px; /* 让分割线对齐文字，而不是图标 */
  right: 20px;
}
</style>
