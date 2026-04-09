<template>
  <div class="container">
    <div style="width: 400px; padding: 30px; background-color: white; border-radius: 5px;">
      <div style="text-align: center; font-size: 20px; margin-bottom: 20px; color: #333">欢迎登录医院预约挂号系统</div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%;">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="患者" value="USER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <div style="display: flex; gap: 10px;">
            <el-button 
              class="btn-gradient"
              @click="login"
            >
              登 录
            </el-button>
            <el-button 
              class="btn-gradient"
              @click="toRegister"
            >
              注 册
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      form: {},
      dialogVisible: false,
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ]
      }
    }
  },
  created() {},
  methods: {
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))
              this.$router.push('/')
              this.$message.success('登录成功')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    toRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  /* 移除背景图，替换为淡绿色纯色背景，和按钮色调统一 */
  background-color: #f0f7f2;
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

:deep(.btn-gradient) {
  flex: 1;
  background: linear-gradient(to right, #7eb488, #5a9966);
  border: none !important;
  color: white !important;
  border-radius: 4px;
  transition: all 0.3s ease;
}
:deep(.btn-gradient:hover), 
:deep(.btn-gradient:focus) {
  background: linear-gradient(to right, #6ea97a, #4e8859);
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