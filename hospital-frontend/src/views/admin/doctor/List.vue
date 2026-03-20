<template>
  <div class="doctor-page">
    <div class="page-header">
      <h2>医生管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增医生</el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="科室">
          <el-select v-model="searchForm.deptId" placeholder="请选择科室" clearable>
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="searchForm.doctorName" placeholder="请输入医生姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="doctorName" label="医生姓名" width="120" />
      <el-table-column prop="deptName" label="所属科室" width="120" />
      <el-table-column prop="title" label="职称" width="120" />
      <el-table-column prop="specialty" label="专业擅长" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button 
            type="text" 
            size="small" 
            :style="{ color: scope.row.status === 1 ? '#E6A23C' : '#67C23A' }"
            @click="handleToggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '停用' : '启用' }}
          </el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username" v-if="!form.id">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="所属科室" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择科室" style="width: 100%">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生姓名" prop="doctorName">
          <el-input v-model="form.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="form.title" placeholder="请输入职称" />
        </el-form-item>
        <el-form-item label="专业擅长" prop="specialty">
          <el-input v-model="form.specialty" type="textarea" :rows="2" placeholder="请输入专业擅长" />
        </el-form-item>
        <el-form-item label="医生简介" prop="doctorDesc">
          <el-input v-model="form.doctorDesc" type="textarea" :rows="3" placeholder="请输入医生简介" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getDoctorPage, addDoctor, updateDoctor, deleteDoctor, updateDoctorStatus } from '@/api/doctor'
import { getDepartmentList } from '@/api/department'

export default {
  name: 'DoctorList',
  data() {
    return {
      searchForm: {
        deptId: null,
        doctorName: '',
        status: null
      },
      tableData: [],
      departmentList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        username: '',
        password: '',
        deptId: null,
        doctorName: '',
        title: '',
        specialty: '',
        doctorDesc: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        deptId: [{ required: true, message: '请选择科室', trigger: 'change' }],
        doctorName: [{ required: true, message: '请输入医生姓名', trigger: 'blur' }]
      },
      submitLoading: false
    }
  },
  created() {
    this.loadDepartments()
    this.loadData()
  },
  methods: {
    async loadDepartments() {
      try {
        const res = await getDepartmentList()
        this.departmentList = res.data || []
      } catch (error) {
        console.error('加载科室列表失败:', error)
      }
    },
    async loadData() {
      try {
        const res = await getDoctorPage({
          current: this.currentPage,
          size: this.pageSize,
          ...this.searchForm
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = { deptId: null, doctorName: '', status: null }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    handleAdd() {
      this.dialogTitle = '新增医生'
      this.form = { id: null, username: '', password: '', deptId: null, doctorName: '', title: '', specialty: '', doctorDesc: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑医生'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) {
            await updateDoctor(this.form)
            this.$message.success('编辑成功')
          } else {
            await addDoctor(this.form)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.loadData()
        } catch (error) {
          console.error('操作失败:', error)
        } finally {
          this.submitLoading = false
        }
      })
    },
    handleToggleStatus(row) {
      const newStatus = row.status === 1 ? 0 : 1
      const action = newStatus === 0 ? '停用' : '启用'
      this.$confirm(`确定要${action}该医生账号吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await updateDoctorStatus(row.id, newStatus)
        this.$message.success(`${action}成功`)
        this.loadData()
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确定要删除该医生吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteDoctor(row.id)
        this.$message.success('删除成功')
        this.loadData()
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.doctor-page {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 20px;
    color: #333;
  }
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>
