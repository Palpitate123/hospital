<template>
  <div>
    <!-- 顶部欢迎区 -->
    <div class="welcome-container">
      <div class="welcome-left">
        <span class="greeting-text">{{ greeting }}，{{ user?.name }} 👋</span>
        <span class="date-text">{{ currentDate }}</span>
      </div>
      <div class="time-box">
        <span class="time-text">{{ currentTime }}</span>
      </div>
    </div>

    <!-- 核心数据看板（6卡片，完全适配你的表结构） -->
    <div class="data-section">
      <el-row :gutter="20">
        <!-- 卡片1：今日挂号总量 -->
        <el-col :span="4">
          <el-card class="data-card" shadow="hover">
            <div class="card-box">
              <div class="card-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <i class="el-icon el-icon-date"></i>
              </div>
              <div class="card-info">
                <div class="number">{{ statistics.todayRegister || 0 }}</div>
                <div class="title">今日挂号</div>
                <div class="trend green">实时统计</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 卡片2：今日已叫号 -->
        <el-col :span="4">
          <el-card class="data-card" shadow="hover">
            <div class="card-box">
              <div class="card-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <i class="el-icon el-icon-user"></i>
              </div>
              <div class="card-info">
                <div class="number">{{ statistics.todayCalled || 0 }}</div>
                <div class="title">已叫号</div>
                <div class="trend green">实时统计</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 卡片3：今日未叫号 -->
        <el-col :span="4">
          <el-card class="data-card" shadow="hover">
            <div class="card-box">
              <div class="card-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                <i class="el-icon el-icon-time"></i>
              </div>
              <div class="card-info">
                <div class="number">{{ statistics.todayUnCalled || 0 }}</div>
                <div class="title">未叫号</div>
                <div class="trend red">待处理</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 卡片4：医生总数（doctor表） -->
        <el-col :span="4">
          <el-card class="data-card" shadow="hover">
            <div class="card-box">
              <div class="card-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <i class="el-icon el-icon-service"></i>
              </div>
              <div class="card-info">
                <div class="number">{{ statistics.doctorCount || 0 }}</div>
                <div class="title">医生总数</div>
                <div class="trend gray">doctor表</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 卡片5：患者总数（user表） -->
        <el-col :span="4">
          <el-card class="data-card" shadow="hover">
            <div class="card-box">
              <div class="card-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
                <i class="el-icon el-icon-s-custom"></i>
              </div>
              <div class="card-info">
                <div class="number">{{ statistics.patientCount || 0 }}</div>
                <div class="title">患者总数</div>
                <div class="trend gray">user表</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 卡片6：管理员总数（admin表） -->
        <el-col :span="4">
          <el-card class="data-card" shadow="hover">
            <div class="card-box">
              <div class="card-icon" style="background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);">
                <i class="el-icon el-icon-setting"></i>
              </div>
              <div class="card-info">
                <div class="number">{{ statistics.adminCount || 0 }}</div>
                <div class="title">管理员数</div>
                <div class="trend gray">admin表</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 近7天挂号趋势图表 -->
      <el-card class="chart-card" shadow="hover" style="margin-top:20px;">
        <div id="chart" style="width:100%;height:300px;"></div>
      </el-card>
    </div>

    <!-- 公告列表 + 今日医生排班 左右双栏布局 -->
    <div style="display: flex; margin: 10px 0; gap: 20px;">
      <!-- 左侧：公告列表（带滚动条） -->
      <div style="width: 50%;" class="card">
        <div style="margin-bottom: 20px; font-size: 20px; font-weight: bold">公告列表</div>
        <div class="notice-scroll-box">
          <el-timeline reverse>
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <!-- 右侧：今日医生排班模块 -->
      <div style="width: 50%;" class="card">
        <div style="margin-bottom: 20px; font-size: 20px; font-weight: bold">今日医生排班</div>
        <el-table :data="todayScheduleList" border style="width: 100%" stripe>
          <el-table-column prop="department" label="所属科室" align="center" />
          <el-table-column prop="name" label="医生姓名" align="center" />
          <el-table-column prop="position" label="医生职称" align="center" />
          <el-table-column prop="price" label="挂号费(元)" align="center" />
        </el-table>
        <div v-if="todayScheduleList.length === 0" style="text-align: center; color: #999; padding: 20px 0;">
          今日暂无排班医生
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Home',
  data() {
    return {
      // 原有用户信息
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      todayScheduleList: [],
      // 欢迎区变量
      greeting: "",
      currentDate: "",
      currentTime: "",
      timeTimer: null,
      // 统计数据
      statistics: {
        todayRegister: 0,
        todayCalled: 0,
        doctorCount: 0,
        todayUnCalled: 0,
        patientCount: 0,
        adminCount: 0,
        weekList: []
      },
      chartInstance: null
    };
  },
  created() {
    // 获取公告
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
    // 获取统计数据
    this.getStatisticsData()
    // 获取今日排班
    this.getTodaySchedule()
  },
  mounted() {
    this.updateTime();
    this.timeTimer = setInterval(() => {
      this.updateTime();
    }, 1000);
  },
  beforeDestroy() {
    clearInterval(this.timeTimer);
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    // 时间更新
    updateTime() {
      const now = new Date();
      const hour = now.getHours();
      const week = ["日", "一", "二", "三", "四", "五", "六"][now.getDay()];

      if (hour < 6) this.greeting = "凌晨好";
      else if (hour < 12) this.greeting = "早上好";
      else if (hour < 18) this.greeting = "下午好";
      else this.greeting = "晚上好";

      this.currentDate = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${week}`;
      this.currentTime = now.toLocaleTimeString();
    },
    // 获取统计数据
    getStatisticsData() {
      this.$request.get('/home/statistics').then(res => {
        this.statistics = res.data || {}
        this.$nextTick(() => {
          this.initChart()
        })
      }).catch(() => {
        this.statistics = {
          todayRegister: 0,
          todayCalled: 0,
          doctorCount: 0,
          todayUnCalled: 0,
          patientCount: 0,
          adminCount: 0,
          weekList: []
        }
      })
    },
    // 图表初始化
    initChart() {
      this.chartInstance = echarts.init(document.getElementById('chart'))
      this.chartInstance.setOption({
        title: { text: '近7天挂号量统计', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: { type: 'value' },
        series: [{
          data: this.statistics.weekList || [0,0,0,0,0,0,0],
          type: 'line',
          smooth: true,
          lineStyle: { color: '#165DFF' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0,0,0,1,[
              {offset:0, color:'rgba(22,93,255,0.3)'},
              {offset:1, color:'rgba(22,93,255,0.05)'}
            ])
          }
        }]
      })
      window.addEventListener('resize', () => this.chartInstance.resize())
    },
    // 获取今日医生排班
    getTodaySchedule() {
      this.$request.get('/doctor/todaySchedule').then(res => {
        this.todayScheduleList = res.data || [];
      }).catch(() => {
        this.todayScheduleList = [];
      });
    }
  }
}
</script>

<style scoped>
/* 欢迎区样式 */
.welcome-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 25px;
  margin-bottom: 20px;
  background: linear-gradient(90deg, #e8f4ff 0%, #f0f7ff 100%);
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
.welcome-left {
  display: flex;
  align-items: center;
  gap: 15px;
}
.greeting-text {
  font-size: 19px;
  font-weight: 600;
  color: #165dff;
  letter-spacing: 1px;
}
.date-text {
  font-size: 14px;
  color: #4e5969;
}
.time-box {
  padding: 6px 12px;
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #dcdfe6;
}
.time-text {
  font-size: 15px;
  font-weight: 500;
  color: #1d2129;
}

/* 数据看板样式 */
.data-section {
  margin-bottom: 20px;
}
.data-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}
.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}
.card-box {
  display: flex;
  align-items: center;
  padding: 10px;
}
.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 18px;
  font-size: 24px;
  color: #fff;
}
.card-info .number {
  font-size: 30px;
  font-weight: bold;
  color: #1d2129;
  line-height: 1.2;
}
.card-info .title {
  font-size: 14px;
  color: #86909c;
  margin: 5px 0;
}
.trend {
  font-size: 12px;
}
.trend.green { color: #00b42a; }
.trend.red { color: #f53f3f; }
.trend.gray { color: #86909c; }

/* 图表样式 */
.chart-card {
  border-radius: 10px;
}

/* 卡片样式 */
.card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 15px;
}

/* 公告滚动条样式 */
.notice-scroll-box {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}
.notice-scroll-box::-webkit-scrollbar {
  width: 6px;
}
.notice-scroll-box::-webkit-scrollbar-track {
  background: #f1f5ff;
  border-radius: 10px;
}
.notice-scroll-box::-webkit-scrollbar-thumb {
  background: #7d7c7c;
  border-radius: 10px;
}
.notice-scroll-box::-webkit-scrollbar-thumb:hover {
  background: #646363; 
}
</style>