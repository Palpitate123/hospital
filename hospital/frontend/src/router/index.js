import Vue from 'vue'
import Router from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import UserInfo from '../views/UserInfo.vue'
import DepartmentList from '../views/DepartmentList.vue'
import DoctorList from '../views/DoctorList.vue'
import Appointment from '../views/Appointment.vue'
import AppointmentList from '../views/AppointmentList.vue'
import MedicalGuide from '../views/MedicalGuide.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'
import AdminUserManagement from '../views/admin/UserManagement.vue'
import AdminDepartmentManagement from '../views/admin/DepartmentManagement.vue'
import AdminDoctorManagement from '../views/admin/DoctorManagement.vue'
import AdminAppointmentManagement from '../views/admin/AppointmentManagement.vue'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      name: 'Home',
      component: Home,
      meta: { requiresAuth: false }
    },
    {
      path: '/user-info',
      name: 'UserInfo',
      component: UserInfo,
      meta: { requiresAuth: true }
    },
    {
      path: '/departments',
      name: 'DepartmentList',
      component: DepartmentList,
      meta: { requiresAuth: false }
    },
    {
      path: '/doctors',
      name: 'DoctorList',
      component: DoctorList,
      meta: { requiresAuth: false }
    },
    {
      path: '/appointment',
      name: 'Appointment',
      component: Appointment,
      meta: { requiresAuth: true }
    },
    {
      path: '/appointment-list',
      name: 'AppointmentList',
      component: AppointmentList,
      meta: { requiresAuth: true }
    },
    {
      path: '/medical-guide',
      name: 'MedicalGuide',
      component: MedicalGuide,
      meta: { requiresAuth: false }
    },
    // 管理员路由
    {
      path: '/admin/dashboard',
      name: 'AdminDashboard',
      component: AdminDashboard,
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/admin/users',
      name: 'AdminUserManagement',
      component: AdminUserManagement,
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/admin/departments',
      name: 'AdminDepartmentManagement',
      component: AdminDepartmentManagement,
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/admin/doctors',
      name: 'AdminDoctorManagement',
      component: AdminDoctorManagement,
      meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
      path: '/admin/appointments',
      name: 'AdminAppointmentManagement',
      component: AdminAppointmentManagement,
      meta: { requiresAuth: true, role: 'ADMIN' }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user'))
  
  // 不需要认证的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }
  
  // 需要认证但没有token，跳转到登录
  if (!token) {
    next('/login')
    return
  }
  
  // 需要特定角色的页面
  if (to.meta.role && user && user.role !== to.meta.role) {
    next('/home')
    return
  }
  
  next()
})

export default router