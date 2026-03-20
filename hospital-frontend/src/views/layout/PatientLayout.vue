<template>
  <div class="patient-layout">
    <el-header class="patient-header">
      <div class="header-content">
        <div class="logo-section">
          <router-link to="/home" class="logo-link">
            <i class="el-icon-first-aid-kit logo-icon"></i>
            <span class="logo-text">医院预约挂号系统</span>
          </router-link>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          background-color="transparent"
          text-color="#333"
          active-text-color="#409EFF"
          router
          class="nav-menu"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/department">科室导航</el-menu-item>
          <el-menu-item index="/doctor">医生团队</el-menu-item>
        </el-menu>
        
        <div class="user-section">
          <template v-if="token">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
                <span class="username">{{ nickName || username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-document" command="orders">我的预约</el-dropdown-item>
                <el-dropdown-item icon="el-icon-user" command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item icon="el-icon-switch-button" command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
            <el-button size="small" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    
    <el-main class="patient-main">
      <transition name="fade" mode="out-in">
        <router-view />
      </transition>
    </el-main>
    
    <el-footer class="patient-footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">联系方式</a>
          <a href="#">帮助中心</a>
          <a href="#">隐私政策</a>
        </div>
        <div class="copyright">
          <p>© 2026 医院预约挂号系统 | 毕业设计作品</p>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'PatientLayout',
  computed: {
    ...mapGetters(['token', 'username', 'nickName']),
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    ...mapActions('user', ['logout']),
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          await this.logout()
          this.$router.push('/home')
        }).catch(() => {})
      } else if (command === 'orders') {
        this.$router.push('/my-orders')
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.patient-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.patient-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 60px !important;
  
  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
  }
  
  .logo-section {
    .logo-link {
      display: flex;
      align-items: center;
      text-decoration: none;
    }
    
    .logo-icon {
      font-size: 28px;
      color: #409EFF;
      margin-right: 10px;
    }
    
    .logo-text {
      font-size: 18px;
      font-weight: 600;
      color: #409EFF;
    }
  }
  
  .nav-menu {
    flex: 1;
    display: flex;
    justify-content: center;
    border-bottom: none;
    
    .el-menu-item {
      font-size: 15px;
      padding: 0 30px;
    }
  }
  
  .user-section {
    display: flex;
    align-items: center;
    
    .el-button {
      margin-left: 10px;
    }
    
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin: 0 8px;
        color: #333;
      }
    }
  }
}

.patient-main {
  flex: 1;
  margin-top: 60px;
  padding: 20px;
  background: #f5f7fa;
}

.patient-footer {
  background: #2c3e50;
  height: auto !important;
  padding: 30px 20px;
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    text-align: center;
  }
  
  .footer-links {
    margin-bottom: 15px;
    
    a {
      color: rgba(255, 255, 255, 0.7);
      margin: 0 20px;
      font-size: 14px;
      text-decoration: none;
      transition: color 0.3s;
      
      &:hover {
        color: #fff;
      }
    }
  }
  
  .copyright {
    color: rgba(255, 255, 255, 0.5);
    font-size: 13px;
  }
}
</style>
