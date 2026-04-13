<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminMessages, deleteAdminMessage } from '@/api/message'
import { getAdminUsers } from '@/api/user'

const senderId = ref(null)
const receiverId = ref(null)
const content = ref('')

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(15)

const senderOptions = ref([])
const senderLoading = ref(false)
const receiverOptions = ref([])
const receiverLoading = ref(false)

function buildUserLabel(u) {
  const nickname = u?.nickname ? String(u.nickname) : ''
  const phone = u?.phone ? String(u.phone) : ''
  const id = u?.id ?? ''
  const base = nickname || phone ? `${nickname}${nickname && phone ? ' / ' : ''}${phone}` : String(id)
  return `${base} [${id}]`
}

async function fetchUserOptions(keyword, target) {
  const isLoading = target === 'sender' ? senderLoading : receiverLoading
  const options = target === 'sender' ? senderOptions : receiverOptions
  isLoading.value = true
  try {
    const data = await getAdminUsers({
      pageNo: 1,
      pageSize: 20,
      keyword: keyword || undefined,
    })
    const userList = Array.isArray(data?.list) ? data.list : []
    options.value = userList.map((u) => ({
      id: u.id,
      nickname: u.nickname,
      phone: u.phone,
    }))
  } finally {
    isLoading.value = false
  }
}

const queryParams = computed(() => ({
  pageNo: pageNo.value,
  pageSize: pageSize.value,
  senderId: senderId.value === null ? undefined : senderId.value,
  receiverId: receiverId.value === null ? undefined : receiverId.value,
  content: content.value || undefined,
}))

async function fetchList() {
  loading.value = true
  try {
    const data = await getAdminMessages(queryParams.value)
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
  senderId.value = null
  receiverId.value = null
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
  await ElMessageBox.confirm('确定要删除该私信吗？删除后不可恢复。', '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
  })
  await deleteAdminMessage(row.id)
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
        <span>私信管理</span>
        <div class="page-actions">
          <el-select
            v-model="senderId"
            filterable
            remote
            clearable
            :remote-method="(q) => fetchUserOptions(q, 'sender')"
            :loading="senderLoading"
            placeholder="发送者"
            style="width: 220px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option
              v-for="u in senderOptions"
              :key="u.id"
              :label="buildUserLabel(u)"
              :value="u.id"
            />
          </el-select>
          <el-select
            v-model="receiverId"
            filterable
            remote
            clearable
            :remote-method="(q) => fetchUserOptions(q, 'receiver')"
            :loading="receiverLoading"
            placeholder="接收者"
            style="width: 220px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option
              v-for="u in receiverOptions"
              :key="u.id"
              :label="buildUserLabel(u)"
              :value="u.id"
            />
          </el-select>
          <el-input
            v-model="content"
            placeholder="消息内容"
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
      <el-table-column prop="senderNickname" label="发送者" width="140" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.senderNickname || '-' }} <span class="text-gray-400 text-xs">[{{ row.senderId }}]</span>
        </template>
      </el-table-column>
      <el-table-column prop="receiverNickname" label="接收者" width="140" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.receiverNickname || '-' }} <span class="text-gray-400 text-xs">[{{ row.receiverId }}]</span>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="消息内容" min-width="300" align="center" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.content || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="isRead" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isRead === 1 ? 'success' : 'warning'" size="small">
            {{ row.isRead === 1 ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发送时间" width="180" align="center" />
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
