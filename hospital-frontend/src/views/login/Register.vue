<template>
  <div class="register-container">
    <div class="register-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <div class="register-content">
      <div class="register-card">
        <div class="register-header">
          <router-link to="/login" class="back-link">
            <i class="el-icon-arrow-left"></i>
            返回登录
          </router-link>
          <h2>用户注册</h2>
          <p>创建您的预约挂号账号</p>
        </div>
        
        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              size="large"
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="nickName">
            <el-input
              v-model="registerForm.nickName"
              placeholder="请输入昵称（选填）"
              prefix-icon="el-icon-postcard"
              size="large"
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password
              @keyup.enter.native="handleRegister"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="register-btn"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
          
          <div class="register-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </el-form>
        
        <div class="register-tips">
          <p>注册即表示您同意我们的服务条款和隐私政策</p>
        </div>
      </div>
    </div>
    
    <div class="copyright">
      <p>© 2026 医院预约挂号系统 | 毕业设计作品</p>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      registerForm: {
        username: '',
        nickName: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(async valid => {
        if (!valid) return
        
        this.loading = true
        try {
          await register(this.registerForm)
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (error) {
          console.error('注册失败:', error)
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.register-background {
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
    animation: float 25s infinite ease-in-out;
  }
  
  .shape-1 {
    width: 500px;
    height: 500px;
    background: #fff;
    top: -200px;
    right: -100px;
    animation-delay: 0s;
  }
  
  .shape-2 {
    width: 300px;
    height: 300px;
    background: #fff;
    bottom: -50px;
    left: -50px;
    animation-delay: -8s;
  }
  
  .shape-3 {
    width: 200px;
    height: 200px;
    background: #fff;
    top: 40%;
    left: 10%;
    animation-delay: -16s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-40px) rotate(180deg);
  }
}

.register-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  z-index: 1;
}

.register-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.25);
  
  .register-header {
    text-align: center;
    margin-bottom: 35px;
    position: relative;
    
    .back-link {
      position: absolute;
      left: 0;
      top: 0;
      color: #999;
      font-size: 14px;
      display: flex;
      align-items: center;
      transition: color 0.3s;
      
      i {
        margin-right: 5px;
      }
      
      &:hover {
        color: #11998e;
      }
    }
    
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
  
  .register-form {
    .el-form-item {
      margin-bottom: 22px;
    }
    
    .el-input {
      ::v-deep .el-input__inner {
        height: 48px;
        line-height: 48px;
        border-radius: 10px;
        padding-left: 45px;
        font-size: 15px;
        border: 2px solid #e8e8e8;
        transition: all 0.3s;
        
        &:focus {
          border-color: #11998e;
          box-shadow: 0 0 0 3px rgba(17, 153, 142, 0.1);
        }
      }
      
      ::v-deep .el-input__prefix {
        left: 15px;
        font-size: 18px;
        color: #999;
      }
    }
    
    .register-btn {
      width: 100%;
      height: 50px;
      font-size: 16px;
      border-radius: 10px;
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      border: none;
      letter-spacing: 5px;
      transition: all 0.3s;
      margin-top: 10px;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 12px 25px rgba(17, 153, 142, 0.4);
      }
    }
  }
  
  .register-footer {
    text-align: center;
    margin-top: 20px;
    color: #999;
    font-size: 14px;
    
    .login-link {
      color: #11998e;
      font-weight: 500;
      margin-left: 5px;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
  
  .register-tips {
    text-align: center;
    margin-top: 25px;
    padding-top: 20px;
    border-top: 1px solid #eee;
    
    p {
      color: #999;
      font-size: 12px;
    }
  }
}

.copyright {
  text-align: center;
  padding: 20px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  position: relative;
  z-index: 1;
}
</style>
