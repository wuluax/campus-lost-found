<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-500 via-blue-600 to-indigo-700 flex flex-col relative overflow-hidden">
    <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-32 -mt-32" />
    <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/5 rounded-full -ml-20 -mb-20" />
    <div class="absolute top-1/3 left-0 w-20 h-20 bg-white/5 rounded-full -ml-10" />

    <div class="flex flex-col items-center pt-20 pb-8 relative z-10">
      <div class="w-20 h-20 bg-white/20 backdrop-blur-sm rounded-3xl flex items-center justify-center mb-5 shadow-lg shadow-black/10">
        <img :src="logoUrl" class="w-12 h-12">
      </div>
      <h1 class="text-2xl font-bold text-white m-0">智能校园失物招领</h1>
      <p class="text-white/60 text-sm mt-1">找回丢失物品，传递校园温暖</p>
    </div>

    <div class="flex-1 bg-[#f8f9fc] rounded-t-[32px] px-6 pt-8 pb-10 relative z-10">
      <van-form ref="formRef" @submit="handleSubmit">
        <div class="space-y-4 mb-8">
          <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
            <van-field
              v-model="formData.phone"
              name="phone" placeholder="手机号" type="tel" maxlength="11"
              :rules="formRules.phone"
              class="!py-4 !px-4" :border="false"
            >
              <template #left-icon>
                <div class="w-9 h-9 rounded-xl bg-blue-50 flex items-center justify-center mr-3">
                  <i class="i-ph:phone-bold text-blue-500 text-lg" />
                </div>
              </template>
            </van-field>
          </div>

          <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
            <van-field
              v-model="formData.password"
              :type="switchPassType ? 'password' : 'text'"
              name="password" placeholder="密码"
              :rules="formRules.password"
              class="!py-4 !px-4" :border="false"
              @click-right-icon="switchPassType = !switchPassType"
            >
              <template #left-icon>
                <div class="w-9 h-9 rounded-xl bg-blue-50 flex items-center justify-center mr-3">
                  <i class="i-ph:lock-bold text-blue-500 text-lg" />
                </div>
              </template>
              <template #right-icon>
                <i :class="switchPassType ? 'i-ph:eye-slash' : 'i-ph:eye'" class="text-xl text-gray-400" />
              </template>
            </van-field>
          </div>
        </div>

        <van-button
          type="primary" block round native-type="submit" :loading="loading"
          class="!h-12 !text-[16px] !font-bold shadow-lg shadow-blue-500/30"
        >
          登 录
        </van-button>

        <div class="mt-4 text-center">
          <span class="text-gray-400 text-sm">还没有账号？</span>
          <span class="text-blue-500 text-sm font-bold ml-1 cursor-pointer" @click="router.push('/register')">立即注册</span>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { showLoadingToast, showSuccessToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'
import logoUrl from '@/assets/logo.svg'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const switchPassType = ref(true)
const formData = reactive({ phone: '', password: '' })

const formRules = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^\d{11}$/, message: '请输入11位数字手机号' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度至少6位' }
  ]
}

async function handleSubmit() {
  try {
    loading.value = true
    showLoadingToast('登录中...')
    const result = await login({ phone: formData.phone, password: formData.password })
    userStore.setToken(result.token)
    userStore.setUserInfo(result.user)
    showSuccessToast('登录成功')
    const redirect = route.query?.redirect
    router.replace(redirect ? decodeURIComponent(redirect) : '/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>
