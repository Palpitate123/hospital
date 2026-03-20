<template>
  <div class="patient-doctor">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>医生列表</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="科室">
          <el-select v-model="queryParams.deptId" placeholder="请选择科室" clearable @change="handleQuery">
            <el-option v-for="dept in departmentOptions" :key="dept.id" :label="dept.deptName" :value="dept.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="queryParams.title" placeholder="请选择职称" clearable @change="handleQuery">
            <el-option label="主任医师" value="主任医师"></el-option>
            <el-option label="副主任医师" value="副主任医师"></el-option>
            <el-option label="主治医师" value="主治医师"></el-option>
            <el-option label="住院医师" value="住院医师"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="搜索">
          <el-input v-model="queryParams.keyword" placeholder="医生姓名" @keyup.enter.native="handleQuery"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" class="doctor-list">
      <el-col :span="6" v-for="doctor in doctorList" :key="doctor.id">
        <el-card class="doctor-card" shadow="hover" @click.native="handleSelect(doctor)">
          <div class="doctor-avatar">
            <el-avatar :size="80" :src="doctor.avatar || defaultAvatar"></el-avatar>
          </div>
          <div class="doctor-info">
            <h3>{{ doctor.doctorName }}</h3>
            <p class="title">{{ doctor.title }}</p>
            <p class="department">{{ doctor.deptName }}</p>
            <p class="specialty">擅长: {{ doctor.specialty }}</p>
          </div>
          <div class="doctor-action">
            <el-button type="primary" size="small" @click.stop="handleAppointment(doctor)">立即预约</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="queryParams.pageNum"
      :page-sizes="[8, 16, 24]"
      :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next"
      :total="total"
      class="pagination"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: 'PatientDoctor',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      queryParams: {
        deptId: null,
        title: '',
        keyword: '',
        pageNum: 1,
        pageSize: 8
      },
      departmentOptions: [
        { id: 1, deptName: '内科' },
        { id: 2, deptName: '外科' },
        { id: 3, deptName: '儿科' },
        { id: 4, deptName: '妇产科' }
      ],
      doctorList: [
        { id: 1, doctorName: '王医生', title: '主任医师', deptName: '内科', specialty: '心血管疾病、高血压', avatar: '' },
        { id: 2, doctorName: '李医生', title: '副主任医师', deptName: '内科', specialty: '呼吸系统疾病', avatar: '' },
        { id: 3, doctorName: '张医生', title: '主治医师', deptName: '外科', specialty: '普外科手术', avatar: '' },
        { id: 4, doctorName: '刘医生', title: '主治医师', deptName: '儿科', specialty: '小儿呼吸系统疾病', avatar: '' },
        { id: 5, doctorName: '陈医生', title: '副主任医师', deptName: '妇产科', specialty: '妇科疾病', avatar: '' },
        { id: 6, doctorName: '赵医生', title: '主任医师', deptName: '骨科', specialty: '骨折、关节疾病', avatar: '' },
        { id: 7, doctorName: '孙医生', title: '主治医师', deptName: '眼科', specialty: '眼部疾病', avatar: '' },
        { id: 8, doctorName: '周医生', title: '副主任医师', deptName: '耳鼻喉科', specialty: '耳鼻喉疾病', avatar: '' }
      ],
      total: 8
    }
  },
  methods: {
    handleQuery() {
      this.queryParams.pageNum = 1
      this.loadDoctors()
    },
    loadDoctors() {
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadDoctors()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.loadDoctors()
    },
    handleSelect(doctor) {
      this.$router.push(`/patient/doctor/${doctor.id}`)
    },
    handleAppointment(doctor) {
      this.$router.push(`/patient/appointment?doctorId=${doctor.id}`)
    }
  }
}
</script>

<style scoped>
.patient-doctor {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.doctor-list {
  margin-bottom: 20px;
}
.doctor-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  text-align: center;
}
.doctor-card:hover {
  transform: translateY(-5px);
}
.doctor-avatar {
  margin-bottom: 15px;
}
.doctor-info h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #303133;
}
.doctor-info .title {
  margin: 0;
  font-size: 14px;
  color: #409EFF;
}
.doctor-info .department {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
}
.doctor-info .specialty {
  margin: 0;
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.doctor-action {
  margin-top: 15px;
}
.pagination {
  text-align: center;
}
</style>
