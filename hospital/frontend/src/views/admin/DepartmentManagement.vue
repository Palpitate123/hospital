<template>
  <div class="department-management">
    <div class="management-header">
      <h2>科室管理</h2>
      <el-button type="primary" @click="showAddDepartmentDialog">
        <i class="el-icon-plus"></i> 添加科室
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索科室名称、描述或负责人"
        prefix-icon="el-icon-search"
        clearable
        @input="handleSearch"
        style="width: 300px; margin-right: 15px;"
      ></el-input>
      
      <el-select
        v-model="selectedStatus"
        placeholder="选择科室状态"
        clearable
        @change="handleSearch"
        style="width: 150px;"
      >
        <el-option label="启用" :value="1"></el-option>
        <el-option label="禁用" :value="0"></el-option>
      </el-select>
    </div>

    <!-- 科室列表 -->
    <div class="department-list">
      <el-table
        :data="filteredDepartments"
        style="width: 100%"
        stripe
        border
        v-loading="loading"
        element-loading-text="加载中..."
        ref="departmentTable"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="科室ID" width="80"></el-table-column>
        <el-table-column prop="dept_name" label="科室名称" min-width="150"></el-table-column>
        <el-table-column prop="description" label="科室描述" show-overflow-tooltip min-width="200"></el-table-column>
        <el-table-column prop="responsiblePerson" label="科室负责人" min-width="120"></el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="120"></el-table-column>
        <el-table-column prop="location" label="科室位置" min-width="150"></el-table-column>
        <el-table-column prop="doctorCount" label="医生数量" width="100">
          <template slot-scope="scope">
            <el-tag type="primary" @click="viewDepartmentDoctors(scope.row)">
              {{ scope.row.doctorCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-color="#67c23a"
              inactive-color="#909399"
              @change="updateDepartmentStatus(scope.row)"
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
            <el-button type="text" size="small" @click="showEditDepartmentDialog(scope.row)">
              编辑
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="deleteDepartment(scope.row)"
              style="color: #f56c6c;"
            >
              删除
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="viewDepartmentDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next, jumper, sizes, total"
          :total="filteredDepartments.length"
          :page-size.sync="pageSize"
          :current-page.sync="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 添加科室对话框 -->
    <el-dialog
      title="添加科室"
      :visible.sync="addDepartmentDialogVisible"
      width="600px"
      @close="resetAddDepartmentForm"
    >
      <el-form
        :model="addDepartmentForm"
        :rules="addDepartmentRules"
        ref="addDepartmentForm"
        label-width="120px"
      >
        <el-form-item label="科室名称" prop="dept_name">
          <el-input v-model="addDepartmentForm.dept_name" placeholder="请输入科室名称" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="排序号" prop="sort">
          <el-input v-model.number="addDepartmentForm.sort" placeholder="请输入排序号" type="number" min="0"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="addDepartmentForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="科室描述" prop="description">
          <el-input
            v-model="addDepartmentForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入科室描述"
            maxlength="200"
          ></el-input>
        </el-form-item>
        <el-form-item label="科室负责人" prop="responsiblePerson">
          <el-input v-model="addDepartmentForm.responsiblePerson" placeholder="请输入科室负责人" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addDepartmentForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="科室位置" prop="location">
          <el-input v-model="addDepartmentForm.location" placeholder="请输入科室位置" maxlength="100"></el-input>
        </el-form-item>
        <el-form-item label="科室图片">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="addDepartmentForm.avatar" :src="addDepartmentForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="upload-tip">点击上传科室图片 (jpg/png, 最大2MB)</div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDepartmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddDepartment">确定</el-button>
      </div>
    </el-dialog>

    <!-- 编辑科室对话框 -->
    <el-dialog
      title="编辑科室"
      :visible.sync="editDepartmentDialogVisible"
      width="600px"
    >
      <el-form
        :model="editDepartmentForm"
        :rules="editDepartmentRules"
        ref="editDepartmentForm"
        label-width="120px"
      >
        <el-form-item label="科室ID">
          <el-input v-model="editDepartmentForm.id" disabled></el-input>
        </el-form-item>
        <el-form-item label="科室名称" prop="dept_name">
          <el-input v-model="editDepartmentForm.dept_name" placeholder="请输入科室名称" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="排序号" prop="sort">
          <el-input v-model.number="editDepartmentForm.sort" placeholder="请输入排序号" type="number" min="0"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editDepartmentForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="科室描述" prop="description">
          <el-input
            v-model="editDepartmentForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入科室描述"
            maxlength="200"
          ></el-input>
        </el-form-item>
        <el-form-item label="科室负责人" prop="responsiblePerson">
          <el-input v-model="editDepartmentForm.responsiblePerson" placeholder="请输入科室负责人" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="editDepartmentForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="科室位置" prop="location">
          <el-input v-model="editDepartmentForm.location" placeholder="请输入科室位置" maxlength="100"></el-input>
        </el-form-item>
        <el-form-item label="科室图片">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-success="handleEditAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="editDepartmentForm.avatar" :src="editDepartmentForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="upload-tip">点击上传科室图片 (jpg/png, 最大2MB)</div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDepartmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditDepartment">确定</el-button>
      </div>
    </el-dialog>

    <!-- 科室详情对话框 -->
    <el-dialog
      title="科室详情"
      :visible.sync="departmentDetailDialogVisible"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="科室ID">{{ currentDepartment.id }}</el-descriptions-item>
        <el-descriptions-item label="科室名称">{{ currentDepartment.name }}</el-descriptions-item>
        <el-descriptions-item label="科室描述">{{ currentDepartment.description }}</el-descriptions-item>
        <el-descriptions-item label="科室负责人">{{ currentDepartment.responsiblePerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentDepartment.phone }}</el-descriptions-item>
        <el-descriptions-item label="科室位置">{{ currentDepartment.location }}</el-descriptions-item>
        <el-descriptions-item label="医生数量">{{ currentDepartment.doctorCount }}</el-descriptions-item>
        <el-descriptions-item label="科室状态">
          <el-tag :type="currentDepartment.status ? 'success' : 'danger'">
            {{ currentDepartment.status ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentDepartment.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="科室图片" v-if="currentDepartment.avatar">
          <img :src="currentDepartment.avatar" class="detail-avatar" />
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="departmentDetailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 科室医生列表对话框 -->
    <el-dialog
      title="科室医生列表"
      :visible.sync="departmentDoctorsDialogVisible"
      width="800px"
    >
      <div v-if="departmentDoctors.length > 0">
        <el-table :data="departmentDoctors" stripe border>
          <el-table-column prop="id" label="医生ID" width="80"></el-table-column>
          <el-table-column prop="name" label="医生姓名"></el-table-column>
          <el-table-column prop="title" label="职称"></el-table-column>
          <el-table-column prop="specialty" label="专长"></el-table-column>
          <el-table-column prop="phone" label="联系电话"></el-table-column>
          <el-table-column prop="status" label="状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status ? 'success' : 'danger'">
                {{ scope.row.status ? '在职' : '离职' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else class="empty-tip">
        <el-empty description="该科室暂无医生"></el-empty>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="departmentDoctorsDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 批量操作 -->
    <div class="batch-operations" v-if="selectedDepartments.length > 0">
      <el-tag>
        已选择 {{ selectedDepartments.length }} 项
      </el-tag>
      <el-button type="danger" size="small" @click="batchDeleteDepartments">
        批量删除
      </el-button>
      <el-button type="warning" size="small" @click="batchEnableDepartments">
        批量启用
      </el-button>
      <el-button type="warning" size="small" @click="batchDisableDepartments">
        批量禁用
      </el-button>
      <el-button size="small" @click="clearSelection">清空选择</el-button>
    </div>
  </div>
</template>

<script>
import departmentAPI from '@/api/department';
export default {
  name: 'DepartmentManagement',
  data() {
    return {
      // 科室列表数据
      departments: [],
      filteredDepartments: [],
      loading: false,
      
      // 搜索和筛选条件
      searchKeyword: '',
      selectedStatus: '',
      
      // 分页信息
      currentPage: 1,
      pageSize: 10,
      
      // 选中的科室
      selectedDepartments: [],
      
      // 当前查看的科室
      currentDepartment: {},
      
      // 添加科室对话框
      addDepartmentDialogVisible: false,
      addDepartmentForm: {
        dept_name: '',
        description: '',
        sort: 0,
        status: 1, // 默认启用状态
        // 以下字段在UI中保留，但在提交时会根据需要处理
        responsiblePerson: '',
        phone: '',
        location: '',
        avatar: ''
      },
      addDepartmentRules: {
        dept_name: [
          { required: true, message: '请输入科室名称', trigger: 'blur' },
          { min: 2, max: 50, message: '科室名称长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入科室描述', trigger: 'blur' },
          { max: 200, message: '科室描述不能超过 200 个字符', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序号', trigger: 'blur' },
          { type: 'number', message: '排序号必须是数字', trigger: 'blur' },
          { min: 0, message: '排序号不能小于0', trigger: 'blur' }
        ],
        // 其他字段保留在UI中但设为非必填
        responsiblePerson: [
          { max: 20, message: '负责人姓名不能超过 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^\d{3,4}-?\d{7,8}$|^1[3-9]\d{9}$/, message: '请输入正确的电话格式', trigger: 'blur' }
        ],
        location: [
          { max: 100, message: '科室位置不能超过 100 个字符', trigger: 'blur' }
        ]
      },
      
      // 编辑科室对话框
      editDepartmentDialogVisible: false,
      editDepartmentForm: {
        id: '',
        dept_name: '',
        description: '',
        sort: 0,
        status: 1,
        // 以下字段在UI中保留，但在提交时会根据需要处理
        responsiblePerson: '',
        phone: '',
        location: '',
        avatar: ''
      },
      editDepartmentRules: {
        dept_name: [
          { required: true, message: '请输入科室名称', trigger: 'blur' },
          { min: 2, max: 50, message: '科室名称长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入科室描述', trigger: 'blur' },
          { max: 200, message: '科室描述不能超过 200 个字符', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序号', trigger: 'blur' },
          { type: 'number', message: '排序号必须是数字', trigger: 'blur' },
          { min: 0, message: '排序号不能小于0', trigger: 'blur' }
        ],
        // 其他字段保留在UI中但设为非必填
        responsiblePerson: [
          { max: 20, message: '负责人姓名不能超过 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^\d{3,4}-?\d{7,8}$|^1[3-9]\d{9}$/, message: '请输入正确的电话格式', trigger: 'blur' }
        ],
        location: [
          { max: 100, message: '科室位置不能超过 100 个字符', trigger: 'blur' }
        ]
      },
      
      // 科室详情对话框
      departmentDetailDialogVisible: false,
      
      // 科室医生列表对话框
      departmentDoctorsDialogVisible: false,
      departmentDoctors: []
    }
  },
  mounted() {
    this.fetchDepartments()
  },
  computed: {
    // 分页后的科室列表
    paginatedDepartments() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredDepartments.slice(start, end)
    }
  },
  methods: {
    // 获取科室列表
    fetchDepartments() {
      this.loading = true
      departmentAPI.getAllDepartments()
        .then(response => {
          // 处理后端返回的数据，确保字段名匹配
          this.departments = response.data.map(dept => ({
            ...dept,
            // 后端使用deptName，前端可能需要name或dept_name
            name: dept.deptName || dept.dept_name,
            dept_name: dept.deptName || dept.dept_name,
            // 确保其他可能需要的字段存在默认值
            responsiblePerson: dept.responsiblePerson || '',
            phone: dept.phone || '',
            location: dept.location || '',
            doctorCount: dept.doctorCount || 0
          }))
          this.filteredDepartments = [...this.departments]
          this.loading = false
        })
        .catch(error => {
          console.error('获取科室列表失败:', error)
          this.$message.error('获取科室列表失败，请稍后重试')
          this.loading = false
          // 如果API调用失败，可以使用本地过滤作为备用
          this.localFilterDepartments()
        })
    },
    
    // 本地过滤科室（作为API调用失败的备用方案）
    localFilterDepartments() {
      let results = [...this.departments]
      
      // 根据关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        results = results.filter(dept => 
          dept.name.toLowerCase().includes(keyword) ||
          dept.description.toLowerCase().includes(keyword) ||
          dept.responsiblePerson.toLowerCase().includes(keyword)
        )
      }
      
      // 根据状态筛选
      if (this.selectedStatus !== '') {
        results = results.filter(dept => dept.status === parseInt(this.selectedStatus))
      }
      
      this.filteredDepartments = results
    },

    // 搜索科室
    handleSearch() {
      let results = [...this.departments]
      
      // 根据关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        results = results.filter(dept => 
          dept.name.toLowerCase().includes(keyword) ||
          dept.description.toLowerCase().includes(keyword) ||
          dept.responsiblePerson.toLowerCase().includes(keyword)
        )
      }
      
      // 根据状态筛选
      if (this.selectedStatus !== '') {
        results = results.filter(dept => dept.status === parseInt(this.selectedStatus))
      }
      
      this.filteredDepartments = results
      this.currentPage = 1 // 重置到第一页
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
    },

    // 显示添加科室对话框
    showAddDepartmentDialog() {
      this.resetAddDepartmentForm()
      this.addDepartmentDialogVisible = true
    },

    // 重置添加科室表单
    resetAddDepartmentForm() {
      this.addDepartmentForm = {
        dept_name: '',
        description: '',
        sort: 0,
        status: 1,
        responsiblePerson: '',
        phone: '',
        location: '',
        avatar: ''
      }
      this.$refs.addDepartmentForm && this.$refs.addDepartmentForm.resetFields()
    },

    // 提交添加科室
    submitAddDepartment() {
      this.$refs.addDepartmentForm.validate((valid) => {
        if (valid) {
          // 只提交数据库中存在的字段
          const submitData = {
            dept_name: this.addDepartmentForm.dept_name,
            description: this.addDepartmentForm.description,
            sort: Number(this.addDepartmentForm.sort),
            status: Number(this.addDepartmentForm.status)
          }
          console.log('提交的科室数据:', submitData)
          departmentAPI.addDepartment(submitData)
            .then(response => {
              console.log('添加科室成功响应:', response)
              this.fetchDepartments() // 重新获取科室列表
              this.addDepartmentDialogVisible = false
              this.$message.success('科室添加成功')
            })
            .catch(error => {
              console.error('添加科室失败:', error)
              console.error('错误详情:', error.response || error.message)
              this.$message.error('添加科室失败，请稍后重试')
            })
        }
      })
    },

    // 显示编辑科室对话框
    showEditDepartmentDialog(department) {
      this.editDepartmentForm = { ...department }
      this.editDepartmentDialogVisible = true
    },

    // 提交编辑科室
    submitEditDepartment() {
      this.$refs.editDepartmentForm.validate((valid) => {
        if (valid) {
          // 只提交数据库中存在的字段
          const submitData = {
            dept_name: this.editDepartmentForm.dept_name,
            description: this.editDepartmentForm.description,
            sort: Number(this.editDepartmentForm.sort),
            status: Number(this.editDepartmentForm.status)
          }
          departmentAPI.updateDepartment(this.editDepartmentForm.id, submitData)
            .then(() => {
              this.fetchDepartments() // 重新获取科室列表
              this.editDepartmentDialogVisible = false
              this.$message.success('科室更新成功')
            })
            .catch(error => {
              console.error('更新科室失败:', error)
              this.$message.error('更新科室失败，请稍后重试')
            })
        }
      })
    },

    // 更新科室状态
    updateDepartmentStatus(department) {
      departmentAPI.updateDepartmentStatus(department.id, department.status)
        .then(() => {
          this.$message.success(`科室状态已更新为${department.status ? '启用' : '禁用'}`)
        })
        .catch(error => {
          console.error('更新科室状态失败:', error)
          this.$message.error('更新科室状态失败，请稍后重试')
          // 恢复原来的状态
          department.status = !department.status
        })
    },

    // 删除科室
    deleteDepartment(department) {
      if (department.doctorCount > 0) {
        this.$message.warning('该科室下还有医生，无法删除')
        return
      }
      
      this.$confirm(`确定要删除科室「${department.name}」吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        departmentAPI.deleteDepartment(department.id)
          .then(() => {
            this.fetchDepartments() // 重新获取科室列表
            this.$message.success('科室删除成功')
          })
          .catch(error => {
            console.error('删除科室失败:', error)
            this.$message.error('删除科室失败，请稍后重试')
          })
      }).catch(() => {
        // 取消删除
      })
    },

    // 查看科室详情
    viewDepartmentDetail(department) {
      this.currentDepartment = { ...department }
      this.departmentDetailDialogVisible = true
    },

    // 查看科室医生
    viewDepartmentDoctors(department) {
      this.loading = true
      departmentAPI.getDepartmentDoctors(department.id)
        .then(response => {
          this.departmentDoctors = response.data
          this.departmentDoctorsDialogVisible = true
          this.loading = false
        })
        .catch(error => {
          console.error('获取科室医生失败:', error)
          this.$message.error('获取科室医生失败，请稍后重试')
          this.loading = false
          this.departmentDoctors = []
          this.departmentDoctorsDialogVisible = true
        })
    },

    // 批量删除科室
    batchDeleteDepartments() {
      const departmentsWithDoctors = this.selectedDepartments.filter(d => d.doctorCount > 0)
      if (departmentsWithDoctors.length > 0) {
        const departmentNames = departmentsWithDoctors.map(d => d.name).join('、')
        this.$message.warning(`以下科室还有医生，无法删除：${departmentNames}`)
        return
      }

      const departmentNames = this.selectedDepartments.map(d => d.name).join('、')
      this.$confirm(`确定要删除以下科室吗？\n${departmentNames}`, '批量删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const idsToDelete = this.selectedDepartments.map(d => d.id)
        departmentAPI.batchDeleteDepartments(idsToDelete)
          .then(() => {
            this.fetchDepartments() // 重新获取科室列表
            this.selectedDepartments = []
            this.$message.success('批量删除成功')
          })
          .catch(error => {
            console.error('批量删除科室失败:', error)
            this.$message.error('批量删除科室失败，请稍后重试')
          })
      }).catch(() => {
        // 取消删除
      })
    },

    // 批量启用科室
    batchEnableDepartments() {
      const idsToEnable = this.selectedDepartments.map(d => d.id)
      departmentAPI.batchUpdateDepartmentStatus(idsToEnable, 1)
        .then(() => {
          this.fetchDepartments() // 重新获取科室列表
          this.selectedDepartments = []
          this.$message.success('批量启用成功')
        })
        .catch(error => {
          console.error('批量启用科室失败:', error)
          this.$message.error('批量启用科室失败，请稍后重试')
        })
    },

    // 批量禁用科室
    batchDisableDepartments() {
      const idsToDisable = this.selectedDepartments.map(d => d.id)
      departmentAPI.batchUpdateDepartmentStatus(idsToDisable, 0)
        .then(() => {
          this.fetchDepartments() // 重新获取科室列表
          this.selectedDepartments = []
          this.$message.success('批量禁用成功')
        })
        .catch(error => {
          console.error('批量禁用科室失败:', error)
          this.$message.error('批量禁用科室失败，请稍后重试')
        })
    },

    // 清空选择
    clearSelection() {
      this.$refs.departmentTable && this.$refs.departmentTable.clearSelection()
      this.selectedDepartments = []
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
      this.selectedDepartments = selection
    },

    // 图片上传成功处理
    handleAvatarSuccess(res, file) {
      // 模拟上传成功
      this.addDepartmentForm.avatar = URL.createObjectURL(file.raw)
    },

    // 编辑图片上传成功处理
    handleEditAvatarSuccess(res, file) {
      // 模拟上传成功
      this.editDepartmentForm.avatar = URL.createObjectURL(file.raw)
    },

    // 图片上传前校验
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>

<style scoped>
.department-management {
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

.department-list {
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

/* 头像上传样式 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color .3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  line-height: 150px;
  text-align: center;
}

.avatar {
  width: 150px;
  height: 150px;
  display: block;
}

.upload-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

/* 详情页头像 */
.detail-avatar {
  width: 200px;
  height: 200px;
  border-radius: 4px;
}

/* 空状态提示 */
.empty-tip {
  padding: 40px 0;
  text-align: center;
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
  
  .avatar-uploader-icon,
  .avatar {
    width: 120px;
    height: 120px;
    line-height: 120px;
  }
  
  .detail-avatar {
    width: 150px;
    height: 150px;
  }
}
</style>