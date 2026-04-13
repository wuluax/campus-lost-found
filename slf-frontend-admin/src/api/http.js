import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { getToken, logout } from '@/utils/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data && data.code === 0) {
      return data.data
    }
    ElMessage.error(data?.msg || '请求失败')
    return Promise.reject(new Error(data?.msg || '请求失败'))
  },
  (error) => {
    if (error?.response?.status === 401) {
      logout()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(error)
    }
    ElMessage.error(error?.response?.data?.msg || error?.message || '请求失败')
    return Promise.reject(error)
  },
)

export default request
