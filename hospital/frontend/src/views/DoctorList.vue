<template>
  <div class="doctor-container">
    <!-- 顶部导航栏 -->
    <header class="header">
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
    <main class="main-content">
      <div class="page-header">
        <h1>医生团队</h1>
        <p class="page-desc">认识我们专业的医疗团队，选择最适合您的医生</p>
      </div>
      <!-- 筛选和搜索 -->
      <div class="filter-section">
        <div class="filter-group">
          <label class="filter-label">科室筛选：</label>
          <el-select v-model="selectedDepartment" placeholder="全部科室" @change="handleFilterChange">
            <el-option label="全部科室" value=""></el-option>
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
          </el-select>
        </div>
        <div class="filter-group">
          <label class="filter-label">搜索医生：</label>
          <el-input v-model="searchKeyword" placeholder="请输入医生姓名或专长" prefix-icon="el-icon-search" @input="handleSearch"></el-input>
        </div>
      </div>
      <!-- 医生列表 -->
      <div class="doctor-list">
        <el-card v-for="doctor in filteredDoctors" :key="doctor.id" class="doctor-card">
          <div class="doctor-header">
            <el-avatar :size="100" :src="doctor.avatar" icon="el-icon-user-solid"></el-avatar>
            <div class="doctor-basic-info">
              <h3 class="doctor-name">{{ doctor.name }}<el-tag size="small" class="doctor-title">{{ doctor.title }}</el-tag></h3>
              <p class="doctor-department">{{ doctor.department?.name || '未分配科室' }}</p>
              <p class="doctor-specialty">专长：{{ doctor.specialty }}</p>
            </div>
          </div>
          <div class="doctor-content">
            <p class="doctor-introduction">{{ doctor.introduction || '暂无简介' }}</p>
          </div>
          <div class="doctor-footer">
            <div class="doctor-stats">
              <div class="stat-item">
                <span class="stat-value">{{ doctor.appointmentCount || 0 }}</span>
                <span class="stat-label">预约量</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ doctor.consultationTime || '8:00-17:30' }}</span>
                <span class="stat-label">出诊时间</span>
              </div>
            </div>
            <router-link :to="{ path: '/appointment', query: { doctorId: doctor.id }}" class="make-appointment-btn" v-if="user && user.role !== 'ADMIN'">
              立即预约
            </router-link>
          </div>
        </el-card>
      </div>
      <!-- 无数据提示 -->
      <div v-if="filteredDoctors.length === 0" class="no-data">
        <el-empty description="暂无医生信息"></el-empty>
      </div>
    </main>
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 医院预约系统 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'DoctorList',
  data() {
    return {
      user: null,
      doctors: [],
      departments: [],
      selectedDepartment: '',
      searchKeyword: '',
      loading: false
    }
  },
  computed: {
    filteredDoctors() {
      let result = this.doctors
      
      // 科室筛选
      if (this.selectedDepartment) {
        result = result.filter(doctor => doctor.departmentId === this.selectedDepartment)
      }
      
      // 关键词搜索
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.toLowerCase().trim()
        result = result.filter(doctor => 
          doctor.name.toLowerCase().includes(keyword) || 
          (doctor.specialty && doctor.specialty.toLowerCase().includes(keyword)) ||
          (doctor.introduction && doctor.introduction.toLowerCase().includes(keyword))
        )
      }
      
      return result
    }
  },
  created() {
    this.loadUserInfo()
    this.loadDepartments()
    this.loadDoctors()
    
    // 检查URL参数中是否有科室筛选条件
    if (this.$route.query.departmentId) {
      this.selectedDepartment = this.$route.query.departmentId
    }
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      }
    },
    loadDepartments() {
      this.$axios.get('/department/list').then(res => {
        if (res.success) {
          this.departments = res.data
        }
      })
    },
    loadDoctors() {
      this.loading = true
      
      // 如果有科室ID参数，只加载该科室的医生
      const params = this.selectedDepartment ? { departmentId: this.selectedDepartment } : {}
      
      this.$axios.get('/doctor/list', { params }).then(res => {
        if (res.success) {
          this.doctors = res.data
        }
      }).catch(err => {
        console.error('加载医生列表失败:', err)
        this.$message.error('加载医生列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    handleFilterChange() {
      this.loadDoctors()
    },
    handleSearch() {
      // 搜索功能已通过computed属性实现，这里可以添加额外的逻辑
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
    }
  }
}
</script>

<style scoped>
.doctor-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 - 复用Home页面的样式 */
.header {
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
}

.btn-primary:hover {
  background-color: #40a9ff;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 32px;
  color: #303133;
  margin-bottom: 10px;
}

.page-desc {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

/* 筛选区域 */
.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 16px;
  color: #606266;
}

.filter-group .el-select {
  width: 200px;
}

.filter-group .el-input {
  width: 300px;
}

/* 医生列表 */
.doctor-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.doctor-card {
  height: 100%;
}

.doctor-header {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.doctor-basic-info {
  flex: 1;
}

.doctor-name {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.doctor-title {
  font-size: 14px;
}

.doctor-department {
  color: #1890ff;
  margin: 0 0 5px 0;
  font-size: 14px;
}

.doctor-specialty {
  color: #606266;
  margin: 0;
  font-size: 14px;
}

.doctor-content {
  margin-bottom: 20px;
}

.doctor-introduction {
  color: #606266;
  line-height: 1.6;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.doctor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.doctor-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #909399;
}

.make-appointment-btn {
  background-color: #1890ff;
  color: white;
  padding: 8px 20px;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.make-appointment-btn:hover {
  background-color: #40a9ff;
}

/* 无数据提示 */
.no-data {
  text-align: center;
  padding: 60px 0;
}

/* 页脚 */
.footer {
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
  
  .filter-group {
    width: 100%;
  }
  
  .filter-group .el-select,
  .filter-group .el-input {
    width: 100%;
  }
  
  .doctor-list {
    grid-template-columns: 1fr;
  }
  
  .doctor-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .doctor-footer {
    flex-direction: column;
    gap: 15px;
  }
  
  .doctor-stats {
    justify-content: center;
  }
}
</style>
