<template>
  <div class="min-h-screen bg-[#f8f9fc]">
    <div class="sticky top-0 z-40 bg-white/90 backdrop-blur-md border-b border-gray-100">
      <NavBar />
    </div>

    <div class="px-4 py-3 flex items-center justify-between">
      <span class="text-xs text-gray-400">共发布 <span class="text-gray-700 font-bold">{{ totalCount }}</span> 条信息</span>
    </div>

    <div class="px-3 mb-3">
      <div class="bg-white p-1 rounded-xl flex shadow-sm">
        <div
          v-for="tab in tabs"
          :key="tab.name"
          @click="handleTypeTabChange(tab.name)"
          class="flex-1 text-center text-[13px] py-2 rounded-lg relative z-10 transition-all duration-300 cursor-pointer font-medium"
          :class="typeTab === tab.name
            ? 'text-gray-900 bg-blue-50 font-bold'
            : 'text-gray-500'"
        >
          {{ tab.title }}
        </div>
      </div>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" class="min-h-[70vh]">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        loading-text="加载中..."
        @load="onLoad"
        class="pb-20 px-3 space-y-3"
      >
        <div v-if="!itemList.length && !loading" class="flex flex-col items-center justify-center py-24">
          <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
            <i class="i-ph:files-bold text-3xl text-gray-300" />
          </div>
          <div class="text-gray-400 text-sm mb-5">你还没有发布过任何内容</div>
          <van-button round type="primary" size="small" class="!h-9 !px-6 !text-sm !font-bold shadow-lg shadow-blue-500/20" @click="router.push('/feature/publish-lost')">
            去发布
          </van-button>
        </div>

        <ItemCard
          v-for="vo in itemList"
          :key="vo.item.id"
          :item="vo.item"
          :category-name="getCategoryName(vo.item.categoryId)"
          :location-name="getLocationName(vo.item.locationId)"
          @click="goToDetail(vo.item.id)"
        >
          <template #action>
            <div class="flex justify-end gap-2">
              <template v-if="vo.item.status === 1">
                <van-button size="mini" plain round class="!px-3 !h-7 !text-emerald-600 !border-emerald-200 !bg-emerald-50 font-medium" @click="handleMarkDone(vo.item.id, vo.item.type)">
                  {{ vo.item.type === 0 ? '确认找回' : '确认认领' }}
                </van-button>
                <van-button size="mini" plain round class="!px-3 !h-7 !text-blue-600 !border-blue-200 !bg-blue-50" @click="handleOffShelf(vo.item.id)">下架</van-button>
                <van-button size="mini" plain round class="!px-3 !h-7 !text-gray-600 !border-gray-200" @click="handleEdit(vo.item.id, vo.item.type)">编辑</van-button>
              </template>
              <template v-else>
                <div class="mr-auto text-xs text-gray-400 flex items-center">
                  <i class="i-ph:info mr-1" />
                  {{ vo.item.status === 0 ? '当前已下架' : '流程已结束' }}
                </div>
                <van-button v-if="vo.item.status !== 1" size="mini" plain round class="!px-3 !h-7 !text-orange-600 !border-orange-200 !bg-orange-50" @click="handleRelist(vo.item.id)">重新上架</van-button>
              </template>
              <van-button size="mini" plain round class="!px-3 !h-7 !text-red-500 !border-red-100 !bg-red-50" @click="handleDelete(vo.item.id)">删除</van-button>
            </div>
          </template>
        </ItemCard>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { getMyItems, deleteItem, updateItemStatus, getMyItemsCount } from '@/api/item'
import { showFailToast, showConfirmDialog, showSuccessToast } from 'vant'
import NavBar from '@/layout/NavBar.vue'
import ItemCard from '@/components/ItemCard.vue'
import { useDictMap } from '@/hooks/useDictMap'

const router = useRouter()
const route = useRoute()

const itemList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNo = ref(1)
const pageSize = ref(20)
const totalCount = ref(0)

const tabs = [{ title: '全部', name: 'all' }, { title: '失物', name: 'lost' }, { title: '招领', name: 'found' }]
const typeTab = ref('all')
if (route.query?.tab === 'lost' || route.query?.tab === 'found') typeTab.value = route.query.tab

const { loadDicts, getCategoryName, getLocationName } = useDictMap()
const loadOptions = async () => { try { await loadDicts() } catch (e) { console.error(e) } }

const loadCount = async () => { try { totalCount.value = await getMyItemsCount() } catch (e) { console.error(e) } }

const loadItems = async () => {
  try {
    loading.value = true
    const params = { pageNo: pageNo.value, pageSize: pageSize.value, sortField: 'createTime', sortOrder: 'desc' }
    if (typeTab.value !== 'all') params.type = (typeTab.value === 'lost' ? 0 : 1)
    const result = await getMyItems(params)
    if (refreshing.value) { itemList.value = []; refreshing.value = false }
    if (pageNo.value === 1) itemList.value = result.list || []; else itemList.value.push(...(result.list || []))
    if ((result.list || []).length < pageSize.value) finished.value = true; else pageNo.value++
  } catch (error) {
    showFailToast('加载失败'); finished.value = true
  } finally {
    loading.value = false; refreshing.value = false
  }
}

const onRefresh = () => { finished.value = false; loading.value = true; pageNo.value = 1; loadItems(); loadCount() }
const onLoad = () => { loadItems() }
const goToDetail = (id) => { router.push(`/items/${id}`) }
const handleTypeTabChange = (name) => { typeTab.value = name; finished.value = false; pageNo.value = 1; itemList.value = []; loadItems() }

const handleEdit = (id, type) => {
  const path = type === 0 ? '/feature/publish-lost' : '/feature/publish-found'
  router.push({ path, query: { editId: id } })
}

const handleDelete = (id) => {
  showConfirmDialog({ title: '删除确认', message: '确定要删除这条发布吗？删除后不可恢复。' })
    .then(async () => {
      await deleteItem(id); showSuccessToast('删除成功')
      itemList.value = itemList.value.filter(item => item.item.id !== id); loadCount()
    }).catch(() => {})
}

const updateStatusWithConfirm = async ({ id, status, title, message, toast }) => {
  try { await showConfirmDialog({ title, message }) } catch { return }
  try {
    await updateItemStatus(id, status)
    const vo = itemList.value.find(it => it.item.id === id)
    if (vo) vo.item.status = status
    if (toast) showSuccessToast(toast)
  } catch (e) {}
}

const handleMarkDone = (id, type) => {
  const label = type === 0 ? '已找回' : '已被认领'
  return updateStatusWithConfirm({ id, status: 2, title: '状态确认', message: `确认将该物品标记为「${label}」吗？`, toast: '操作成功' })
}
const handleOffShelf = (id) => updateStatusWithConfirm({ id, status: 0, title: '下架确认', message: '下架后将在列表中不可见，确定继续吗？', toast: '已下架' })
const handleRelist = (id) => updateStatusWithConfirm({ id, status: 1, title: '上架确认', message: '确定要重新上架该物品吗？', toast: '已上架' })

onMounted(() => { loadOptions(); loadItems(); loadCount() })
</script>
