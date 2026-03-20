import axios from 'axios'
import { Message } from 'element-ui'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  timeout: 15000
})

service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message({
        message: res.message || '系统异常，请稍后再试',
        type: 'error',
        duration: 3000
      })
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '系统异常'))
    }
    return res
  },
  error => {
    console.error('Response error:', error)
    let message = '系统异常，请稍后再试'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '登录已过期，请重新登录'
          removeToken()
          router.push('/login')
          break
        case 403:
          message = '没有权限访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || '系统异常'
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时，请稍后再试'
    } else if (error.message.includes('Network')) {
      message = '网络连接异常，请检查网络'
    }
    Message({
      message,
      type: 'error',
      duration: 3000
    })
    return Promise.reject(error)
  }
)

export default service
