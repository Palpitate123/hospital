<template>
  <div class="patient-management">
    <div class="page-header">
      <h2>患者管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddPatient">添加患者</el-button>
      </div>
    </div>

    <!-- 搜索筛选区 -->
    <div class="search-filter">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.name" placeholder="请输入患者姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="searchForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="searchForm.gender" placeholder="请选择性别" clearable>
            <el-option label="男" value="male"></el-option>
            <el-option label="女" value="female"></el-option>
          </el-select>
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
        :data="filteredPatients"
        style="width: 100%"
        stripe
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="患者ID" width="120"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            {{ scope.row.gender === 'male' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180"></el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" :formatter="formatDate"></el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" :formatter="formatDateTime"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">查看详情</el-button>
            <el-button type="success" size="small" @click="handleEditPatient(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeletePatient(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="filteredPatientsData.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>

    <!-- 添加/编辑患者对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      top="10vh"
    >
      <el-form ref="patientForm" :model="patientForm" :rules="rules" label-width="120px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="patientForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="patientForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="patientForm.birthDate" type="date" placeholder="选择日期" style="width: 100%;" @change="calculateAge"></el-date-picker>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model.number="patientForm.age" placeholder="请输入年龄" disabled></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="patientForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="patientForm.email" type="email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="patientForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="patientForm.address" type="textarea" :rows="3" placeholder="请输入地址"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>

    <!-- 患者详情对话框 -->
    <el-dialog
      title="患者详情"
      :visible.sync="detailDialogVisible"
      width="600px"
      top="10vh"
    >
      <div v-if="selectedPatient" class="patient-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="患者ID">{{ selectedPatient.id }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ selectedPatient.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedPatient.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ selectedPatient.age }}岁</el-descriptions-item>
          <el-descriptions-item label="出生日期">{{ formatDate(selectedPatient.birthDate) }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedPatient.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedPatient.email || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ selectedPatient.idCard }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ selectedPatient.address || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDateTime(selectedPatient.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(selectedPatient.updateTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 最近预约记录 -->
        <div class="recent-appointments" v-if="selectedPatient.recentAppointments && selectedPatient.recentAppointments.length > 0">
          <h3>最近预约记录</h3>
          <el-table :data="selectedPatient.recentAppointments" size="small" style="width: 100%;">
            <el-table-column prop="id" label="预约编号" width="120"></el-table-column>
            <el-table-column prop="departmentName" label="科室" width="100"></el-table-column>
            <el-table-column prop="doctorName" label="医生" width="100"></el-table-column>
            <el-table-column prop="appointmentDate" label="预约日期" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 批量操作工具栏 -->
    <div class="batch-operations" v-if="selectedRows.length > 0">
      <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
      <el-button type="primary" @click="handleBatchExport">导出选中</el-button>
      <span>已选择 {{ selectedRows.length }} 条记录</span>
    </div>
  </div>
</template>

<script>
import patientAPI from '@/api/patient'

export default {
  name: 'PatientManagement',
  data() {
    return {
      patients: [],
      loading: false,
      submitLoading: false,
      searchForm: {
        name: '',
        phone: '',
        idCard: '',
        gender: ''
      },
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      detailDialogVisible: false,
      dialogTitle: '',
      patientForm: {
        id: '',
        name: '',
        gender: 'male',
        age: 0,
        birthDate: '',
        phone: '',
        email: '',
        idCard: '',
        address: ''
      },
      selectedPatient: null,
      selectedRows: [],
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birthDate: [
          { required: true, message: '请选择出生日期', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        email: [
          { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { pattern: /^\d{17}[\dXx]$/, message: '请输入正确的身份证号格式', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    filteredPatientsData() {
      return this.patients.filter(patient => {
        // 姓名筛选
        if (this.searchForm.name && !patient.name.toLowerCase().includes(this.searchForm.name.toLowerCase())) {
          return false
        }
        // 手机号筛选
        if (this.searchForm.phone && !patient.phone.includes(this.searchForm.phone)) {
          return false
        }
        // 身份证号筛选
        if (this.searchForm.idCard && !patient.idCard.includes(this.searchForm.idCard)) {
          return false
        }
        // 性别筛选
        if (this.searchForm.gender && patient.gender !== this.searchForm.gender) {
          return false
        }
        return true
      })
    },
    filteredPatients() {
      // 分页
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredPatientsData.slice(start, end)
    }
  },
  created() {
    this.loadPatients()
  },
  methods: {
    async loadPatients() {
      this.loading = true
      try {
        const response = await patientAPI.getAllPatients()
        this.patients = response.data || []
      } catch (error) {
        this.$message.error('加载患者数据失败')
        console.error('Failed to load patients:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
    },
    handleReset() {
      this.searchForm = {
        name: '',
        phone: '',
        idCard: '',
        gender: ''
      }
      this.currentPage = 1
    },
    handleCurrentChange(page) {
      this.currentPage = page
    },
    handleAddPatient() {
      this.dialogTitle = '添加患者'
      this.patientForm = {
        id: '',
        name: '',
        gender: 'male',
        age: 0,
        birthDate: '',
        phone: '',
        email: '',
        idCard: '',
        address: ''
      }
      this.dialogVisible = true
    },
    handleEditPatient(patient) {
      this.dialogTitle = '编辑患者'
      this.patientForm = {
        id: patient.id,
        name: patient.name,
        gender: patient.gender,
        age: patient.age,
        birthDate: patient.birthDate,
        phone: patient.phone,
        email: patient.email,
        idCard: patient.idCard,
        address: patient.address
      }
      this.dialogVisible = true
    },
    handleViewDetail(patient) {
      // 复制一份数据以避免直接修改原始数据
      this.selectedPatient = JSON.parse(JSON.stringify(patient))
      this.detailDialogVisible = true
    },
    handleDeletePatient(patient) {
      this.$confirm(`确定要删除患者"${patient.name}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await patientAPI.deletePatient(patient.id)
          this.$message.success('删除成功')
          // 重新加载患者列表
          this.loadPatients()
        } catch (error) {
          this.$message.error('删除失败')
          console.error('Failed to delete patient:', error)
        }
      }).catch(() => {
        // 取消操作
      })
    },
    handleBatchDelete() {
      this.$confirm(`确定要删除选中的 ${this.selectedRows.length} 个患者吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const idsToDelete = this.selectedRows.map(row => row.id)
          await patientAPI.batchDeletePatients(idsToDelete)
          this.$message.success('批量删除成功')
          this.selectedRows = []
          // 重新加载患者列表
          this.loadPatients()
        } catch (error) {
          this.$message.error('批量删除失败')
          console.error('Failed to batch delete patients:', error)
        }
      }).catch(() => {
        // 取消操作
      })
    },
    handleBatchExport() {
      this.$message.success('导出成功')
      // 这里应该实现导出功能，暂时只显示提示
    },
    async handleSubmit() {
      this.$refs.patientForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.patientForm.id) {
              // 编辑模式
              await patientAPI.updatePatient(this.patientForm.id, this.patientForm)
              this.$message.success('编辑成功')
            } else {
              // 添加模式
              await patientAPI.addPatient(this.patientForm)
              this.$message.success('添加成功')
            }
            
            this.dialogVisible = false
            // 重新加载患者列表
            this.loadPatients()
          } catch (error) {
            this.$message.error(this.patientForm.id ? '编辑失败' : '添加失败')
            console.error('Failed to submit patient:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    calculateAge() {
      if (this.patientForm.birthDate) {
        const birthDate = new Date(this.patientForm.birthDate)
        const today = new Date()
        let age = today.getFullYear() - birthDate.getFullYear()
        const monthDiff = today.getMonth() - birthDate.getMonth()
        
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--
        }
        
        this.patientForm.age = age
      }
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
    // 查看患者预约详情时可以获取真实数据
    async handleViewDetail(patient) {
      try {
        const response = await patientAPI.getPatientAppointments(patient.id)
        this.selectedPatient = {
          ...JSON.parse(JSON.stringify(patient)),
          recentAppointments: response.data || []
        }
      } catch (error) {
        // 如果获取预约记录失败，使用本地数据
        this.selectedPatient = JSON.parse(JSON.stringify(patient))
      }
      this.detailDialogVisible = true
    }
  }
}
</script>

<style scoped>
.patient-management {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.header-actions .el-button {
  margin-left: 10px;
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

.patient-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.recent-appointments {
  margin-top: 20px;
}

.recent-appointments h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .patient-management {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    margin-top: 15px;
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