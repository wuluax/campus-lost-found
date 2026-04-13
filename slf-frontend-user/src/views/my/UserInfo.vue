<template>
  <div class="min-h-screen bg-[#F2F3F5] pb-20">
    <NavBar title="个人信息" class="!bg-white/90 backdrop-blur-md sticky top-0 z-50 border-b border-gray-100" />

    <div class="h-4"></div>
    <div class="px-5 mb-2 flex items-center text-xs font-bold text-gray-500 tracking-wide">
      基本资料
    </div>

    <van-cell-group inset class="!mx-4 !rounded-2xl !shadow-sm overflow-hidden mb-6">
      
      <van-field
        label="头像"
        label-class="!font-medium text-gray-700"
        input-align="right"
        center
        is-link
        readonly
        class="!py-4 items-center"
        @click="showAvatarUpload = true"
      >
        <template #left-icon>
          <div class="mr-3 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-50 text-gray-600">
            <i class="i-ph:image-square-bold text-lg" />
          </div>
        </template>
        <template #input>
          <div class="flex w-full">
            <van-uploader
              v-model="avatarFile"
              :max-count="1"
              :max-size="2 * 1024 * 1024"
              :before-read="beforeAvatarRead"
              :after-read="afterAvatarRead"
              @oversize="onAvatarOversize"
              @delete="onAvatarDelete"
              class="!ml-auto"
            >
              <template #default>
                <div class="relative group cursor-pointer">
                  <van-image
                    class="h-14 w-14 border-2 border-white shadow-md group-active:scale-95 transition-all duration-200"
                    round
                    fit="cover"
                    :src="userInfo.avatar || '/cat.jpeg'"
                  />
                </div>
              </template>
            </van-uploader>
          </div>
        </template>
      </van-field>

      <van-field
        v-model="userInfo.nickname"
        label="昵称"
        readonly
        label-class="!font-medium text-gray-700"
        input-align="right"
        center
        is-link
        class="!py-4"
        @click="openNicknamePopup"
      >
        <template #left-icon>
          <div class="mr-3 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-50 text-gray-600">
            <i class="i-ph:text-t-bold text-lg" />
          </div>
        </template>
      </van-field>

      <van-field
        v-model="afterPhone"
        label="手机号"
        readonly
        is-link
        label-class="!font-medium text-gray-700"
        input-align="right"
        center
        class="!py-4"
        @click="openPhonePopup"
      >
        <template #left-icon>
          <div class="mr-3 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-50 text-gray-600">
            <i class="i-ph:device-mobile-bold text-lg" />
          </div>
        </template>
      </van-field>

      <van-field
        label="安全设置"
        model-value="修改登录密码"
        readonly
        label-class="!font-medium text-gray-700"
        input-align="right"
        center
        is-link
        class="!py-4"
        @click="openPasswordPopup"
      >
        <template #left-icon>
          <div class="mr-3 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-50 text-gray-600">
            <i class="i-ph:lock-key-bold text-lg" />
          </div>
        </template>
      </van-field>
    </van-cell-group>

    <div class="px-5 mb-2 mt-6 flex items-center text-xs font-bold text-gray-500 tracking-wide">
        身份认证 (AI识别用)
    </div>

    <van-cell-group inset class="!mx-4 !rounded-2xl !shadow-sm overflow-hidden">
      <van-field
        v-model="userInfo.studentNo"
        label="学号"
        readonly
        label-class="!font-medium text-gray-700"
        input-align="right"
        center
        is-link
        class="!py-4"
        @click="openStudentNoPopup"
      >
        <template #left-icon>
          <div class="mr-3 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-50 text-gray-600">
             <i class="i-ph:identification-card-bold text-lg" />
          </div>
        </template>
      </van-field>

      <van-field
        v-model="userInfo.realName"
        label="真实姓名"
        readonly
        label-class="!font-medium text-gray-700"
        input-align="right"
        center
        is-link
        :border="false"
        class="!py-4"
        @click="openRealNamePopup"
      >
        <template #left-icon>
          <div class="mr-3 flex h-9 w-9 items-center justify-center rounded-lg bg-gray-50 text-gray-600">
             <i class="i-ph:user-list-bold text-lg" />
          </div>
        </template>
      </van-field>
    </van-cell-group>

    <van-popup v-model:show="showNicknamePopup" position="bottom" round class="!rounded-t-3xl">
      <div class="px-6 pt-4 pb-8">
        <div class="flex justify-center mb-6">
            <div class="w-10 h-1 bg-gray-200 rounded-full"></div>
        </div>
        <div class="text-xl font-bold text-gray-800 mb-4">修改昵称</div>
        
        <van-form ref="nicknameFormRef">
            <van-field
              v-model="nicknameInput"
              name="nickname"
              placeholder="请输入新昵称 (2-12位)"
              :rules="[
                { required: true, message: '请输入昵称' },
                { validator: validateNickname(), trigger: 'onChange' }
              ]"
              class="!bg-gray-50 !rounded-xl !px-4 !py-3 mb-8 !border-0"
              :border="false"
            />
        </van-form>
        
        <div class="flex gap-3">
           <van-button class="!bg-gray-100 !border-none !text-gray-600 !h-11 !rounded-xl flex-1 font-medium" @click="showNicknamePopup = false">
             取消
           </van-button>
           <van-button class="!h-11 !rounded-xl flex-[2] !font-bold shadow-lg shadow-blue-500/20" color="#3b82f6" :loading="savingNickname" @click="submitNickname">
             保存修改
           </van-button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showStudentNoPopup" position="bottom" round class="!rounded-t-3xl">
      <div class="px-6 pt-4 pb-8">
         <div class="flex justify-center mb-6">
            <div class="w-10 h-1 bg-gray-200 rounded-full"></div>
        </div>
        <div class="text-xl font-bold text-gray-800 mb-4">绑定学号</div>

        <van-form ref="studentNoFormRef">
            <van-field
              v-model="studentNoInput"
              name="studentNo"
              type="digit"
              placeholder="请输入10位学号"
              :rules="[
                { required: true, message: '请输入学号' },
                { pattern: /^\d{10}$/, message: '学号必须为10位数字' }
              ]"
              class="!bg-gray-50 !rounded-xl !px-4 !py-3 mb-8 !border-0"
              :border="false"
            />
        </van-form>
        <div class="flex gap-3">
          <van-button class="!bg-gray-100 !border-none !text-gray-600 !h-11 !rounded-xl flex-1 font-medium" @click="showStudentNoPopup = false">取消</van-button>
          <van-button class="!h-11 !rounded-xl flex-[2] !font-bold shadow-lg shadow-indigo-500/20" color="#6366f1" :loading="savingStudentNo" @click="submitStudentNo">保存</van-button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showRealNamePopup" position="bottom" round class="!rounded-t-3xl">
      <div class="px-6 pt-4 pb-8">
        <div class="flex justify-center mb-6">
            <div class="w-10 h-1 bg-gray-200 rounded-full"></div>
        </div>
        <div class="text-xl font-bold text-gray-800 mb-4">实名认证</div>

        <van-form ref="realNameFormRef">
            <van-field
              v-model="realNameInput"
              name="realName"
              placeholder="请输入您的真实姓名"
              :rules="[
                { required: true, message: '请输入真名' },
                { validator: validateRealName(), trigger: 'onChange' }
              ]"
              class="!bg-gray-50 !rounded-xl !px-4 !py-3 mb-8 !border-0"
              :border="false"
            />
        </van-form>
        <div class="flex gap-3">
          <van-button class="!bg-gray-100 !border-none !text-gray-600 !h-11 !rounded-xl flex-1 font-medium" @click="showRealNamePopup = false">取消</van-button>
          <van-button class="!h-11 !rounded-xl flex-[2] !font-bold shadow-lg shadow-teal-500/20" color="#14b8a6" :loading="savingRealName" @click="submitRealName">保存</van-button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showPhonePopup" position="bottom" round class="!rounded-t-3xl">
      <div class="px-6 pt-4 pb-8">
        <div class="flex justify-center mb-6">
            <div class="w-10 h-1 bg-gray-200 rounded-full"></div>
        </div>
        <div class="text-xl font-bold text-gray-800 mb-4">更换手机号</div>

        <van-form ref="phoneFormRef">
          <div class="bg-gray-50 rounded-xl p-1 mb-8 overflow-hidden">
            <van-field
              v-model="phoneInput"
              name="phone"
              type="tel"
              placeholder="请输入新手机号"
              maxlength="11"
              :rules="[
                { required: true, message: '请输入手机号' },
                { pattern: /^\d{11}$/, message: '请输入11位数字手机号' }
              ]"
              class="!bg-transparent !py-3"
              :border="false"
            >
              <template #left-icon>
                <i class="i-ph:phone text-gray-400 text-lg mr-2" />
              </template>
            </van-field>

            <div class="h-[1px] bg-gray-200 mx-4"></div>

            <van-field
              v-model="phonePassword"
              name="password"
              type="password"
              placeholder="请输入当前登录密码"
              :rules="[{ required: true, message: '请输入登录密码' }]"
              class="!bg-transparent !py-3"
              :border="false"
            >
              <template #left-icon>
                <i class="i-ph:lock-simple text-gray-400 text-lg mr-2" />
              </template>
            </van-field>
          </div>
        </van-form>

        <div class="flex gap-3">
          <van-button class="!bg-gray-100 !border-none !text-gray-600 !h-11 !rounded-xl flex-1 font-medium" @click="showPhonePopup = false">取消</van-button>
          <van-button class="!h-11 !rounded-xl flex-[2] !font-bold shadow-lg shadow-emerald-500/20" color="#10b981" :loading="savingPhone" @click="submitPhone">确认更换</van-button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showPasswordPopup" position="bottom" round class="!rounded-t-3xl">
      <div class="px-6 pt-4 pb-8">
        <div class="flex justify-center mb-6">
            <div class="w-10 h-1 bg-gray-200 rounded-full"></div>
        </div>
        <div class="text-xl font-bold text-gray-800 mb-4">修改密码</div>

        <van-form ref="passwordFormRef">
          <div class="bg-gray-50 rounded-xl p-1 mb-8">
            <van-field
              v-model="oldPassword"
              name="oldPassword"
              :type="switchOldPassType ? 'password' : 'text'"
              placeholder="请输入原密码"
              :rules="[{ required: true, message: '请输入原密码' }]"
              class="!bg-transparent !py-3"
              :border="false"
            >
               <template #left-icon>
                <i class="i-ph:lock-open text-gray-400 text-lg mr-2" />
              </template>
              <template #right-icon>
                <i :class="switchOldPassType ? 'i-ph:eye-slash' : 'i-ph:eye'" class="text-gray-400 cursor-pointer" @click="switchOldPassType = !switchOldPassType" />
              </template>
            </van-field>

            <div class="h-[1px] bg-gray-200 mx-4"></div>

            <van-field
              v-model="newPassword"
              name="newPassword"
              :type="switchNewPassType ? 'password' : 'text'"
              placeholder="请输入新密码 (建议包含字母和数字)"
              :rules="[{ required: true, message: '请输入新密码' }]"
              class="!bg-transparent !py-3"
              :border="false"
            >
               <template #left-icon>
                <i class="i-ph:lock-key text-gray-400 text-lg mr-2" />
              </template>
              <template #right-icon>
                <i :class="switchNewPassType ? 'i-ph:eye-slash' : 'i-ph:eye'" class="text-gray-400 cursor-pointer" @click="switchNewPassType = !switchNewPassType" />
              </template>
            </van-field>

            <div class="h-[1px] bg-gray-200 mx-4"></div>

            <van-field
              v-model="confirmPassword"
              name="confirmPassword"
              :type="switchConfirmPassType ? 'password' : 'text'"
              placeholder="请再次输入新密码"
              :rules="[
                { required: true, message: '请再次输入新密码' },
                { validator: validateConfirmPassword(), trigger: 'onChange' }
              ]"
              class="!bg-transparent !py-3"
              :border="false"
            >
               <template #left-icon>
                <i class="i-ph:check-circle text-gray-400 text-lg mr-2" />
              </template>
              <template #right-icon>
                <i :class="switchConfirmPassType ? 'i-ph:eye-slash' : 'i-ph:eye'" class="text-gray-400 cursor-pointer" @click="switchConfirmPassType = !switchConfirmPassType" />
              </template>
            </van-field>
          </div>
        </van-form>

        <div class="flex gap-3">
          <van-button class="!bg-gray-100 !border-none !text-gray-600 !h-11 !rounded-xl flex-1 font-medium" @click="showPasswordPopup = false">取消</van-button>
          <van-button class="!h-11 !rounded-xl flex-[2] !font-bold shadow-lg shadow-orange-500/20" color="#f97316" :loading="savingPassword" @click="submitPassword">确认修改</van-button>
        </div>
      </div>
    </van-popup>

  </div>
</template>

<script setup>
import { showToast, showSuccessToast, showFailToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, updatePhone, updatePassword } from '@/api/user'
import { uploadAvatar } from '@/api/common'
import NavBar from '@/layout/NavBar.vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const showAvatarUpload = ref(false) 
const avatarFile = ref([])

// 底部弹窗与输入值状态
const showNicknamePopup = ref(false)
const showStudentNoPopup = ref(false)
const showRealNamePopup = ref(false)

const nicknameInput = ref('')
const studentNoInput = ref('')
const realNameInput = ref('')

const nicknameFormRef = ref()
const studentNoFormRef = ref()
const realNameFormRef = ref()

const savingNickname = ref(false)
const savingStudentNo = ref(false)
const savingRealName = ref(false)

const afterPhone = computed(() => userInfo.value.phone || '')

const showPhonePopup = ref(false)
const showPasswordPopup = ref(false)

const phoneFormRef = ref()
const passwordFormRef = ref()

const phoneInput = ref('')
const phonePassword = ref('')

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const switchOldPassType = ref(true)
const switchNewPassType = ref(true)
const switchConfirmPassType = ref(true)

const savingPhone = ref(false)
const savingPassword = ref(false)

// 头像上传前检查
const beforeAvatarRead = (file) => {
  if (file.type.indexOf('image/') !== 0) {
    showToast('请上传图片文件')
    return false
  }
  if (file.size > 2 * 1024 * 1024) {
    showToast('头像大小不能超过2MB')
    return false
  }
  return true
}

const onAvatarOversize = () => {
  showFailToast('头像大小不能超过2MB')
}

// 头像上传后处理
const afterAvatarRead = async (file) => {
  try {
    file.status = 'uploading'
    file.message = '上传中'
    
    const avatarUrl = await uploadAvatar(file.file)
    await updateUserInfo({ avatar: avatarUrl })
    userStore.updateUserInfo({ avatar: avatarUrl })
    
    file.status = 'done'
    showSuccessToast('头像上传成功')
    avatarFile.value = [] 
  } catch (error) {
    console.error('头像上传失败:', error)
    file.status = 'failed'
    file.message = '失败'
    showToast('头像上传失败')
  }
}

const onAvatarDelete = () => {
  avatarFile.value = []
}

const openNicknamePopup = () => {
  nicknameInput.value = userInfo.value.nickname || ''
  showNicknamePopup.value = true
}
const openStudentNoPopup = () => {
  studentNoInput.value = userInfo.value.studentNo || ''
  showStudentNoPopup.value = true
}
const openRealNamePopup = () => {
  realNameInput.value = userInfo.value.realName || ''
  showRealNamePopup.value = true
}

const openPhonePopup = () => {
  phoneInput.value = ''
  phonePassword.value = ''
  showPhonePopup.value = true
}

const openPasswordPopup = () => {
  oldPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  showPasswordPopup.value = true
}

const validateConfirmPassword = () => {
  return async (value) => {
    if (value !== newPassword.value) {
      return Promise.resolve('两次输入密码不一致')
    }
    return Promise.resolve(true)
  }
}

function validateNickname() {
  return async (value) => {
    const pattern = /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/
    if (!pattern.test(value)) {
      return Promise.resolve('只能包含汉字、字母、数字和下划线')
    }
    if (value.length < 2 || value.length > 30) {
      return Promise.resolve('长度需在2-30位之间')
    }
    return Promise.resolve(true)
  }
}

function validateRealName() {
  return async (value) => {
    const pattern = /^[\u4e00-\u9fa5]+$/
    if (!pattern.test(value)) {
      return Promise.resolve('必须为纯中文')
    }
    if (value.length < 2) {
      return Promise.resolve('至少2位')
    }
    return Promise.resolve(true)
  }
}

const submitNickname = async () => {
  try {
    await nicknameFormRef.value?.validate()
    savingNickname.value = true
    await updateUserInfo({ nickname: nicknameInput.value })
    userStore.updateUserInfo({ nickname: nicknameInput.value })
    showSuccessToast('昵称修改成功')
    showNicknamePopup.value = false
  } catch (error) {
    console.error('修改昵称失败:', error)
    if (error?.name !== 'ValidationError') {
        showFailToast('修改失败')
    }
  } finally {
    savingNickname.value = false
  }
}

const submitStudentNo = async () => {
  try {
    await studentNoFormRef.value?.validate()
    savingStudentNo.value = true
    await updateUserInfo({ studentNo: studentNoInput.value })
    userStore.updateUserInfo({ studentNo: studentNoInput.value })
    showSuccessToast('学号修改成功')
    showStudentNoPopup.value = false
  } catch (error) {
    console.error('修改学号失败:', error)
    if (error?.name !== 'ValidationError') {
        showFailToast('修改失败')
    }
  } finally {
    savingStudentNo.value = false
  }
}

const submitRealName = async () => {
  try {
    await realNameFormRef.value?.validate()
    savingRealName.value = true
    await updateUserInfo({ realName: realNameInput.value })
    userStore.updateUserInfo({ realName: realNameInput.value })
    showSuccessToast('真名修改成功')
    showRealNamePopup.value = false
  } catch (error) {
    console.error('修改真名失败:', error)
    if (error?.name !== 'ValidationError') {
        showFailToast('修改失败')
    }
  } finally {
    savingRealName.value = false
  }
}

const submitPhone = async () => {
  try {
    await phoneFormRef.value?.validate()
    savingPhone.value = true
    await updatePhone({ phone: phoneInput.value, password: phonePassword.value })
    userStore.updateUserInfo({ phone: phoneInput.value })
    showSuccessToast('手机号修改成功')
    showPhonePopup.value = false
  } catch (error) {
    console.error('修改手机号失败:', error)
    if (error?.name !== 'ValidationError') {
      showFailToast('修改失败')
    }
  } finally {
    savingPhone.value = false
  }
}

const submitPassword = async () => {
  try {
    await passwordFormRef.value?.validate()
    savingPassword.value = true
    await updatePassword({ oldPassword: oldPassword.value, newPassword: newPassword.value })
    showSuccessToast('密码修改成功')
    showPasswordPopup.value = false
  } catch (error) {
    console.error('修改密码失败:', error)
    if (error?.name !== 'ValidationError') {
      showFailToast('修改失败')
    }
  } finally {
    savingPassword.value = false
  }
}

onMounted(() => {
  nicknameInput.value = userInfo.value.nickname || ''
  studentNoInput.value = userInfo.value.studentNo || ''
  realNameInput.value = userInfo.value.realName || ''
})
</script>
