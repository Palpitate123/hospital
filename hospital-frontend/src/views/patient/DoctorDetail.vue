<template>
  <div class="patient-doctor-detail">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/patient/doctor' }">医生列表</el-breadcrumb-item>
      <el-breadcrumb-item>{{ doctor.doctorName }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="doctor-card">
      <div class="doctor-header">
        <el-avatar :size="120" :src="doctor.avatar || defaultAvatar"></el-avatar>
        <div class="doctor-info">
          <h2>{{ doctor.doctorName }} <span class="title">{{ doctor.title }}</span></h2>
          <p class="department"><i class="el-icon-location-outline"></i> {{ doctor.deptName }}</p>
          <p class="specialty"><i class="el-icon-star-on"></i> 擅长: {{ doctor.specialty }}</p>
          <p class="introduction">{{ doctor.introduction }}</p>
        </div>
        <div class="action-area">
          <el-button type="primary" size="large" @click="handleAppointment">立即预约</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="schedule-card">
      <div slot="header">
        <span>排班信息</span>
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          value-format="yyyy-MM-dd"
          :picker-options="datePickerOptions"
          @change="loadSchedules"
          style="float: right"
        ></el-date-picker>
      </div>
      <el-table :data="scheduleList" style="width: 100%">
        <el-table-column prop="scheduleDate" label="日期" width="120"></el-table-column>
        <el-table-column prop="timePeriod" label="时段" width="100">
          <template slot-scope="scope">
            {{ scope.row.timePeriod === 'morning' ? '上午' : '下午' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalNumber" label="总号源" width="100"></el-table-column>
        <el-table-column prop="remainingNumber" label="剩余号源" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.remainingNumber > 0 ? 'success' : 'danger'">
              {{ scope.row.remainingNumber }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '可预约' : '停诊' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button 
              type="primary" 
              size="small" 
              :disabled="scope.row.remainingNumber <= 0 || scope.row.status !== 1"
              @click="handleBook(scope.row)"
            >
              预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'PatientDoctorDetail',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      doctor: {
        id: 1,
        doctorName: '王医生',
        title: '主任医师',
        deptName: '内科',
        specialty: '心血管疾病、高血压、糖尿病等内科常见病',
        introduction: '从医20年，具有丰富的临床经验，擅长心血管疾病的诊治。',
        avatar: ''
      },
      selectedDate: '',
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      },
      scheduleList: [
        { id: 1, scheduleDate: '2024-01-15', timePeriod: 'morning', totalNumber: 20, remainingNumber: 8, status: 1 },
        { id: 2, scheduleDate: '2024-01-15', timePeriod: 'afternoon', totalNumber: 20, remainingNumber: 15, status: 1 },
        { id: 3, scheduleDate: '2024-01-16', timePeriod: 'morning', totalNumber: 20, remainingNumber: 0, status: 1 },
        { id: 4, scheduleDate: '2024-01-16', timePeriod: 'afternoon', totalNumber: 20, remainingNumber: 5, status: 0 }
      ]
    }
  },
  created() {
    const doctorId = this.$route.params.id
    if (doctorId) {
      this.loadDoctorInfo(doctorId)
    }
  },
  methods: {
    loadDoctorInfo(doctorId) {
    },
    loadSchedules() {
    },
    handleAppointment() {
      this.$router.push(`/patient/appointment?doctorId=${this.doctor.id}`)
    },
    handleBook(schedule) {
      this.$router.push(`/patient/appointment?doctorId=${this.doctor.id}&scheduleId=${schedule.id}`)
    }
  }
}
</script>

<style scoped>
.patient-doctor-detail {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.doctor-card {
  margin-bottom: 20px;
}
.doctor-header {
  display: flex;
  align-items: flex-start;
}
.doctor-info {
  flex: 1;
  margin-left: 30px;
}
.doctor-info h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #303133;
}
.doctor-info h2 .title {
  font-size: 16px;
  color: #409EFF;
  font-weight: normal;
  margin-left: 10px;
}
.doctor-info p {
  margin: 8px 0;
  color: #606266;
}
.doctor-info .specialty {
  color: #E6A23C;
}
.doctor-info .introduction {
  color: #909399;
  line-height: 1.6;
}
.action-area {
  margin-left: 30px;
}
.schedule-card {
  margin-bottom: 20px;
}
</style>
