<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { deleteUser, getAdminUsers, updateUserRole, updateUserStatus } from '@/api/user'

// 搜索关键字
const keyword = ref('')
// 列表加载状态
const loading = ref(false)
// 用户列表数据
const list = ref([])
// 数据总条数
const total = ref(0)
// 当前页码
const pageNo = ref(1)
// 每页大小
const pageSize = ref(10)

// 管理员角色筛选：null 表示不限，0-普通用户，1-管理员
const role = ref(null)
// 用户状态筛选：null 表示不限，0-封禁，1-正常
const status = ref(null)

// 组合分页、搜索与筛选参数
const queryParams = computed(() => ({
  pageNo: pageNo.value,
  pageSize: pageSize.value,
  keyword: keyword.value || undefined,
  // 角色筛选参数：为 null 时不传递该参数，避免影响查询
  role: role.value === null ? undefined : role.value,
  // 状态筛选参数：为 null 时不传递该参数，避免影响查询
  status: status.value === null ? undefined : status.value,
}))

// 加载用户列表数据
async function fetchList() {
  loading.value = true
  try {
    const data = await getAdminUsers(queryParams.value)
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
  keyword.value = ''
  role.value = null
  status.value = null
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

// 切换用户状态（正常 / 封禁）
async function handleToggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  const actionText = nextStatus === 0 ? '封禁' : '解封'
  await ElMessageBox.confirm(`确定要${actionText}该用户吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
  await updateUserStatus(row.id, nextStatus)
  ElMessage.success('操作成功')
  fetchList()
}

// 切换用户角色（普通用户 / 管理员）
async function handleToggleRole(row) {
  const nextRole = row.role === 1 ? 0 : 1
  const actionText = nextRole === 1 ? '设为管理员' : '取消管理员'
  await ElMessageBox.confirm(`确定要${actionText}吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
  await updateUserRole(row.id, nextRole)
  ElMessage.success('操作成功')
  fetchList()
}

function formatOptionalText(_row, _column, cellValue) {
  if (cellValue === null || cellValue === undefined) return '-'
  if (typeof cellValue === 'string' && cellValue.trim() === '') return '-'
  return cellValue
}

// 删除用户（管理端操作）
async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该用户吗？删除后不可恢复。', '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
  })
  await deleteUser(row.id)
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
        <span>用户管理</span>
        <div class="page-actions">
          <el-select
            v-model="role"
            placeholder="角色"
            clearable
            style="width: 120px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="0" />
          </el-select>
          <el-select
            v-model="status"
            placeholder="状态"
            clearable
            style="width: 120px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option label="正常" :value="1" />
            <el-option label="封禁" :value="0" />
          </el-select>
          <el-input
            v-model="keyword"
            placeholder="手机号/昵称/学号"
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
      <el-table-column prop="id" label="ID" width="120" align="center" />
      <el-table-column label="头像" width="120" align="center">
        <template #default="{ row }">
          <el-image
            :src="row.avatar || '/cat.jpeg'"
            :preview-src-list="[row.avatar || '/cat.jpeg']"
            fit="cover"
            class="user-avatar-thumb"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" width="180" align="center" show-overflow-tooltip />      
      <el-table-column prop="phone" label="手机号" width="160" align="center" />
      <el-table-column prop="studentNo" label="学号" width="160" align="center" show-overflow-tooltip :formatter="formatOptionalText" />
      <el-table-column prop="realName" label="真名" width="160" align="center" show-overflow-tooltip :formatter="formatOptionalText" />
      <el-table-column label="是否管理员" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.role === 1" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">正常</el-tag>
          <el-tag v-else type="danger">封禁</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="200" align="center" />
      <!--
      <el-table-column prop="updateTime" label="登录时间" width="180" align="center" />
      -->
      <el-table-column label="操作" width="280" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="warning" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '封禁' : '解封' }}
          </el-button>
          <el-button size="small" type="primary" @click="handleToggleRole(row)">
            {{ row.role === 1 ? '取消管理员' : '设为管理员' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
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
        :page-sizes="[10, 20, 50, 100]"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      />
    </div>
  </el-card>
</template>
