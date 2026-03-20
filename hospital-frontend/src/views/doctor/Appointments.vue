<template>
  <div class="doctor-appointments">
    <el-card>
      <div slot="header">
        <span>预约管理</span>
      </div>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="queryParams.scheduleDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            @change="handleQuery"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable @change="handleQuery">
            <el-option label="待支付" :value="0"></el-option>
            <el-option label="已支付" :value="1"></el-option>
            <el-option label="已完成" :value="2"></el-option>
            <el-option label="已取消" :value="3"></el-option>
            <el-option label="已退款" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="queryParams.patientName" placeholder="请输入" @keyup.enter.native="handleQuery"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="appointmentList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="100"></el-table-column>
        <el-table-column prop="patientPhone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="departmentName" label="科室" width="120"></el-table-column>
        <el-table-column prop="scheduleDate" label="预约日期" width="120"></el-table-column>
        <el-table-column prop="timePeriod" label="时段" width="100">
          <template slot-scope="scope">
            {{ scope.row.timePeriod === 'morning' ? '上午' : '下午' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预约时间" width="160"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button 
              type="text" 
              size="small" 
              @click="handleComplete(scope.row)"
              v-if="scope.row.status === 1"
            >
              完成
            </el-button>
            <el-button 
              type="text" 
              size="small" 
              style="color: #F56C6C"
              @click="handleCancel(scope.row)"
              v-if="scope.row.status === 0 || scope.row.status === 1"
            >
              取消
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

    <el-dialog title="预约详情" :visible.sync="detailVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ currentOrder.patientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.patientPhone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentOrder.patientIdCard }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ currentOrder.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentOrder.scheduleDate }}</el-descriptions-item>
        <el-descriptions-item label="时段">{{ currentOrder.timePeriod === 'morning' ? '上午' : '下午' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DoctorAppointments',
  data() {
    return {
      loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        scheduleDate: '',
        status: null,
        patientName: ''
      },
      appointmentList: [
        { id: 1, orderNo: 'AP202401150001', patientName: '张三', patientPhone: '13800138001', patientIdCard: '110101199001011234', departmentName: '内科', scheduleDate: '2024-01-15', timePeriod: 'morning', createTime: '2024-01-14 10:30:00', status: 1, remark: '' },
        { id: 2, orderNo: 'AP202401150002', patientName: '李四', patientPhone: '13800138002', patientIdCard: '110101199002022345', departmentName: '内科', scheduleDate: '2024-01-15', timePeriod: 'afternoon', createTime: '2024-01-14 11:20:00', status: 1, remark: '' }
      ],
      total: 2,
      detailVisible: false,
      currentOrder: {}
    }
  },
  methods: {
    handleQuery() {
      this.queryParams.pageNum = 1
      this.loadAppointmentList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        scheduleDate: '',
        status: null,
        patientName: ''
      }
      this.loadAppointmentList()
    },
    loadAppointmentList() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadAppointmentList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.loadAppointmentList()
    },
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger', 4: 'info' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待支付', 1: '已支付', 2: '已完成', 3: '已取消', 4: '已退款' }
      return texts[status] || '未知'
    },
    handleView(row) {
      this.currentOrder = { ...row }
      this.detailVisible = true
    },
    handleComplete(row) {
      this.$confirm('确认完成该预约?', '提示', {
        type: 'warning'
      }).then(() => {
        row.status = 2
        this.$message.success('操作成功')
      }).catch(() => {})
    },
    handleCancel(row) {
      this.$confirm('确认取消该预约?', '提示', {
        type: 'warning'
      }).then(() => {
        row.status = 3
        this.$message.success('取消成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.doctor-appointments {
  padding: 0;
}
.search-form {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
