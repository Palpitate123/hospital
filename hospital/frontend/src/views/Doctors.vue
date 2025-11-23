<template>
  <div class="doctors-container">
    <!-- 顶部导航栏 -->
    <header class="doctors-header">
      <div class="header-content">
        <div class="logo">
          <i class="el-icon-medal-1"></i>
          <span>医院预约系统</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/departments" class="nav-item">科室介绍</router-link>
          <router-link to="/doctors" class="nav-item active">医生团队</router-link>
          <router-link to="/medical-guide" class="nav-item">医疗导诊</router-link>
        </nav>
        <div class="user-info">
          <div v-if="user" class="logged-in">
              <span class="welcome">欢迎，{{ user.name || user.username }}</span>
              <router-link to="/user-info" class="btn-text">个人信息</router-link>
              <router-link to="/appointment-list" class="btn-text">预约记录</router-link>
              <button class="btn-text" @click="handleLogout">退出登录</button>
          </div>
          <div v-else class="not-logged-in">
            <router-link to="/login" class="btn-primary">登录</router-link>
            <router-link to="/register" class="btn-text">注册</router-link>
          </div>
        </div>
      </div>
    </header>
    
    <!-- 内容区域 -->
    <main class="doctors-main">
      <div class="page-title">
        <h1>医生团队</h1>
        <p>我们拥有一支经验丰富、技术精湛的医疗团队，为您提供专业的医疗服务</p>
      </div>
      
      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-select v-model="selectedDepartment" placeholder="选择科室" style="width: 200px; margin-right: 15px;">
          <el-option label="全部科室" value="" />
          <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
        </el-select>
        <el-input v-model="searchKeyword" placeholder="搜索医生姓名" style="width: 300px; margin-right: 15px;">
          <template #append>
            <el-button @click="searchDoctors">搜索</el-button>
          </template>
        </el-input>
      </div>
      
      <!-- 医生列表 -->
      <div class="doctors-list" v-if="!loading">
        <div class="doctor-card" v-for="doctor in doctors" :key="doctor.id">
          <div class="doctor-info">
            <div class="doctor-avatar">
              <img :src="doctor.avatar || '/default-avatar.jpg'" alt="医生头像" />
            </div>
            <div class="doctor-details">
              <h3 class="doctor-name">{{ doctor.name }}</h3>
              <p class="doctor-title">{{ doctor.title }}</p>
              <p class="doctor-department">{{ getDepartmentName(doctor.departmentId) }}</p>
              <p class="doctor-specialty">专长：{{ doctor.specialty }}</p>
            </div>
          </div>
          <div class="doctor-description">
            <p>{{ doctor.description }}</p>
          </div>
          <div class="doctor-actions">
            <router-link :to="'/appointment?doctorId=' + doctor.id" class="btn-primary" v-if="user && user.role !== 'ADMIN'">
              立即预约
            </router-link>
            <router-link :to="'/doctor-detail/' + doctor.id" class="btn-text">
              查看详情
            </router-link>
          </div>
        </div>
        
        <!-- 无数据提示 -->
        <div class="no-data" v-if="doctors.length === 0">
          <el-empty description="暂无医生信息" />
        </div>
      </div>
      
      <!-- 加载中 -->
      <div class="loading-container" v-else>
        <el-loading :fullscreen="true" text="加载中..." />
      </div>
    </main>
    
    <!-- 页脚 -->
    <footer class="doctors-footer">
      <div class="footer-content">
        <p>© 2024 医院预约系统 版权所有</p>
        <p>联系方式：电话 0123-4567890 | 地址：医院路88号</p>
      </div>
    </footer>
  </div>
</template>

<script>
import departmentAPI from '../api/department'
import doctorAPI from '../api/doctor'

export default {
  name: 'Doctors',
  data() {
    return {
      user: null,
      doctors: [],
      departments: [],
      originalDoctors: [], // 用于存储原始数据
      selectedDepartment: '',
      searchKeyword: '',
      loading: false
    }
  },
  created() {
    this.loadUserInfo()
    this.loadDepartments()
    this.loadDoctors()
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        this.user = null
        this.$router.push('/login')
        this.$message.success('退出登录成功')
      }).catch(() => {
        // 取消退出
      })
    },
    async loadDepartments() {
      try {
        const response = await departmentAPI.getAllDepartments()
        this.departments = response.data || []
      } catch (error) {
        console.error('加载科室信息失败:', error)
      }
    },
    
    async loadDoctors() {
      this.loading = true
      try {
        // 使用真实API获取医生数据
        const response = await doctorAPI.getAllDoctors()
        this.doctors = response.data || []
        this.originalDoctors = [...this.doctors] // 保存原始数据
      } catch (error) {
        console.error('加载医生信息失败:', error)
        this.$message.error('加载医生信息失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    async searchDoctors() {
      this.loading = true
      try {
        // 构建搜索参数
        const params = {}
        if (this.selectedDepartment) {
          params.departmentId = this.selectedDepartment
        }
        if (this.searchKeyword) {
          params.keyword = this.searchKeyword
        }
        
        // 调用搜索API
        const response = await doctorAPI.searchDoctors(params)
        this.doctors = response.data || []
      } catch (error) {
        console.error('搜索医生失败:', error)
        this.$message.error('搜索失败，请稍后重试')
        // 如果API调用失败，使用本地过滤作为备用
        this.localFilterDoctors()
      } finally {
        this.loading = false
      }
    },
    
    localFilterDoctors() {
      // 本地过滤作为备用方案
      let filteredDoctors = [...this.originalDoctors]
      
      if (this.selectedDepartment) {
        filteredDoctors = filteredDoctors.filter(doctor => doctor.departmentId === Number(this.selectedDepartment))
      }
      
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filteredDoctors = filteredDoctors.filter(doctor => 
          doctor.name.toLowerCase().includes(keyword) || 
          doctor.specialty.toLowerCase().includes(keyword) ||
          doctor.description.toLowerCase().includes(keyword)
        )
      }
      
      this.doctors = filteredDoctors
    },
    getDepartmentName(departmentId) {
      const department = this.departments.find(dept => dept.id === departmentId)
      return department ? department.name : '未知科室'
    }
  },
  // 监听路由变化，刷新用户信息
  watch: {
    $route: {
      handler() {
        this.loadUserInfo()
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
.doctors-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.doctors-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.logo i {
  margin-right: 10px;
  font-size: 24px;
}

.nav-menu {
  display: flex;
}

.nav-item {
  margin: 0 20px;
  color: #606266;
  text-decoration: none;
  font-size: 16px;
  padding: 5px 0;
  position: relative;
  transition: color 0.3s;
}

.nav-item:hover,
.nav-item.active {
  color: #1890ff;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
}

.logged-in {
  display: flex;
  align-items: center;
}

.welcome {
  margin-right: 20px;
  color: #606266;
}

.btn-text,
.btn-primary {
  padding: 5px 15px;
  margin-left: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.btn-text {
  color: #606266;
  background: none;
  border: none;
}

.btn-text:hover {
  color: #1890ff;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
  border: none;
  text-decoration: none;
  display: inline-block;
  text-align: center;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

/* 内容区域 */
.doctors-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
}

.page-title {
  text-align: center;
  margin-bottom: 30px;
}

.page-title h1 {
  font-size: 32px;
  color: #303133;
  margin-bottom: 10px;
}

.page-title p {
  color: #606266;
  font-size: 16px;
}

/* 筛选条件 */
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  display: flex;
  align-items: center;
}

/* 医生列表 */
.doctors-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(550px, 1fr));
  gap: 20px;
}

.doctor-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 25px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.doctor-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.doctor-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.doctor-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.doctor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.doctor-details {
  flex: 1;
}

.doctor-name {
  font-size: 20px;
  color: #303133;
  margin: 0 0 5px 0;
}

.doctor-title {
  font-size: 16px;
  color: #1890ff;
  margin: 0 0 5px 0;
}

.doctor-department,
.doctor-specialty {
  font-size: 14px;
  color: #606266;
  margin: 0 0 5px 0;
}

.doctor-description {
  margin-bottom: 20px;
}

.doctor-description p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.doctor-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 无数据提示 */
.no-data {
  grid-column: 1 / -1;
  padding: 60px 0;
}

/* 加载中 */
.loading-container {
  min-height: 400px;
}

/* 页脚 */
.doctors-footer {
  background-color: #f0f2f5;
  padding: 20px 0;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #606266;
}

.footer-content p {
  margin: 5px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 15px;
  }
  
  .nav-menu {
    margin: 15px 0;
  }
  
  .nav-item {
    margin: 0 10px;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 15px;
  }
  
  .filter-section .el-select,
  .filter-section .el-input {
    width: 100% !important;
  }
  
  .doctors-list {
    grid-template-columns: 1fr;
  }
  
  .doctor-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .doctor-avatar {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .doctor-actions {
    justify-content: center;
  }
}
</style>