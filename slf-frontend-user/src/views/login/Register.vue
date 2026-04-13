<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-500 via-blue-600 to-indigo-700 flex flex-col relative overflow-hidden">
    <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-32 -mt-32" />
    <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/5 rounded-full -ml-20 -mb-20" />

    <div class="flex flex-col items-center pt-16 pb-6 relative z-10">
      <div class="w-16 h-16 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center mb-4 shadow-lg shadow-black/10">
        <img :src="logoUrl" class="w-10 h-10">
      </div>
      <h1 class="text-xl font-bold text-white m-0">创建账号</h1>
      <p class="text-white/60 text-sm mt-1">加入校园失物招领平台</p>
    </div>

    <div class="flex-1 bg-[#f8f9fc] rounded-t-[32px] px-6 pt-8 pb-10 relative z-10">
      <van-form ref="formRef" @submit="handleRegister">
        <div class="space-y-3 mb-8">
          <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
            <van-field v-model="formData.phone" name="phone" placeholder="手机号" type="tel" maxlength="11"
              :rules="formRules.phone" class="!py-3.5 !px-4" :border="false">
              <template #left-icon>
                <div class="w-8 h-8 rounded-lg bg-blue-50 flex items-center justify-center mr-3">
                  <i class="i-ph:phone-bold text-blue-500" />
                </div>
              </template>
            </van-field>
          </div>
          <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
            <van-field v-model="formData.nickname" name="nickname" placeholder="昵称"
              :rules="formRules.nickname" class="!py-3.5 !px-4" :border="false">
              <template #left-icon>
                <div class="w-8 h-8 rounded-lg bg-emerald-50 flex items-center justify-center mr-3">
                  <i class="i-ph:user-bold text-emerald-500" />
                </div>
              </template>
            </van-field>
          </div>
          <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
            <van-field v-model="formData.password" :type="switchPassType ? 'password' : 'text'"
              name="password" placeholder="密码（至少6位）" :rules="formRules.password"
              class="!py-3.5 !px-4" :border="false" @click-right-icon="switchPassType = !switchPassType">
              <template #left-icon>
                <div class="w-8 h-8 rounded-lg bg-violet-50 flex items-center justify-center mr-3">
                  <i class="i-ph:lock-bold text-violet-500" />
                </div>
              </template>
              <template #right-icon>
                <i :class="switchPassType ? 'i-ph:eye-slash' : 'i-ph:eye'" class="text-xl text-gray-400" />
              </template>
            </van-field>
          </div>
          <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
            <van-field v-model="formData.confirmPassword" :type="switchConfirmPassType ? 'password' : 'text'"
              name="confirmPassword" placeholder="确认密码" :rules="formRules.confirmPassword"
              class="!py-3.5 !px-4" :border="false" @click-right-icon="switchConfirmPassType = !switchConfirmPassType">
              <template #left-icon>
                <div class="w-8 h-8 rounded-lg bg-amber-50 flex items-center justify-center mr-3">
                  <i class="i-ph:check-circle-bold text-amber-500" />
                </div>
              </template>
              <template #right-icon>
                <i :class="switchConfirmPassType ? 'i-ph:eye-slash' : 'i-ph:eye'" class="text-xl text-gray-400" />
              </template>
            </van-field>
          </div>
        </div>

        <van-button type="primary" block round native-type="submit" :loading="loading"
          class="!h-12 !text-[16px] !font-bold shadow-lg shadow-blue-500/30">
          注 册
        </van-button>

        <div class="mt-4 text-center">
          <span class="text-gray-400 text-sm">已有账号？</span>
          <span class="text-blue-500 text-sm font-bold ml-1 cursor-pointer" @click="router.push('/login')">返回登录</span>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { showLoadingToast, showSuccessToast } from 'vant'
import { register } from '@/api/user'
import logoUrl from '@/assets/logo.svg'

const router = useRouter()
const loading = ref(false)
const formData = reactive({ phone: '', nickname: '', password: '', confirmPassword: '' })
const switchPassType = ref(true)
const switchConfirmPassType = ref(true)

const formRules = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^\d{11}$/, message: '请输入11位数字手机号' }
  ],
  nickname: [
    { required: true, message: '请输入昵称' },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/, message: '昵称只能包含汉字、字母、数字和下划线' },
    { min: 2, max: 30, message: '昵称长度2-30位' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度至少6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: (value) => value === formData.password, message: '两次输入的密码不一致' }
  ]
}

async function handleRegister() {
  try {
    loading.value = true
    showLoadingToast('注册中...')
    await register({ phone: formData.phone, nickname: formData.nickname, password: formData.password })
    showSuccessToast('注册成功')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>
