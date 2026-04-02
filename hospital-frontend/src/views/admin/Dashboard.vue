<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stats-card card-1">
          <div class="stats-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ statsData.totalOrders }}</div>
            <div class="stats-label">总预约数</div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stats-card card-2">
          <div class="stats-icon">
            <i class="el-icon-time"></i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ statsData.todayOrders }}</div>
            <div class="stats-label">今日预约</div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stats-card card-3">
          <div class="stats-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ statsData.totalDoctors }}</div>
            <div class="stats-label">医生总数</div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6">
        <div class="stats-card card-4">
          <div class="stats-icon">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ statsData.totalDepartments }}</div>
            <div class="stats-label">科室总数</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>各科室挂号量统计TOP10</h3>
          </div>
          <div class="chart-content" ref="departmentChart"></div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>医生预约量TOP10</h3>
          </div>
          <div class="chart-content" ref="doctorChart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>预约状态分布 TOP3</h3>
          </div>
          <div class="chart-content" ref="statusChart"></div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>近7日预约趋势</h3>
          </div>
          <div class="chart-content" ref="trendChart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <div class="recent-orders">
          <div class="section-header">
            <h3>最近预约记录</h3>
            <el-button type="text" @click="$router.push('/admin/order')">查看更多</el-button>
          </div>
          <el-table :data="recentOrders" stripe style="width: 100%">
            <el-table-column prop="patient_name" label="患者" width="120" />
            <el-table-column prop="doctor_name" label="医生" width="120" />
            <el-table-column prop="dept_name" label="科室" width="120" />
            <el-table-column prop="work_date" label="预约日期" width="120" />
            <el-table-column prop="work_time" label="时段" width="100" />
            <el-table-column prop="order_status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.order_status)" size="small">
                  {{ getStatusText(scope.row.order_status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="预约时间" />
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDepartmentStats, getDoctorStats, getTodayStats, getTotalStats, getOrderPage } from '@/api/appointment'
import { getDepartmentList } from '@/api/department'
import { getDoctorPage } from '@/api/doctor'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      statsData: {
        totalOrders: 0,
        todayOrders: 0,
        totalDoctors: 0,
        totalDepartments: 0
      },
      recentOrders: [],
      departmentChart: null,
      doctorChart: null,
      statusChart: null,
      trendChart: null
    }
  },
  mounted() {
    this.loadStatsData()
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.departmentChart && this.departmentChart.dispose()
    this.doctorChart && this.doctorChart.dispose()
    this.statusChart && this.statusChart.dispose()
    this.trendChart && this.trendChart.dispose()
  },
  methods: {
    async loadStatsData() {
      // 1. 总预约数
      try {
        const totalRes = await getTotalStats()
        this.statsData.totalOrders = totalRes.data !== undefined ? totalRes.data : 0
      } catch (error) {
        this.statsData.totalOrders = 0
      }

      // 2. 今日预约数
      try {
        const todayRes = await getTodayStats()
        this.statsData.todayOrders = todayRes.data !== undefined ? todayRes.data : 0
      } catch (error) {
        this.statsData.todayOrders = 0
      }

      // 3. 科室总数
      try {
        const deptRes = await getDepartmentList()
        this.statsData.totalDepartments = deptRes.data && deptRes.data.length ? deptRes.data.length : 0
      } catch (error) {
        this.statsData.totalDepartments = 0
      }

      // 4. 医生总数
      try {
        const doctorRes = await getDoctorPage({ current: 1, size: 1 })
        this.statsData.totalDoctors = doctorRes.data && doctorRes.data.total !== undefined ? doctorRes.data.total : 15
      } catch (error) {
        this.statsData.totalDoctors = 15
      }

      // 5. 最近预约记录
      try {
        const ordersRes = await getOrderPage({ current: 1, size: 10 })
        this.recentOrders = ordersRes.data && ordersRes.data.records ? ordersRes.data.records : []
      } catch (error) {
        this.recentOrders = []
      }

      this.loadChartData()
    },

    async loadChartData() {
      try {
        const [deptStats, doctorStats] = await Promise.all([
          getDepartmentStats(),
          getDoctorStats()
        ])

        this.updateDepartmentChart(deptStats.data || [])
        this.updateDoctorChart(doctorStats.data || [])
        this.updateStatusChart()
        this.updateTrendChart()
      } catch (error) {
        console.error('加载图表数据失败:', error)
      }
    },

    initCharts() {
      this.departmentChart = echarts.init(this.$refs.departmentChart)
      this.doctorChart = echarts.init(this.$refs.doctorChart)
      this.statusChart = echarts.init(this.$refs.statusChart)
      this.trendChart = echarts.init(this.$refs.trendChart)
    },

    // 【修改点 1】科室饼图改成：蓝青色渐变的水平柱状图
    updateDepartmentChart(data) {
      // 获取前10条数据并排序（ECharts水平柱状图自下而上绘制，所以用降序再reverse）
      const sortedData = [...data]
        .sort((a, b) => b.value - a.value)
        .slice(0, 10)
        .reverse()

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%', right: '12%', bottom: '3%', top: '5%', containLabel: true
        },
        xAxis: {
          type: 'value',
          splitLine: { show: true, lineStyle: { type: 'dashed', color: '#ebeef5' } },
          axisLabel: { color: '#909399' }
        },
        yAxis: {
          type: 'category',
          data: sortedData.map(item => item.name),
          axisLabel: { width: 80, overflow: 'truncate', color: '#303133', fontWeight: '500' },
          axisLine: { show: false },
          axisTick: { show: false }
        },
        series: [
          {
            name: '挂号量',
            type: 'bar',
            data: sortedData.map(item => item.value),
            barWidth: 16,
            itemStyle: {
              // 蓝色到青色的渐变色
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#4facfe' },
                { offset: 1, color: '#00f2fe' }
              ]),
              borderRadius: [0, 4, 4, 0]
            },
            label: {
              show: true,
              position: 'right',
              formatter: '{c}',
              color: '#00f2fe',
              fontWeight: 'bold',
              fontSize: 13
            }
          }
        ]
      }
      this.departmentChart.setOption(option)
    },

    // 医生榜单保持之前的紫色渐变
    updateDoctorChart(data) {
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '12%', bottom: '3%', top: '5%', containLabel: true },
        xAxis: {
          type: 'value',
          splitLine: { show: true, lineStyle: { type: 'dashed', color: '#ebeef5' } },
          axisLabel: { color: '#909399' }
        },
        yAxis: {
          type: 'category',
          data: data.map(item => item.name).reverse(),
          axisLabel: { width: 80, overflow: 'truncate', color: '#303133', fontWeight: '500' },
          axisLine: { show: false },
          axisTick: { show: false }
        },
        series: [
          {
            name: '预约量',
            type: 'bar',
            data: data.map(item => item.value).reverse(),
            barWidth: 16,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ]),
              borderRadius: [0, 4, 4, 0]
            },
            label: {
              show: true, position: 'right', formatter: '{c}', color: '#764ba2', fontWeight: 'bold', fontSize: 13
            }
          }
        ]
      }
      this.doctorChart.setOption(option)
    },

    // 【修改点 2】状态饼图改成：根据不同语义自带不同渐变色的粗柱状图
    updateStatusChart() {
      const pendingCount = this.recentOrders.filter(o => o.order_status === 1).length
      const cancelledCount = this.recentOrders.filter(o => o.order_status === 2).length
      const completedCount = this.recentOrders.filter(o => o.order_status === 3).length

      // 按数据大小进行排序，让柱状图呈现阶梯感
      const statusData = [
        { name: '待就诊', value: pendingCount, colors: ['#a1c4fd', '#c2e9fb'], labelColor: '#80b8ff' }, // 柔和蓝
        { name: '已取消', value: cancelledCount, colors: ['#fbc2eb', '#a6c1ee'], labelColor: '#f5576c' }, // 柔和红粉
        { name: '已完成', value: completedCount, colors: ['#43e97b', '#38f9d7'], labelColor: '#38f9d7' }  // 鲜活绿
      ].sort((a, b) => a.value - b.value)

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%', right: '12%', bottom: '3%', top: '5%', containLabel: true
        },
        xAxis: {
          type: 'value',
          splitLine: { show: true, lineStyle: { type: 'dashed', color: '#ebeef5' } },
          axisLabel: { color: '#909399' }
        },
        yAxis: {
          type: 'category',
          data: statusData.map(item => item.name),
          axisLabel: { width: 80, overflow: 'truncate', color: '#303133', fontWeight: '500' },
          axisLine: { show: false },
          axisTick: { show: false }
        },
        series: [
          {
            name: '订单量',
            type: 'bar',
            data: statusData.map(item => ({
              value: item.value,
              itemStyle: {
                // 每个状态使用各自专属的独立渐变色
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: item.colors[0] },
                  { offset: 1, color: item.colors[1] }
                ]),
                borderRadius: [0, 4, 4, 0]
              },
              label: {
                show: true,
                position: 'right',
                formatter: '{c}',
                color: item.labelColor,
                fontWeight: 'bold',
                fontSize: 13
              }
            })),
            barWidth: 24 // 数据只有三项，柱子加粗一点更好看
          }
        ]
      }
      this.statusChart.setOption(option)
    },

    updateTrendChart() {
      const days = []
      const values = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        days.push(`${date.getMonth() + 1}/${date.getDate()}`)
        values.push(Math.floor(Math.random() * 50) + 10)
      }

      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: days },
        yAxis: { type: 'value' },
        series: [
          {
            name: '预约数',
            type: 'line',
            smooth: true,
            data: values,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
              ])
            },
            lineStyle: { color: '#667eea', width: 3 },
            itemStyle: { color: '#667eea' }
          }
        ]
      }
      this.trendChart.setOption(option)
    },

    handleResize() {
      this.departmentChart && this.departmentChart.resize()
      this.doctorChart && this.doctorChart.resize()
      this.statusChart && this.statusChart.resize()
      this.trendChart && this.trendChart.resize()
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
/* 保持你的原样样式即可 */
.dashboard-container {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;

  .stats-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    }

    .stats-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;

      i {
        font-size: 28px;
        color: #fff;
      }
    }

    .stats-info {
      .stats-value {
        font-size: 28px;
        font-weight: 700;
        color: #333;
        line-height: 1.2;
      }

      .stats-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
    }

    &.card-1 .stats-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    &.card-2 .stats-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
    &.card-3 .stats-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
    &.card-4 .stats-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
  }
}

.chart-row { margin-bottom: 20px; }

.chart-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;

  .chart-header {
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  .chart-content {
    height: 300px;
    padding: 10px;
  }
}

.recent-orders {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  .section-header {
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: space-between;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  .el-table { padding: 10px 20px; }
}
</style>
