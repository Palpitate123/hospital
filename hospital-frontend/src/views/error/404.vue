<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <div class="error-desc">
        <h2>抱歉，您访问的页面不存在</h2>
        <p>请检查您输入的网址是否正确，或点击下面的按钮返回首页。</p>
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
    <div class="error-illustration">
      <svg viewBox="0 0 400 300" xmlns="http://www.w3.org/2000/svg">
        <circle cx="200" cy="150" r="100" fill="#f5f7fa" />
        <path d="M160 120 L200 180 L240 120" stroke="#409EFF" stroke-width="8" fill="none" stroke-linecap="round" stroke-linejoin="round" />
        <circle cx="160" cy="100" r="8" fill="#409EFF" />
        <circle cx="240" cy="100" r="8" fill="#409EFF" />
        <path d="M150 200 Q200 230 250 200" stroke="#409EFF" stroke-width="6" fill="none" stroke-linecap="round" />
      </svg>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Error404',
  methods: {
    goHome() {
      this.$router.push('/')
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 40px;
}
.error-content {
  text-align: center;
  margin-right: 60px;
}
.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
  text-shadow: 2px 2px 4px rgba(64, 158, 255, 0.2);
}
.error-desc h2 {
  font-size: 24px;
  color: #303133;
  margin: 20px 0 10px;
}
.error-desc p {
  font-size: 14px;
  color: #909399;
  margin-bottom: 30px;
}
.error-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}
.error-illustration svg {
  width: 300px;
  height: 225px;
}
</style>
