<template>
  <div class="user-info-wrapper">
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
    
    <!-- 主内容容器 -->
    <div class="user-info-container">
      <!-- 侧边导航栏 -->
      <div class="sidebar">
        <div class="user-avatar">
          <el-avatar :size="100" :src="userAvatar" icon="el-icon-user-solid"></el-avatar>
        </div>
        <h3 class="user-name">{{ user.name || user.username }}</h3>
        <p class="user-role">{{ userRoleText }}</p>
        <nav class="sidebar-menu">
          <div class="menu-item active">
            <i class="el-icon-info"></i>
            <span>基本信息</span>
          </div>
          <div class="menu-item">
            <i class="el-icon-lock"></i>
            <span>修改密码</span>
          </div>
          <router-link to="/appointment-list" class="menu-item">
            <i class="el-icon-document"></i>
            <span>预约记录</span>
          </router-link>
        </nav>
      </div>
      
      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="content-header">
          <h2>基本信息</h2>
        </div>
        <div class="content-body">
          <!-- 这里应该是基本信息表单 -->
          <el-form ref="userForm" :model="userForm" label-width="120px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="userForm.name"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="userForm.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" type="email"></el-input>
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="userForm.idCard"></el-input>
            </el-form-item>
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="userForm.birthDate" type="date" placeholder="选择日期" style="width: 100%;"></el-date-picker>
            </el-form-item>
            
            <!-- 医生特有信息 -->
            <el-form-item v-if="user && user.role === 'DOCTOR'" label="科室">
              <el-input v-model="departmentName" disabled></el-input>
            </el-form-item>
            <el-form-item v-if="user && user.role === 'DOCTOR'" label="职称" prop="title">
              <el-input v-model="userForm.title"></el-input>
            </el-form-item>
            <el-form-item v-if="user && user.role === 'DOCTOR'" label="专长" prop="specialty">
              <el-input v-model="userForm.specialty"></el-input>
            </el-form-item>
            <el-form-item v-if="user && user.role === 'DOCTOR'" label="简介" prop="introduction">
              <el-input v-model="userForm.introduction" type="textarea" :rows="4"></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleUpdate" :loading="loading">保存</el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserInfo',
  data() {
    return {
      user: null,
      userForm: {
        username: '',
        name: '',
        phone: '',
        gender: 'male',
        email: '',
        idCard: '',
        birthDate: '',
        title: '',
        specialty: '',
        introduction: ''
      },
      userAvatar: '',
      departmentName: '',
      loading: false,
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        email: [
          { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        idCard: [
          { pattern: /^\d{17}[\dXx]$/, message: '请输入正确的身份证号格式', trigger: 'blur' }
        ],
        title: [
          { required: this.user && this.user.role === 'DOCTOR', message: '请输入职称', trigger: 'blur' }
        ],
        specialty: [
          { required: this.user && this.user.role === 'DOCTOR', message: '请输入专长', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    userRoleText() {
      if (!this.user) return ''
      switch (this.user.role) {
        case 'PATIENT':
          return '患者'
        case 'DOCTOR':
          return '医生'
        case 'ADMIN':
          return '管理员'
        default:
          return ''
      }
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
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
    loadUserInfo() {
      // 从本地存储获取用户信息
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
        
        // 获取详细的用户信息
        this.$axios.get('/user/info').then(res => {
          if (res.success) {
            const userData = res.data
            // 更新表单数据
            this.userForm = {
              username: userData.username || '',
              name: userData.name || '',
              phone: userData.phone || '',
              gender: userData.gender || 'male',
              email: userData.email || '',
              idCard: userData.idCard || '',
              birthDate: userData.birthDate || '',
              title: userData.title || '',
              specialty: userData.specialty || '',
              introduction: userData.introduction || ''
            }
            
            // 如果是医生，获取科室信息
            if (this.user.role === 'DOCTOR' && userData.department) {
              this.departmentName = userData.department.name
            }
            
            // 设置头像
            this.userAvatar = userData.avatar || ''
          }
        })
      } else {
        // 未登录，跳转到登录页
        this.$router.push('/login')
      }
    },
    handleUpdate() {
      this.$refs.userForm.validate((valid) => {
        if (valid) {
          this.loading = true
          
          this.$axios.put('/user/update', this.userForm).then(res => {
            if (res.success) {
              // 更新本地存储的用户信息
              const updatedUser = { ...this.user, ...this.userForm }
              localStorage.setItem('user', JSON.stringify(updatedUser))
              
              this.$message.success('信息更新成功')
            } else {
              this.$message.error(res.message || '更新失败')
            }
          }).catch(err => {
            this.$message.error('更新失败，请稍后重试')
            console.error('更新用户信息失败:', err)
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    handleCancel() {
      // 重置表单
      this.loadUserInfo()
    }
  }
}
</script>

<style scoped>
.user-info-wrapper {
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

.user-info-container {
  flex: 1;
  min-height: calc(100vh - 60px);
  display: flex;
  background-color: #f5f7fa;
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  background-color: #fff;
  padding: 30px 20px;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  text-align: center;
  margin-bottom: 20px;
}

.user-name {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.user-role {
  text-align: center;
  color: #606266;
  margin-bottom: 30px;
}

.sidebar-menu .menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  margin-bottom: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.sidebar-menu .menu-item:hover {
  background-color: #ecf5ff;
  color: #1890ff;
}

.sidebar-menu .menu-item.active {
  background-color: #ecf5ff;
  color: #1890ff;
  font-weight: bold;
}

.sidebar-menu .menu-item i {
  margin-right: 10px;
  font-size: 16px;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 30px;
}

.content-header {
  margin-bottom: 30px;
}

.content-header h2 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.content-body {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 表单样式 */
.el-form {
  max-width: 600px;
}

.el-form-item {
  margin-bottom: 25px;
}

.el-button {
  margin-right: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-info-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar-menu {
    display: flex;
    overflow-x: auto;
  }
  
  .sidebar-menu .menu-item {
    margin-right: 10px;
    white-space: nowrap;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .content-body {
    padding: 20px;
  }
}
</style>
