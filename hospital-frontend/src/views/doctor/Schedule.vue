<template>
  <div class="doctor-schedule">
    <el-card>
      <div slot="header" class="card-header">
        <span>排班管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增排班</el-button>
      </div>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            @change="handleQuery"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="scheduleList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="scheduleDate" label="排班日期" width="120"></el-table-column>
        <el-table-column prop="timePeriod" label="时段" width="120">
          <template slot-scope="scope">
            {{ scope.row.timePeriod === 'morning' ? '上午' : '下午' }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="科室" width="150"></el-table-column>
        <el-table-column prop="totalNumber" label="总号源" width="100"></el-table-column>
        <el-table-column prop="remainingNumber" label="剩余号源" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '可用' : '停诊' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              type="text" 
              size="small" 
              @click="handleToggleStatus(scope.row)"
              :style="{ color: scope.row.status === 1 ? '#F56C6C' : '#67C23A' }"
            >
              {{ scope.row.status === 1 ? '停诊' : '复诊' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.pageNum"
        :page-sizes="[10, 20, 50]"
        :page-size="queryParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        class="pagination"
      ></el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            :picker-options="datePickerOptions"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="时段" prop="timePeriod">
          <el-radio-group v-model="form.timePeriod">
            <el-radio label="morning">上午</el-radio>
            <el-radio label="afternoon">下午</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="总号源" prop="totalNumber">
          <el-input-number v-model="form.totalNumber" :min="1" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DoctorSchedule',
  data() {
    return {
      loading: false,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startDate: '',
        endDate: ''
      },
      scheduleList: [
        { id: 1, scheduleDate: '2024-01-15', timePeriod: 'morning', departmentName: '内科', totalNumber: 20, remainingNumber: 15, status: 1, remark: '' },
        { id: 2, scheduleDate: '2024-01-15', timePeriod: 'afternoon', departmentName: '内科', totalNumber: 20, remainingNumber: 18, status: 1, remark: '' }
      ],
      total: 2,
      dialogVisible: false,
      dialogTitle: '新增排班',
      form: {
        id: null,
        scheduleDate: '',
        timePeriod: 'morning',
        totalNumber: 20,
        remark: ''
      },
      rules: {
        scheduleDate: [{ required: true, message: '请选择排班日期', trigger: 'change' }],
        timePeriod: [{ required: true, message: '请选择时段', trigger: 'change' }],
        totalNumber: [{ required: true, message: '请输入总号源', trigger: 'blur' }]
      },
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      }
    }
  },
  methods: {
    handleQuery() {
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.startDate = this.dateRange[0]
        this.queryParams.endDate = this.dateRange[1]
      } else {
        this.queryParams.startDate = ''
        this.queryParams.endDate = ''
      }
      this.queryParams.pageNum = 1
      this.loadScheduleList()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        startDate: '',
        endDate: ''
      }
      this.loadScheduleList()
    },
    loadScheduleList() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadScheduleList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.loadScheduleList()
    },
    handleAdd() {
      this.dialogTitle = '新增排班'
      this.form = {
        id: null,
        scheduleDate: '',
        timePeriod: 'morning',
        totalNumber: 20,
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑排班'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.$message.success(this.form.id ? '编辑成功' : '新增成功')
          this.dialogVisible = false
          this.loadScheduleList()
        }
      })
    },
    handleToggleStatus(row) {
      const action = row.status === 1 ? '停诊' : '复诊'
      this.$confirm(`确认${action}该排班?`, '提示', {
        type: 'warning'
      }).then(() => {
        row.status = row.status === 1 ? 0 : 1
        this.$message.success(`${action}成功`)
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.doctor-schedule {
  padding: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
