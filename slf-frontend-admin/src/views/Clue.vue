<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAdminClue, getAdminClues } from '@/api/clue'
import { getAdminUsers } from '@/api/user'

const itemId = ref('')
const userId = ref(null)
const content = ref('')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(15)

const userOptions = ref([])
const userLoading = ref(false)

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
    const list = Array.isArray(data?.list) ? data.list : []
    userOptions.value = list.map((u) => ({
      id: u.id,
      nickname: u.nickname,
      phone: u.phone,
    }))
  } finally {
    userLoading.value = false
  }
}

async function onUserRemoteSearch(query) {
  await fetchUserOptions(query)
}

const queryParams = computed(() => ({
  pageNo: pageNo.value,
  pageSize: pageSize.value,
  itemId: itemId.value ? Number(itemId.value) : undefined,
  userId: userId.value === null ? undefined : userId.value,
  content: content.value || undefined,
}))

async function fetchList() {
  loading.value = true
  try {
    const data = await getAdminClues(queryParams.value)
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
  itemId.value = ''
  userId.value = null
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
  await ElMessageBox.confirm('确定要删除该线索吗？删除后不可恢复。', '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
  })
  await deleteAdminClue(row.id)
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
        <span>线索管理</span>
        <div class="page-actions">
          <el-input
            v-model="itemId"
            placeholder="物品ID"
            clearable
            style="width: 140px"
            @clear="onSearch"
            @keyup.enter="onSearch"
          />
          <el-select
            v-model="userId"
            filterable
            remote
            clearable
            :remote-method="onUserRemoteSearch"
            :loading="userLoading"
            placeholder="选择用户"
            style="width: 240px"
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
          <el-input
            v-model="content"
            placeholder="线索内容"
            clearable
            style="width: 240px"
            @clear="onSearch"
            @keyup.enter="onSearch"
          />
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button type="default" @click="onReset">重置/刷新</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="itemId" label="物品ID" width="100" align="center" />
      <el-table-column prop="userNickname" label="用户昵称" width="160" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.userNickname || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="线索内容" min-width="320" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.content || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="180" align="center" />
      <el-table-column label="操作" width="120" align="center" fixed="right">
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
