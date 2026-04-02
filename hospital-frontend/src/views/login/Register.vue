<template>
  <div class="register-container">
    <div class="glass-bg">
      <div class="circle circle-top"></div>
      <div class="circle circle-bottom"></div>
    </div>

    <div class="register-content">
      <div class="register-card">
        <div class="back-nav">
          <router-link to="/login"><i class="el-icon-back"></i> 返回登录</router-link>
        </div>

        <div class="header">
          <h2>创建账号</h2>
          <p>请填写以下信息以完成注册</p>
        </div>

        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-position="top">
          <div class="form-row">
            <el-form-item prop="username" label="登录账号" class="flex-1">
              <el-input v-model="registerForm.username" prefix-icon="el-icon-user" placeholder="3-20位字符" />
            </el-form-item>
            <el-form-item prop="nickName" label="用户昵称" class="flex-1">
              <el-input v-model="registerForm.nickName" prefix-icon="el-icon-postcard" placeholder="您的称呼" />
            </el-form-item>
          </div>

          <el-form-item prop="password" label="设置密码">
            <el-input v-model="registerForm.password" type="password" prefix-icon="el-icon-lock" show-password placeholder="至少6位密码" />
          </el-form-item>

          <el-form-item prop="confirmPassword" label="确认密码">
            <el-input v-model="registerForm.confirmPassword" type="password" prefix-icon="el-icon-circle-check" show-password placeholder="请再次输入密码" />
          </el-form-item>

          <el-button type="primary" class="reg-btn" :loading="loading" @click="handleRegister">
            {{ loading ? '提交中...' : '注 册' }}
          </el-button>

          <div class="footer-links">
            已经有账号了？ <router-link to="/login">去登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth'

export default {
  name: 'Register',
  data() {
    const checkPwd = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
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
          { validator: checkPwd, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async handleRegister() {
      this.$refs.registerForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await register(this.registerForm)
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
          console.error(e)
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
  height: 100vh;
  width: 100vw;
  background: #eef2f7;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.glass-bg {
  .circle { position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.5; }
  .circle-top { width: 600px; height: 600px; background: rgba(17, 153, 142, 0.2); top: -20%; right: -10%; }
  .circle-bottom { width: 500px; height: 500px; background: rgba(56, 239, 125, 0.2); bottom: -10%; left: -5%; }
}

.register-content { z-index: 10; width: 550px; }

.register-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(25px);
  padding: 45px;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 40px 80px -20px rgba(0,0,0,0.1);

  .back-nav a {
    color: #94a3b8;
    text-decoration: none;
    font-size: 14px;
    display: flex;
    align-items: center;
    transition: color 0.3s;
    i { margin-right: 5px; }
    &:hover { color: #11998e; }
  }

  .header {
    text-align: center;
    margin: 30px 0;
    h2 { font-size: 30px; margin: 0; color: #1e293b; }
    p { color: #94a3b8; margin-top: 10px; font-size: 14px; }
  }
}

.form-row { display: flex; gap: 20px; .flex-1 { flex: 1; } }

::v-deep .el-form-item__label {
  font-weight: 600;
  color: #64748b;
  padding: 0 0 8px;
  font-size: 14px;
}

::v-deep .el-input__inner {
  border-radius: 12px;
  height: 50px;
  border: 1px solid #e2e8f0;
  background: #fff;
  transition: all 0.3s;
  &:focus { border-color: #11998e; box-shadow: 0 0 0 3px rgba(17, 153, 142, 0.1); }
}

.reg-btn {
  width: 100%;
  height: 55px;
  border-radius: 15px;
  font-size: 17px;
  font-weight: 700;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  margin-top: 25px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(17, 153, 142, 0.2);
  }
}

.footer-links {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #94a3b8;
  a { color: #11998e; font-weight: 600; text-decoration: none; margin-left: 5px; }
}
</style>
