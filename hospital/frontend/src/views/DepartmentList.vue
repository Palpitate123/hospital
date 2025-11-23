<template>
  <div class="department-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <i class="el-icon-medal-1"></i>
          <span>医院预约系统</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/departments" class="nav-item active">科室介绍</router-link>
          <router-link to="/doctors" class="nav-item">医生团队</router-link>
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
        <h1>科室介绍</h1>
        <p class="page-desc">了解我院各科室的专业领域和医疗特色</p>
      </div>
      <!-- 科室搜索 -->
      <div class="search-section">
        <el-input v-model="searchKeyword" placeholder="请输入科室名称或关键词" prefix-icon="el-icon-search" size="large">
          <template v-slot:append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      <!-- 科室列表 -->
      <div class="department-list">
        <el-card v-for="department in filteredDepartments" :key="department.id" class="department-card">
          <div class="department-header">
            <h3 class="department-name">{{ department.name }}</h3>
            <el-tag size="medium" type="primary">{{ department.type }}</el-tag>
          </div>
          <div class="department-content">
            <p class="department-description">{{ department.description }}</p>
            <div class="department-info">
              <div class="info-item">
                <i class="el-icon-location"></i>
                <span>位置：{{ department.location }}</span>
              </div>
              <div class="info-item">
                <i class="el-icon-phone"></i>
                <span>电话：{{ department.phone }}</span>
              </div>
              <div class="info-item">
                <i class="el-icon-user-solid"></i>
                <span>医生数量：{{ department.doctorCount || 0 }}</span>
              </div>
            </div>
          </div>
          <div class="department-footer">
            <router-link :to="{ path: '/doctors', query: { departmentId: department.id }}" class="view-doctors-btn">
              查看医生列表
              <i class="el-icon-arrow-right"></i>
            </router-link>
            <router-link :to="{ path: '/appointment', query: { departmentId: department.id }}" class="make-appointment-btn" v-if="user && user.role !== 'ADMIN'">
              立即预约
            </router-link>
          </div>
        </el-card>
      </div>
      <!-- 无数据提示 -->
      <div v-if="filteredDepartments.length === 0" class="no-data">
        <el-empty description="暂无科室信息"></el-empty>
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
  name: 'DepartmentList',
  data() {
    return {
      user: null,
      departments: [],
      searchKeyword: '',
      loading: false
    }
  },
  computed: {
    filteredDepartments() {
      if (!this.searchKeyword.trim()) {
        return this.departments
      }
      
      const keyword = this.searchKeyword.toLowerCase().trim()
      return this.departments.filter(dept => 
        dept.name.toLowerCase().includes(keyword) || 
        dept.description.toLowerCase().includes(keyword) ||
        dept.type.toLowerCase().includes(keyword)
      )
    }
  },
  created() {
    this.loadUserInfo()
    this.loadDepartments()
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      }
    },
    loadDepartments() {
      this.loading = true
      this.$axios.get('/department/list').then(res => {
        if (res.success) {
          this.departments = res.data
        }
      }).catch(err => {
        console.error('加载科室列表失败:', err)
        this.$message.error('加载科室列表失败')
      }).finally(() => {
        this.loading = false
      })
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
.department-container {
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

/* 搜索区域 */
.search-section {
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

/* 科室列表 */
.department-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.department-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.department-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.department-name {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.department-content {
  flex: 1;
}

.department-description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.department-info {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #606266;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item i {
  margin-right: 8px;
  color: #1890ff;
}

.department-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.view-doctors-btn {
  color: #1890ff;
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: color 0.3s;
}

.view-doctors-btn:hover {
  color: #40a9ff;
}

.view-doctors-btn i {
  margin-left: 5px;
  font-size: 14px;
}

.make-appointment-btn {
  background-color: #1890ff;
  color: white;
  padding: 8px 16px;
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
  
  .department-list {
    grid-template-columns: 1fr;
  }
  
  .department-footer {
    flex-direction: column;
    gap: 10px;
  }
  
  .view-doctors-btn {
    margin-bottom: 10px;
  }
}
</style>
