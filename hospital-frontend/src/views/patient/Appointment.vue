<template>
  <div class="patient-appointment">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>预约挂号</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="step-card">
      <el-steps :active="activeStep" finish-status="success" simple>
        <el-step title="选择医生"></el-step>
        <el-step title="选择时间"></el-step>
        <el-step title="填写信息"></el-step>
        <el-step title="确认预约"></el-step>
      </el-steps>
    </el-card>

    <el-card v-if="activeStep === 0" class="content-card">
      <div slot="header">
        <span>选择医生</span>
      </div>
      <el-form :inline="true" class="search-form">
        <el-form-item label="科室">
          <el-select v-model="queryParams.deptId" placeholder="请选择科室" @change="handleDeptChange">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="doctorList" style="width: 100%" highlight-current-row @current-change="handleDoctorSelect">
        <el-table-column prop="doctorName" label="医生姓名" width="120"></el-table-column>
        <el-table-column prop="deptName" label="科室" width="120"></el-table-column>
        <el-table-column prop="title" label="职称" width="120"></el-table-column>
        <el-table-column prop="specialty" label="擅长"></el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="activeStep === 1" class="content-card">
      <div slot="header">
        <span>选择时间 - {{ selectedDoctor.doctorName }}</span>
      </div>
      <el-calendar v-model="selectedDate">
        <template slot="dateCell" slot-scope="{date, data}">
          <div class="calendar-cell" @click="handleDateSelect(data.day)">
            {{ data.day.split('-')[2] }}
            <div v-if="hasSchedule(data.day)" class="schedule-dot"></div>
          </div>
        </template>
      </el-calendar>
      <div class="time-slots" v-if="scheduleList.length > 0">
        <h4>选择时段</h4>
        <el-radio-group v-model="selectedSchedule">
          <el-radio 
            v-for="schedule in scheduleList" 
            :key="schedule.id" 
            :label="schedule.id"
            :disabled="schedule.remainingNumber <= 0"
            border
          >
            {{ schedule.timePeriod === 'morning' ? '上午' : '下午' }} (剩余{{ schedule.remainingNumber }}号)
          </el-radio>
        </el-radio-group>
      </div>
    </el-card>

    <el-card v-if="activeStep === 2" class="content-card">
      <div slot="header">
        <span>填写患者信息</span>
      </div>
      <el-form :model="patientForm" :rules="patientRules" ref="patientFormRef" label-width="100px" style="max-width: 500px">
        <el-form-item label="患者姓名" prop="patientName">
          <el-input v-model="patientForm.patientName"></el-input>
        </el-form-item>
        <el-form-item label="身份证号" prop="patientIdCard">
          <el-input v-model="patientForm.patientIdCard"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="patientPhone">
          <el-input v-model="patientForm.patientPhone"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="patientForm.remark" rows="3"></el-input>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="activeStep === 3" class="content-card">
      <div slot="header">
        <span>确认预约信息</span>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="医生">{{ selectedDoctor.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ selectedDoctor.deptName }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ selectedScheduleInfo.scheduleDate }}</el-descriptions-item>
        <el-descriptions-item label="时段">{{ selectedScheduleInfo.timePeriod === 'morning' ? '上午' : '下午' }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ patientForm.patientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ patientForm.patientPhone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ patientForm.patientIdCard }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ patientForm.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <div class="action-buttons">
      <el-button v-if="activeStep > 0" @click="prevStep">上一步</el-button>
      <el-button v-if="activeStep < 3" type="primary" @click="nextStep">下一步</el-button>
      <el-button v-if="activeStep === 3" type="success" @click="handleSubmit">确认预约</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PatientAppointment',
  data() {
    return {
      activeStep: 0,
      queryParams: {
        deptId: null
      },
      departmentList: [
        { id: 1, deptName: '内科' },
        { id: 2, deptName: '外科' },
        { id: 3, deptName: '儿科' },
        { id: 4, deptName: '妇产科' }
      ],
      doctorList: [
        { id: 1, doctorName: '王医生', deptName: '内科', title: '主任医师', specialty: '心血管疾病' },
        { id: 2, doctorName: '李医生', deptName: '内科', title: '副主任医师', specialty: '呼吸系统疾病' }
      ],
      selectedDoctor: {},
      selectedDate: new Date(),
      scheduleList: [
        { id: 1, scheduleDate: '2024-01-15', timePeriod: 'morning', remainingNumber: 8 },
        { id: 2, scheduleDate: '2024-01-15', timePeriod: 'afternoon', remainingNumber: 15 }
      ],
      selectedSchedule: null,
      selectedScheduleInfo: {},
      patientForm: {
        patientName: '',
        patientIdCard: '',
        patientPhone: '',
        remark: ''
      },
      patientRules: {
        patientName: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
        patientIdCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
        patientPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      }
    }
  },
  created() {
    const doctorId = this.$route.query.doctorId
    if (doctorId) {
      this.selectedDoctor = { id: doctorId, doctorName: '王医生', deptName: '内科', title: '主任医师' }
      this.activeStep = 1
    }
  },
  methods: {
    handleDeptChange() {
      this.loadDoctors()
    },
    loadDoctors() {
    },
    handleDoctorSelect(row) {
      this.selectedDoctor = row
    },
    hasSchedule(day) {
      return true
    },
    handleDateSelect(day) {
      this.loadSchedules(day)
    },
    loadSchedules(date) {
    },
    prevStep() {
      this.activeStep--
    },
    nextStep() {
      if (this.activeStep === 0 && !this.selectedDoctor.id) {
        this.$message.warning('请选择医生')
        return
      }
      if (this.activeStep === 1 && !this.selectedSchedule) {
        this.$message.warning('请选择时段')
        return
      }
      if (this.activeStep === 2) {
        this.$refs.patientFormRef.validate(valid => {
          if (valid) {
            this.activeStep++
          }
        })
        return
      }
      this.activeStep++
    },
    handleSubmit() {
      this.$confirm('确认提交预约?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('预约成功')
        this.$router.push('/patient/my-orders')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.patient-appointment {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.step-card {
  margin-bottom: 20px;
}
.content-card {
  margin-bottom: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.calendar-cell {
  position: relative;
  height: 100%;
  cursor: pointer;
}
.schedule-dot {
  position: absolute;
  bottom: 5px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  background: #67C23A;
  border-radius: 50%;
}
.time-slots {
  margin-top: 20px;
}
.time-slots h4 {
  margin-bottom: 15px;
}
.time-slots .el-radio {
  margin-right: 15px;
  margin-bottom: 10px;
}
.action-buttons {
  text-align: center;
  margin-top: 20px;
}
</style>
