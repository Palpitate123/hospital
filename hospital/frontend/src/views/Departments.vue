<template>
  <div class="departments-container">
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
          <router-link to="/medical-guide" class="nav-item">医疗导诊</router-link>
        </nav>
        <div class="user-info">
          <div v-if="user" class="logged-in">
            <span class="welcome">欢迎，{{ user.name || user.username }}</span>
            <router-link to="/user-info" class="btn-text">个人信息</router-link>
            <button class="btn-text" @click="handleLogout">退出登录</button>
          </div>
          <div v-else class="not-logged-in">
            <router-link to="/login" class="btn-primary">登录</router-link>
            <router-link to="/register" class="btn-text">注册</router-link>
          </div>
        </div>
      </div>
    </header>
    
    <!-- 主内容区域 -->
    <main class="main-content">
      <div class="page-header">
        <h1>科室介绍</h1>
        <p class="page-desc">了解我院各科室的专业领域和服务范围</p>
      </div>
      
      <!-- 科室搜索 -->
      <div class="search-section">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索科室名称或关键词"
          prefix-icon="el-icon-search"
          @input="handleSearch"
          clearable
        ></el-input>
      </div>
      
      <!-- 科室分类导航 -->
      <div class="category-nav">
        <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange">
          <el-tab-pane label="全部科室" name="all"></el-tab-pane>
          <el-tab-pane v-for="category in departmentCategories" :key="category.id" :label="category.name" :name="category.id"></el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 科室列表 -->
      <div class="departments-list">
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="8" animated></el-skeleton>
        </div>
        
        <div v-else-if="filteredDepartments.length > 0" class="departments-grid">
          <div 
            v-for="department in filteredDepartments" 
            :key="department.id" 
            class="department-card"
            @click="viewDepartmentDetail(department)"
          >
            <div class="department-header">
              <div class="department-icon">
                <i class="el-icon-building"></i>
              </div>
              <h3 class="department-name">{{ department.name }}</h3>
              <div class="department-badge" v-if="department.category">
                {{ department.category.name }}
              </div>
            </div>
            
            <div class="department-info">
              <div class="info-item">
                <span class="info-label">科室主任：</span>
                <span class="info-value">{{ department.director || '待定' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">医生数量：</span>
                <span class="info-value">{{ department.doctorCount || 0 }} 人</span>
              </div>
              <div class="info-item">
                <span class="info-label">所在楼层：</span>
                <span class="info-value">{{ department.floor || '未设置' }}</span>
              </div>
            </div>
            
            <div class="department-description">
              {{ department.description || '暂无介绍' }}
            </div>
            
            <div class="department-actions">
              <el-button type="primary" size="small" @click.stop="viewDoctors(department.id)">
                查看医生团队
              </el-button>
            </div>
          </div>
        </div>
        
        <div v-else class="no-data">
          <el-empty description="暂无科室信息"></el-empty>
        </div>
      </div>
      
      <!-- 分页 -->
      <div v-if="!loading && filteredDepartments.length > 0" class="pagination">
        <el-pagination
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredDepartments.length"
          :page-size="pageSize"
          :page-sizes="[6, 12, 24, 48]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </main>
    
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 医院预约系统 版权所有</p>
      </div>
    </footer>
    
    <!-- 科室详情弹窗 -->
    <el-dialog
      :title="selectedDepartment?.name || '科室详情'"
      :visible.sync="showDetailDialog"
      width="60%"
      :close-on-click-modal="false"
    >
      <div v-if="selectedDepartment" class="department-detail">
        <div class="detail-header">
          <div class="detail-icon">
            <i class="el-icon-building"></i>
          </div>
          <div class="detail-basic">
            <h3 class="detail-name">{{ selectedDepartment.name }}</h3>
            <p class="detail-category">分类：{{ selectedDepartment.category?.name || '未分类' }}</p>
          </div>
        </div>
        
        <div class="detail-info">
          <div class="detail-section">
            <h4 class="section-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">科室主任：</span>
                <span class="value">{{ selectedDepartment.director || '待定' }}</span>
              </div>
              <div class="info-item">
                <span class="label">医生数量：</span>
                <span class="value">{{ selectedDepartment.doctorCount || 0 }} 人</span>
              </div>
              <div class="info-item">
                <span class="label">所在楼层：</span>
                <span class="value">{{ selectedDepartment.floor || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value">{{ selectedDepartment.phone || '未设置' }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h4 class="section-title">科室介绍</h4>
            <p class="full-description">{{ selectedDepartment.description || '暂无详细介绍' }}</p>
          </div>
          
          <div class="detail-section">
            <h4 class="section-title">诊疗范围</h4>
            <div class="treatment-scope">
              <el-tag 
                v-for="(item, index) in (selectedDepartment.treatmentScope || []).split(',')" 
                :key="index" 
                type="info"
                :disable-transitions="false"
              >
                {{ item.trim() }}
              </el-tag>
              <span v-if="!selectedDepartment.treatmentScope" class="no-scope">暂无诊疗范围信息</span>
            </div>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="viewDoctors(selectedDepartment?.id)">
          查看医生团队
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Departments',
  data() {
    return {
      user: null,
      departments: [],
      departmentCategories: [],
      selectedDepartment: null,
      showDetailDialog: false,
      searchKeyword: '',
      activeCategory: 'all',
      loading: false,
      pageSize: 6,
      currentPage: 1
    }
  },
  computed: {
    filteredDepartments() {
      let result = [...this.departments]
      
      // 关键词搜索
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.toLowerCase().trim()
        result = result.filter(dept => 
          dept.name.toLowerCase().includes(keyword) ||
          (dept.description && dept.description.toLowerCase().includes(keyword)) ||
          (dept.treatmentScope && dept.treatmentScope.toLowerCase().includes(keyword))
        )
      }
      
      // 分类筛选
      if (this.activeCategory !== 'all') {
        result = result.filter(dept => dept.categoryId === this.activeCategory)
      }
      
      return result
    },
    
    paginatedDepartments() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredDepartments.slice(start, end)
    }
  },
  created() {
    this.loadUserInfo()
    this.loadDepartmentCategories()
    this.loadDepartments()
  },
  methods: {
    loadUserInfo() {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        this.user = JSON.parse(userStr)
      }
    },
    
    loadDepartmentCategories() {
      this.$axios.get('/department/categories').then(res => {
        if (res.success) {
          this.departmentCategories = res.data
        }
      }).catch(err => {
        console.error('加载科室分类失败:', err)
      })
    },
    
    loadDepartments() {
      this.loading = true
      this.$axios.get('/department/list').then(res => {
        if (res.success) {
          this.departments = res.data
        }
      }).catch(err => {
        console.error('加载科室列表失败:', err)
        this.$message.error('加载科室信息失败')
      }).finally(() => {
        this.loading = false
      })
    },
    
    handleSearch() {
      this.currentPage = 1
    },
    
    handleCategoryChange() {
      this.currentPage = 1
    },
    
    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
    },
    
    handleCurrentChange(current) {
      this.currentPage = current
    },
    
    viewDepartmentDetail(department) {
      this.selectedDepartment = department
      this.showDetailDialog = true
    },
    
    viewDoctors(departmentId) {
      this.showDetailDialog = false
      this.$router.push({ 
        path: '/doctors', 
        query: { departmentId } 
      })
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
.departments-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏样式 */
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
  gap: 20px;
}

.nav-item {
  color: #606266;
  text-decoration: none;
  padding: 0 10px;
  height: 60px;
  line-height: 60px;
  position: relative;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #1890ff;
}

.nav-item.active {
  color: #1890ff;
  font-weight: bold;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 10px;
  right: 10px;
  height: 2px;
  background-color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome {
  color: #606266;
}

.btn-text {
  color: #1890ff;
  cursor: pointer;
  text-decoration: none;
}

.btn-text:hover {
  color: #40a9ff;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
  padding: 6px 16px;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #303133;
}

.page-desc {
  color: #606266;
  font-size: 16px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 20px;
}

.search-section .el-input {
  max-width: 600px;
  margin: 0 auto;
}

/* 分类导航 */
.category-nav {
  margin-bottom: 30px;
  background-color: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 科室列表 */
.departments-list {
  margin-bottom: 30px;
}

.loading-state {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
}

.departments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.department-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #ebeef5;
}

.department-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.department-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.department-icon {
  font-size: 32px;
  color: #1890ff;
  margin-right: 15px;
}

.department-name {
  flex: 1;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.department-badge {
  background-color: #ecf5ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.department-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-label {
  color: #606266;
  margin-right: 8px;
  min-width: 80px;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.department-description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.department-actions {
  text-align: right;
}

.no-data {
  text-align: center;
  padding: 60px 0;
}

/* 分页 */
.pagination {
  text-align: center;
  margin-top: 30px;
}

/* 科室详情弹窗 */
.department-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.detail-icon {
  font-size: 48px;
  color: #1890ff;
  margin-right: 20px;
}

.detail-name {
  font-size: 24px;
  margin: 0 0 5px 0;
  color: #303133;
}

.detail-category {
  margin: 0;
  color: #606266;
}

.detail-section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 18px;
  margin: 0 0 15px 0;
  color: #303133;
  border-left: 4px solid #1890ff;
  padding-left: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.detail-section .info-item {
  margin-bottom: 10px;
}

.detail-section .label {
  color: #606266;
  font-weight: 500;
  margin-right: 10px;
}

.detail-section .value {
  color: #303133;
}

.full-description {
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}

.treatment-scope {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.no-scope {
  color: #909399;
  font-style: italic;
}

.dialog-footer {
  text-align: right;
}

/* 页脚样式 */
.footer {
  background-color: #f5f7fa;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 10px;
  }
  
  .nav-menu {
    margin: 10px 0;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .departments-grid {
    grid-template-columns: 1fr;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-header {
    flex-direction: column;
    text-align: center;
  }
  
  .detail-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style>