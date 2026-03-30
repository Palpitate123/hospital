import axios from 'axios'
import { Message } from 'element-ui'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

// 【核心强制指定】后端的完整地址，绝对不会再请求到8081
const BASE_API = 'http://localhost:8080/api'

const service = axios.create({
  baseURL: BASE_API, // 强制使用完整后端地址
  timeout: 15000,
  // 强制不跟随前端地址，确保请求到后端
  withCredentials: false
})

// 请求拦截器
service.interceptors.request.use(
    config => {
      // 【强制打印请求地址】F12控制台可以看到最终请求的完整地址，方便排查
      console.log('最终请求地址：', BASE_API + config.url)
      const token = getToken()
      if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
      }
      return config
    },
    error => {
      console.error('请求错误:', error)
      return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
      const res = response.data
      if (res.code !== 200) {
        Message({
          message: res.message || '请求失败',
          type: 'error',
          duration: 3000
        })
        if (res.code === 401) {
          removeToken()
          router.push('/login')
        }
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res
    },
    error => {
      console.error('响应错误:', error)
      Message({
        message: error.message || '网络错误',
        type: 'error',
        duration: 3000
      })
      return Promise.reject(error)
    }
)

export default service
