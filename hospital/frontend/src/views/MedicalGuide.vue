<template>
  <div class="medical-guide-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <i class="el-icon-medal-1"></i>
          <span>医院预约系统</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/departments" class="nav-item">科室介绍</router-link>
          <router-link to="/doctors" class="nav-item">医生团队</router-link>
          <router-link to="/medical-guide" class="nav-item active">医疗导诊</router-link>
        </nav>
        <div class="user-info">
          <div v-if="user" class="logged-in">
            <span class="welcome">欢迎，{{ user.name || user.username }}</span>
            <router-link to="/user-info" class="btn-text">个人信息</router-link>
            <router-link to="/appointment-list" class="btn-text">预约记录</router-link>
            <button class="btn-text" @click="handleLogout">退出登录</button>
          </div>
          <div v-else class="not-logged-in">
            <router-link to="/login" class="btn-primary">登录</router-link>
            <router-link to="/register" class="btn-text">注册</router-link>
          </div>
        </div>
      </div>
    </header>
    <!-- 内容区域 -->
    <main class="main-content">
      <div class="page-header">
        <h1>医疗导诊服务</h1>
        <p class="page-desc">根据您的症状和需求，为您推荐合适的科室和就诊建议</p>
      </div>
      <!-- 症状自查工具 -->
      <el-card class="guide-card symptom-checker">
        <template slot="header">
          <h3 class="card-title">症状自查向导</h3>
        </template>
        <div class="symptom-form">
          <el-form :model="symptomForm" label-width="100px">
            <el-form-item label="主要症状">
              <el-select v-model="symptomForm.mainSymptom" placeholder="请选择主要症状">
                <el-option
                  v-for="symptom in commonSymptoms"
                  :key="symptom.value"
                  :label="symptom.label"
                  :value="symptom.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="伴随症状">
              <el-select v-model="symptomForm.secondarySymptoms" multiple placeholder="可多选">
                <el-option
                  v-for="symptom in allSymptoms"
                  :key="symptom.value"
                  :label="symptom.label"
                  :value="symptom.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="症状持续时间">
              <el-radio-group v-model="symptomForm.duration">
                <el-radio-button label="几小时"></el-radio-button>
                <el-radio-button label="1-3天"></el-radio-button>
                <el-radio-button label="1周内"></el-radio-button>
                <el-radio-button label="长期存在"></el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="症状严重程度">
              <el-rate v-model="symptomForm.severity" show-score :max="5"></el-rate>
            </el-form-item>
            <el-form-item class="submit-section">
              <el-button type="primary" @click="analyzeSymptoms" :loading="analyzing">分析症状</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
      <!-- 分析结果 -->
      <el-card v-if="analysisResult" class="guide-card analysis-result">
        <template slot="header">
          <h3 class="card-title">初步分析结果</h3>
        </template>
        <div class="result-content">
          <div class="recommended-dept">
            <h4 class="result-subtitle">建议就诊科室：</h4>
            <div class="dept-tags">
              <el-tag type="primary" closable v-for="dept in analysisResult.recommendedDepartments" :key="dept" @click="goToDepartment(dept)">{{ dept }}</el-tag>
            </div>
          </div>
          <div class="advice">
            <h4 class="result-subtitle">就诊建议：</h4>
            <p class="advice-text">{{ analysisResult.advice }}</p>
          </div>
          <div class="emergency-warning" v-if="analysisResult.isEmergency">
            <el-alert
              title="紧急提示"
              description="您的症状可能需要紧急处理，请立即前往急诊科就诊或拨打120急救电话"
              type="error"
              show-icon
            ></el-alert>
          </div>
          <div class="action-buttons">
            <router-link :to="{ path: '/appointment' }" class="make-appointment-btn">
              立即预约
            </router-link>
            <router-link :to="{ path: '/doctors' }" class="view-doctors-btn">
              查看医生列表
            </router-link>
          </div>
        </div>
      </el-card>
      <!-- 常见问题指南 -->
      <el-card class="guide-card faq-section">
        <template slot="header">
          <h3 class="card-title">常见问题指南</h3>
        </template>
        <el-collapse accordion>
          <el-collapse-item title="如何选择合适的科室就诊？" name="1">
            <div class="faq-content">
              <p>选择科室主要根据您的症状和不适部位：</p>
              <ul>
                <li>头痛、头晕：建议先看神经内科</li>
                <li>心脏不适、胸痛：建议心内科</li>
                <li>腹痛、消化不良：建议消化内科</li>
                <li>关节疼痛：建议骨科或风湿免疫科</li>
                <li>发热、咳嗽：建议呼吸内科或感染科</li>
              </ul>
              <p>如不确定，可先到全科医学科或内科进行初步筛查。</p>
            </div>
          </el-collapse-item>
          <el-collapse-item title="初诊患者需要注意什么？" name="2">
            <div class="faq-content">
              <p>初诊患者需要注意以下几点：</p>
              <ul>
                <li>携带有效身份证件（身份证、医保卡等）</li>
                <li>提前准备好症状描述，包括发病时间、持续时长、症状变化等信息</li>
                <li>如有既往病历、检查报告等，请一并携带</li>
                <li>提前通过我院APP或网站预约，避免长时间排队等候</li>
                <li>建议上午前往就诊，以便进行各项检查</li>
              </ul>
            </div>
          </el-collapse-item>
          <el-collapse-item title="哪些情况需要看急诊？" name="3">
            <div class="faq-content">
              <p>以下情况建议立即前往急诊科就诊：</p>
              <ul>
                <li>突发剧烈胸痛、呼吸困难</li>
                <li>严重外伤、大量出血</li>
                <li>剧烈头痛、呕吐、意识不清</li>
                <li>高热（体温超过39.5℃）、持续不退</li>
                <li>突发腹痛、疑似急腹症</li>
                <li>严重过敏反应，如喉头水肿、呼吸困难等</li>
                <li>食物中毒症状严重者</li>
              </ul>
              <p>紧急情况下请直接拨打120急救电话。</p>
            </div>
          </el-collapse-item>
          <el-collapse-item title="预约挂号有哪些注意事项？" name="4">
            <div class="faq-content">
              <p>预约挂号需要注意：</p>
              <ul>
                <li>预约成功后，请在就诊当天提前30分钟到院取号</li>
                <li>如需取消预约，请至少提前4小时操作，避免影响个人信誉记录</li>
                <li>一个账号同一科室同一天只能预约一个号源</li>
                <li>挂号后请按照预约时间准时就诊，过时未到者可能需要重新排队</li>
                <li>特殊科室（如专家门诊）可能需要提前一周预约</li>
              </ul>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      <!-- 健康知识 -->
      <el-card class="guide-card health-tips">
        <template slot="header">
          <h3 class="card-title">健康小贴士</h3>
        </template>
        <div class="health-tips-content">
          <div class="tip-item">
            <i class="el-icon-info-circle health-icon"></i>
            <div class="tip-text">
              <p class="tip-title">保持良好作息习惯</p>
              <p class="tip-desc">每天保持7-8小时充足睡眠，有助于增强免疫力，促进身体健康。</p>
            </div>
          </div>
          <div class="tip-item">
            <i class="el-icon-medal health-icon"></i>
            <div class="tip-text">
              <p class="tip-title">合理饮食结构</p>
              <p class="tip-desc">多吃蔬菜水果，控制脂肪和盐分摄入，保持饮食多样化。</p>
            </div>
          </div>
          <div class="tip-item">
            <i class="el-icon-cpu health-icon"></i>
            <div class="tip-text">
              <p class="tip-title">适量运动锻炼</p>
              <p class="tip-desc">每周至少进行150分钟中等强度有氧运动，保持身体健康活力。</p>
            </div>
        </div>
          <div class="tip-item">
            <i class="el-icon-info-circle health-icon"></i>
            <div class="tip-text">
              <p class="tip-title">保持良好作息习惯</p>
              <p class="tip-desc">每天保持7-8小时充足睡眠，有助于增强免疫力，促进身体健康。</p>
            </div>
          </div>
          <div class="tip-item">
            <i class="el-icon-medal health-icon"></i>
            <div class="tip-text">
              <p class="tip-title">合理饮食结构</p>
              <p class="tip-desc">多吃蔬菜水果，控制脂肪和盐分摄入，保持饮食多样化。</p>
            </div>
          </div>
          <div class="tip-item">
            <i class="el-icon-cpu health-icon"></i>
            <div class="tip-text">
              <p class="tip-title">适量运动锻炼</p>
              <p class="tip-desc">每周至少进行150分钟中等强度有氧运动，保持身体健康活力。</p>
            </div>
          </div>
        </div>
      </el-card>
    </main>
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 医院预约系统 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script>
import medicalGuideAPI from '../api/medicalGuide';

export default {
  name: 'MedicalGuide',
  data() {
    return {
      user: null,
      analyzing: false,
      analysisResult: null,
      symptomForm: {
        mainSymptom: '',
        secondarySymptoms: [],
        duration: '',
        severity: 3
      },
      commonSymptoms: [
        { label: '头痛', value: 'headache' },
        { label: '腹痛', value: 'abdominalPain' },
        { label: '发热', value: 'fever' },
        { label: '咳嗽', value: 'cough' },
        { label: '胸痛', value: 'chestPain' },
        { label: '头晕', value: 'dizziness' },
        { label: '恶心呕吐', value: 'nausea' },
        { label: '关节疼痛', value: 'jointPain' }
      ],
      allSymptoms: [
        { label: '头痛', value: 'headache' },
        { label: '头晕', value: 'dizziness' },
        { label: '发热', value: 'fever' },
        { label: '咳嗽', value: 'cough' },
        { label: '胸闷', value: 'chestTightness' },
        { label: '胸痛', value: 'chestPain' },
        { label: '腹痛', value: 'abdominalPain' },
        { label: '恶心', value: 'nausea' },
        { label: '呕吐', value: 'vomiting' },
        { label: '腹泻', value: 'diarrhea' },
        { label: '便秘', value: 'constipation' },
        { label: '关节疼痛', value: 'jointPain' },
        { label: '肌肉酸痛', value: 'musclePain' },
        { label: '呼吸困难', value: 'difficultyBreathing' },
        { label: '乏力', value: 'fatigue' },
        { label: '失眠', value: 'insomnia' },
        { label: '皮疹', value: 'rash' },
        { label: '视力模糊', value: 'blurredVision' }
      ],
      // 症状与科室推荐映射关系
      symptomToDepartment: {
        'headache': ['神经内科', '神经外科'],
        'dizziness': ['神经内科', '耳鼻喉科'],
        'fever': ['呼吸内科', '感染科'],
        'cough': ['呼吸内科'],
        'chestPain': ['心内科', '呼吸内科'],
        'abdominalPain': ['消化内科', '普通外科'],
        'nausea': ['消化内科'],
        'jointPain': ['骨科', '风湿免疫科']
      }
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      }
    },
    analyzeSymptoms() {
      if (!this.symptomForm.mainSymptom) {
        this.$message.warning('请选择主要症状');
        return
      }
      
      this.analyzing = true
      
      // 构建症状字符串
      let symptoms = this.symptomForm.mainSymptom;
      if (this.symptomForm.secondarySymptoms && this.symptomForm.secondarySymptoms.length > 0) {
        symptoms += ',' + this.symptomForm.secondarySymptoms.join(',');
      }
      
      // 调用后端API进行症状分析
      medicalGuideAPI.getDepartmentRecommendation(symptoms)
        .then(response => {
          // 生成分析结果
          this.analysisResult = {
            recommendedDepartments: response.recommendedDepartments || ['全科医学科'],
            advice: response.advice || '建议前往推荐科室就诊',
            isEmergency: response.isEmergency || false
          }
        })
        .catch(error => {
          console.error('症状分析失败:', error);
          this.$message.error('症状分析失败，请稍后重试');
          
          // 使用本地备用逻辑
          let recommendedDepartments = this.symptomToDepartment[this.symptomForm.mainSymptom] || ['全科医学科']
          let advice = '系统暂时无法连接后端服务，以下是基于本地数据的推荐。';
          let isEmergency = false
          
          if (this.symptomForm.severity > 4) {
            if (this.symptomForm.mainSymptom === 'chestPain' || 
                this.symptomForm.mainSymptom === 'difficultyBreathing') {
              isEmergency = true
            }
          }
          
          this.analysisResult = {
            recommendedDepartments,
            advice,
            isEmergency
          }
        })
        .finally(() => {
          this.analyzing = false
        })
    },
    goToDepartment(deptName) {
      // 跳转到科室列表，带上科室名称作为筛选条件
      this.$router.push({ path: '/departments', query: { search: deptName } })
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        this.user = null
        this.$router.push('/login')
        this.$message.success('退出登录成功')
      }).catch(() => {
        // 取消退出
      })
    }
  }
}
</script>

<style scoped>
.medical-guide-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 顶部导航栏 - 复用样式 */
.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.logo i {
  margin-right: 10px;
  font-size: 24px;
}

.nav-menu {
  display: flex;
}

.nav-item {
  margin: 0 20px;
  color: #606266;
  text-decoration: none;
  font-size: 16px;
  padding: 5px 0;
  position: relative;
  transition: color 0.3s;
}

.nav-item:hover,
.nav-item.active {
  color: #1890ff;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
}

.logged-in {
  display: flex;
  align-items: center;
}

.welcome {
  margin-right: 20px;
  color: #606266;
}

.btn-text,
.btn-primary {
  padding: 5px 15px;
  margin-left: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.btn-text {
  color: #606266;
  background: none;
  border: none;
}

.btn-text:hover {
  color: #1890ff;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
  border: none;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.page-desc {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

/* 导诊卡片 */
.guide-card {
  margin-bottom: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 症状自查表单 */
.symptom-form {
  padding: 0 10px;
}

.submit-section {
  text-align: center;
}

/* 分析结果 */
.analysis-result .el-card__body {
  padding: 20px;
}

.result-content {
  line-height: 1.6;
}

.result-subtitle {
  font-size: 16px;
  color: #303133;
  margin: 15px 0 10px 0;
  font-weight: 500;
}

.dept-tags {
  margin-bottom: 20px;
}

.dept-tags .el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
  cursor: pointer;
}

.advice-text {
  color: #606266;
  background-color: #f0f9ff;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #1890ff;
}

.emergency-warning {
  margin: 20px 0;
}

.action-buttons {
  margin-top: 25px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.make-appointment-btn,
.view-doctors-btn {
  padding: 10px 24px;
  border-radius: 4px;
  text-decoration: none;
  transition: all 0.3s;
  display: inline-block;
  font-weight: 500;
}

.make-appointment-btn {
  background-color: #1890ff;
  color: white;
}

.make-appointment-btn:hover {
  background-color: #40a9ff;
}

.view-doctors-btn {
  background-color: #fff;
  color: #1890ff;
  border: 1px solid #1890ff;
}

.view-doctors-btn:hover {
  background-color: #f0f9ff;
}

/* FAQ部分 */
.faq-section .el-collapse-item__header {
  font-size: 16px;
  color: #303133;
}

.faq-content {
  padding: 10px 0;
  color: #606266;
  line-height: 1.8;
}

.faq-content ul {
  padding-left: 20px;
  margin: 10px 0;
}

.faq-content li {
  margin-bottom: 6px;
}

/* 健康小贴士 */
.health-tips-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.tip-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background-color: #f6ffed;
  border-radius: 4px;
  border: 1px solid #b7eb8f;
  flex: 1 1 300px;
  min-width: 280px;
}

.health-icon {
  font-size: 24px;
  color: #52c41a;
  flex-shrink: 0;
  margin-top: 3px;
}

.tip-text {
  flex: 1;
}

.tip-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 8px 0;
}

.tip-desc {
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

/* 页脚 */
.footer {
  background-color: #f0f2f5;
  padding: 20px 0;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 15px;
  }
  
  .nav-menu {
    margin: 15px 0;
  }
  
  .nav-item {
    margin: 0 10px;
  }
  
  .health-tips-content {
    flex-direction: column;
  }
  
  .tip-item {
    min-width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .make-appointment-btn,
  .view-doctors-btn {
    text-align: center;
  }
}
</style>
