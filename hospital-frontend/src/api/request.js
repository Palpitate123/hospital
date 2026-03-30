import axios from 'axios'
import { Message } from 'element-ui'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const service = axios.create({
    // 【核心修改】必须写后端的完整地址：http://localhost:8080/api
    // 不能只写 /api，否则会请求到前端自己的8081端口
    baseURL: 'http://localhost:8080/api',
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
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

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
