import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'
import { getToken } from '@/utils/auth'

Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace

VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      return Promise.reject(err)
    }
  })
}

VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      return Promise.reject(err)
    }
  })
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/PatientLayout.vue'),
    redirect: '/login',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/patient/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/patient/Department.vue'),
        meta: { title: '科室列表' }
      },
      {
        path: 'department/:id',
        name: 'DepartmentDetail',
        component: () => import('@/views/patient/DepartmentDetail.vue'),
        meta: { title: '科室详情' }
      },
      {
        path: 'doctor',
        name: 'Doctor',
        component: () => import('@/views/patient/Doctor.vue'),
        meta: { title: '医生列表' }
      },
      {
        path: 'doctor/:id',
        name: 'DoctorDetail',
        component: () => import('@/views/patient/DoctorDetail.vue'),
        meta: { title: '医生详情' }
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/patient/Appointment.vue'),
        meta: { title: '预约挂号', requireAuth: true }
      },
      {
        path: 'my-orders',
        name: 'MyOrders',
        component: () => import('@/views/patient/MyOrders.vue'),
        meta: { title: '我的预约', requireAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/patient/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '后台首页' }
      },
      {
        path: 'department',
        name: 'AdminDepartment',
        component: () => import('@/views/admin/department/List.vue'),
        meta: { title: '科室管理' }
      },
      {
        path: 'doctor',
        name: 'AdminDoctor',
        component: () => import('@/views/admin/doctor/List.vue'),
        meta: { title: '医生管理' }
      },
      {
        path: 'schedule',
        name: 'AdminSchedule',
        component: () => import('@/views/admin/schedule/List.vue'),
        meta: { title: '排班管理' }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user/List.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'order',
        name: 'AdminOrder',
        component: () => import('@/views/admin/order/List.vue'),
        meta: { title: '订单管理' }
      }
    ]
  },
  {
    path: '/doctor',
    component: () => import('@/views/layout/DoctorLayout.vue'),
    redirect: '/doctor/dashboard',
    meta: { requireAuth: true, requireDoctor: true },
    children: [
      {
        path: 'dashboard',
        name: 'DoctorDashboard',
        component: () => import('@/views/doctor/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'schedule',
        name: 'DoctorSchedule',
        component: () => import('@/views/doctor/Schedule.vue'),
        meta: { title: '我的排班' }
      },
      {
        path: 'appointments',
        name: 'DoctorAppointments',
        component: () => import('@/views/doctor/Appointments.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'profile',
        name: 'DoctorProfile',
        component: () => import('@/views/doctor/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 医院预约挂号系统` : '医院预约挂号系统'
  
  const token = getToken()
  const whiteList = ['/login', '/register']
  const isWhiteList = whiteList.includes(to.path)
  
  if (!token) {
    if (isWhiteList) {
      next()
    } else {
      next('/login')
    }
    return
  }
  
  if (token && !store.state.user.userId) {
    try {
      await store.dispatch('user/getUserInfo')
    } catch (error) {
      store.commit('user/CLEAR_USER')
      next('/login')
      return
    }
  }
  
  if (isWhiteList) {
    const roleCode = store.getters.roleCode
    if (roleCode === 'admin') {
      next('/admin/dashboard')
    } else if (roleCode === 'doctor') {
      next('/doctor/dashboard')
    } else {
      next('/home')
    }
    return
  }
  
  const roleCode = store.getters.roleCode
  if (to.meta.requireAdmin && roleCode !== 'admin') {
    next('/home')
    return
  }
  if (to.meta.requireDoctor && roleCode !== 'doctor') {
    next('/home')
    return
  }
  
  next()
})

export default router
