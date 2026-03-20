<template>
  <div class="doctor-profile">
    <el-card>
      <div slot="header">
        <span>个人信息</span>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.userName" disabled></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="所属科室">
          <el-input v-model="form.departmentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="form.title" disabled></el-input>
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-input type="textarea" v-model="form.specialty" rows="3" disabled></el-input>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input type="textarea" v-model="form.introduction" rows="4"></el-input>
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
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" style="max-width: 400px">
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
  </div>
</template>

<script>
export default {
  name: 'DoctorProfile',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        avatar: '',
        userName: 'doctor001',
        realName: '王医生',
        gender: 1,
        phone: '13800138000',
        email: 'doctor@example.com',
        departmentName: '内科',
        title: '主治医师',
        specialty: '心血管疾病、高血压、糖尿病',
        introduction: '从医20年，具有丰富的临床经验。'
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
        this.form.avatar = response.data
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
.doctor-profile {
  padding: 0;
}
.password-card {
  margin-top: 20px;
}
.avatar-uploader >>> .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader >>> .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>
