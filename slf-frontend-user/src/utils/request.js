import axios from 'axios'
import { showFailToast, closeToast } from 'vant'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 获取token
    const userStore = useUserStore()
    const token = userStore.token
    
    // 如果存在token，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response
    
    // 统一处理响应
    if (data.code === 0) {
      return data.data
    } else {
      // 业务错误
      showFailToast(data.msg || '请求失败')
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
  },
  (error) => {
    closeToast()
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          showFailToast('登录已过期，请重新登录')
          break
        case 403:
          // 403 可能包含多种业务场景（如：账号封禁/权限不足等），优先展示后端返回的 msg
          showFailToast(data?.msg || '无权限访问')
          break
        case 404:
          showFailToast('请求的资源不存在')
          break
        case 500:
          showFailToast('服务器错误')
          break
        default:
          showFailToast(data?.msg || '请求失败')
      }
    } else if (error.request) {
      showFailToast('网络错误，请检查网络连接')
    } else {
      showFailToast('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default request
