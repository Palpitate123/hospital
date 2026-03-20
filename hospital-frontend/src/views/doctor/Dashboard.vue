<template>
  <div class="doctor-dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon today">
            <i class="el-icon-date"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayAppointments }}</div>
            <div class="stat-label">今日预约</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon week">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.weekAppointments }}</div>
            <div class="stat-label">本周预约</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon schedule">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todaySchedules }}</div>
            <div class="stat-label">今日排班</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon pending">
            <i class="el-icon-warning"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingCount }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <div slot="header">
            <span>预约趋势（近7天）</span>
          </div>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="schedule-card">
          <div slot="header">
            <span>今日排班</span>
          </div>
          <el-timeline v-if="todaySchedules.length > 0">
            <el-timeline-item
              v-for="item in todaySchedules"
              :key="item.id"
              :timestamp="item.timePeriod"
              placement="top"
            >
              <el-card>
                <h4>{{ item.departmentName }}</h4>
                <p>剩余号源: {{ item.remainingCount }}/{{ item.totalCount }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="今日暂无排班"></el-empty>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>今日预约列表</span>
          </div>
          <el-table :data="todayAppointments" stripe style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
            <el-table-column prop="departmentName" label="科室" width="120"></el-table-column>
            <el-table-column prop="scheduleDate" label="预约日期" width="120"></el-table-column>
            <el-table-column prop="timePeriod" label="时段" width="100"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
                <el-button type="text" size="small" @click="handleComplete(scope.row)" v-if="scope.row.status === 1">
                  完成
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'DoctorDashboard',
  data() {
    return {
      stats: {
        todayAppointments: 12,
        weekAppointments: 56,
        todaySchedules: 3,
        pendingCount: 5
      },
      todaySchedules: [
        { id: 1, departmentName: '内科门诊', timePeriod: '上午 08:00-12:00', remainingCount: 8, totalCount: 20 },
        { id: 2, departmentName: '内科门诊', timePeriod: '下午 14:00-17:30', remainingCount: 15, totalCount: 20 }
      ],
      todayAppointments: [],
      chart: null
    }
  },
  mounted() {
    this.initChart()
    this.loadDashboardData()
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chartRef)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: [8, 12, 15, 10, 18, 6, 4],
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          },
          lineStyle: {
            color: '#409EFF'
          },
          itemStyle: {
            color: '#409EFF'
          }
        }]
      }
      this.chart.setOption(option)
    },
    loadDashboardData() {
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
      this.$message.info('查看订单: ' + row.orderNo)
    },
    handleComplete(row) {
      this.$confirm('确认完成该预约?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('操作成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.doctor-dashboard {
  padding: 0;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}
.stat-card >>> .el-card__body {
  display: flex;
  align-items: center;
  width: 100%;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}
.stat-icon i {
  font-size: 28px;
  color: #fff;
}
.stat-icon.today {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
}
.stat-icon.week {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}
.stat-icon.schedule {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}
.stat-icon.pending {
  background: linear-gradient(135deg, #F56C6C, #f78989);
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.chart-card {
  height: 350px;
}
.chart-container {
  height: 280px;
}
.schedule-card {
  height: 350px;
  overflow-y: auto;
}
.content-row {
  margin-top: 20px;
}
</style>
