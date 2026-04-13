<template>
  <div class="min-h-screen bg-[#f8f9fc]">

    <!-- 顶部搜索区 -->
    <div class="bg-gradient-to-br from-blue-500 to-blue-600 pt-10 pb-6 px-4 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-40 h-40 bg-white/10 rounded-full -mr-16 -mt-16" />
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/5 rounded-full -ml-10 -mb-10" />

      <h1 class="text-[22px] font-bold text-white m-0 mb-3 relative z-10">失物招领</h1>

      <div class="relative z-10">
        <van-search
          v-model="searchKeyword"
          placeholder="搜索物品名称、特征..."
          shape="round"
          background="transparent"
          class="!p-0 search-white"
          @search="handleSearch"
          @clear="handleSearch"
        />
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="sticky top-0 z-40 bg-white shadow-sm">
      <van-dropdown-menu :overlay="true" active-color="#3b82f6">
        <van-dropdown-item v-model="typeTab" :options="typeOptions" @change="handleTypeChange" />
        <van-dropdown-item v-model="sortType" :options="sortOptions" @change="handleSortChange" />
        <van-dropdown-item v-model="categoryValue" :title="categoryTitle" :options="categoryOptions" @change="handleCategoryChange" />
        <van-dropdown-item v-model="locationValue" :title="locationTitle" :options="locationOptions" @change="handleLocationChange" />
      </van-dropdown-menu>
    </div>

    <!-- 列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" class="min-h-[80vh]">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        :immediate-check="false"
        finished-text="没有更多了"
        loading-text="加载中..."
        @load="onLoad"
        class="pb-24 pt-3 px-3 space-y-3"
      >
        <div v-if="loading && !itemList.length" class="space-y-3">
          <div v-for="i in 6" :key="i" class="bg-white rounded-2xl p-3 flex gap-3 h-32 animate-pulse">
            <div class="bg-gray-100 w-28 h-full rounded-xl shrink-0" />
            <div class="flex-1 space-y-3 py-1">
              <div class="bg-gray-100 h-4 w-3/4 rounded-lg" />
              <div class="bg-gray-100 h-3 w-1/2 rounded-lg" />
              <div class="bg-gray-100 h-3 w-1/3 rounded-lg mt-auto" />
            </div>
          </div>
        </div>

        <div v-if="!itemList.length && !loading" class="flex flex-col items-center justify-center py-24">
          <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
            <i class="i-ph:magnifying-glass text-3xl text-gray-300" />
          </div>
          <div class="text-gray-400 text-sm">暂无相关物品</div>
        </div>

        <ItemCard
          v-for="item in itemList"
          :key="item.item.id"
          :item="item.item"
          :category-name="getCategoryName(item.item.categoryId)"
          :location-name="getLocationName(item.item.locationId)"
          @click="goToDetail(item.item.id)"
        />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { getItems } from '@/api/item'
import { showFailToast } from 'vant'
import ItemCard from '@/components/ItemCard.vue'
import { useDictMap } from '@/hooks/useDictMap'

const router = useRouter()

const searchKeyword = ref('')
const typeTab = ref('all')
const typeOptions = [
  { text: '全部', value: 'all' },
  { text: '失物', value: 'lost' },
  { text: '招领', value: 'found' },
]

const sortType = ref('itemTime')
const sortOptions = [
  { text: '最新发布', value: 'createTime' },
  { text: '最新丢/拾', value: 'itemTime' },
]

const selectedCategory = ref(null)
const selectedLocation = ref(null)
const categoryValue = ref(null)
const locationValue = ref(null)

const categoryTitle = computed(() => selectedCategory.value ? selectedCategory.value.name : '分类')
const locationTitle = computed(() => selectedLocation.value ? selectedLocation.value.name : '地点')

const itemList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const isRequesting = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const {
  categoryOptions, locationOptions, categoryNameMap, locationNameMap,
  loadDicts, getCategoryName, getLocationName,
} = useDictMap({ includeAllOption: true })

const loadOptions = async () => {
  try { await loadDicts() } catch (error) { console.error(error) }
}

const handleTypeChange = () => { resetAndLoad() }
const handleSearch = () => { resetAndLoad() }
const handleSortChange = () => { resetAndLoad() }

const handleCategoryChange = (value) => {
  if (value === null || value === undefined || value === '') {
    selectedCategory.value = null
  } else {
    selectedCategory.value = { id: value, name: categoryNameMap.value.get(value) || '分类' }
  }
  resetAndLoad()
}

const handleLocationChange = (value) => {
  if (value === null || value === undefined || value === '') {
    selectedLocation.value = null
  } else {
    selectedLocation.value = { id: value, name: locationNameMap.value.get(value) || '地点' }
  }
  resetAndLoad()
}

const resetAndLoad = () => {
  pageNo.value = 1; itemList.value = []; finished.value = false; loading.value = true; startLoad()
}
const onRefresh = () => { finished.value = false; loading.value = true; pageNo.value = 1; startLoad() }
const onLoad = () => { startLoad() }

const startLoad = () => {
  if (finished.value || isRequesting.value) return
  isRequesting.value = true
  if (!loading.value) loading.value = true
  loadItems()
}

const loadItems = async () => {
  try {
    const categoryId = selectedCategory.value ? selectedCategory.value.id : undefined
    const locationId = selectedLocation.value ? selectedLocation.value.id : undefined
    const params = {
      pageNo: pageNo.value, pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      type: typeTab.value === 'all' ? undefined : (typeTab.value === 'lost' ? 0 : 1),
      categoryId, locationId, sortField: sortType.value, sortOrder: 'desc'
    }
    const result = await getItems(params)
    if (refreshing.value) { itemList.value = []; refreshing.value = false }
    if (pageNo.value === 1) itemList.value = result.list; else itemList.value.push(...result.list)
    total.value = result.total
    if (itemList.value.length >= result.total) finished.value = true; else pageNo.value++
  } catch (error) {
    console.error(error); showFailToast('加载失败'); finished.value = true
  } finally {
    loading.value = false; refreshing.value = false; isRequesting.value = false
  }
}

const goToDetail = (id) => { router.push(`/items/${id}`) }

onMounted(() => { loadOptions(); onLoad() })
</script>

<style scoped>
:deep(.van-dropdown-menu__bar) {
  box-shadow: none;
  background-color: #ffffff;
}
:deep(.van-dropdown-menu__item) {
  justify-content: center;
  flex: 1;
}
:deep(.van-dropdown-menu__title) {
  font-size: 13px;
  padding: 0 4px;
  color: #374151;
}
:deep(.van-dropdown-menu__title--active) {
  color: #3b82f6;
  font-weight: 600;
}
/* 白色搜索框 */
:deep(.search-white .van-search__content) {
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
}
:deep(.search-white .van-field__control) {
  color: #fff;
}
:deep(.search-white .van-field__control::placeholder) {
  color: rgba(255, 255, 255, 0.7);
}
:deep(.search-white .van-icon) {
  color: rgba(255, 255, 255, 0.7);
}
</style>
