<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminCategory,
  deleteAdminCategory,
  getAdminCategories,
  updateAdminCategory,
} from '@/api/category'

const keyword = ref('')
const status = ref(null)

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(15)

const queryParams = computed(() => ({
  pageNo: pageNo.value,
  pageSize: pageSize.value,
  keyword: keyword.value || undefined,
  status: status.value === null ? undefined : status.value,
}))

async function fetchList() {
  loading.value = true
  try {
    const data = await getAdminCategories(queryParams.value)
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
  keyword.value = ''
  status.value = null
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

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const formRef = ref()
const formData = reactive({
  id: null,
  name: '',
  status: 1,
})

const isEdit = computed(() => formData.id !== null && formData.id !== undefined)
const dialogTitle = computed(() => (isEdit.value ? '编辑分类' : '新增分类'))

function openCreate() {
  formData.id = null
  formData.name = ''
  formData.status = 1
  dialogVisible.value = true
}

function openEdit(row) {
  formData.id = row.id
  formData.name = row.name || ''
  formData.status = row.status ?? 1
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formData.name || !formData.name.trim()) {
    ElMessage.error('请输入分类名称')
    return
  }
  dialogLoading.value = true
  try {
    const payload = { name: formData.name.trim(), status: formData.status }
    if (isEdit.value) {
      await updateAdminCategory(formData.id, payload)
      ElMessage.success('更新成功')
    } else {
      await createAdminCategory(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    dialogLoading.value = false
  }
}

async function handleToggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  const actionText = nextStatus === 0 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定要${actionText}该分类吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
  await updateAdminCategory(row.id, { status: nextStatus })
  ElMessage.success('操作成功')
  fetchList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该分类吗？删除后不可恢复。', '提示', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
  })
  await deleteAdminCategory(row.id)
  ElMessage.success('删除成功')
  if (list.value.length <= 1 && pageNo.value > 1) {
    pageNo.value = pageNo.value - 1
  }
  fetchList()
}

function statusTagType(rowStatus) {
  if (rowStatus === 1) return 'success'
  return 'info'
}

onMounted(fetchList)
</script>

<template>
  <el-card class="page-index">
    <template #header>
      <div class="page-header">
        <span>分类管理</span>
        <div class="page-actions">
          <el-select
            v-model="status"
            placeholder="状态"
            clearable
            style="width: 120px"
            @clear="onSearch"
            @change="onSearch"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-input
            v-model="keyword"
            placeholder="分类名称"
            clearable
            style="width: 200px"
            @clear="onSearch"
            @keyup.enter="onSearch"
          />
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button type="default" @click="onReset">重置/刷新</el-button>
          <el-button type="success" @click="openCreate">新增分类</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="120" align="center" />
      <el-table-column prop="name" label="分类名称" min-width="240" align="center" show-overflow-tooltip />
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
      <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
      <el-table-column label="操作" width="240" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
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
  </el-card>

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
    <el-form ref="formRef" label-width="90px">
      <el-form-item label="分类名称">
        <el-input v-model="formData.name" placeholder="请输入分类名称" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="formData.status" style="width: 100%">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="handleSubmit">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>
