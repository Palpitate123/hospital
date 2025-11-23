<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h2>管理员仪表盘</h2>
      <div class="system-info">
        <el-date-picker 
          type="date" 
          placeholder="选择日期" 
          v-model="selectedDate" 
          style="width: 180px;"
          @change="handleDateChange"
        ></el-date-picker>
        <span class="current-time">{{ currentTime }}</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon user-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i> {{ userGrowthRate }}%
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon doctor-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ totalDoctors }}</div>
            <div class="stat-label">医生总数</div>
          </div>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i> {{ doctorGrowthRate }}%
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon dept-icon">
            <i class="el-icon-building"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ totalDepartments }}</div>
            <div class="stat-label">科室总数</div>
          </div>
          <div class="stat-change neutral">
            <i class="el-icon-minus"></i> 0%
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon appointment-icon">
            <i class="el-icon-tickets"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ totalAppointments }}</div>
            <div class="stat-label">今日预约数</div>
          </div>
          <div class="stat-change positive">
            <i class="el-icon-caret-top"></i> {{ appointmentGrowthRate }}%
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主要内容区域 -->
    <div class="dashboard-content">
      <!-- 最近预约列表 -->
      <div class="recent-appointments">
        <div class="section-header">
          <h3>最近预约</h3>
          <router-link to="/admin/appointments" class="view-all">查看全部</router-link>
        </div>
        <el-table :data="recentAppointments" style="width: 100%">
          <el-table-column prop="id" label="预约ID" width="80"></el-table-column>
          <el-table-column prop="patientName" label="患者姓名"></el-table-column>
          <el-table-column prop="doctorName" label="医生姓名"></el-table-column>
          <el-table-column prop="departmentName" label="科室"></el-table-column>
          <el-table-column prop="appointmentTime" label="预约时间">
            <template slot-scope="scope">
              {{ formatDate(scope.row.appointmentTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template>
              <el-button type="text" size="small">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 系统状态 -->
      <div class="system-status">
        <div class="section-header">
          <h3>系统状态</h3>
        </div>
        <el-card shadow="hover">
          <div class="status-grid">
            <div class="status-item">
              <div class="status-label">系统运行时间</div>
              <div class="status-value">{{ systemUptime }}</div>
            </div>
            <div class="status-item">
              <div class="status-label">在线用户数</div>
              <div class="status-value">{{ onlineUsers }}</div>
            </div>
            <div class="status-item">
              <div class="status-label">服务器状态</div>
              <div class="status-value">
                <span class="status-badge online">正常</span>
              </div>
            </div>
            <div class="status-item">
              <div class="status-label">数据库状态</div>
              <div class="status-value">
                <span class="status-badge online">正常</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <div class="section-header">
        <h3>快速操作</h3>
      </div>
      <div class="action-buttons">
        <el-button type="primary" icon="el-icon-user" @click="goToUserManagement">用户管理</el-button>
        <el-button type="primary" icon="el-icon-building" @click="goToDepartmentManagement">科室管理</el-button>
        <el-button type="primary" icon="el-icon-user-solid" @click="goToDoctorManagement">医生管理</el-button>
        <el-button type="primary" icon="el-icon-tickets" @click="goToAppointmentManagement">预约管理</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import patientAPI from '@/api/patient'
import doctorAPI from '@/api/doctor'
import departmentAPI from '@/api/department'
import appointmentAPI from '@/api/appointment'

export default {
  name: 'Dashboard',
  data() {
    return {
      selectedDate: new Date(),
      currentTime: '',
      // 统计数据
      totalUsers: 0,
      userGrowthRate: 0,
      totalDoctors: 0,
      doctorGrowthRate: 0,
      totalDepartments: 0,
      totalAppointments: 0,
      appointmentGrowthRate: 0,
      // 系统状态
      systemUptime: '7天 12小时',
      onlineUsers: 25,
      // 最近预约
      recentAppointments: [],
      // loading状态
      loading: false
    }
  },
  mounted() {
    this.initializeDashboard()
    this.startTimeUpdate()
  },
  methods: {
    initializeDashboard() {
      // 模拟获取统计数据
      this.fetchStatistics()
      // 获取最近预约
      this.fetchRecentAppointments()
    },
    
    // 从API获取统计数据
    async fetchStatistics() {
      this.loading = true
      try {
        // 并行获取各类数据
        const [patientsRes, doctorsRes, departmentsRes, appointmentsRes] = await Promise.all([
          patientAPI.getAllPatients(),
          doctorAPI.getAllDoctors(),
          departmentAPI.getAllDepartments(),
          appointmentAPI.getAppointments({ limit: 10 })
        ])
        
        // 设置统计数据
        this.totalUsers = patientsRes.data ? patientsRes.data.length : 0
        this.totalDoctors = doctorsRes.data ? doctorsRes.data.length : 0
        this.totalDepartments = departmentsRes.data ? departmentsRes.data.length : 0
        
        // 计算今日预约数
        const today = new Date().toDateString()
        this.totalAppointments = appointmentsRes.data ? 
          appointmentsRes.data.filter(apt => {
            return new Date(apt.appointmentTime).toDateString() === today
          }).length : 0
          
        // 简单计算增长率（这里可以根据实际业务逻辑调整）
        this.userGrowthRate = 2.5
        this.doctorGrowthRate = 4.2
        this.appointmentGrowthRate = 6.8
      } catch (error) {
        console.error('获取统计数据失败:', error)
        this.$message.error('获取统计数据失败')
        // 失败时使用默认值
        this.totalUsers = 0
        this.totalDoctors = 0
        this.totalDepartments = 0
        this.totalAppointments = 0
      } finally {
        this.loading = false
      }
    },
    
    // 从API获取最近预约
    async fetchRecentAppointments() {
      try {
        const response = await appointmentAPI.getRecentAppointments()
        
        if (response.data) {
          // 确保返回的数据结构与前端模板匹配
          this.recentAppointments = response.data.map(apt => ({
            id: apt.id || apt.appointmentId,
            patientName: apt.patientName || '患者',
            doctorName: apt.doctorName || '医生',
            departmentName: apt.departmentName || '科室',
            appointmentTime: apt.appointmentTime || new Date().toISOString(),
            status: apt.status || '未知'
          }))
        }
      } catch (error) {
        console.error('获取最近预约失败:', error)
        this.$message.error('获取最近预约失败')
        this.recentAppointments = []
      }
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 获取状态对应的标签类型
    getStatusType(status) {
      switch(status) {
        case '已确认': return 'success'
        case '待确认': return 'warning'
        case '已取消': return 'danger'
        default: return 'info'
      }
    },
    
    // 日期选择变化处理
    handleDateChange(date) {
      console.log('选择日期:', date)
      // 这里可以根据选择的日期重新加载数据
      this.fetchStatistics()
      this.fetchRecentAppointments()
    },
    
    // 更新当前时间
    updateCurrentTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    
    // 启动时间更新
    startTimeUpdate() {
      this.updateCurrentTime()
      setInterval(() => {
        this.updateCurrentTime()
      }, 1000)
    },
    
    // 导航到用户管理
    goToUserManagement() {
      this.$router.push('/admin/users')
    },
    
    // 导航到科室管理
    goToDepartmentManagement() {
      this.$router.push('/admin/departments')
    },
    
    // 导航到医生管理
    goToDoctorManagement() {
      this.$router.push('/admin/doctors')
    },
    
    // 导航到预约管理
    goToAppointmentManagement() {
      this.$router.push('/admin/appointments')
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h2 {
  margin: 0;
  color: #303133;
}

.system-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.currentTime {
  color: #606266;
}

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.user-icon {
  background-color: #409eff;
}

.doctor-icon {
  background-color: #67c23a;
}

.dept-icon {
  background-color: #e6a23c;
}

.appointment-icon {
  background-color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-change {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.positive {
  color: #67c23a;
}

.stat-change.negative {
  color: #f56c6c;
}

.stat-change.neutral {
  color: #909399;
}

/* 内容区域 */
.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.view-all {
  color: #409eff;
  font-size: 14px;
  text-decoration: none;
}

.view-all:hover {
  color: #66b1ff;
}

.recent-appointments,
.system-status {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.status-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.status-label {
  font-size: 14px;
  color: #909399;
}

.status-value {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.status-badge.online {
  background-color: #f0f9ff;
  color: #409eff;
}

/* 快速操作 */
.quick-actions {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  min-width: 120px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .action-buttons {
    justify-content: center;
  }
}
</style>