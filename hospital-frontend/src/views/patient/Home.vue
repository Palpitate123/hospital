<template>
  <div class="home-container">
    <div class="banner-section">
      <el-carousel height="400px" :interval="5000">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ background: item.bgColor }">
            <div class="banner-content">
              <h2>{{ item.title }}</h2>
              <p>{{ item.desc }}</p>
              <el-button type="primary" size="large" @click="$router.push('/department')">
                立即预约
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    
    <div class="quick-actions">
      <div class="section-container">
        <h3 class="section-title">快速服务</h3>
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6" v-for="action in quickActions" :key="action.id">
            <div class="action-card" @click="$router.push(action.path)">
              <div class="action-icon" :style="{ background: action.bgColor }">
                <i :class="action.icon"></i>
              </div>
              <div class="action-text">{{ action.name }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <div class="department-section">
      <div class="section-container">
        <div class="section-header">
          <h3 class="section-title">热门科室</h3>
          <el-button type="text" @click="$router.push('/department')">
            查看全部 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
        <el-row :gutter="20">
          <el-col :xs="12" :sm="8" :md="6" v-for="dept in hotDepartments" :key="dept.id">
            <div class="department-card" @click="$router.push(`/department/${dept.id}`)">
              <div class="dept-icon">
                <i class="el-icon-first-aid-kit"></i>
              </div>
              <h4>{{ dept.deptName }}</h4>
              <p>{{ dept.deptDesc || '暂无介绍' }}</p>
              <span class="doctor-count">{{ dept.doctorCount || 0 }}位医生</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <div class="doctor-section">
      <div class="section-container">
        <div class="section-header">
          <h3 class="section-title">推荐医生</h3>
          <el-button type="text" @click="$router.push('/doctor')">
            查看全部 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="doctor in recommendDoctors" :key="doctor.id">
            <div class="doctor-card" @click="$router.push(`/doctor/${doctor.id}`)">
              <div class="doctor-avatar">
                <el-avatar :size="80" :src="doctor.avatar" icon="el-icon-user-solid"></el-avatar>
              </div>
              <div class="doctor-info">
                <h4>{{ doctor.doctorName }}</h4>
                <p class="doctor-title">{{ doctor.title || '医师' }}</p>
                <p class="doctor-dept">{{ doctor.deptName }}</p>
                <p class="doctor-specialty">{{ doctor.specialty || '暂无擅长信息' }}</p>
              </div>
              <el-button type="primary" size="small" class="appointment-btn">预约挂号</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <div class="guide-section">
      <div class="section-container">
        <h3 class="section-title">预约指南</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8" v-for="(guide, index) in guides" :key="index">
            <div class="guide-card">
              <div class="guide-step">{{ index + 1 }}</div>
              <h4>{{ guide.title }}</h4>
              <p>{{ guide.desc }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { getDepartmentList } from '@/api/department'
import { getDoctorPage } from '@/api/doctor'

export default {
  name: 'Home',
  data() {
    return {
      banners: [
        { id: 1, title: '便捷预约挂号', desc: '足不出户，轻松预约专家号源', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
        { id: 2, title: '专业医疗团队', desc: '汇聚各科室资深专家，为您提供优质医疗服务', bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
        { id: 3, title: '智能导诊服务', desc: '智能分诊，精准推荐，让就医更简单', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
      ],
      quickActions: [
        { id: 1, name: '预约挂号', icon: 'el-icon-date', bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', path: '/department' },
        { id: 2, name: '我的预约', icon: 'el-icon-document', bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', path: '/my-orders' },
        { id: 3, name: '科室导航', icon: 'el-icon-office-building', bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', path: '/department' },
        { id: 4, name: '医生团队', icon: 'el-icon-user', bgColor: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', path: '/doctor' }
      ],
      hotDepartments: [],
      recommendDoctors: [],
      guides: [
        { title: '选择科室', desc: '根据您的症状选择合适的科室，或使用智能导诊功能' },
        { title: '选择医生', desc: '查看医生排班信息，选择合适的出诊时间和医生' },
        { title: '确认预约', desc: '填写预约信息，确认无误后提交预约申请' }
      ]
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [deptRes, doctorRes] = await Promise.all([
          getDepartmentList(),
          getDoctorPage({ current: 1, size: 8 })
        ])
        
        this.hotDepartments = deptRes.data ? deptRes.data.slice(0, 8) : []
        this.recommendDoctors = doctorRes.data ? doctorRes.data.records : []
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.home-container {
  background: #f5f7fa;
}

.banner-section {
  .banner-item {
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .banner-content {
      text-align: center;
      color: #fff;
      
      h2 {
        font-size: 42px;
        margin-bottom: 15px;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
      }
      
      p {
        font-size: 18px;
        margin-bottom: 30px;
        opacity: 0.9;
      }
    }
  }
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 25px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 25px;
}

.quick-actions {
  background: #fff;
  
  .action-card {
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 20px;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }
    
    .action-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 15px;
      
      i {
        font-size: 28px;
        color: #fff;
      }
    }
    
    .action-text {
      font-size: 16px;
      color: #333;
      font-weight: 500;
    }
  }
}

.department-section {
  background: #f5f7fa;
  
  .department-card {
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    margin-bottom: 20px;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }
    
    .dept-icon {
      width: 50px;
      height: 50px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 15px;
      
      i {
        font-size: 24px;
        color: #fff;
      }
    }
    
    h4 {
      font-size: 16px;
      color: #333;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 13px;
      color: #999;
      line-height: 1.5;
      height: 40px;
      overflow: hidden;
      margin-bottom: 10px;
    }
    
    .doctor-count {
      font-size: 12px;
      color: #409EFF;
    }
  }
}

.doctor-section {
  background: #fff;
  
  .doctor-card {
    background: #fff;
    border-radius: 12px;
    padding: 25px;
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    margin-bottom: 20px;
    position: relative;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }
    
    .doctor-avatar {
      margin-right: 20px;
    }
    
    .doctor-info {
      flex: 1;
      
      h4 {
        font-size: 18px;
        color: #333;
        margin-bottom: 5px;
      }
      
      .doctor-title {
        font-size: 14px;
        color: #409EFF;
        margin-bottom: 5px;
      }
      
      .doctor-dept {
        font-size: 13px;
        color: #666;
        margin-bottom: 5px;
      }
      
      .doctor-specialty {
        font-size: 12px;
        color: #999;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    
    .appointment-btn {
      position: absolute;
      right: 20px;
      bottom: 20px;
    }
  }
}

.guide-section {
  background: #f5f7fa;
  
  .guide-card {
    background: #fff;
    border-radius: 12px;
    padding: 30px;
    text-align: center;
    margin-bottom: 20px;
    
    .guide-step {
      width: 50px;
      height: 50px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      font-size: 24px;
      font-weight: bold;
      color: #fff;
    }
    
    h4 {
      font-size: 18px;
      color: #333;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 14px;
      color: #999;
      line-height: 1.6;
    }
  }
}
</style>
