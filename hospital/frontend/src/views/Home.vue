<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="header-content">
        <div class="logo">
          <i class="el-icon-medal-1"></i>
          <span>医院预约系统</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item active">首页</router-link>
          <router-link to="/departments" class="nav-item">科室介绍</router-link>
          <router-link to="/doctors" class="nav-item">医生团队</router-link>
          <router-link to="/medical-guide" class="nav-item">医疗导诊</router-link>
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
    <main class="home-main">
      <!-- 轮播图 -->
      <el-carousel height="400px" indicator-position="outside">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <div class="carousel-item">
            <h3 class="carousel-title">{{ item.title }}</h3>
            <p class="carousel-desc">{{ item.description }}</p>
          </div>
        </el-carousel-item>
      </el-carousel>
      <!-- 快捷服务 -->
      <section class="quick-services">
        <h2 class="section-title">快捷服务</h2>
        <div class="service-grid">
          <router-link to="/appointment" class="service-card" v-if="user && user.role !== 'ADMIN'">
            <div class="service-icon">
              <i class="el-icon-date"></i>
            </div>
            <h3 class="service-title">在线预约</h3>
            <p class="service-desc">快速预约医生，无需排队等候</p>
          </router-link>
          <router-link to="/appointment-list" class="service-card" v-if="user && user.role !== 'ADMIN'">
            <div class="service-icon">
              <i class="el-icon-document"></i>
            </div>
            <h3 class="service-title">预约记录</h3>
            <p class="service-desc">查看和管理您的预约信息</p>
          </router-link>
          <router-link to="/departments" class="service-card">
            <div class="service-icon">
              <i class="el-icon-office-building"></i>
            </div>
            <h3 class="service-title">科室查询</h3>
            <p class="service-desc">了解医院各科室信息和专长</p>
          </router-link>
          <router-link to="/doctors" class="service-card">
            <div class="service-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <h3 class="service-title">医生查询</h3>
            <p class="service-desc">查找专业医生，了解医生详情</p>
          </router-link>
          <router-link to="/medical-guide" class="service-card">
            <div class="service-icon">
              <i class="el-icon-guide"></i>
            </div>
            <h3 class="service-title">医疗导诊</h3>
            <p class="service-desc">智能推荐科室，提供就诊建议</p>
          </router-link>
          <router-link to="/admin/dashboard" class="service-card" v-if="user && user.role === 'ADMIN'">
            <div class="service-icon">
              <i class="el-icon-setting"></i>
            </div>
            <h3 class="service-title">管理中心</h3>
            <p class="service-desc">进入系统管理后台</p>
          </router-link>
        </div>
      </section>
      <!-- 医院介绍 -->
      <section class="hospital-info">
        <h2 class="section-title">医院介绍</h2>
        <div class="info-content">
          <div class="info-text">
            <p>我院是一家集医疗、教学、科研、预防保健为一体的综合性医院，拥有先进的医疗设备和专业的医护团队。</p>
            <p>医院占地面积广阔，环境优美，科室设置齐全，包括内科、外科、妇产科、儿科、眼科、口腔科等多个临床科室。</p>
            <p>我们始终坚持"以患者为中心"的服务理念，为患者提供高质量、便捷的医疗服务。</p>
          </div>
        </div>
      </section>
    </main>
    <!-- 页脚 -->
    <footer class="home-footer">
      <div class="footer-content">
        <p>© 2024 医院预约系统 版权所有</p>
        <p>联系方式：电话 0123-4567890 | 地址：医院路88号</p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      user: null,
      carouselItems: [
        {
          id: 1,
          title: '便捷的在线预约服务',
          description: '随时随地预约挂号，免去排队等待的烦恼'
        },
        {
          id: 2,
          title: '专业的医疗团队',
          description: '汇聚各科专家，为您提供优质的医疗服务'
        },
        {
          id: 3,
          title: '先进的医疗设备',
          description: '配备现代化医疗设备，确保诊断准确可靠'
        }
      ]
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
  },
  // 监听路由变化，刷新用户信息
  watch: {
    $route: {
      handler() {
        this.loadUserInfo()
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.home-header {
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

/* 内容区域 */
.home-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
}

/* 轮播图 */
.carousel-item {
  height: 400px;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #303133;
}

.carousel-title {
  font-size: 32px;
  margin-bottom: 20px;
  font-weight: bold;
}

.carousel-desc {
  font-size: 18px;
  color: #606266;
}

/* 快捷服务 */
.quick-services {
  margin-top: 40px;
}

.section-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 30px;
  color: #303133;
  font-weight: bold;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.service-card {
  background: white;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  text-decoration: none;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.service-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  background-color: #f0f9ff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 30px;
  color: #1890ff;
}

.service-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #303133;
}

.service-desc {
  color: #606266;
  font-size: 14px;
}

/* 医院介绍 */
.hospital-info {
  margin-top: 40px;
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-content {
  display: flex;
  gap: 30px;
}

.info-text p {
  line-height: 1.8;
  color: #606266;
  margin-bottom: 15px;
}

/* 页脚 */
.home-footer {
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

.footer-content p {
  margin: 5px 0;
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
  
  .carousel-title {
    font-size: 24px;
  }
  
  .carousel-desc {
    font-size: 16px;
  }
  
  .service-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
}
</style>
