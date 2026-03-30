<template>
  <div class="patient-list">
    <div class="page-header">
      <h1 class="page-title">患者管理</h1>
      <el-button type="primary" icon="el-icon-plus" @click="showAddModal">新增患者</el-button>
    </div>

    <!-- Search Form -->
    <div class="card search-card">
      <el-form :inline="true" :model="searchForm" @submit.native.prevent>
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="searchForm.idCard" placeholder="请输入身份证号" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Patient Table -->
    <div class="card">
      <el-table :data="patientList" v-loading="loading" stripe>
        <el-table-column prop="patientId" label="患者ID" width="120"></el-table-column>
        <el-table-column prop="patientName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="gender" label="性别" width="60">
          <template slot-scope="{ row }">{{ row.gender === '1' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120"></el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
        <el-table-column prop="deptName" label="科室" width="100"></el-table-column>
        <el-table-column prop="visitCount" label="就诊次数" width="100" align="center"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="viewPatient(row)">查看</el-button>
            <el-button type="text" size="small" @click="editPatient(row)">编辑</el-button>
            <el-button type="text" size="small" @click="createExam(row)">新建检查</el-button>
            <el-button type="text" size="small" class="danger" @click="deletePatient(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        ></el-pagination>
      </div>
    </div>

    <!-- Add/Edit Patient Modal -->
    <el-dialog :visible.sync="patientModalVisible" :title="modalTitle" width="600px" @close="resetForm">
      <el-form ref="patientForm" :model="patientForm" :rules="rules" label-width="100px">
        <el-form-item label="患者姓名" prop="patientName">
          <el-input v-model="patientForm.patientName" placeholder="请输入患者姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="patientForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="patientForm.birthDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="patientForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="patientForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="patientForm.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="过敏史" prop="allergyHistory">
          <el-input v-model="patientForm.allergyHistory" type="textarea" placeholder="请输入过敏史"></el-input>
        </el-form-item>
        <el-form-item label="病史" prop="medicalHistory">
          <el-input v-model="patientForm.medicalHistory" type="textarea" placeholder="请输入病史"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="patientModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </span>
    </el-dialog>

    <!-- Patient Detail Modal -->
    <el-dialog :visible.sync="detailModalVisible" title="患者详情" width="700px">
      <div class="patient-detail" v-if="currentPatient">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item"><label>患者ID：</label><span>{{ currentPatient.patientId }}</span></div>
            <div class="detail-item"><label>姓名：</label><span>{{ currentPatient.patientName }}</span></div>
            <div class="detail-item"><label>性别：</label><span>{{ currentPatient.gender === '1' ? '男' : '女' }}</span></div>
            <div class="detail-item"><label>出生日期：</label><span>{{ currentPatient.birthDate }}</span></div>
            <div class="detail-item"><label>身份证号：</label><span>{{ currentPatient.idCard }}</span></div>
            <div class="detail-item"><label>手机号：</label><span>{{ currentPatient.phone }}</span></div>
            <div class="detail-item"><label>地址：</label><span>{{ currentPatient.address }}</span></div>
          </div>
        </div>
        <div class="detail-section">
          <h4>医学信息</h4>
          <div class="detail-grid">
            <div class="detail-item"><label>过敏史：</label><span>{{ currentPatient.allergyHistory || '无' }}</span></div>
            <div class="detail-item"><label>病史：</label><span>{{ currentPatient.medicalHistory || '无' }}</span></div>
          </div>
        </div>
        <div class="detail-section">
          <h4>就诊历史</h4>
          <el-table :data="currentPatient.examinations || []" size="small">
            <el-table-column prop="examId" label="检查号" width="140"></el-table-column>
            <el-table-column prop="modality" label="检查类型" width="80"></el-table-column>
            <el-table-column prop="examPart" label="检查部位" width="80"></el-table-column>
            <el-table-column prop="examDate" label="检查日期" width="120"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template slot-scope="{ row }"><StatusTag :status="row.status" /></template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- Delete Confirm -->
    <ConfirmDialog
      :visible="deleteDialogVisible"
      title="删除患者"
      message="确定要删除该患者吗？此操作不可恢复。"
      confirmType="danger"
      @confirm="confirmDelete"
      @cancel="deleteDialogVisible = false"
    />
  </div>
</template>

<script>
import { getPatientList, addPatient, updatePatient, deletePatient } from '@/api/patient'
import { getExaminationList } from '@/api/examination'

export default {
  name: 'PatientList',
  data() {
    return {
      loading: false,
      submitLoading: false,
      patientModalVisible: false,
      detailModalVisible: false,
      deleteDialogVisible: false,
      modalTitle: '新增患者',
      isEdit: false,
      currentPatient: null,
      patientList: [],
      searchForm: {
        patientName: '',
        idCard: '',
        phone: ''
      },
      patientForm: {
        patientId: '',
        patientName: '',
        gender: '1',
        birthDate: '',
        idCard: '',
        phone: '',
        address: '',
        allergyHistory: '',
        medicalHistory: ''
      },
      rules: {
        patientName: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
        idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  async created() {
    await this.loadPatients()
  },
  methods: {
    async loadPatients() {
      this.loading = true
      try {
        const params = {
          ...this.searchForm,
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
        const data = await getPatientList(params)
        this.patientList = data.rows || []
        this.pagination.total = data.total || 0
      } catch (error) {
        // Use mock data
        this.patientList = [
          { patientId: 'P202603001', patientName: '王小红', gender: '0', birthDate: '1981-03-25', idCard: '110101198103252234', phone: '13800138000', deptName: '神经内科', visitCount: 12, address: '北京市朝阳区', allergyHistory: '无', medicalHistory: '高血压病史3年' },
          { patientId: 'P202603002', patientName: '王建国', gender: '1', birthDate: '1965-08-12', idCard: '110101196508121234', phone: '13900139000', deptName: '心内科', visitCount: 8, address: '北京市海淀区', allergyHistory: '青霉素过敏', medicalHistory: '冠心病支架术后' },
          { patientId: 'P202603003', patientName: '李秀英', gender: '0', birthDate: '1970-05-20', idCard: '110101197005201234', phone: '13700137000', deptName: '骨科', visitCount: 5, address: '北京市西城区', allergyHistory: '无', medicalHistory: '腰椎间盘突出' },
          { patientId: 'P202603004', patientName: '张伟', gender: '1', birthDate: '1988-11-03', idCard: '110101198811031234', phone: '13600136000', deptName: '呼吸内科', visitCount: 3, address: '北京市东城区', allergyHistory: '磺胺类过敏', medicalHistory: '支气管哮喘' },
          { patientId: 'P202603005', patientName: '李明', gender: '1', birthDate: '1975-02-28', idCard: '110101197502281234', phone: '13500135000', deptName: '消化内科', visitCount: 15, address: '北京市丰台区', allergyHistory: '无', medicalHistory: '胃溃疡病史' },
          { patientId: 'P202603006', patientName: '刘芳', gender: '0', birthDate: '1990-07-15', idCard: '110101199007151234', phone: '13400134000', deptName: '妇产科', visitCount: 6, address: '北京市石景山区', allergyHistory: '芒果过敏', medicalHistory: '妊娠期糖尿病' },
          { patientId: 'P202603007', patientName: '陈刚', gender: '1', birthDate: '1958-12-08', idCard: '110101195812081234', phone: '13300133000', deptName: '内分泌科', visitCount: 20, address: '北京市通州区', allergyHistory: '无', medicalHistory: '2型糖尿病20年' },
          { patientId: 'P202603008', patientName: '赵丽', gender: '0', birthDate: '1985-09-22', idCard: '110101198509221234', phone: '13200132000', deptName: '肿瘤科', visitCount: 10, address: '北京市大兴区', allergyHistory: '头孢类过敏', medicalHistory: '乳腺癌术后' }
        ]
        this.pagination.total = this.patientList.length
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadPatients()
    },
    resetSearch() {
      this.searchForm = { patientName: '', idCard: '', phone: '' }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadPatients()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadPatients()
    },
    showAddModal() {
      this.modalTitle = '新增患者'
      this.isEdit = false
      this.patientModalVisible = true
    },
    editPatient(row) {
      this.modalTitle = '编辑患者'
      this.isEdit = true
      this.patientForm = { ...row }
      this.patientModalVisible = true
    },
    async viewPatient(row) {
      // Load exam history for this patient
      try {
        const data = await getExaminationList({ patientId: row.patientId, pageNum: 1, pageSize: 100 })
        this.currentPatient = { ...row, examinations: data.rows || [] }
      } catch {
        this.currentPatient = { ...row, examinations: [] }
      }
      this.detailModalVisible = true
    },
    createExam(row) {
      this.$router.push(`/examinations?action=add&patientId=${row.patientId}&patientName=${row.patientName}`)
    },
    deletePatient(row) {
      this.currentPatient = row
      this.deleteDialogVisible = true
    },
    async confirmDelete() {
      try {
        await deletePatient(this.currentPatient.patientId)
        this.$message.success('删除成功')
        this.deleteDialogVisible = false
        this.loadPatients()
      } catch (error) {
        this.$message.error('删除失败')
      }
    },
    async submitForm() {
      const valid = await this.$refs.patientForm.validate().catch(() => false)
      if (!valid) return

      this.submitLoading = true
      try {
        if (this.isEdit) {
          await updatePatient(this.patientForm)
          this.$message.success('修改成功')
        } else {
          await addPatient(this.patientForm)
          this.$message.success('新增成功')
        }
        this.patientModalVisible = false
        this.loadPatients()
      } catch (error) {
        this.$message.error(this.isEdit ? '修改失败' : '新增失败')
      } finally {
        this.submitLoading = false
      }
    },
    resetForm() {
      this.$refs.patientForm.resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
.patient-list {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .search-card {
    margin-bottom: 16px;

    .el-form-item {
      margin-bottom: 0;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }

  .danger {
    color: #ff4d4f;
  }
}

.patient-detail {
  .detail-section {
    margin-bottom: 24px;

    h4 {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #ebeef5;
    }
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .detail-item {
    label {
      color: #909399;
      margin-right: 8px;
    }
  }
}
</style>
