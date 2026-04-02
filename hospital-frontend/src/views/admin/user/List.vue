<template>
  <div class="user-page">
    <div class="page-header">
      <h2>用户管理系统</h2>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增用户</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="输入用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="登录账号" width="120" />
      <el-table-column prop="nickName" label="昵称" width="120" />
      <el-table-column label="角色" width="150">
        <template slot-scope="scope">
          <el-tag :type="scope.row.roleName === '未分配' ? 'info' : 'primary'" size="small">
            {{ scope.row.roleName || '未分配' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" min-width="180" />
      <el-table-column label="操作" width="200" fixed="right" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)">删除账号</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" @current-change="handlePageChange" />
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="form.nickName" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="分配角色" style="width: 100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="医生" :value="2" />
            <el-option label="患者" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserPage, addUser, updateUser, deleteUser } from '@/api/user'

export default {
  data() {
    return {
      tableData: [], total: 0, currentPage: 1, pageSize: 10,
      searchForm: { username: '' },
      dialogVisible: false, dialogTitle: '', submitLoading: false,
      form: { id: null, username: '', password: '', nickName: '', roleId: null },
      rules: {
        username: [{ required: true, message: '必填', trigger: 'blur' }],
        roleId: [{ required: true, message: '必选', trigger: 'change' }]
      }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const res = await getUserPage({ current: this.currentPage, size: this.pageSize, ...this.searchForm })
      this.tableData = res.data.records
      this.total = res.data.total
    },
    handleSearch() { this.currentPage = 1; this.loadData(); },
    handleReset() { this.searchForm = { username: '' }; this.handleSearch(); },
    handlePageChange(val) { this.currentPage = val; this.loadData(); },
    handleAdd() {
      this.form = { id: null, username: '', password: '', nickName: '', roleId: null }
      this.dialogTitle = '新增'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑'
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.form.id) await updateUser(this.form)
          else await addUser(this.form)
          this.$message.success('成功')
          this.dialogVisible = false
          this.loadData()
        } catch (e) {
          // 捕捉 500 异常，防止红屏崩溃
          this.$message.error('保存失败：数据库异常，请检查关联表 id 是否自增')
        } finally { this.submitLoading = false }
      })
    },
    handleDelete(row) {
      this.$confirm('确定注销？', '警告', { type: 'error' }).then(async () => {
        await deleteUser(row.id)
        this.$message.success('已注销')
        this.loadData()
      })
    }
  }
}
</script>

<style scoped>
.user-page { padding: 20px; background: #fff; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.search-form { margin-bottom: 20px; background: #f9f9f9; padding: 15px; }
</style>
