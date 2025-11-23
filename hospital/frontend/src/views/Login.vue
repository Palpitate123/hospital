<template>
  <div class="login-container">
    <div class="login-form">
      <h2>医院预约系统登录</h2>
      <el-form ref="loginForm" :model="loginForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-radio-group v-model="loginForm.userType">
            <el-radio label="patient">患者</el-radio>
            <el-radio label="doctor">医生</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
          <el-button @click="goToRegister" class="register-btn">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '../api/request'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        userType: 'patient'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          request.post('/api/user/login', {
            username: this.loginForm.username,
            password: this.loginForm.password,
            userType: this.loginForm.userType
          }).then(res => {
            if (res.code === 200) {
              // 保存token和用户信息
              localStorage.setItem('token', res.data.token)
              // 构建前端需要的用户对象格式
              const userData = {
                username: res.data.username,
                realName: res.data.realName,
                role: res.data.roleCode.replace('ROLE_', '') // 将ROLE_ADMIN转换为ADMIN格式
              }
              localStorage.setItem('user', JSON.stringify(userData))
              
              // 根据用户角色跳转到相应页面
              if (res.data.roleCode === 'ROLE_ADMIN') {
                this.$router.push('/admin/dashboard')
              } else {
                this.$router.push('/home')
              }
              
              this.$message.success('登录成功')
            } else {
              this.$message.error(res.message || '登录失败')
            }
          }).catch(err => {
            this.$message.error('登录失败，请稍后重试')
            console.error('登录失败:', err)
          })
        }
      })
    },
    goToRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.login-btn {
  width: 60%;
  margin-right: 20px;
}

.register-btn {
  width: calc(40% - 20px);
}
</style>
