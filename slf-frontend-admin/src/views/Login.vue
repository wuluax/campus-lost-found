<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { adminLogin } from '@/api/user'
import { setAuth } from '@/utils/auth'

const router = useRouter()

const formRef = ref()
const form = ref({
  phone: '',
  password: '',
})
const loading = ref(false)

const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await adminLogin(form.value)
    setAuth(data?.token, data?.user)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="h-full flex items-center justify-center bg-[linear-gradient(135deg,#ecf5ff_0%,#f9fafc_100%)]">
    <div class="w-[360px] px-7 py-8 rounded-[12px] bg-white shadow-[0_8px_24px_rgba(0,0,0,0.08)]">
      <h1 class="m-0 mb-4 text-[20px] font-600 text-center">智能校园失物招领管理系统</h1>
      <p class="m-0 mb-6 text-[13px] text-center text-[#909399]">请使用管理员账号登录</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" show-password clearable />
        </el-form-item>
        <el-button type="primary" class="w-full" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>
