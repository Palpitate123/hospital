<template>
  <div class="patient-department-detail">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/patient/department' }">科室列表</el-breadcrumb-item>
      <el-breadcrumb-item>{{ department.deptName }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="dept-info-card">
      <div class="dept-header">
        <div class="dept-icon">
          <i class="el-icon-first-aid-kit"></i>
        </div>
        <div class="dept-content">
          <h2>{{ department.deptName }}</h2>
          <p class="dept-intro">{{ department.introduction }}</p>
        </div>
      </div>
    </el-card>

    <el-card class="doctor-card">
      <div slot="header">
        <span>科室医生</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="doctor in doctorList" :key="doctor.id">
          <div class="doctor-item" @click="handleSelectDoctor(doctor)">
            <el-avatar :size="80" :src="doctor.avatar || defaultAvatar"></el-avatar>
            <div class="doctor-info">
              <h4>{{ doctor.doctorName }}</h4>
              <p class="title">{{ doctor.title }}</p>
              <p class="specialty">{{ doctor.specialty }}</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'PatientDepartmentDetail',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      department: {
        id: 1,
        deptName: '内科',
        introduction: '内科是临床医学的一个专科，几乎是所有其他临床医学的基础，主要诊治内脏器官疾病。内科医生通过问诊、体格检查、实验室检查等手段诊断疾病，并采用药物治疗、介入治疗等方法进行治疗。'
      },
      doctorList: [
        { id: 1, doctorName: '王医生', title: '主任医师', specialty: '心血管疾病', avatar: '' },
        { id: 2, doctorName: '李医生', title: '副主任医师', specialty: '呼吸系统疾病', avatar: '' },
        { id: 3, doctorName: '张医生', title: '主治医师', specialty: '消化系统疾病', avatar: '' },
        { id: 4, doctorName: '刘医生', title: '主治医师', specialty: '内分泌疾病', avatar: '' }
      ]
    }
  },
  created() {
    const deptId = this.$route.params.id
    if (deptId) {
      this.loadDepartmentInfo(deptId)
    }
  },
  methods: {
    loadDepartmentInfo(deptId) {
    },
    handleSelectDoctor(doctor) {
      this.$router.push(`/patient/doctor/${doctor.id}`)
    }
  }
}
</script>

<style scoped>
.patient-department-detail {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.dept-info-card {
  margin-bottom: 20px;
}
.dept-header {
  display: flex;
  align-items: center;
}
.dept-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}
.dept-icon i {
  font-size: 40px;
  color: #fff;
}
.dept-content h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #303133;
}
.dept-intro {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}
.doctor-card {
  margin-bottom: 20px;
}
.doctor-item {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}
.doctor-item:hover {
  background: #f5f7fa;
}
.doctor-info h4 {
  margin: 10px 0 5px;
  font-size: 16px;
  color: #303133;
}
.doctor-info .title {
  margin: 0;
  font-size: 14px;
  color: #409EFF;
}
.doctor-info .specialty {
  margin: 5px 0 0;
  font-size: 13px;
  color: #909399;
}
</style>
