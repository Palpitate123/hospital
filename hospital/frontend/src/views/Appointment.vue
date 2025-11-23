<template>
  <div class="appointment-container">
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
          description="预约功能需要登录后使用，请先登录账号"
          type="info"
          show-icon
          :closable="false"
        >
          <router-link to="/login" slot="after-close">立即登录</router-link>
        </el-alert>
      </div>
      <div v-else>
        <div class="page-header">
          <h1>在线预约</h1>
          <p class="page-desc">选择合适的医生和时间进行预约</p>
        </div>
        <!-- 预约表单 -->
        <el-form :model="appointmentForm" :rules="rules" ref="appointmentFormRef" label-width="120px" class="appointment-form">
          <el-form-item label="选择科室" prop="departmentId">
            <el-select v-model="appointmentForm.departmentId" placeholder="请选择科室" @change="handleDepartmentChange">
              <el-option
                v-for="dept in departments"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="选择医生" prop="doctorId">
            <el-select v-model="appointmentForm.doctorId" placeholder="请先选择科室">
              <el-option
                v-for="doctor in filteredDoctors"
                :key="doctor.id"
                :label="doctor.name + ' - ' + doctor.title"
                :value="doctor.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="预约日期" prop="appointmentDate">
            <el-date-picker
              v-model="appointmentForm.appointmentDate"
              type="date"
              placeholder="请选择预约日期"
              format="yyyy-MM-dd"
              :disabled-date="disabledDate"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="预约时段" prop="appointmentTime">
            <el-select v-model="appointmentForm.appointmentTime" placeholder="请选择预约时段">
              <el-option
                v-for="timeSlot in availableTimeSlots"
                :key="timeSlot"
                :label="timeSlot"
                :value="timeSlot"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="症状描述" prop="symptoms">
            <el-input
              v-model="appointmentForm.symptoms"
              type="textarea"
              :rows="4"
              placeholder="请简要描述您的症状或就诊需求"
            ></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="contactPhone"
            :rules="[{ required: true, message: '请输入联系电话', trigger: 'blur' }, { validator: validatePhone, trigger: 'blur' }]"
          >
            <el-input v-model="appointmentForm.contactPhone" placeholder="请输入联系电话"></el-input>
          </el-form-item>
          <el-form-item class="submit-section">
            <el-button type="primary" @click="submitForm" :loading="submitting">提交预约</el-button>
            <el-button @click="resetForm">重置表单</el-button>
          </el-form-item>
        </el-form>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'Appointment',
  data() {
    return {
      user: null,
      appointmentForm: {
        departmentId: '',
        doctorId: '',
        appointmentDate: '',
        appointmentTime: '',
        symptoms: '',
        contactPhone: ''
      },
      departments: [],
      filteredDoctors: [],
      availableTimeSlots: ['上午', '下午'],
      rules: {},
      submitting: false
    }
  },
  created() {
    this.loadUserInfo()
    this.loadDepartments()
    
    // 检查URL参数中是否有科室或医生ID
    if (this.$route.query.departmentId) {
      this.appointmentForm.departmentId = this.$route.query.departmentId
      this.handleDepartmentChange()
    }
    if (this.$route.query.doctorId) {
      this.appointmentForm.doctorId = this.$route.query.doctorId
    }
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
        // 设置默认联系电话
        if (this.user.phone) {
          this.appointmentForm.contactPhone = this.user.phone
        }
      }
    },
    loadDepartments() {
      this.$axios.get('/department/list').then(res => {
        if (res.success) {
          this.departments = res.data
        }
      }).catch(err => {
        console.error('加载科室列表失败:', err)
        this.$message.error('加载科室列表失败')
      })
    },
    handleDepartmentChange() {
      if (this.appointmentForm.departmentId) {
        // 清空医生选择
        this.appointmentForm.doctorId = ''
        // 加载该科室的医生列表
        this.$axios.get('/doctor/list', { 
          params: { departmentId: this.appointmentForm.departmentId } 
        }).then(res => {
          if (res.success) {
            this.filteredDoctors = res.data
          }
        }).catch(err => {
          console.error('加载医生列表失败:', err)
          this.$message.error('加载医生列表失败')
        })
      }
    },
    disabledDate(time) {
      // 禁用日期逻辑：只能选择今天之后的日期
      return time.getTime() < Date.now() - 8.64e7
    },
    validatePhone(rule, value, callback) {
      // 电话号码验证
      const phoneReg = /^1[3-9]\d{9}$/
      if (!value) {
        callback(new Error('请输入联系电话'))
      } else if (!phoneReg.test(value)) {
        callback(new Error('请输入有效的手机号码'))
      } else {
        callback()
      }
    },
    submitForm() {
      this.$refs.appointmentFormRef.validate((valid) => {
        if (valid) {
          this.submitting = true
          // 准备提交数据
          const submitData = {
            ...this.appointmentForm,
            userId: this.user.id
          }
          
          this.$axios.post('/appointment/create', submitData).then(res => {
            if (res.success) {
              this.$message.success('预约成功！')
              this.resetForm()
              // 跳转到预约列表页
              this.$router.push('/appointment-list')
            } else {
              this.$message.error(res.message || '预约失败')
            }
          }).catch(err => {
            console.error('预约提交失败:', err)
            this.$message.error('预约提交失败，请重试')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    resetForm() {
      this.$refs.appointmentFormRef.resetFields()
      this.appointmentForm.doctorId = ''
      // 保留联系电话
      if (this.user && this.user.phone) {
        this.appointmentForm.contactPhone = this.user.phone
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
    }
  }
}
</script>

<style scoped>
.appointment-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 样式内容 */
</style>