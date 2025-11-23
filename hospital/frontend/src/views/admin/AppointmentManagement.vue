<template>
  <div class="appointment-management">
    <div class="page-header">
      <h2>预约管理</h2>
    </div>

    <!-- 搜索筛选区 -->
    <div class="search-filter">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入患者姓名"></el-input>
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名"></el-input>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="searchForm.departmentId" placeholder="请选择科室" clearable>
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select v-model="searchForm.status" placeholder="请选择预约状态" clearable>
            <el-option label="待确认" value="PENDING"></el-option>
            <el-option label="已确认" value="CONFIRMED"></el-option>
            <el-option label="已取消" value="CANCELLED"></el-option>
            <el-option label="已完成" value="COMPLETED"></el-option>
            <el-option label="已爽约" value="NO_SHOW"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="filteredAppointments"
        style="width: 100%"
        stripe
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="预约编号" width="120"></el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
        <el-table-column prop="patientPhone" label="患者电话" width="150"></el-table-column>
        <el-table-column prop="departmentName" label="科室" width="120"></el-table-column>
        <el-table-column prop="doctorName" label="医生姓名" width="120"></el-table-column>
        <el-table-column prop="appointmentDate" label="预约日期" width="120" :formatter="formatDate"></el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间段" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" :formatter="formatDateTime"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">查看详情</el-button>
            <el-button v-if="scope.row.status === 'PENDING'" type="success" size="small" @click="handleConfirm(scope.row)">确认预约</el-button>
            <el-button v-if="['PENDING', 'CONFIRMED'].includes(scope.row.status)" type="danger" size="small" @click="handleCancel(scope.row)">取消预约</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="filteredAppointments.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>

    <!-- 预约详情对话框 -->
    <el-dialog
      title="预约详情"
      :visible.sync="detailDialogVisible"
      width="600px"
      top="10vh"
    >
      <div v-if="selectedAppointment" class="appointment-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预约编号">{{ selectedAppointment.id }}</el-descriptions-item>
          <el-descriptions-item label="患者信息">
            <div>{{ selectedAppointment.patientName }} ({{ selectedAppointment.patientPhone }})</div>
            <div>{{ selectedAppointment.patientGender === 'male' ? '男' : '女' }}，{{ selectedAppointment.patientAge }}岁</div>
          </el-descriptions-item>
          <el-descriptions-item label="就诊科室">{{ selectedAppointment.departmentName }}</el-descriptions-item>
          <el-descriptions-item label="预约医生">{{ selectedAppointment.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ formatDate(selectedAppointment.appointmentDate) }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ selectedAppointment.appointmentTime }}</el-descriptions-item>
          <el-descriptions-item label="预约状态"><el-tag :type="getStatusType(selectedAppointment.status)">{{ getStatusText(selectedAppointment.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="病情描述">{{ selectedAppointment.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(selectedAppointment.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(selectedAppointment.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 批量操作工具栏 -->
    <div class="batch-operations" v-if="selectedRows.length > 0">
      <el-button type="danger" @click="handleBatchCancel">批量取消</el-button>
      <el-button type="success" @click="handleBatchConfirm" :disabled="!canBatchConfirm">批量确认</el-button>
      <span>已选择 {{ selectedRows.length }} 条记录</span>
    </div>
  </div>
</template>

<script>
import appointmentAPI from '../../api/appointment'
import departmentAPI from '../../api/department'

export default {
  name: 'AppointmentManagement',
  data() {
    return {
      appointments: [],
      departments: [],
      loading: false,
      searchForm: {
        patientName: '',
        doctorName: '',
        departmentId: '',
        status: ''
      },
      dateRange: [],
      currentPage: 1,
      pageSize: 10,
      detailDialogVisible: false,
      selectedAppointment: null,
      selectedRows: []
    }
  },
  computed: {
    filteredAppointments() {
      let filtered = this.appointments.filter(appointment => {
        // 姓名筛选
        if (this.searchForm.patientName && !appointment.patientName.toLowerCase().includes(this.searchForm.patientName.toLowerCase())) {
          return false
        }
        if (this.searchForm.doctorName && !appointment.doctorName.toLowerCase().includes(this.searchForm.doctorName.toLowerCase())) {
          return false
        }
        // 科室筛选
        if (this.searchForm.departmentId && appointment.departmentId !== this.searchForm.departmentId) {
          return false
        }
        // 状态筛选
        if (this.searchForm.status && appointment.status !== this.searchForm.status) {
          return false
        }
        // 日期范围筛选
        if (this.dateRange && this.dateRange.length === 2) {
          const appointmentDate = new Date(appointment.appointmentDate)
          const startDate = new Date(this.dateRange[0])
          const endDate = new Date(this.dateRange[1])
          if (appointmentDate < startDate || appointmentDate > endDate) {
            return false
          }
        }
        return true
      })
      
      // 分页
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return filtered.slice(start, end)
    },
    canBatchConfirm() {
      return this.selectedRows.every(row => row.status === 'PENDING')
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        // 并行获取数据
        const [departmentsResponse, appointmentsResponse] = await Promise.all([
          departmentAPI.getAllDepartments(),
          appointmentAPI.getAppointments()
        ])
        this.departments = departmentsResponse.data || []
        this.appointments = appointmentsResponse.data || []
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
        this.departments = []
        this.appointments = []
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
    },
    handleReset() {
      this.searchForm = {
        patientName: '',
        doctorName: '',
        departmentId: '',
        status: ''
      }
      this.dateRange = []
      this.currentPage = 1
    },
    handleCurrentChange(page) {
      this.currentPage = page
    },
    handleViewDetail(row) {
      this.selectedAppointment = row
      this.detailDialogVisible = true
    },
    handleConfirm(row) {
      this.$confirm('确认该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await appointmentAPI.updateAppointmentStatus(row.id, 'CONFIRMED')
          row.status = 'CONFIRMED'
          row.updateTime = new Date().toISOString()
          this.$message.success('预约确认成功')
        } catch (error) {
          console.error('确认预约失败:', error)
          this.$message.error('确认预约失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    handleCancel(row) {
      this.$confirm('取消该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await appointmentAPI.cancelAppointment(row.id)
          row.status = 'CANCELLED'
          row.updateTime = new Date().toISOString()
          this.$message.success('预约已取消')
        } catch (error) {
          console.error('取消预约失败:', error)
          this.$message.error('取消预约失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    handleBatchCancel() {
      this.$confirm(`确定要取消选中的 ${this.selectedRows.length} 个预约吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const appointmentIds = this.selectedRows.map(row => row.id)
          await appointmentAPI.batchCancelAppointments(appointmentIds)
          // 更新本地数据
          this.selectedRows.forEach(row => {
            row.status = 'CANCELLED'
            row.updateTime = new Date().toISOString()
          })
          this.selectedRows = []
          this.$message.success('批量取消成功')
        } catch (error) {
          console.error('批量取消失败:', error)
          this.$message.error('批量取消失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    handleBatchConfirm() {
      this.$confirm(`确定要确认选中的 ${this.selectedRows.length} 个预约吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const appointmentIds = this.selectedRows.map(row => row.id)
          await appointmentAPI.batchConfirmAppointments(appointmentIds)
          // 更新本地数据
          this.selectedRows.forEach(row => {
            row.status = 'CONFIRMED'
            row.updateTime = new Date().toISOString()
          })
          this.selectedRows = []
          this.$message.success('批量确认成功')
        } catch (error) {
          console.error('批量确认失败:', error)
          this.$message.error('批量确认失败')
        }
      }).catch(() => {
        // 取消操作
      })
    },
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'CANCELLED': '已取消',
        'COMPLETED': '已完成',
        'NO_SHOW': '已爽约'
      }
      return statusMap[status] || status
    },
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'CONFIRMED': 'success',
        'CANCELLED': 'danger',
        'COMPLETED': 'info',
        'NO_SHOW': 'danger'
      }
      return typeMap[status] || 'default'
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const d = new Date(dateTime)
      return `${this.formatDate(d)} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
    },

  }
}
</script>

<style scoped>
.appointment-management {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.search-filter {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.search-form .el-form-item {
  margin-right: 20px;
  margin-bottom: 15px;
}

.table-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pagination {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: flex-end;
}

.batch-operations {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 1000;
}

.batch-operations span {
  color: #606266;
  font-size: 14px;
}

.appointment-detail {
  max-height: 60vh;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .appointment-management {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-form .el-form-item {
    margin-right: 0;
    width: 100%;
  }
  
  .table-container {
    padding: 10px;
    overflow-x: auto;
  }
  
  .pagination {
    justify-content: center;
    padding: 10px;
  }
  
  .batch-operations {
    position: static;
    margin-top: 10px;
    justify-content: center;
  }
}
</style>