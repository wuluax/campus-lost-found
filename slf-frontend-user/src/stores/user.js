import { defineStore } from 'pinia'
export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('ACCESS-TOKEN') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('CURRENT-USER') || '{}'))
  
  // 计算属性
  const isLogin = computed(() => !!token.value)
  const userId = computed(() => userInfo.value.id)
  
  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('ACCESS-TOKEN', newToken)
    } else {
      localStorage.removeItem('ACCESS-TOKEN')
    }
  }
  
  const setUserInfo = (info) => {
    userInfo.value = info
    if (info && Object.keys(info).length > 0) {
      localStorage.setItem('CURRENT-USER', JSON.stringify(info))
    } else {
      localStorage.removeItem('CURRENT-USER')
    }
  }
  
  const logout = () => {
    setToken('')
    setUserInfo({})
  }
  
  const updateUserInfo = (newInfo) => {
    userInfo.value = { ...userInfo.value, ...newInfo }
    localStorage.setItem('CURRENT-USER', JSON.stringify(userInfo.value))
  }
  
  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLogin,
    userId,
    // 方法
    setToken,
    setUserInfo,
    logout,
    updateUserInfo
  }
})
