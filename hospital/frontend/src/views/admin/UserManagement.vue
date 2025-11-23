<template>
  <div class="user-management">
    <div class="management-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="showAddUserDialog">
        <i class="el-icon-plus"></i> 添加用户
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名、姓名或手机号"
        prefix-icon="el-icon-search"
        clearable
        @input="handleSearch"
        style="width: 300px; margin-right: 15px;"
      ></el-input>
      
      <el-select
        v-model="selectedRole"
        placeholder="选择用户角色"
        clearable
        @change="handleSearch"
        style="width: 150px; margin-right: 15px;"
      >
        <el-option label="患者" value="PATIENT"></el-option>
        <el-option label="医生" value="DOCTOR"></el-option>
        <el-option label="管理员" value="ADMIN"></el-option>
      </el-select>
      
      <el-select
        v-model="selectedStatus"
        placeholder="选择用户状态"
        clearable
        @change="handleSearch"
        style="width: 150px;"
      >
        <el-option label="启用" :value="1"></el-option>
        <el-option label="禁用" :value="0"></el-option>
      </el-select>
    </div>

    <!-- 用户列表 -->
    <div class="user-list">
      <el-table
        :data="filteredUsers"
        style="width: 100%"
        stripe
        border
        v-loading="loading"
        element-loading-text="加载中..."
        ref="userTable"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="用户ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="realName" label="真实姓名"></el-table-column>
        <el-table-column prop="phone" label="手机号"></el-table-column>
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip></el-table-column>
        <el-table-column prop="role" label="角色">
          <template slot-scope="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleName(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-color="#67c23a"
              inactive-color="#909399"
              @change="updateUserStatus(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showEditUserDialog(scope.row)">
              编辑
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="deleteUser(scope.row)"
              v-if="scope.row.role !== 'ADMIN'"
              style="color: #f56c6c;"
            >
              删除
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="resetPassword(scope.row)"
              v-if="scope.row.role !== 'ADMIN'"
            >
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next, jumper, sizes, total"
          :total="filteredUsers.length"
          :page-size.sync="pageSize"
          :current-page.sync="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 添加用户对话框 -->
    <el-dialog
      title="添加用户"
      :visible.sync="addUserDialogVisible"
      width="500px"
      @close="resetAddUserForm"
    >
      <el-form
        :model="addUserForm"
        :rules="addUserRules"
        ref="addUserForm"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addUserForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="addUserForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addUserForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addUserForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="用户角色" prop="role">
          <el-select v-model="addUserForm.role" placeholder="请选择用户角色">
            <el-option label="患者" value="PATIENT"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="初始密码" prop="password">
          <el-input
            v-model="addUserForm.password"
            type="password"
            placeholder="请输入初始密码"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addUserDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddUser">确定</el-button>
      </div>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog
      title="编辑用户"
      :visible.sync="editUserDialogVisible"
      width="500px"
    >
      <el-form
        :model="editUserForm"
        :rules="editUserRules"
        ref="editUserForm"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editUserForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editUserForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editUserForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editUserForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="用户角色" prop="role" v-if="editUserForm.role !== 'ADMIN'">
          <el-select v-model="editUserForm.role" placeholder="请选择用户角色">
            <el-option label="患者" value="PATIENT"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户角色" v-else>
          <el-input :value="getRoleName(editUserForm.role)" disabled></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editUserDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditUser">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量操作 -->
    <div class="batch-operations" v-if="selectedUsers.length > 0">
      <el-tag>
        已选择 {{ selectedUsers.length }} 项
      </el-tag>
      <el-button
        type="danger"
        size="small"
        @click="batchDeleteUsers"
        :disabled="selectedUsers.some(user => user.role === 'ADMIN')"
      >
        批量删除
      </el-button>
      <el-button
        type="warning"
        size="small"
        @click="batchEnableUsers"
      >
        批量启用
      </el-button>
      <el-button
        type="warning"
        size="small"
        @click="batchDisableUsers"
      >
        批量禁用
      </el-button>
      <el-button size="small" @click="clearSelection">清空选择</el-button>
    </div>
  </div>
</template>

<script>
import patientAPI from '@/api/patient'
import doctorAPI from '@/api/doctor'
import request from '@/api/request'

export default {
  name: 'UserManagement',
  data() {
    return {
      // 用户列表数据
      users: [],
      filteredUsers: [],
      loading: false,
      
      // 搜索和筛选条件
      searchKeyword: '',
      selectedRole: '',
      selectedStatus: '',
      
      // 分页信息
      currentPage: 1,
      pageSize: 10,
      
      // 选中的用户
      selectedUsers: [],
      // 用户管理专用API
      userAPI: {
        // 获取所有用户（管理员视角）
        getAllUsers: () => {
          return request({
            url: '/api/user/all',
            method: 'get'
          })
        },
        // 添加用户
        addUser: (userData) => {
          return request({
            url: '/api/user/add',
            method: 'post',
            data: userData
          })
        },
        // 更新用户
        updateUser: (userId, userData) => {
          return request({
            url: `/api/user/update/${userId}`,
            method: 'put',
            data: userData
          })
        },
        // 删除用户
        deleteUser: (userId) => {
          return request({
            url: `/api/user/delete/${userId}`,
            method: 'delete'
          })
        },
        // 更新用户状态
        updateUserStatus: (userId, status) => {
          return request({
            url: `/api/user/status/${userId}`,
            method: 'put',
            data: { status: status }
          })
        },
        // 重置密码
        resetPassword: (userId) => {
          return request({
            url: `/api/user/reset-password/${userId}`,
            method: 'put'
          })
        }
      },
      
      // 添加用户对话框
      addUserDialogVisible: false,
      addUserForm: {
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: 'PATIENT',
        password: ''
      },
      addUserRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择用户角色', trigger: 'change' }
        ],
        password: [
          { required: true, message: '请输入初始密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
        ]
      },
      
      // 编辑用户对话框
      editUserDialogVisible: false,
      editUserForm: {
        id: '',
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: ''
      },
      editUserRules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择用户角色', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.fetchUsers()
  },
  computed: {
    // 分页后的用户列表
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredUsers.slice(start, end)
    }
  },
  methods: {
    // 获取用户列表
    async fetchUsers() {
      this.loading = true
      try {
        // 尝试获取所有用户（管理员视角）
        try {
          const response = await this.userAPI.getAllUsers()
          if (response.data && response.data.length > 0) {
            this.users = response.data.map(user => ({
              ...user,
              role: user.role || 'UNKNOWN'
            }))
          } else {
            // 如果没有统一的用户API，尝试从不同模块获取
            await this.fetchUsersFromModules()
          }
        } catch (error) {
          console.log('统一用户API不可用，尝试从不同模块获取数据')
          await this.fetchUsersFromModules()
        }
        
        this.filteredUsers = [...this.users]
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败')
        // 失败时使用空列表
        this.users = []
        this.filteredUsers = []
      } finally {
        this.loading = false
      }
    },
    
    // 从不同模块获取用户数据
    async fetchUsersFromModules() {
      try {
        // 并行获取患者和医生数据
        const [patientsRes, doctorsRes] = await Promise.all([
          patientAPI.getAllPatients(),
          doctorAPI.getAllDoctors()
        ])
        
        // 处理患者数据
        const patients = patientsRes.data ? patientsRes.data.map(p => ({
          id: p.id,
          username: p.username || `patient${p.id}`,
          realName: p.realName || p.name,
          phone: p.phone || '',
          email: p.email || '',
          role: 'PATIENT',
          status: p.status !== undefined ? p.status : 1,
          createTime: p.createTime || new Date().toISOString()
        })) : []
        
        // 处理医生数据
        const doctors = doctorsRes.data ? doctorsRes.data.map(d => ({
          id: d.id,
          username: d.username || `doctor${d.id}`,
          realName: d.realName || d.name,
          phone: d.phone || '',
          email: d.email || '',
          role: 'DOCTOR',
          status: d.status !== undefined ? d.status : 1,
          createTime: d.createTime || new Date().toISOString()
        })) : []
        
        // 添加管理员账户（模拟）
        const adminUser = {
          id: 1,
          username: 'admin',
          realName: '管理员',
          phone: '13800138000',
          email: 'admin@hospital.com',
          role: 'ADMIN',
          status: 1,
          createTime: '2023-01-01 00:00:00'
        }
        
        // 合并所有用户数据
        this.users = [adminUser, ...patients, ...doctors]
      } catch (error) {
        console.error('从模块获取用户数据失败:', error)
        throw error
      }
    },

    // 搜索用户
    handleSearch() {
      let results = [...this.users]
      
      // 根据关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        results = results.filter(user => 
          user.username.toLowerCase().includes(keyword) ||
          user.realName.toLowerCase().includes(keyword) ||
          user.phone.includes(keyword)
        )
      }
      
      // 根据角色筛选
      if (this.selectedRole) {
        results = results.filter(user => user.role === this.selectedRole)
      }
      
      // 根据状态筛选
      if (this.selectedStatus !== '') {
        results = results.filter(user => user.status === parseInt(this.selectedStatus))
      }
      
      this.filteredUsers = results
      this.currentPage = 1 // 重置到第一页
    },

    // 获取角色名称
    getRoleName(role) {
      const roleMap = {
        'PATIENT': '患者',
        'DOCTOR': '医生',
        'ADMIN': '管理员'
      }
      return roleMap[role] || role
    },

    // 获取角色标签类型
    getRoleType(role) {
      const typeMap = {
        'PATIENT': 'primary',
        'DOCTOR': 'success',
        'ADMIN': 'danger'
      }
      return typeMap[role] || 'info'
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
    },

    // 显示添加用户对话框
    showAddUserDialog() {
      this.resetAddUserForm()
      this.addUserDialogVisible = true
    },

    // 重置添加用户表单
    resetAddUserForm() {
      this.addUserForm = {
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: 'PATIENT',
        password: ''
      }
      this.$refs.addUserForm && this.$refs.addUserForm.resetFields()
    },

    // 提交添加用户
    submitAddUser() {
      this.$refs.addUserForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await this.userAPI.addUser(this.addUserForm)
            
            // 根据用户角色，可能需要向对应的模块添加记录
            if (this.addUserForm.role === 'PATIENT' && patientAPI.addPatient) {
              await patientAPI.addPatient({
                ...this.addUserForm,
                id: response.data.id
              })
            } else if (this.addUserForm.role === 'DOCTOR' && doctorAPI.addDoctor) {
              await doctorAPI.addDoctor({
                ...this.addUserForm,
                id: response.data.id
              })
            }
            
            this.$message.success('用户添加成功')
            this.fetchUsers() // 重新获取用户列表
            this.addUserDialogVisible = false
            this.resetAddUserForm()
          } catch (error) {
            console.error('添加用户失败:', error)
            this.$message.error('添加用户失败')
          }
        }
      })
    },

    // 显示编辑用户对话框
    showEditUserDialog(user) {
      this.editUserForm = { ...user }
      this.editUserDialogVisible = true
    },

    // 提交编辑用户
    submitEditUser() {
      this.$refs.editUserForm.validate(async (valid) => {
        if (valid) {
          try {
            await this.userAPI.updateUser(this.editUserForm.id, this.editUserForm)
            
            // 根据用户角色，可能需要更新对应的模块记录
            if (this.editUserForm.role === 'PATIENT' && patientAPI.updatePatient) {
              await patientAPI.updatePatient(this.editUserForm.id, this.editUserForm)
            } else if (this.editUserForm.role === 'DOCTOR' && doctorAPI.updateDoctor) {
              await doctorAPI.updateDoctor(this.editUserForm.id, this.editUserForm)
            }
            
            this.$message.success('用户更新成功')
            this.fetchUsers() // 重新获取用户列表
            this.editUserDialogVisible = false
          } catch (error) {
            console.error('更新用户失败:', error)
            this.$message.error('更新用户失败')
          }
        }
      })
    },

    // 更新用户状态
    async updateUserStatus(user) {
      try {
        await this.userAPI.updateUserStatus(user.id, user.status)
        
        // 根据用户角色，可能需要更新对应的模块记录
        if (user.role === 'PATIENT' && patientAPI.updatePatientStatus) {
          await patientAPI.updatePatientStatus(user.id, user.status)
        } else if (user.role === 'DOCTOR' && doctorAPI.updateDoctorStatus) {
          await doctorAPI.updateDoctorStatus(user.id, user.status)
        }
        
        this.$message.success(`用户状态已更新为${user.status ? '启用' : '禁用'}`)
        this.fetchUsers() // 重新获取用户列表以确保数据一致
      } catch (error) {
        console.error('更新用户状态失败:', error)
        this.$message.error('更新用户状态失败')
        // 恢复原状态
        user.status = user.status === 1 ? 0 : 1
      }
    },

    // 删除用户
    deleteUser(user) {
      this.$confirm(`确定要删除用户「${user.realName}」吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.userAPI.deleteUser(user.id)
          
          // 根据用户角色，可能需要删除对应的模块记录
          if (user.role === 'PATIENT' && patientAPI.deletePatient) {
            await patientAPI.deletePatient(user.id)
          } else if (user.role === 'DOCTOR' && doctorAPI.deleteDoctor) {
            await doctorAPI.deleteDoctor(user.id)
          }
          
          this.fetchUsers() // 重新获取用户列表
          this.$message.success('用户删除成功')
        } catch (error) {
          console.error('删除用户失败:', error)
          this.$message.error('删除用户失败')
        }
      }).catch(() => {
        // 取消删除
      })
    },

    // 重置密码
    resetPassword(user) {
      this.$confirm(`确定要重置用户「${user.realName}」的密码吗？`, '重置确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.userAPI.resetPassword(user.id)
          this.$message.success(`用户「${user.realName}」的密码已重置为：123456`)
        } catch (error) {
          console.error('重置密码失败:', error)
          this.$message.error('重置密码失败')
        }
      }).catch(() => {
        // 取消重置
      })
    },

    // 批量删除用户
    batchDeleteUsers() {
      if (this.selectedUsers.some(user => user.role === 'ADMIN')) {
        this.$message.warning('不能删除管理员用户')
        return
      }

      const userNames = this.selectedUsers.map(u => u.realName).join('、')
      this.$confirm(`确定要删除以下用户吗？\n${userNames}`, '批量删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 并行删除所有选中的用户
          const deletePromises = this.selectedUsers.map(user => {
            const deleteUserPromise = this.userAPI.deleteUser(user.id)
            
            // 根据用户角色，可能需要删除对应的模块记录
            if (user.role === 'PATIENT' && patientAPI.deletePatient) {
              return Promise.all([deleteUserPromise, patientAPI.deletePatient(user.id)])
            } else if (user.role === 'DOCTOR' && doctorAPI.deleteDoctor) {
              return Promise.all([deleteUserPromise, doctorAPI.deleteDoctor(user.id)])
            }
            
            return deleteUserPromise
          })
          
          await Promise.all(deletePromises)
          
          this.fetchUsers() // 重新获取用户列表
          this.selectedUsers = []
          this.$message.success('批量删除成功')
        } catch (error) {
          console.error('批量删除用户失败:', error)
          this.$message.error('批量删除用户失败')
        }
      }).catch(() => {
        // 取消删除
      })
    },

    // 批量启用用户
    async batchEnableUsers() {
      try {
        // 并行启用所有选中的用户
        const enablePromises = this.selectedUsers.map(user => {
          const enableUserPromise = this.userAPI.updateUserStatus(user.id, 1)
          
          // 根据用户角色，可能需要更新对应的模块记录
          if (user.role === 'PATIENT' && patientAPI.updatePatientStatus) {
            return Promise.all([enableUserPromise, patientAPI.updatePatientStatus(user.id, 1)])
          } else if (user.role === 'DOCTOR' && doctorAPI.updateDoctorStatus) {
            return Promise.all([enableUserPromise, doctorAPI.updateDoctorStatus(user.id, 1)])
          }
          
          return enableUserPromise
        })
        
        await Promise.all(enablePromises)
        
        this.fetchUsers() // 重新获取用户列表
        this.selectedUsers = []
        this.$message.success('批量启用成功')
      } catch (error) {
        console.error('批量启用用户失败:', error)
        this.$message.error('批量启用用户失败')
      }
    },

    // 批量禁用用户
    async batchDisableUsers() {
      if (this.selectedUsers.some(user => user.role === 'ADMIN')) {
        this.$message.warning('不能禁用管理员用户')
        return
      }

      try {
        // 并行禁用所有选中的用户
        const disablePromises = this.selectedUsers.map(user => {
          const disableUserPromise = this.userAPI.updateUserStatus(user.id, 0)
          
          // 根据用户角色，可能需要更新对应的模块记录
          if (user.role === 'PATIENT' && patientAPI.updatePatientStatus) {
            return Promise.all([disableUserPromise, patientAPI.updatePatientStatus(user.id, 0)])
          } else if (user.role === 'DOCTOR' && doctorAPI.updateDoctorStatus) {
            return Promise.all([disableUserPromise, doctorAPI.updateDoctorStatus(user.id, 0)])
          }
          
          return disableUserPromise
        })
        
        await Promise.all(disablePromises)
        
        this.fetchUsers() // 重新获取用户列表
        this.selectedUsers = []
        this.$message.success('批量禁用成功')
      } catch (error) {
        console.error('批量禁用用户失败:', error)
        this.$message.error('批量禁用用户失败')
      }
    },

    // 清空选择
    clearSelection() {
      this.$refs.userTable && this.$refs.userTable.clearSelection()
      this.selectedUsers = []
    },

    // 分页大小变化
    handleSizeChange(val) {
      this.pageSize = val
    },

    // 当前页码变化
    handleCurrentChange(val) {
      this.currentPage = val
    },

    // 多选变化
    handleSelectionChange(selection) {
      this.selectedUsers = selection
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.management-header h2 {
  margin: 0;
  color: #303133;
}

.search-filter {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-list {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 15px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.batch-operations {
  margin-top: 15px;
  padding: 10px 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.batch-operations .el-tag {
  background-color: #409eff;
  color: #fff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-filter {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .search-filter .el-input,
  .search-filter .el-select {
    width: 100% !important;
    margin-right: 0 !important;
  }
  
  .management-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .pagination {
    justify-content: center;
  }
}
</style>