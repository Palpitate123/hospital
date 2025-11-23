import axios from 'axios';

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8083', // 后端API基础路径
  timeout: 15000 // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['token'] = token;
    }
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;
    // 根据后端实际返回的状态码（200表示成功）
    if (res.code !== 200) {
      // 处理错误情况
      console.error('错误:', res.message || res.msg || '请求失败');
      return Promise.reject(new Error(res.message || res.msg || '请求失败'));
    }
    return res;
  },
  error => {
    console.error('响应错误:', error);
    return Promise.reject(error);
  }
);

export default service;
