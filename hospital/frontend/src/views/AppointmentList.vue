<template>
  <div class="appointment-list-container">
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
      <div v-if="!user">
        <el-alert
          title="请先登录"
          description="查看预约记录需要登录后使用，请先登录账号"
          type="info"
          show-icon
          :closable="false"
        >
          <router-link to="/login" slot="after-close">立即登录</router-link>
        </el-alert>
      </div>
      <div v-else>
        <div class="page-header">
          <h1>我的预约记录</h1>
          <p class="page-desc">查看和管理您的所有预约信息</p>
        </div>
        
        <!-- 筛选和搜索 -->
        <div class="filter-section">
          <div class="filter-group">
            <label class="filter-label">状态筛选：</label>
            <el-select v-model="statusFilter" placeholder="全部状态" @change="loadAppointments">
              <el-option label="全部状态" value=""></el-option>
              <el-option label="待确认" value="PENDING"></el-option>
              <el-option label="已确认" value="CONFIRMED"></el-option>
              <el-option label="已完成" value="COMPLETED"></el-option>
              <el-option label="已取消" value="CANCELLED"></el-option>
            </el-select>
          </div>
          <div class="filter-group">
            <label class="filter-label">搜索：</label>
            <el-input v-model="searchKeyword" placeholder="输入医生姓名或科室名称" prefix-icon="el-icon-search" @input="handleSearch"></el-input>
          </div>
        </div>
        
        <!-- 预约列表 -->
        <div class="appointment-table">
          <el-table :data="filteredAppointments" style="width: 100%" stripe>
            <el-table-column prop="id" label="预约编号" width="120"></el-table-column>
            <el-table-column prop="doctorName" label="医生" width="120">
              <template slot-scope="scope">{{ scope.row.doctor?.name || '-' }}</template>
            </el-table-column>
            <el-table-column prop="departmentName" label="科室" width="150">
              <template slot-scope="scope">{{ scope.row.department?.name || '-' }}</template>
            </el-table-column>
            <el-table-column prop="appointmentDate" label="预约日期" width="120"></el-table-column>
            <el-table-column prop="appointmentTime" label="预约时段" width="100"></el-table-column>
            <el-table-column prop="symptoms" label="症状描述" show-overflow-tooltip></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column label="操作" width="150" v-if="user.role !== 'ADMIN'">
              <template slot-scope="scope">
                <el-button 
                  v-if="scope.row.status === 'PENDING'" 
                  type="danger" 
                  size="small" 
                  @click="cancelAppointment(scope.row.id)"
                >
                  取消预约
                </el-button>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <!-- 无数据提示 -->
        <div v-if="filteredAppointments.length === 0 && !loading" class="no-data">
          <el-empty description="暂无预约记录"></el-empty>
        </div>
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
  name: 'AppointmentList',
  data() {
    return {
      user: null,
      appointments: [],
      statusFilter: '',
      searchKeyword: '',
      loading: false
    }
  },
  computed: {
    filteredAppointments() {
      let result = [...this.appointments]
      
      // 状态筛选
      if (this.statusFilter) {
        result = result.filter(app => app.status === this.statusFilter)
      }
      
      // 关键词搜索
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.toLowerCase().trim()
        result = result.filter(app => 
          (app.doctor?.name && app.doctor.name.toLowerCase().includes(keyword)) ||
          (app.department?.name && app.department.name.toLowerCase().includes(keyword))
        )
      }
      
      return result
    }
  },
  created() {
    this.loadUserInfo()
    if (this.user) {
      this.loadAppointments()
    }
  },
  watch: {
    user(newVal) {
      if (newVal) {
        this.loadAppointments()
      }
    }
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      }
    },
    loadAppointments() {
      if (!this.user) return
      
      this.loading = true
      const params = this.user.role === 'ADMIN' ? {} : { userId: this.user.id }
      
      this.$axios.get('/appointment/list', { params }).then(res => {
        if (res.success) {
          this.appointments = res.data
        }
      }).catch(err => {
        console.error('加载预约列表失败:', err)
        this.$message.error('加载预约列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    handleSearch() {
      // 搜索功能已通过computed属性实现
    },
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'CONFIRMED': 'success',
        'COMPLETED': 'info',
        'CANCELLED': 'danger'
      }
      return typeMap[status] || 'default'
    },
    getStatusText(status) {
      const textMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return textMap[status] || status
    },
    cancelAppointment(appointmentId) {
      this.$confirm('确定要取消该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.put(`/appointment/cancel/${appointmentId}`).then(res => {
          if (res.success) {
            this.$message.success('取消预约成功')
            this.loadAppointments()
          } else {
            this.$message.error(res.message || '取消预约失败')
          }
        }).catch(err => {
          console.error('取消预约失败:', err)
          this.$message.error('取消预约失败，请重试')
        })
      }).catch(() => {
        // 取消操作
      })
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
.appointment-list-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏样式 */
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
  gap: 20px;
}

.nav-item {
  color: #606266;
  text-decoration: none;
  padding: 0 10px;
  height: 60px;
  line-height: 60px;
  position: relative;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #1890ff;
}

.nav-item.active {
  color: #1890ff;
  font-weight: bold;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 10px;
  right: 10px;
  height: 2px;
  background-color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome {
  color: #606266;
}

.btn-text {
  color: #1890ff;
  cursor: pointer;
  text-decoration: none;
}

.btn-text:hover {
  color: #40a9ff;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
  padding: 6px 16px;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #303133;
}

.page-desc {
  color: #606266;
  font-size: 16px;
}

.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  color: #606266;
  font-weight: 500;
  white-space: nowrap;
}

.appointment-table {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.no-data {
  margin: 40px 0;
  text-align: center;
}

/* 页脚样式 */
.footer {
  background-color: #f5f7fa;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 10px;
  }
  
  .nav-menu {
    margin: 10px 0;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .filter-section {
    flex-direction: column;
  }
}
</style>