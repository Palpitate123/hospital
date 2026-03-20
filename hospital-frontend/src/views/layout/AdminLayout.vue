<template>
  <div class="admin-layout">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
        <div class="logo-container">
          <i class="el-icon-first-aid-kit logo-icon" v-if="!isCollapse"></i>
          <span class="logo-text" v-if="!isCollapse">医院预约挂号系统</span>
          <span class="logo-mini" v-else>H</span>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-s-home"></i>
            <span slot="title">后台首页</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/department">
            <i class="el-icon-office-building"></i>
            <span slot="title">科室管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/doctor">
            <i class="el-icon-user"></i>
            <span slot="title">医生管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/schedule">
            <i class="el-icon-date"></i>
            <span slot="title">排班管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/user">
            <i class="el-icon-s-custom"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/order">
            <i class="el-icon-document"></i>
            <span slot="title">订单管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="admin-header">
          <div class="header-left">
            <i
              :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
              class="collapse-btn"
              @click="toggleCollapse"
            ></i>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
                <span class="username">{{ username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-user" command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item icon="el-icon-switch-button" command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <transition name="fade" mode="out-in">
            <router-view />
          </transition>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'AdminLayout',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    ...mapGetters(['username']),
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    ...mapActions('user', ['logout']),
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          await this.logout()
          this.$router.push('/login')
        }).catch(() => {})
      } else if (command === 'profile') {
        this.$message.info('个人信息功能开发中')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
}

.admin-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  
  .logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #263445;
    overflow: hidden;
    
    .logo-icon {
      font-size: 28px;
      color: #409EFF;
      margin-right: 10px;
    }
    
    .logo-text {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      white-space: nowrap;
    }
    
    .logo-mini {
      color: #fff;
      font-size: 24px;
      font-weight: bold;
    }
  }
  
  .el-menu {
    border-right: none;
  }
}

.admin-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .header-left {
    display: flex;
    align-items: center;
    
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 15px;
      color: #606266;
      
      &:hover {
        color: #409EFF;
      }
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin: 0 8px;
        color: #606266;
      }
    }
  }
}

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
