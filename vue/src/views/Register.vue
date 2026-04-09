<template>
  <div class="container">
    <div style="width: 400px; padding: 30px; background-color: white; border-radius: 5px;">
      <div style="text-align: center; font-size: 20px; margin-bottom: 20px; color: #333">欢迎注册医院预约挂号系统</div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPass">
          <el-input prefix-icon="el-icon-lock" placeholder="请确认密码" show-password  v-model="form.confirmPass"></el-input>
        </el-form-item>
        <el-form-item>
          <div style="display: flex; gap: 10px;">
            <el-button
              class="btn-gradient"
              @click="register"
            >
              注 册
            </el-button>
            <el-button
              class="btn-gradient"
              @click="toLogin"
            >
              登 录
            </el-button
          ></div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    const validatePassword = (rule, confirmPass, callback) => {
      if (confirmPass === '') {
        callback(new Error('请确认密码'))
      } else if (confirmPass !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: { role: 'USER' },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        confirmPass: [
          { validator: validatePassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {},
  methods: {
    register() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          this.$request.post('/register', this.form).then(res => {
            if (res.code === '200') {
              this.$router.push('/login')
              this.$message.success('注册成功')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    toLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  /* 和登录页一致，移除背景图，替换为淡绿色纯色背景 */
  background-color: #f0f7f2;
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

:deep(.btn-gradient) {
  flex: 1;
  background: linear-gradient(to right, #67b7a7, #429888);
  border: none !important;
  color: white !important;
  border-radius: 4px;
  transition: all 0.3s ease;
}
:deep(.btn-gradient:hover), 
:deep(.btn-gradient:focus) {
  background: linear-gradient(to right, #58a999, #388778);
  color: white !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(126, 180, 136, 0.3);
}
:deep(.btn-gradient:active) {
  transform: translateY(0);
  box-shadow: none;
}

a {
  color: #2a60c9;
  text-decoration: none;
}
</style>