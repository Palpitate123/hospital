<template>
  <div class="patient-department">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/patient/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>科室列表</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="search-card">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索科室名称"
        prefix-icon="el-icon-search"
        clearable
        @input="handleSearch"
        style="width: 300px"
      ></el-input>
    </el-card>

    <el-row :gutter="20" class="department-list">
      <el-col :span="6" v-for="dept in departmentList" :key="dept.id">
        <el-card class="department-card" shadow="hover" @click.native="handleSelect(dept)">
          <div class="dept-icon">
            <i :class="getDeptIcon(dept.category)"></i>
          </div>
          <div class="dept-info">
            <h3>{{ dept.deptName }}</h3>
            <p class="dept-intro">{{ dept.introduction }}</p>
            <div class="dept-meta">
              <span><i class="el-icon-user"></i> {{ dept.doctorCount }}位医生</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="queryParams.pageNum"
      :page-sizes="[8, 16, 24]"
      :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next"
      :total="total"
      class="pagination"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: 'PatientDepartment',
  data() {
    return {
      searchKeyword: '',
      queryParams: {
        pageNum: 1,
        pageSize: 8
      },
      departmentList: [
        { id: 1, deptName: '内科', category: 'internal', introduction: '内科是临床医学的一个专科，几乎是所有其他临床医学的基础...', doctorCount: 15 },
        { id: 2, deptName: '外科', category: 'surgery', introduction: '外科是以手术为主要治疗方法的临床医学专科...', doctorCount: 12 },
        { id: 3, deptName: '儿科', category: 'pediatrics', introduction: '儿科是研究小儿生长发育规律及其相关疾病的医学学科...', doctorCount: 8 },
        { id: 4, deptName: '妇产科', category: 'obstetrics', introduction: '妇产科是诊治女性生殖系统疾病及妊娠分娩的专科...', doctorCount: 10 },
        { id: 5, deptName: '骨科', category: 'orthopedics', introduction: '骨科是研究骨骼、肌肉系统疾病的医学专科...', doctorCount: 6 },
        { id: 6, deptName: '眼科', category: 'ophthalmology', introduction: '眼科是研究眼部疾病的医学专科...', doctorCount: 5 },
        { id: 7, deptName: '耳鼻喉科', category: 'ent', introduction: '耳鼻喉科是诊治耳、鼻、喉疾病的专科...', doctorCount: 4 },
        { id: 8, deptName: '皮肤科', category: 'dermatology', introduction: '皮肤科是诊治皮肤疾病的医学专科...', doctorCount: 3 }
      ],
      total: 8
    }
  },
  methods: {
    getDeptIcon(category) {
      const icons = {
        internal: 'el-icon-first-aid-kit',
        surgery: 'el-icon-edit-outline',
        pediatrics: 'el-icon-sunny',
        obstetrics: 'el-icon-s-custom',
        orthopedics: 'el-icon-s-platform',
        ophthalmology: 'el-icon-view',
        ent: 'el-icon-headset',
        dermatology: 'el-icon-picture'
      }
      return icons[category] || 'el-icon-s-grid'
    },
    handleSearch() {
      this.queryParams.pageNum = 1
      this.loadDepartments()
    },
    loadDepartments() {
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.loadDepartments()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.loadDepartments()
    },
    handleSelect(dept) {
      this.$router.push(`/patient/department/${dept.id}`)
    }
  }
}
</script>

<style scoped>
.patient-department {
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.department-list {
  margin-bottom: 20px;
}
.department-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}
.department-card:hover {
  transform: translateY(-5px);
}
.dept-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
}
.dept-icon i {
  font-size: 28px;
  color: #fff;
}
.dept-info {
  text-align: center;
}
.dept-info h3 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #303133;
}
.dept-intro {
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.dept-meta {
  margin-top: 10px;
  font-size: 13px;
  color: #67C23A;
}
.pagination {
  text-align: center;
}
</style>
