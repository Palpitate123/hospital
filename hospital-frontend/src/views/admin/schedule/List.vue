<template>
  <div class="schedule-page">
    <div class="page-header">
      <h2>排班管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增排班</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="科室">
          <el-select v-model="searchForm.deptId" placeholder="请选择科室" clearable>
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名" clearable />
        </el-form-item>
        <el-form-item label="排班状态">
          <el-select v-model="searchForm.scheduleStatus" placeholder="请选择状态" clearable>
            <el-option label="未发布" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="doctorName" label="医生姓名" width="120">
        <template slot-scope="scope">{{ scope.row.doctorName || scope.row.doctor_name }}</template>
      </el-table-column>
      <el-table-column prop="deptName" label="科室" width="120">
        <template slot-scope="scope">{{ scope.row.deptName || scope.row.dept_name }}</template>
      </el-table-column>

      <el-table-column prop="workDate" label="出诊日期" min-width="120">
        <template slot-scope="scope">{{ scope.row.workDate || scope.row.work_date }}</template>
      </el-table-column>

      <el-table-column prop="workTime" label="时段" width="100">
        <template slot-scope="scope">{{ scope.row.workTime || scope.row.work_time }}</template>
      </el-table-column>
      <el-table-column prop="totalNumber" label="号源总量" width="100">
        <template slot-scope="scope">{{ scope.row.totalNumber || scope.row.total_number }}</template>
      </el-table-column>
      <el-table-column prop="remainNumber" label="剩余号源" width="100">
        <template slot-scope="scope">
          <span :style="{ color: (scope.row.remainNumber || scope.row.remain_number) <= 5 ? '#F56C6C' : '#67C23A' }">
            {{ scope.row.remainNumber || scope.row.remain_number }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="排班状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(getNormalizedStatus(scope.row))">
            {{ getStatusText(getNormalizedStatus(scope.row)) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="250" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)" v-if="getNormalizedStatus(scope.row) !== 1">编辑</el-button>

          <el-button
              type="text"
              size="small"
              style="color: #67C23A"
              @click="handlePublish(scope.row)"
              v-if="getNormalizedStatus(scope.row) === 0 || getNormalizedStatus(scope.row) === 2"
          >
            {{ getNormalizedStatus(scope.row) === 0 ? '发布' : '重新上架' }}
          </el-button>

          <el-button type="text" size="small" style="color: #E6A23C" @click="handleUnpublish(scope.row)" v-if="getNormalizedStatus(scope.row) === 1">下架</el-button>

          <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)" v-if="getNormalizedStatus(scope.row) !== 1">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="请选择医生" style="width: 100%" @change="handleDoctorChange">
            <el-option v-for="doctor in doctorList" :key="doctor.id" :label="doctor.doctorName || doctor.doctor_name" :value="doctor.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="出诊日期" prop="workDate">
          <el-date-picker v-model="form.workDate" type="date" placeholder="请选择日期" style="width: 100%" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="出诊时段" prop="workTime">
          <el-select v-model="form.workTime" placeholder="请选择时段" style="width: 100%">
            <el-option label="上午" value="上午" />
            <el-option label="下午" value="下午" />
          </el-select>
        </el-form-item>
        <el-form-item label="号源总量" prop="totalNumber">
          <el-input-number v-model="form.totalNumber" :min="1" :max="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getSchedulePage, addSchedule, updateSchedule, deleteSchedule, publishSchedule, unpublishSchedule } from '@/api/schedule'
import { getDepartmentList } from '@/api/department'
import { getDoctorPage } from '@/api/doctor'

export default {
  name: 'ScheduleList',
  data() {
    return {
      searchForm: {
        deptId: null,
        doctorName: '',
        scheduleStatus: null
      },
      tableData: [],
      departmentList: [],
      doctorList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        doctorId: null,
        deptId: null,
        workDate: '',
        workTime: '',
        totalNumber: 20
      },
      rules: {
        doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
        workDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
        workTime: [{ required: true, message: '请选择时段', trigger: 'change' }],
        totalNumber: [{ required: true, message: '请输入号源总量', trigger: 'blur' }]
      },
      submitLoading: false
    }
  },
  created() {
    this.loadDepartments()
    this.loadDoctors()
    this.loadData()
  },
  methods: {
    async loadDepartments() {
      try {
        const res = await getDepartmentList()
        this.departmentList = res.data || []
      } catch (error) {
        console.error('加载科室列表失败:', error)
      }
    },
    async loadDoctors() {
      try {
        const res = await getDoctorPage({ current: 1, size: 100, status: 1 })
        this.doctorList = res.data.records || []
      } catch (error) {
        console.error('加载医生列表失败:', error)
      }
    },
    async loadData() {
      try {
        const res = await getSchedulePage({
          current: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleDoctorChange(doctorId) {
      const doctor = this.doctorList.find(d => d.id === doctorId)
      if (doctor) {
        this.form.deptId = doctor.deptId || doctor.dept_id
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { deptId: null, doctorName: '', scheduleStatus: null }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleAdd() {
      this.dialogTitle = '新增排班'
      this.form = { id: null, doctorId: null, deptId: null, workDate: '', workTime: '', totalNumber: 20 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑排班'
      this.form = {
        id: row.id,
        doctorId: row.doctorId || row.doctor_id,
        deptId: row.deptId || row.dept_id,
        workDate: row.workDate || row.work_date,
        workTime: row.workTime || row.work_time,
        totalNumber: row.totalNumber || row.total_number
      }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) {
            await updateSchedule(this.form)
            this.$message.success('编辑成功')
          } else {
            await addSchedule(this.form)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.loadData()
        } catch (error) {
          console.error('操作失败:', error)
        } finally {
          this.submitLoading = false
        }
      })
    },

    // 【修改点】优化了操作提示语，兼容发布和重新上架
    handlePublish(row) {
      this.$confirm('确定要将该排班上架发布吗？患者将可以进行预约。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await publishSchedule(row.id)
        this.$message.success('操作成功')
        this.loadData()
      }).catch(() => {})
    },

    handleUnpublish(row) {
      this.$confirm('确定要下架该排班吗？下架后患者无法预约。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await unpublishSchedule(row.id)
        this.$message.success('下架成功')
        this.loadData()
      }).catch(() => {})
    },

    handleDelete(row) {
      this.$confirm('确定要删除该排班吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteSchedule(row.id)
        this.$message.success('删除成功')
        this.loadData()
      }).catch(() => {})
    },

    // 万能状态转换器，处理 0/1/2 状态和偶尔出现的 boolean 值
    getNormalizedStatus(row) {
      let status = row.scheduleStatus !== undefined ? row.scheduleStatus : row.schedule_status;
      if (status === true) return 1;
      if (status === false) return 0;
      return status;
    },
    getStatusType(status) {
      const types = { 0: 'info', 1: 'success', 2: 'warning' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '未发布', 1: '已发布', 2: '已下架' }
      return texts[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.schedule-page {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 { margin: 0; font-size: 20px; color: #333; }
}
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>
