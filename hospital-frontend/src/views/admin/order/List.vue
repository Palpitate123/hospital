<template>
  <div class="order-page">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="科室名称">
          <el-input v-model="searchForm.deptName" placeholder="请输入科室名称" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.orderStatus" placeholder="请选择状态" clearable>
            <el-option label="待就诊" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="patientName" label="患者" width="120" />
      <el-table-column prop="doctorName" label="医生" width="120" />
      <el-table-column prop="deptName" label="科室" width="120" />
      <el-table-column prop="workDate" label="预约日期" width="120" />
      <el-table-column prop="workTime" label="时段" width="100" />
      <el-table-column prop="orderStatus" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.orderStatus)">
            {{ getStatusText(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="预约时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleDetail(scope.row)">详情</el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleCancel(scope.row)" v-if="scope.row.orderStatus === 1">取消</el-button>
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
    
    <el-dialog title="订单详情" :visible.sync="dialogVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单ID">{{ orderDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ orderDetail.patientName }}</el-descriptions-item>
        <el-descriptions-item label="医生姓名">{{ orderDetail.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ orderDetail.deptName }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ orderDetail.workDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时段">{{ orderDetail.workTime }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(orderDetail.orderStatus)">{{ getStatusText(orderDetail.orderStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ orderDetail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getOrderPage, getOrderDetail, forceCancelOrder } from '@/api/appointment'

export default {
  name: 'OrderList',
  data() {
    return {
      searchForm: {
        deptName: '',
        orderStatus: null
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      orderDetail: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getOrderPage({
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
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { deptName: '', orderStatus: null }
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
    async handleDetail(row) {
      try {
        const res = await getOrderDetail(row.id)
        this.orderDetail = res.data
        this.dialogVisible = true
      } catch (error) {
        console.error('加载详情失败:', error)
      }
    },
    handleCancel(row) {
      this.$confirm('确定要强制取消该订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await forceCancelOrder(row.id)
        this.$message.success('取消成功')
        this.loadData()
      }).catch(() => {})
    },
    getStatusType(status) {
      const types = { 1: 'primary', 2: 'danger', 3: 'success' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 1: '待就诊', 2: '已取消', 3: '已完成' }
      return texts[status] || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.order-page {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 20px;
    color: #333;
  }
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
