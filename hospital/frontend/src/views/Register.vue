<template>
  <div class="register-container">
    <div class="register-form">
      <h2>医院预约系统注册</h2>
      <el-form ref="registerForm" :model="registerForm" :rules="rules" label-width="100px">
        <el-form-item label="用户类型">
          <el-radio-group v-model="registerForm.userType" @change="handleUserTypeChange">
            <el-radio label="patient">患者</el-radio>
            <el-radio label="doctor">医生</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 医生特有字段 -->
        <div v-if="registerForm.userType === 'doctor'">
          <el-form-item label="科室" prop="departmentId">
            <el-select v-model="registerForm.departmentId" placeholder="请选择科室">
              <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="职称" prop="title">
            <el-input v-model="registerForm.title" placeholder="请输入职称"></el-input>
          </el-form-item>
          <el-form-item label="专长" prop="specialty">
            <el-input v-model="registerForm.specialty" placeholder="请输入专长"></el-input>
          </el-form-item>
        </div>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="register-btn">注册</el-button>
          <el-button @click="goToLogin" class="login-btn">已有账号，去登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        phone: '',
        gender: 'male',
        userType: 'patient',
        departmentId: '',
        title: '',
        specialty: ''
      },
      departments: [],
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 4, max: 20, message: '用户名长度应在4-20个字符之间', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        departmentId: [
          { required: () => this.registerForm.userType === 'doctor', message: '请选择科室', trigger: 'blur' }
        ],
        title: [
          { required: () => this.registerForm.userType === 'doctor', message: '请输入职称', trigger: 'blur' }
        ],
        specialty: [
          { required: () => this.registerForm.userType === 'doctor', message: '请输入专长', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // 加载科室列表
    this.loadDepartments()
  },
  methods: {
    loadDepartments() {
      this.$axios.get('/api/department/list').then(res => {
        if (res.code === 200) {
          this.departments = res.data
        }
      })
    },
    handleUserTypeChange() {
      // 用户类型改变时，需要重新验证表单
      this.$nextTick(() => {
        this.$refs.registerForm.clearValidate()
      })
    },
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    handleRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          const url = this.registerForm.userType === 'doctor' 
            ? '/api/user/register-doctor' 
            : '/api/user/register'
          
          // 构建请求数据，只包含后端需要的字段
          const requestData = {
            username: this.registerForm.username,
            password: this.registerForm.password,
            realName: this.registerForm.realName,
            phone: this.registerForm.phone,
            gender: this.registerForm.gender
          }
          
          // 医生注册需要额外字段
          if (this.registerForm.userType === 'doctor') {
            requestData.deptId = this.registerForm.departmentId
            requestData.title = this.registerForm.title
            requestData.specialty = this.registerForm.specialty
          }
          
          this.$axios.post(url, requestData).then(res => {
            if (res.code === 200) {
              this.$message.success('注册成功，请登录')
              this.$router.push('/login')
            } else {
              this.$message.error(res.message || '注册失败')
            }
          }).catch(err => {
            console.error('注册失败:', err)
            this.$message.error('注册失败，请稍后重试')
          })
        }
      })
    },
    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.register-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 500px;
}

.register-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.register-btn {
  width: 60%;
  margin-right: 20px;
}

.login-btn {
  width: calc(40% - 20px);
}
</style>
