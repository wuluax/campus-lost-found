<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminNotifications, deleteAdminNotification } from '@/api/notification'
import { getAdminUsers } from '@/api/user'

const userId = ref(null)
const type = ref('')
const content = ref('')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(15)

const userOptions = ref([])
const userLoading = ref(false)

const typeOptions = [
  { label: '新线索', value: 'clue' },
  { label: '被收藏', value: 'favorite' },
  { label: '新私信', value: 'message' },
  { label: '系统通知', value: 'system' },
]

function buildUserLabel(u) {
  const nickname = u?.nickname ? String(u.nickname) : ''
  const phone = u?.phone ? String(u.phone) : ''
  const id = u?.id ?? ''
  const base = nickname || phone ? `${nickname}${nickname && phone ? ' / ' : ''}${phone}` : String(id)
  return `${base} [${id}]`
}

async function fetchUserOptions(keyword) {
  userLoading.value = true
  try {
    const data = await getAdminUsers({
      pageNo: 1,
      pageSize: 20,
      keyword: keyword || undefined,
    })
    const userList = Array.isArray(data?.list) ? data.list : []
    userOptions.value = userList.map((u) => ({
      id: u.id,
      nickname: u.nickname,
      phone: u.phone,
    }))
  } finally {
    userLoading.value = false
  }
}

function typeLabel(val) {
  const map = { clue: '新线索', favorite: '被收藏', message: '新私信', system: '系统通知' }
  return map[val] || val
}

function typeTagType(val) {
  const map = { clue: '', favorite: 'warning', message: 'success', system: 'info' }
  return map[val] || 'info'
}

const queryParams = computed(() => ({
  pageNo: pageNo.value,
  pageSize: pageSize.value,
  userId: userId.value === null ? undefined : userId.value,
  type: type.value || undefined,
  content: content.value || undefined,
}))

async function fetchList() {
  loading.value = true
  try {
    const data = await getAdminNotifications(queryParams.value)
    list.value = data?.list || []
    total.value = Number(data?.total || 0)
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNo.value = 1
  fetchList()
}

function onReset() {
  userId.value = null
  type.value = ''
  content.value = ''
  pageNo.value = 1
  pageSize.value = 15
  fetchList()
}

function onSizeChange(size) {
  pageSize.value = size
  pageNo.value = 1
  fetchList()
}

function onCurrentChange(no) {
  pageNo.value = no
  fetchList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该通知吗？删除后不可恢复。', '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
  })
  await deleteAdminNotification(row.id)
  ElMessage.success('删除成功')
  if (list.value.length <= 1 && pageNo.value > 1) {
    pageNo.value = pageNo.value - 1
  }
  fetchList()
}

onMounted(fetchList)
</script>

<template>
  <el-card class="page-index">
    <template #header>
      <div class="page-header">
        <span>通知管理</span>
        <div class="page-actions">
          <el-select
            v-model="userId"
            filterable
            remote
            clearable
            :remote-method="fetchUserOptions"
            :loading="userLoading"
            placeholder="接收用户"
            style="width: 220px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option
              v-for="u in userOptions"
              :key="u.id"
              :label="buildUserLabel(u)"
              :value="u.id"
            />
          </el-select>
          <el-select
            v-model="type"
            clearable
            placeholder="通知类型"
            style="width: 140px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option
              v-for="t in typeOptions"
              :key="t.value"
              :label="t.label"
              :value="t.value"
            />
          </el-select>
          <el-input
            v-model="content"
            placeholder="通知内容"
            clearable
            style="width: 220px"
            @clear="onSearch"
            @keyup.enter="onSearch"
          />
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button type="default" @click="onReset">重置/刷新</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="userNickname" label="接收者" width="130" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.userNickname || '-' }} <span class="text-gray-400 text-xs">[{{ row.userId }}]</span>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="typeTagType(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200" align="center" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="220" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.content || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="senderNickname" label="触发者" width="120" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.senderNickname || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="isRead" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isRead === 1 ? 'success' : 'warning'" size="small">
            {{ row.isRead === 1 ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" align="center" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
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
  </el-card>
</template>
