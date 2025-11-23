<template>
  <div class="doctor-management">
    <div class="management-header">
      <h2>医生管理</h2>
      <el-button type="primary" @click="showAddDoctorDialog">
        <i class="el-icon-plus"></i> 添加医生
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索医生姓名、专长或电话"
        prefix-icon="el-icon-search"
        clearable
        @input="handleSearch"
        style="width: 300px; margin-right: 15px;"
      ></el-input>
      
      <el-select
        v-model="selectedDepartmentId"
        placeholder="选择科室"
        clearable
        @change="handleSearch"
        style="width: 150px; margin-right: 15px;"
      >
        <el-option
          v-for="department in departments"
          :key="department.id"
          :label="department.name"
          :value="department.id"
        ></el-option>
      </el-select>
      
      <el-select
        v-model="selectedTitle"
        placeholder="选择职称"
        clearable
        @change="handleSearch"
        style="width: 120px; margin-right: 15px;"
      >
        <el-option label="主任医师" value="主任医师"></el-option>
        <el-option label="副主任医师" value="副主任医师"></el-option>
        <el-option label="主治医师" value="主治医师"></el-option>
        <el-option label="住院医师" value="住院医师"></el-option>
      </el-select>
      
      <el-select
        v-model="selectedStatus"
        placeholder="选择状态"
        clearable
        @change="handleSearch"
        style="width: 100px;"
      >
        <el-option label="在职" :value="1"></el-option>
        <el-option label="离职" :value="0"></el-option>
      </el-select>
    </div>

    <!-- 医生列表 -->
    <div class="doctor-list">
      <el-table
        :data="paginatedDoctors"
        style="width: 100%"
        stripe
        border
        v-loading="loading"
        element-loading-text="加载中..."
        ref="doctorTable"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="医生ID" width="80"></el-table-column>
        <el-table-column label="头像" width="80">
          <template slot-scope="scope">
            <img :src="scope.row.avatar" class="doctor-avatar" alt="医生头像">
          </template>
        </el-table-column>
        <el-table-column prop="name" label="医生姓名" min-width="100">
          <template slot-scope="scope">
            <el-tooltip effect="dark" :content="`点击查看详情`" placement="top">
              <span class="doctor-name" @click="viewDoctorDetail(scope.row)">{{ scope.row.name }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="所属科室" min-width="120"></el-table-column>
        <el-table-column prop="title" label="职称" min-width="100"></el-table-column>
        <el-table-column prop="specialty" label="专长" show-overflow-tooltip min-width="200"></el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip min-width="180"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.gender === '男' ? 'info' : 'danger'">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="experience" label="工作年限" width="100">
          <template slot-scope="scope">
            {{ scope.row.experience }} 年
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-color="#67c23a"
              inactive-color="#909399"
              @change="updateDoctorStatus(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入职时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showEditDoctorDialog(scope.row)">
              编辑
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="deleteDoctor(scope.row)"
              style="color: #f56c6c;"
            >
              删除
            </el-button>
            <el-button
              type="text"
              size="small"
              @click="viewDoctorSchedule(scope.row)"
            >
              排班
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next, jumper, sizes, total"
          :total="filteredDoctors.length"
          :page-size.sync="pageSize"
          :current-page.sync="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 添加医生对话框 -->
    <el-dialog
      title="添加医生"
      :visible.sync="addDoctorDialogVisible"
      width="700px"
      @close="resetAddDoctorForm"
    >
      <el-form
        :model="addDoctorForm"
        :rules="addDoctorRules"
        ref="addDoctorForm"
        label-width="120px"
        class="doctor-form"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="医生头像">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :on-success="handleAddAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="addDoctorForm.avatar" :src="addDoctorForm.avatar" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div class="upload-tip">点击上传医生头像 (jpg/png, 最大2MB)</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生姓名" prop="name">
              <el-input v-model="addDoctorForm.name" placeholder="请输入医生姓名" maxlength="20"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="addDoctorForm.gender">
                <el-radio label="男"></el-radio>
                <el-radio label="女"></el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number
                v-model="addDoctorForm.age"
                :min="22"
                :max="80"
                :step="1"
                placeholder="请输入年龄"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作年限" prop="experience">
              <el-input-number
                v-model="addDoctorForm.experience"
                :min="0"
                :max="50"
                :step="1"
                placeholder="请输入工作年限"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属科室" prop="departmentId">
              <el-select
                v-model="addDoctorForm.departmentId"
                placeholder="请选择科室"
                filterable
                allow-create
              >
                <el-option
                  v-for="department in departments"
                  :key="department.id"
                  :label="department.name"
                  :value="department.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="addDoctorForm.title" placeholder="请选择职称">
                <el-option label="主任医师" value="主任医师"></el-option>
                <el-option label="副主任医师" value="副主任医师"></el-option>
                <el-option label="主治医师" value="主治医师"></el-option>
                <el-option label="住院医师" value="住院医师"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="addDoctorForm.phone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="addDoctorForm.email" placeholder="请输入电子邮箱"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="专长" prop="specialty">
              <el-input
                v-model="addDoctorForm.specialty"
                type="textarea"
                rows="3"
                placeholder="请输入医生专长"
                maxlength="200"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人简介" prop="introduction">
              <el-input
                v-model="addDoctorForm.introduction"
                type="textarea"
                rows="4"
                placeholder="请输入个人简介"
                maxlength="500"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDoctorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddDoctor">确定</el-button>
      </div>
    </el-dialog>

    <!-- 编辑医生对话框 -->
    <el-dialog
      title="编辑医生"
      :visible.sync="editDoctorDialogVisible"
      width="700px"
    >
      <el-form
        :model="editDoctorForm"
        :rules="editDoctorRules"
        ref="editDoctorForm"
        label-width="120px"
        class="doctor-form"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="医生头像">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :on-success="handleEditAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="editDoctorForm.avatar" :src="editDoctorForm.avatar" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div class="upload-tip">点击上传医生头像 (jpg/png, 最大2MB)</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生ID">
              <el-input v-model="editDoctorForm.id" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生姓名" prop="name">
              <el-input v-model="editDoctorForm.name" placeholder="请输入医生姓名" maxlength="20"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="editDoctorForm.gender">
                <el-radio label="男"></el-radio>
                <el-radio label="女"></el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number
                v-model="editDoctorForm.age"
                :min="22"
                :max="80"
                :step="1"
                placeholder="请输入年龄"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作年限" prop="experience">
              <el-input-number
                v-model="editDoctorForm.experience"
                :min="0"
                :max="50"
                :step="1"
                placeholder="请输入工作年限"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属科室" prop="departmentId">
              <el-select
                v-model="editDoctorForm.departmentId"
                placeholder="请选择科室"
                filterable
              >
                <el-option
                  v-for="department in departments"
                  :key="department.id"
                  :label="department.name"
                  :value="department.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="editDoctorForm.title" placeholder="请选择职称">
                <el-option label="主任医师" value="主任医师"></el-option>
                <el-option label="副主任医师" value="副主任医师"></el-option>
                <el-option label="主治医师" value="主治医师"></el-option>
                <el-option label="住院医师" value="住院医师"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editDoctorForm.phone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="editDoctorForm.email" placeholder="请输入电子邮箱"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="专长" prop="specialty">
              <el-input
                v-model="editDoctorForm.specialty"
                type="textarea"
                rows="3"
                placeholder="请输入医生专长"
                maxlength="200"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人简介" prop="introduction">
              <el-input
                v-model="editDoctorForm.introduction"
                type="textarea"
                rows="4"
                placeholder="请输入个人简介"
                maxlength="500"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDoctorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditDoctor">确定</el-button>
      </div>
    </el-dialog>

    <!-- 医生详情对话框 -->
    <el-dialog
      title="医生详情"
      :visible.sync="doctorDetailDialogVisible"
      width="800px"
    >
      <div class="doctor-detail">
        <div class="doctor-basic-info">
          <img :src="currentDoctor.avatar" class="detail-avatar" alt="医生头像">
          <div class="doctor-info">
            <h3>{{ currentDoctor.name }}</h3>
            <div class="info-row">
              <el-tag size="small" :type="currentDoctor.gender === '男' ? 'info' : 'danger'">
                {{ currentDoctor.gender }}
              </el-tag>
              <span class="age">{{ currentDoctor.age }} 岁</span>
              <span class="experience">{{ currentDoctor.experience }} 年工作经验</span>
            </div>
            <div class="info-row">
              <el-tag size="small" type="success">{{ currentDoctor.departmentName }}</el-tag>
              <el-tag size="small" type="primary">{{ currentDoctor.title }}</el-tag>
            </div>
          </div>
        </div>
        
        <el-divider></el-divider>
        
        <el-descriptions :column="1" border>
          <el-descriptions-item label="联系电话">{{ currentDoctor.phone }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ currentDoctor.email }}</el-descriptions-item>
          <el-descriptions-item label="专长">{{ currentDoctor.specialty }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="3">{{ currentDoctor.introduction }}</el-descriptions-item>
          <el-descriptions-item label="医生状态">
            <el-tag :type="currentDoctor.status ? 'success' : 'danger'">
              {{ currentDoctor.status ? '在职' : '离职' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="入职时间">{{ formatDate(currentDoctor.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="doctorDetailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 医生排班对话框 -->
    <el-dialog
      title="医生排班管理"
      :visible.sync="scheduleDialogVisible"
      width="800px"
    >
      <div class="schedule-header">
        <h3>{{ selectedDoctorForSchedule.name }} - 排班设置</h3>
        <div class="schedule-period">
          <el-date-picker
            v-model="scheduleDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="fetchDoctorSchedule"
          ></el-date-picker>
        </div>
      </div>
      
      <div class="schedule-calendar">
        <el-table :data="doctorSchedule" border>
          <el-table-column prop="date" label="日期" width="120"></el-table-column>
          <el-table-column prop="weekday" label="星期" width="80">
            <template slot-scope="scope">
              <el-tag :type="getWeekdayType(scope.row.weekday)">
                {{ scope.row.weekday }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="morning" label="上午">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.morning"
                active-color="#67c23a"
                inactive-color="#909399"
                @change="updateSchedule(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column prop="afternoon" label="下午">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.afternoon"
                active-color="#67c23a"
                inactive-color="#909399"
                @change="updateSchedule(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column prop="evening" label="晚上">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.evening"
                active-color="#67c23a"
                inactive-color="#909399"
                @change="updateSchedule(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="schedule-actions">
        <el-button @click="batchSetSchedule('all')">全选</el-button>
        <el-button @click="batchSetSchedule('morning')">全选上午</el-button>
        <el-button @click="batchSetSchedule('afternoon')">全选下午</el-button>
        <el-button @click="batchSetSchedule('evening')">全选晚上</el-button>
        <el-button @click="batchSetSchedule('none')">全不选</el-button>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSchedule">保存排班</el-button>
      </div>
    </el-dialog>

    <!-- 批量操作 -->
    <div class="batch-operations" v-if="selectedDoctors.length > 0">
      <el-tag>
        已选择 {{ selectedDoctors.length }} 项
      </el-tag>
      <el-button type="danger" size="small" @click="batchDeleteDoctors">
        批量删除
      </el-button>
      <el-button type="warning" size="small" @click="batchEnableDoctors">
        批量启用
      </el-button>
      <el-button type="warning" size="small" @click="batchDisableDoctors">
        批量禁用
      </el-button>
      <el-button size="small" @click="batchMoveDepartment">调整科室</el-button>
      <el-button size="small" @click="clearSelection">清空选择</el-button>
    </div>

    <!-- 批量调整科室对话框 -->
    <el-dialog
      title="批量调整科室"
      :visible.sync="batchMoveDialogVisible"
      width="400px"
    >
      <el-form :model="batchMoveForm" :rules="batchMoveRules" ref="batchMoveForm" label-width="80px">
        <el-form-item label="目标科室" prop="departmentId">
          <el-select v-model="batchMoveForm.departmentId" placeholder="请选择目标科室">
            <el-option
              v-for="department in departments"
              :key="department.id"
              :label="department.name"
              :value="department.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="batchMoveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchMove">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import doctorAPI from '../../api/doctor'
import departmentAPI from '../../api/department'

export default {
  name: 'DoctorManagement',
  data() {
    return {
      // 医生列表数据
      doctors: [],
      filteredDoctors: [],
      loading: false,
      
      // 科室数据
      departments: [],
      
      // 搜索和筛选条件
      searchKeyword: '',
      selectedDepartmentId: '',
      selectedTitle: '',
      selectedStatus: '',
      
      // 分页信息
      currentPage: 1,
      pageSize: 10,
      
      // 选中的医生
      selectedDoctors: [],
      
      // 当前查看的医生
      currentDoctor: {},
      selectedDoctorForSchedule: {},
      
      // 添加医生对话框
      addDoctorDialogVisible: false,
      addDoctorForm: {
        name: '',
        gender: '男',
        age: '',
        experience: '',
        departmentId: '',
        title: '',
        phone: '',
        email: '',
        specialty: '',
        introduction: '',
        avatar: ''
      },
      addDoctorRules: {
        name: [
          { required: true, message: '请输入医生姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '医生姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' },
          { type: 'number', min: 22, max: 80, message: '年龄在 22 到 80 之间', trigger: 'blur' }
        ],
        experience: [
          { required: true, message: '请输入工作年限', trigger: 'blur' },
          { type: 'number', min: 0, max: 50, message: '工作年限在 0 到 50 之间', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '请选择所属科室', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请选择职称', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        specialty: [
          { required: true, message: '请输入医生专长', trigger: 'blur' },
          { max: 200, message: '专长不能超过 200 个字符', trigger: 'blur' }
        ],
        introduction: [
          { required: true, message: '请输入个人简介', trigger: 'blur' },
          { max: 500, message: '个人简介不能超过 500 个字符', trigger: 'blur' }
        ]
      },
      
      // 编辑医生对话框
      editDoctorDialogVisible: false,
      editDoctorForm: {
        id: '',
        name: '',
        gender: '男',
        age: '',
        experience: '',
        departmentId: '',
        departmentName: '',
        title: '',
        phone: '',
        email: '',
        specialty: '',
        introduction: '',
        avatar: '',
        status: 1,
        createTime: ''
      },
      editDoctorRules: {
        name: [
          { required: true, message: '请输入医生姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '医生姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' },
          { type: 'number', min: 22, max: 80, message: '年龄在 22 到 80 之间', trigger: 'blur' }
        ],
        experience: [
          { required: true, message: '请输入工作年限', trigger: 'blur' },
          { type: 'number', min: 0, max: 50, message: '工作年限在 0 到 50 之间', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '请选择所属科室', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请选择职称', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        specialty: [
          { required: true, message: '请输入医生专长', trigger: 'blur' },
          { max: 200, message: '专长不能超过 200 个字符', trigger: 'blur' }
        ],
        introduction: [
          { required: true, message: '请输入个人简介', trigger: 'blur' },
          { max: 500, message: '个人简介不能超过 500 个字符', trigger: 'blur' }
        ]
      },
      
      // 医生详情对话框
      doctorDetailDialogVisible: false,
      
      // 医生排班对话框
      scheduleDialogVisible: false,
      scheduleDateRange: [new Date(), new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)],
      doctorSchedule: [],
      
      // 批量调整科室对话框
      batchMoveDialogVisible: false,
      batchMoveForm: {
        departmentId: ''
      },
      batchMoveRules: {
        departmentId: [
          { required: true, message: '请选择目标科室', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.fetchDepartments()
    this.fetchDoctors()
  },
  computed: {
    // 分页后的医生列表
    paginatedDoctors() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredDoctors.slice(start, end)
    }
  },
  methods: {
    // 获取科室列表
    async fetchDepartments() {
      try {
        const response = await departmentAPI.getAllDepartments()
        this.departments = response.data || []
      } catch (error) {
        console.error('获取科室列表失败:', error)
        this.$message.error('获取科室列表失败')
        this.departments = []
      }
    },

    // 获取医生列表
    async fetchDoctors() {
      this.loading = true
      try {
        const response = await doctorAPI.getAllDoctors()
        this.doctors = response.data || []
        this.filteredDoctors = [...this.doctors]
      } catch (error) {
        console.error('获取医生列表失败:', error)
        this.$message.error('获取医生列表失败')
        this.doctors = []
        this.filteredDoctors = []
      } finally {
        this.loading = false
      }
    },
            // 医生数据现在通过API从后端获取


    // 搜索医生
    handleSearch() {
      let results = [...this.doctors]
      
      // 根据关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        results = results.filter(doctor => 
          doctor.name.toLowerCase().includes(keyword) ||
          doctor.specialty.toLowerCase().includes(keyword) ||
          doctor.phone.includes(keyword) ||
          doctor.email.toLowerCase().includes(keyword)
        )
      }
      
      // 根据科室筛选
      if (this.selectedDepartmentId !== '') {
        results = results.filter(doctor => doctor.departmentId === parseInt(this.selectedDepartmentId))
      }
      
      // 根据职称筛选
      if (this.selectedTitle !== '') {
        results = results.filter(doctor => doctor.title === this.selectedTitle)
      }
      
      // 根据状态筛选
      if (this.selectedStatus !== '') {
        results = results.filter(doctor => doctor.status === parseInt(this.selectedStatus))
      }
      
      this.filteredDoctors = results
      this.currentPage = 1 // 重置到第一页
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
    },

    // 显示添加医生对话框
    showAddDoctorDialog() {
      this.resetAddDoctorForm()
      this.addDoctorDialogVisible = true
    },

    // 重置添加医生表单
    resetAddDoctorForm() {
      this.addDoctorForm = {
        name: '',
        gender: '男',
        age: '',
        experience: '',
        departmentId: '',
        title: '',
        phone: '',
        email: '',
        specialty: '',
        introduction: '',
        avatar: ''
      }
      this.$refs.addDoctorForm && this.$refs.addDoctorForm.resetFields()
    },

    // 提交添加医生
    submitAddDoctor() {
      this.$refs.addDoctorForm.validate((valid) => {
        if (valid) {
          // 这里应该是API调用，现在使用模拟数据
          const departmentName = this.departments.find(d => d.id === this.addDoctorForm.departmentId)?.name || ''
          const newDoctor = {
            id: this.doctors.length + 1,
            ...this.addDoctorForm,
            departmentName,
            status: 1,
            createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
          }
          this.doctors.unshift(newDoctor)
          this.handleSearch() // 重新搜索以更新列表
          this.addDoctorDialogVisible = false
          this.$message.success('医生添加成功')
        }
      })
    },

    // 显示编辑医生对话框
    showEditDoctorDialog(doctor) {
      this.editDoctorForm = { ...doctor }
      this.editDoctorDialogVisible = true
    },

    // 提交编辑医生
    submitEditDoctor() {
      this.$refs.editDoctorForm.validate((valid) => {
        if (valid) {
          // 这里应该是API调用，现在使用模拟数据
          const departmentName = this.departments.find(d => d.id === this.editDoctorForm.departmentId)?.name || ''
          const index = this.doctors.findIndex(d => d.id === this.editDoctorForm.id)
          if (index !== -1) {
            // 保留状态和创建时间
            const original = this.doctors[index]
            this.doctors[index] = { 
              ...this.editDoctorForm,
              departmentName,
              status: original.status,
              createTime: original.createTime
            }
            this.handleSearch() // 重新搜索以更新列表
            this.editDoctorDialogVisible = false
            this.$message.success('医生信息更新成功')
          }
        }
      })
    },

    // 更新医生状态
    updateDoctorStatus(doctor) {
      // 这里应该是API调用，现在只是更新本地数据
      this.$message.success(`医生状态已更新为${doctor.status ? '在职' : '离职'}`)
    },

    // 删除医生
    deleteDoctor(doctor) {
      this.$confirm(`确定要删除医生「${doctor.name}」吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里应该是API调用，现在只是更新本地数据
        const index = this.doctors.findIndex(d => d.id === doctor.id)
        if (index !== -1) {
          this.doctors.splice(index, 1)
          this.handleSearch() // 重新搜索以更新列表
          this.$message.success('医生删除成功')
        }
      }).catch(() => {
        // 取消删除
      })
    },

    // 查看医生详情
    viewDoctorDetail(doctor) {
      this.currentDoctor = { ...doctor }
      this.doctorDetailDialogVisible = true
    },

    // 查看医生排班
    viewDoctorSchedule(doctor) {
      this.selectedDoctorForSchedule = { ...doctor }
      this.fetchDoctorSchedule()
      this.scheduleDialogVisible = true
    },

    // 获取医生排班
    fetchDoctorSchedule() {
      // 这里应该是API调用，现在使用模拟数据
      const startDate = this.scheduleDateRange[0]
      const endDate = this.scheduleDateRange[1]
      const weekdayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const schedule = []
      
      let currentDate = new Date(startDate)
      while (currentDate <= endDate) {
        const dateStr = `${currentDate.getFullYear()}-${(currentDate.getMonth() + 1).toString().padStart(2, '0')}-${currentDate.getDate().toString().padStart(2, '0')}`
        const weekday = weekdayNames[currentDate.getDay()]
        
        // 默认工作日上午和下午出诊，晚上不出诊；周末不出诊
        const isWeekend = currentDate.getDay() === 0 || currentDate.getDay() === 6
        
        schedule.push({
          date: dateStr,
          weekday,
          morning: !isWeekend,
          afternoon: !isWeekend,
          evening: false
        })
        
        // 增加一天
        currentDate.setDate(currentDate.getDate() + 1)
      }
      
      this.doctorSchedule = schedule
    },

    // 获取星期类型
    getWeekdayType(weekday) {
      if (weekday === '周六' || weekday === '周日') {
        return 'warning'
      }
      return 'primary'
    },

    // 更新排班
    updateSchedule() {
      // 这里应该是API调用，现在只是更新本地数据
    },

    // 批量设置排班
    batchSetSchedule(type) {
      this.doctorSchedule.forEach(item => {
        if (type === 'all') {
          item.morning = true
          item.afternoon = true
          item.evening = true
        } else if (type === 'morning') {
          item.morning = true
        } else if (type === 'afternoon') {
          item.afternoon = true
        } else if (type === 'evening') {
          item.evening = true
        } else if (type === 'none') {
          item.morning = false
          item.afternoon = false
          item.evening = false
        }
      })
    },

    // 保存排班
    saveSchedule() {
      // 这里应该是API调用，现在只是更新本地数据
      this.scheduleDialogVisible = false
      this.$message.success('排班保存成功')
    },

    // 批量删除医生
    batchDeleteDoctors() {
      const doctorNames = this.selectedDoctors.map(d => d.name).join('、')
      this.$confirm(`确定要删除以下医生吗？\n${doctorNames}`, '批量删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里应该是API调用，现在只是更新本地数据
        const idsToDelete = this.selectedDoctors.map(d => d.id)
        this.doctors = this.doctors.filter(d => !idsToDelete.includes(d.id))
        this.handleSearch() // 重新搜索以更新列表
        this.selectedDoctors = []
        this.$message.success('批量删除成功')
      }).catch(() => {
        // 取消删除
      })
    },

    // 批量启用医生
    batchEnableDoctors() {
      // 这里应该是API调用，现在只是更新本地数据
      this.selectedDoctors.forEach(doctor => {
        doctor.status = 1
      })
      this.selectedDoctors = []
      this.$message.success('批量启用成功')
    },

    // 批量禁用医生
    batchDisableDoctors() {
      // 这里应该是API调用，现在只是更新本地数据
      this.selectedDoctors.forEach(doctor => {
        doctor.status = 0
      })
      this.selectedDoctors = []
      this.$message.success('批量禁用成功')
    },

    // 批量调整科室
    batchMoveDepartment() {
      if (this.selectedDoctors.length === 0) {
        this.$message.warning('请先选择医生')
        return
      }
      this.batchMoveForm.departmentId = ''
      this.batchMoveDialogVisible = true
    },

    // 确认批量调整科室
    confirmBatchMove() {
      this.$refs.batchMoveForm.validate((valid) => {
        if (valid) {
          // 这里应该是API调用，现在只是更新本地数据
          const departmentName = this.departments.find(d => d.id === this.batchMoveForm.departmentId)?.name || ''
          this.selectedDoctors.forEach(doctor => {
            doctor.departmentId = this.batchMoveForm.departmentId
            doctor.departmentName = departmentName
          })
          this.handleSearch() // 重新搜索以更新列表
          this.selectedDoctors = []
          this.batchMoveDialogVisible = false
          this.$message.success('批量调整科室成功')
        }
      })
    },

    // 清空选择
    clearSelection() {
      this.$refs.doctorTable && this.$refs.doctorTable.clearSelection()
      this.selectedDoctors = []
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
      this.selectedDoctors = selection
    },

    // 添加医生头像上传成功处理
    handleAddAvatarSuccess(res, file) {
      // 模拟上传成功
      this.addDoctorForm.avatar = URL.createObjectURL(file.raw)
    },

    // 编辑医生头像上传成功处理
    handleEditAvatarSuccess(res, file) {
      // 模拟上传成功
      this.editDoctorForm.avatar = URL.createObjectURL(file.raw)
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
.doctor-management {
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
  flex-wrap: wrap;
}

.doctor-list {
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
  flex-wrap: wrap;
}

.batch-operations .el-tag {
  background-color: #409eff;
  color: #fff;
}

/* 医生头像 */
.doctor-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

/* 医生姓名链接 */
.doctor-name {
  cursor: pointer;
  color: #409eff;
  text-decoration: underline;
}

.doctor-name:hover {
  color: #66b1ff;
}

/* 医生表单 */
.doctor-form {
  max-height: 500px;
  overflow-y: auto;
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

/* 医生详情页 */
.doctor-detail {
  padding: 20px 0;
}

.doctor-basic-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.detail-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-right: 30px;
}

.doctor-info h3 {
  margin: 0 0 15px 0;
  font-size: 24px;
  color: #303133;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.info-row .age,
.info-row .experience {
  color: #606266;
}

/* 排班样式 */
.schedule-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-header h3 {
  margin: 0;
  color: #303133;
}

.schedule-calendar {
  margin-bottom: 20px;
}

.schedule-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 20px;
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
  
  .doctor-basic-info {
    flex-direction: column;
    text-align: center;
  }
  
  .detail-avatar {
    margin-right: 0;
    margin-bottom: 20px;
  }
  
  .schedule-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .schedule-actions {
    flex-wrap: wrap;
  }
}
</style>