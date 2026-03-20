<template>
  <div class="patient-orders">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>我的预约</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <div slot="header" class="card-header">
        <span>我的预约</span>
        <el-radio-group v-model="queryParams.status" @change="handleQuery">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="1">待就诊</el-radio-button>
          <el-radio-button :label="2">已完成</el-radio-button>
          <el-radio-button :label="3">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="orderList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="doctorName" label="医生" width="100"></el-table-column>
        <el-table-column prop="departmentName" label="科室" width="100"></el-table-column>
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
              style="color: #F56C6C"
              @click="handleCancel(scope.row)"
              v-if="scope.row.status === 1"
            >
              取消预约
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
        <el-descriptions-item label="医生">{{ currentOrder.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ currentOrder.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentOrder.scheduleDate }}</el-descriptions-item>
        <el-descriptions-item label="时段">{{ currentOrder.timePeriod === 'morning' ? '上午' : '下午' }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ currentOrder.patientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.patientPhone }}</el-descriptions-item>
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
  name: 'PatientMyOrders',
  data() {
    return {
      loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: null
      },
      orderList: [
        { id: 1, orderNo: 'AP202401150001', doctorName: '王医生', departmentName: '内科', scheduleDate: '2024-01-15', timePeriod: 'morning', patientName: '张三', patientPhone: '13800138001', createTime: '2024-01-14 10:30:00', status: 1, remark: '' },
        { id: 2, orderNo: 'AP202401140001', doctorName: '李医生', departmentName: '外科', scheduleDate: '2024-01-14', timePeriod: 'afternoon', patientName: '张三', patientPhone: '13800138001', createTime: '2024-01-13 09:20:00', status: 2, remark: '' },
        { id: 3, orderNo: 'AP202401130001', doctorName: '张医生', departmentName: '儿科', scheduleDate: '2024-01-13', timePeriod: 'morning', patientName: '张三', patientPhone: '13800138001', createTime: '2024-01-12 14:15:00', status: 3, remark: '临时有事' }
      ],
      total: 3,
      detailVisible: false,
      currentOrder: {}
    }
  },
  methods: {
    handleQuery() {
      this.queryParams.pageNum = 1
      this.loadOrders()
    },
    loadOrders() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadOrders()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.loadOrders()
    },
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger', 4: 'info' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待支付', 1: '待就诊', 2: '已完成', 3: '已取消', 4: '已退款' }
      return texts[status] || '未知'
    },
    handleView(row) {
      this.currentOrder = { ...row }
      this.detailVisible = true
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
.patient-orders {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
