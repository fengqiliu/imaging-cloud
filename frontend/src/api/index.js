import axios from 'axios'
import { Message } from 'element-ui'
import Cookies from 'js-cookie'
import router from '@/router'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 30000
})

// Request interceptor - add auth token
service.interceptors.request.use(
  config => {
    const token = Cookies.get('Admin-Token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor - handle errors
service.interceptors.response.use(
  response => {
    const res = response.data

    // Some endpoints return raw data directly without code wrapper
    if (res.code === undefined) {
      return res
    }

    if (res.code === 200) {
      return res.data !== undefined ? res.data : res
    }

    // Handle specific error codes
    if (res.code === 401) {
      Message.error('登录已过期，请重新登录')
      Cookies.remove('Admin-Token')
      router.push('/login')
      return Promise.reject(new Error('Unauthorized'))
    }

    if (res.code === 403) {
      Message.error('没有权限访问该资源')
      return Promise.reject(new Error('Forbidden'))
    }

    Message.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || 'Error'))
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          Message.error('登录已过期，请重新登录')
          Cookies.remove('Admin-Token')
          router.push('/login')
          break
        case 403:
          Message.error('没有权限访问该资源')
          break
        case 404:
          Message.error('请求的资源不存在')
          break
        case 500:
          Message.error('服务器错误')
          break
        default:
          Message.error(error.message || '网络错误')
      }
    } else {
      Message.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default service
