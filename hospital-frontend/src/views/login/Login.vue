<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
      <div class="bg-shape shape-4"></div>
    </div>
    
    <div class="login-content">
      <div class="login-left">
        <div class="welcome-section">
          <h1 class="welcome-title">医院预约挂号系统</h1>
          <p class="welcome-subtitle">Hospital Appointment System</p>
          <div class="welcome-features">
            <div class="feature-item">
              <i class="el-icon-time"></i>
              <span>24小时在线预约</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-user"></i>
              <span>专家号源实时查询</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-document-checked"></i>
              <span>便捷的就诊流程</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-card">
          <div class="login-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号信息</p>
          </div>
          
          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
                @keyup.enter.native="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
            
            <div class="login-footer">
              <span>还没有账号？</span>
              <router-link to="/register" class="register-link">立即注册</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
    
    <div class="copyright">
      <p>© 2026 医院预约挂号系统 | 毕业设计作品</p>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    ...mapActions('user', ['login']),
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        
        this.loading = true
        try {
          await this.login(this.loginForm)
          this.$message.success('登录成功')
          
          const roleCode = this.$store.getters.roleCode
          if (roleCode === 'admin') {
            this.$router.push('/admin/dashboard')
          } else if (roleCode === 'doctor') {
            this.$router.push('/doctor/dashboard')
          } else {
            this.$router.push('/home')
          }
        } catch (error) {
          console.error('登录失败:', error)
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
  
  .bg-shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    animation: float 20s infinite ease-in-out;
  }
  
  .shape-1 {
    width: 400px;
    height: 400px;
    background: #fff;
    top: -100px;
    left: -100px;
    animation-delay: 0s;
  }
  
  .shape-2 {
    width: 300px;
    height: 300px;
    background: #fff;
    top: 50%;
    right: -50px;
    animation-delay: -5s;
  }
  
  .shape-3 {
    width: 200px;
    height: 200px;
    background: #fff;
    bottom: 100px;
    left: 20%;
    animation-delay: -10s;
  }
  
  .shape-4 {
    width: 150px;
    height: 150px;
    background: #fff;
    top: 30%;
    left: 40%;
    animation-delay: -15s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.login-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1;
  max-width: 500px;
  padding-right: 60px;
  
  .welcome-section {
    color: #fff;
  }
  
  .welcome-title {
    font-size: 42px;
    font-weight: 700;
    margin-bottom: 10px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  }
  
  .welcome-subtitle {
    font-size: 18px;
    opacity: 0.9;
    margin-bottom: 40px;
    letter-spacing: 2px;
  }
  
  .welcome-features {
    .feature-item {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      font-size: 16px;
      
      i {
        font-size: 24px;
        margin-right: 15px;
        width: 40px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 50%;
      }
    }
  }
}

.login-right {
  flex: 0 0 400px;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  
  .login-header {
    text-align: center;
    margin-bottom: 30px;
    
    h2 {
      font-size: 28px;
      color: #333;
      margin-bottom: 10px;
    }
    
    p {
      color: #999;
      font-size: 14px;
    }
  }
  
  .login-form {
    .el-form-item {
      margin-bottom: 25px;
    }
    
    .el-input {
      ::v-deep .el-input__inner {
        height: 48px;
        line-height: 48px;
        border-radius: 8px;
        padding-left: 45px;
        font-size: 15px;
        border: 2px solid #e8e8e8;
        transition: all 0.3s;
        
        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
      
      ::v-deep .el-input__prefix {
        left: 15px;
        font-size: 18px;
        color: #999;
      }
    }
    
    .login-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
      border-radius: 8px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      letter-spacing: 5px;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
      }
    }
  }
  
  .login-footer {
    text-align: center;
    margin-top: 20px;
    color: #999;
    font-size: 14px;
    
    .register-link {
      color: #667eea;
      font-weight: 500;
      margin-left: 5px;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.copyright {
  text-align: center;
  padding: 20px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  position: relative;
  z-index: 1;
}

@media (max-width: 992px) {
  .login-left {
    display: none;
  }
  
  .login-right {
    flex: 0 0 100%;
    max-width: 400px;
  }
}
</style>
