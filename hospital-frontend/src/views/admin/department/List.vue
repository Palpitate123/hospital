<template>
  <div class="department-page">
    <div class="page-header">
      <h2>科室管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增科室</el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="科室名称">
          <el-input v-model="searchForm.deptName" placeholder="请输入科室名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
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
      <el-table-column prop="deptName" label="科室名称" min-width="150" />
      <el-table-column prop="deptDesc" label="科室简介" min-width="250" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '停用' }}
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
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="科室名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室简介" prop="deptDesc">
          <el-input v-model="form.deptDesc" type="textarea" :rows="4" placeholder="请输入科室简介" />
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
import { getDepartmentPage, addDepartment, updateDepartment, deleteDepartment, updateDepartmentStatus } from '@/api/department'

export default {
  name: 'DepartmentList',
  data() {
    return {
      searchForm: {
        deptName: '',
        status: null
      },
      tableData: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        deptName: '',
        deptDesc: ''
      },
      rules: {
        deptName: [{ required: true, message: '请输入科室名称', trigger: 'blur' }]
      },
      submitLoading: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getDepartmentPage({
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
      this.searchForm = { deptName: '', status: null }
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
      this.dialogTitle = '新增科室'
      this.form = { id: null, deptName: '', deptDesc: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑科室'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) {
            await updateDepartment(this.form)
            this.$message.success('编辑成功')
          } else {
            await addDepartment(this.form)
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
      this.$confirm(`确定要${action}该科室吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await updateDepartmentStatus(row.id, newStatus)
        this.$message.success(`${action}成功`)
        this.loadData()
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确定要删除该科室吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteDepartment(row.id)
        this.$message.success('删除成功')
        this.loadData()
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.department-page {
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
