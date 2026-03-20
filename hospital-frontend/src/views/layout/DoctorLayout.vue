<template>
  <el-container class="doctor-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <i class="el-icon-first-aid-kit"></i>
        <span>医生工作台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#1e3a5f"
        text-color="#fff"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/doctor/dashboard">
          <i class="el-icon-s-home"></i>
          <span>工作台首页</span>
        </el-menu-item>
        <el-menu-item index="/doctor/schedule">
          <i class="el-icon-date"></i>
          <span>排班管理</span>
        </el-menu-item>
        <el-menu-item index="/doctor/appointments">
          <i class="el-icon-s-order"></i>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/doctor/profile">
          <i class="el-icon-user"></i>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="hospital-name">{{ doctorInfo.hospitalName || '医院预约挂号系统' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" :src="doctorInfo.avatar || defaultAvatar"></el-avatar>
              <span class="user-name">{{ doctorInfo.realName || doctorInfo.userName }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'DoctorLayout',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    doctorInfo() {
      return this.$store.state.user.userInfo || {}
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/doctor/profile')
      } else if (command === 'logout') {
        this.$store.dispatch('user/logout').then(() => {
          this.$router.push('/login')
        })
      }
    }
  }
}
</script>

<style scoped>
.doctor-layout {
  height: 100vh;
}
.sidebar {
  background-color: #1e3a5f;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.logo i {
  font-size: 28px;
  margin-right: 10px;
  color: #409EFF;
}
.sidebar-menu {
  border-right: none;
}
.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.hospital-name {
  font-size: 18px;
  font-weight: 600;
  color: #1e3a5f;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.user-name {
  margin: 0 8px;
  color: #333;
}
.main-content {
  background: #f0f2f5;
  padding: 20px;
}
</style>
