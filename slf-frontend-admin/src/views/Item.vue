<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAdminItem, getAdminItemDetail, getAdminItems, updateAdminItemStatus } from '@/api/item'
import { getAdminCategoryList } from '@/api/category'
import { getAdminLocationList } from '@/api/location'
import { getAdminUsers } from '@/api/user'

// 发布人筛选：选择某个用户（按用户ID精确筛选）
const publisherId = ref(null)
// 描述搜索关键字：支持按描述模糊搜索
const description = ref('')
// 类型筛选：null 表示不限，0-失物，1-招领
const type = ref(null)
// 状态筛选：null 表示不限，0-下架，1-正常，2-完成
const status = ref(null)
// 分类筛选：null 表示不限
const categoryId = ref(null)
// 地点筛选：null 表示不限
const locationId = ref(null)
// 发布时间范围筛选（开始/结束）
const createTimeRange = ref([])

function toStartDateTime(dateText) {
  if (!dateText) return undefined
  return `${dateText} 00:00:00`
}

function toEndDateTime(dateText) {
  if (!dateText) return undefined
  return `${dateText} 23:59:59`
}

// 列表加载状态
const loading = ref(false)
// 物品列表数据
const list = ref([])
// 数据总条数
const total = ref(0)
// 当前页码
const pageNo = ref(1)
// 每页大小
const pageSize = ref(15)

// 分类与地点列表数据（用于筛选下拉）
const categoryList = ref([])
const locationList = ref([])
const categoryOptions = computed(() => {
  const list = Array.isArray(categoryList.value) ? categoryList.value : []
  return list.map((c) => ({
    id: c.id,
    name: c.name,
  }))
})
const locationOptions = computed(() => {
  const list = Array.isArray(locationList.value) ? locationList.value : []
  return list.map((l) => ({
    id: l.id,
    name: l.name,
  }))
})

// 发布人候选（远程搜索）
const publisherOptions = ref([])
const publisherLoading = ref(false)

function buildUserLabel(u) {
  const nickname = u?.nickname ? String(u.nickname) : ''
  const phone = u?.phone ? String(u.phone) : ''
  const id = u?.id ?? ''
  const base = nickname || phone ? `${nickname}${nickname && phone ? ' / ' : ''}${phone}` : String(id)
  return `${base} [${id}]`
}

async function fetchPublisherOptions(keyword) {
  publisherLoading.value = true
  try {
    const data = await getAdminUsers({
      pageNo: 1,
      pageSize: 20,
      keyword: keyword || undefined,
    })
    const list = Array.isArray(data?.list) ? data.list : []
    publisherOptions.value = list.map((u) => ({
      id: u.id,
      nickname: u.nickname,
      phone: u.phone,
    }))
  } finally {
    publisherLoading.value = false
  }
}

async function onPublisherRemoteSearch(query) {
  await fetchPublisherOptions(query)
}

async function fetchCategoryList() {
  const data = await getAdminCategoryList()
  categoryList.value = Array.isArray(data) ? data : []
}

async function fetchLocationList() {
  const data = await getAdminLocationList()
  locationList.value = Array.isArray(data) ? data : []
}

// 组合分页、搜索与筛选参数
const queryParams = computed(() => ({
  pageNo: pageNo.value,
  pageSize: pageSize.value,
  publisherId: publisherId.value === null ? undefined : publisherId.value,
  description: description.value || undefined,
  type: type.value === null ? undefined : type.value,
  status: status.value === null ? undefined : status.value,
  categoryId: categoryId.value === null ? undefined : categoryId.value,
  locationId: locationId.value === null ? undefined : locationId.value,
  createStartTime:
    Array.isArray(createTimeRange.value) && createTimeRange.value.length === 2 ? toStartDateTime(createTimeRange.value[0]) : undefined,
  createEndTime:
    Array.isArray(createTimeRange.value) && createTimeRange.value.length === 2 ? toEndDateTime(createTimeRange.value[1]) : undefined,
}))

// 加载物品列表
async function fetchList() {
  loading.value = true
  try {
    const data = await getAdminItems(queryParams.value)
    list.value = data?.list || []
    total.value = Number(data?.total || 0)
  } finally {
    loading.value = false
  }
}

// 触发搜索
function onSearch() {
  pageNo.value = 1
  fetchList()
}

function onReset() {
  publisherId.value = null
  description.value = ''
  type.value = null
  status.value = null
  categoryId.value = null
  locationId.value = null
  createTimeRange.value = []
  pageNo.value = 1
  pageSize.value = 15
  fetchList()
}

// 切换每页条数
function onSizeChange(size) {
  pageSize.value = size
  pageNo.value = 1
  fetchList()
}

// 切换页码
function onCurrentChange(no) {
  pageNo.value = no
  fetchList()
}

function formatType(type) {
  if (type === 0) return '失物'
  if (type === 1) return '招领'
  return '-'
}

function formatStatus(status) {
  if (status === 0) return '下架'
  if (status === 1) return '正常'
  if (status === 2) return '完成'
  return '-'
}

function statusTagType(status) {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  return 'info'
}

// 下架物品：将 status 修改为 0
async function handleDown(row) {
  if (row.status === 0) return
  try {
    await ElMessageBox.confirm('确定下架该物品吗？', '下架物品', {
      type: 'warning',
      confirmButtonText: '确认下架',
      cancelButtonText: '取消',
    })
    await updateAdminItemStatus(row.id, 0)
    ElMessage.success('下架成功')
    fetchList()
  } catch (e) {
    // 取消或校验不通过时不做处理
  }
}

// 删除物品
async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该物品吗？删除后不可恢复。', '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
  })
  await deleteAdminItem(row.id)
  ElMessage.success('删除成功')
  if (list.value.length <= 1 && pageNo.value > 1) {
    pageNo.value = pageNo.value - 1
  }
  fetchList()
}

// 详情弹窗
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref(null)

function safeName(name) {
  return name || '-'
}

const detailItem = computed(() => detailData.value?.item || null)
const publisherDisplay = computed(() => detailData.value?.publisherDisplay || '-')
const categoryDisplay = computed(() => detailData.value?.categoryDisplay || '-')
const locationDisplay = computed(() => detailData.value?.locationDisplay || '-')
const fullLocation = computed(() => detailData.value?.fullLocation || '-')

async function openDetail(row) {
  detailVisible.value = true
  detailLoading.value = true
  detailData.value = null
  try {
    detailData.value = await getAdminItemDetail(row.id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchCategoryList(), fetchLocationList()])
  fetchList()
})
</script>

<template>
  <el-card class="page-index">
    <template #header>
      <div class="page-header">
        <span>物品管理</span>
        <div class="page-actions">
          <el-select
            v-model="type"
            placeholder="类型"
            clearable
            style="width: 100px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option label="失物" :value="0" />
            <el-option label="招领" :value="1" />
          </el-select>
          <el-select
            v-model="status"
            placeholder="状态"
            clearable
            style="width: 100px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option label="正常" :value="1" />
            <el-option label="完成" :value="2" />
            <el-option label="下架" :value="0" />
          </el-select>
          <el-select
            v-model="categoryId"
            placeholder="分类"
            clearable
            filterable
            style="width: 150px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option v-for="c in categoryOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-select
            v-model="locationId"
            placeholder="地点"
            clearable
            filterable
            style="width: 160px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option v-for="l in locationOptions" :key="l.id" :label="l.name" :value="l.id" />
          </el-select>
          <el-select
            v-model="publisherId"
            filterable
            remote
            clearable
            :remote-method="onPublisherRemoteSearch"
            :loading="publisherLoading"
            placeholder="选择发布人"
            style="width: 220px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option
              v-for="u in publisherOptions"
              :key="u.id"
              :label="buildUserLabel(u)"
              :value="u.id"
            />
          </el-select>          
          <el-date-picker
            v-model="createTimeRange"
            type="daterange"
            range-separator="~"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            clearable
            style="width: 240px"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @clear="onSearch"
            @change="onSearch"
          />
          <el-input
            v-model="description"
            placeholder="描述"
            clearable
            style="width: 200px"
            @clear="onSearch"
            @keyup.enter="onSearch"
          />
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button type="default" @click="onReset">重置/刷新</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="50" align="center" />
      <el-table-column label="类型" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.type === 1 ? 'warning' : 'success'">{{ formatType(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publisherNickname" label="发布人" width="140" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.publisherNickname || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类" min-width="50" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.categoryName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="locationName" label="地点" min-width="120" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.fullLocation || row.locationName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="300" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.description || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ formatStatus(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180" align="center" />
      <el-table-column label="操作" width="240" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDetail(row)">查看详情</el-button>
          <el-button size="small" type="warning" :disabled="row.status === 0" @click="handleDown(row)">
            下架
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="page-pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNo"
        :page-sizes="[15, 30, 50, 100]"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      />
    </div>

    <el-dialog v-model="detailVisible" title="物品详情" width="820px" destroy-on-close>
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="物品ID">
            {{ detailItem?.id ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="物品类型">
            {{ formatType(detailItem?.type) }}
          </el-descriptions-item>

          <el-descriptions-item label="发布人">
            {{ publisherDisplay }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            {{ formatStatus(detailItem?.status) }}
          </el-descriptions-item>

          <el-descriptions-item label="分类">
            {{ categoryDisplay }}
          </el-descriptions-item>
          <el-descriptions-item label="地点">
            {{ locationDisplay }}
          </el-descriptions-item>

          <el-descriptions-item label="完整地点" :span="2">
            {{ fullLocation }}
          </el-descriptions-item>

          <el-descriptions-item label="物品时间">
            {{ safeName(detailItem?.itemTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="联系方式">
            {{ safeName(detailItem?.contact) }}
          </el-descriptions-item>

          <el-descriptions-item label="发布时间">
            {{ safeName(detailItem?.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ safeName(detailItem?.updateTime) }}
          </el-descriptions-item>

          <el-descriptions-item label="图片" :span="2">
            <el-image
              v-if="detailItem?.imageUrl"
              :src="detailItem.imageUrl"
              :preview-src-list="[detailItem.imageUrl]"
              fit="cover"
              class="item-image-thumb"
              preview-teleported
            />
            <span v-else>-</span>
          </el-descriptions-item>

          <el-descriptions-item label="描述" :span="2">
            {{ safeName(detailItem?.description) }}
          </el-descriptions-item>

          <el-descriptions-item label="AI特征" :span="2">
            {{ safeName(detailItem?.aiTags) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

