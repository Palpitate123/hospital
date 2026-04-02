<template>
  <div class="login-container">
    <div class="glass-bg">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-content">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo-box">
            <i class="el-icon-first-aid-kit"></i>
          </div>
          <h1 class="brand-title">智慧健康管理平台</h1>
          <p class="brand-tagline">SMART HEALTH COMPANION</p>

          <div class="feature-list">
            <div class="feature-card">
              <i class="el-icon-monitor"></i>
              <div class="text">
                <h4>全流程在线</h4>
                <p>从预约到诊察，一站式搞定</p>
              </div>
            </div>
            <div class="feature-card">
              <i class="el-icon-medal"></i>
              <div class="text">
                <h4>专家资源</h4>
                <p>实时连接全市顶尖医疗专家</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="glass-card">
          <div class="card-header">
            <h2>欢迎登录</h2>
            <div class="sub-title">请输入您的账号信息以进入平台</div>
          </div>

          <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                  v-model="loginForm.username"
                  placeholder="用户名"
                  prefix-icon="el-icon-user"
                  size="large"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  prefix-icon="el-icon-lock"
                  size="large"
                  show-password
                  @keyup.enter.native="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                  type="primary"
                  :loading="loading"
                  class="submit-btn"
                  @click="handleLogin"
              >
                {{ loading ? '身份验证中...' : '登 录' }}
              </el-button>
            </el-form-item>

            <div class="card-footer">
              还没有账号？ <router-link to="/register">点击立即注册</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>

    <div class="copyright">
      © 2026 医院预约挂号系统 | 数字化健康服务平台
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
          // 调用 Vuex 的登录动作
          await this.login(this.loginForm)
          this.$message.success('登录成功')

          // 根据角色码进行路由跳转
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
          this.$message.error(error.response?.data?.message || '登录失败，请检查账号密码')
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
  height: 100vh;
  width: 100vw;
  background: #f0f4f8;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.glass-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  .circle {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.6;
  }
  .circle-1 { width: 500px; height: 500px; background: rgba(102, 126, 234, 0.4); top: -10%; left: -10%; }
  .circle-2 { width: 400px; height: 400px; background: rgba(118, 75, 162, 0.3); bottom: -5%; right: -5%; }
  .circle-3 { width: 300px; height: 300px; background: rgba(56, 239, 125, 0.3); top: 20%; right: 20%; }
}

.login-content {
  display: flex;
  width: 1000px;
  height: 600px;
  z-index: 10;
}

.login-left {
  flex: 1;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .logo-box {
    font-size: 40px;
    color: #409EFF;
    margin-bottom: 20px;
  }
  .brand-title {
    font-size: 36px;
    font-weight: 800;
    color: #2d3436;
    margin: 0;
  }
  .brand-tagline {
    letter-spacing: 4px;
    color: #b2bec3;
    font-weight: 600;
    margin-bottom: 50px;
    text-transform: uppercase;
  }
  .feature-card {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.6);
    padding: 20px;
    border-radius: 15px;
    margin-bottom: 20px;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
    i { font-size: 28px; color: #409EFF; margin-right: 20px; }
    h4 { margin: 0; color: #2d3436; font-size: 16px; }
    p { margin: 5px 0 0; font-size: 13px; color: #636e72; }
  }
}

.login-right {
  flex: 0 0 450px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.glass-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 30px;
  padding: 60px 40px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);

  .card-header {
    text-align: center;
    margin-bottom: 40px;
    h2 { font-size: 28px; margin: 0; color: #2d3436; }
    .sub-title { color: #b2bec3; margin-top: 10px; font-size: 14px; }
  }
}

.login-form {
  ::v-deep .el-input__inner {
    background: rgba(240, 244, 248, 0.8);
    border: none;
    border-radius: 12px;
    height: 54px;
    font-size: 15px;
    &:focus { background: #fff; box-shadow: 0 0 0 2px #409EFF33; }
  }

  .submit-btn {
    width: 100%;
    height: 54px;
    border-radius: 12px;
    font-size: 17px;
    font-weight: 600;
    background: linear-gradient(135deg, #409EFF 0%, #764ba2 100%);
    border: none;
    margin-top: 10px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 20px rgba(64, 158, 255, 0.3);
    }
  }

  .card-footer {
    text-align: center;
    margin-top: 30px;
    font-size: 14px;
    color: #636e72;
    a { color: #409EFF; font-weight: 600; text-decoration: none; margin-left: 5px; &:hover { text-decoration: underline; } }
  }
}

.copyright {
  position: absolute;
  bottom: 20px;
  color: #b2bec3;
  font-size: 12px;
}
</style>
