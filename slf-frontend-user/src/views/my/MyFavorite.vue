<template>
  <div class="min-h-screen bg-[#f8f9fc] pb-20">
    <div class="sticky top-0 z-40 bg-white/90 backdrop-blur-md border-b border-gray-100">
      <NavBar />
    </div>

    <div class="px-4 py-3">
      <span class="text-xs text-gray-400">共收藏 <span class="text-gray-700 font-bold">{{ totalCount }}</span> 条物品</span>
    </div>

    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
      class="px-3 space-y-3"
    >
      <div v-if="!itemList.length && !loading" class="flex flex-col items-center justify-center py-24">
        <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <i class="i-ph:star text-3xl text-gray-300" />
        </div>
        <div class="text-gray-400 text-sm">暂无收藏的物品</div>
      </div>

      <ItemCard
        v-for="vo in itemList"
        :key="vo.item.id"
        :item="vo.item"
        :category-name="getCategoryName(vo.item.categoryId)"
        :location-name="getLocationName(vo.item.locationId)"
        @click="goDetail(vo.item.id)"
      >
        <template #action>
          <div class="flex justify-end">
            <van-button
              size="mini" round plain
              class="!px-3 !h-7 !text-red-500 !border-red-100 !bg-red-50"
              @click="unfavorite(vo.item.id)"
            >
              取消收藏
            </van-button>
          </div>
        </template>
      </ItemCard>
    </van-list>
  </div>
</template>

<script setup>
import NavBar from '@/layout/NavBar.vue'
import { showFailToast, showSuccessToast } from 'vant'
import ItemCard from '@/components/ItemCard.vue'
import { useDictMap } from '@/hooks/useDictMap'

const router = useRouter()

const itemList = ref([])
const loading = ref(false)
const finished = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

const { loadDicts, getCategoryName, getLocationName } = useDictMap()

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites({ pageNo: pageNo.value, pageSize: pageSize.value })
    totalCount.value = res.total || totalCount.value
    const list = res.list || []
    if (pageNo.value === 1) itemList.value = list; else itemList.value.push(...list)
    if (list.length < pageSize.value) finished.value = true; else pageNo.value++
  } catch (e) {
    showFailToast('加载失败'); finished.value = true
  } finally {
    loading.value = false
  }
}

const onLoad = () => { loadFavorites() }
const goDetail = (id) => { router.push(`/items/${id}`) }
const unfavorite = async (id) => {
  try {
    await removeFavorite(id)
    itemList.value = itemList.value.filter(vo => vo.item.id !== id)
    totalCount.value = Math.max(0, totalCount.value - 1)
    showSuccessToast('已取消收藏')
  } catch (e) {}
}

onMounted(async () => {
  try { await loadDicts() } catch (e) {}
  loadFavorites()
})
</script>
