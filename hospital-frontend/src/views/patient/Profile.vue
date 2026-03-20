<template>
  <div class="patient-profile">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>个人中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              action="/api/file/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar"></el-avatar>
              <div class="avatar-edit">
                <i class="el-icon-edit"></i>
              </div>
            </el-upload>
            <h3>{{ userInfo.realName || userInfo.userName }}</h3>
            <p class="user-type">普通用户</p>
          </div>
          <div class="stats-section">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalAppointments }}</div>
              <div class="stat-label">预约总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.completedAppointments }}</div>
              <div class="stat-label">已完成</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.cancelledAppointments }}</div>
              <div class="stat-label">已取消</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>基本信息</span>
          </div>
          <el-form :model="userInfo" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.userName" disabled></el-input>
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userInfo.realName"></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="userInfo.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userInfo.phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userInfo.email"></el-input>
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="userInfo.idCard"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="password-card">
          <div slot="header">
            <span>修改密码</span>
          </div>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePasswordSubmit">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'PatientProfile',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      userInfo: {
        avatar: '',
        userName: 'patient001',
        realName: '张三',
        gender: 1,
        phone: '13800138000',
        email: 'patient@example.com',
        idCard: '110101199001011234'
      },
      stats: {
        totalAppointments: 5,
        completedAppointments: 3,
        cancelledAppointments: 1
      },
      rules: {
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    beforeAvatarUpload(file) {
      const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    handleAvatarSuccess(response) {
      if (response.code === 200) {
        this.userInfo.avatar = response.data
        this.$message.success('头像上传成功')
      }
    },
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.$message.success('保存成功')
        }
      })
    },
    handlePasswordSubmit() {
      this.$refs.passwordFormRef.validate(valid => {
        if (valid) {
          this.$message.success('密码修改成功')
          this.passwordForm = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.patient-profile {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.profile-card {
  text-align: center;
}
.avatar-section {
  position: relative;
  padding: 20px 0;
}
.avatar-uploader {
  display: inline-block;
  position: relative;
}
.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 30px;
  height: 30px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.avatar-section h3 {
  margin: 15px 0 5px;
  font-size: 18px;
  color: #303133;
}
.user-type {
  color: #909399;
  font-size: 14px;
}
.stats-section {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid #EBEEF5;
  margin-top: 15px;
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.password-card {
  margin-top: 20px;
}
</style>
