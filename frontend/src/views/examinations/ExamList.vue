<template>
  <div class="exam-list">
    <div class="page-header">
      <h1 class="page-title">检查管理</h1>
      <el-button type="primary" icon="el-icon-plus" @click="showAddModal">新建检查</el-button>
    </div>

    <!-- Status Tabs -->
    <div class="card status-tabs-card">
      <el-tabs v-model="activeStatus" @tab-click="handleStatusChange">
        <el-tab-pane label="全部" name="all">
          <span slot="label">全部 <el-badge :value="counts.all" /></span>
        </el-tab-pane>
        <el-tab-pane label="待检查" name="pending">
          <span slot="label">待检查 <el-badge :value="counts.pending" type="warning" /></span>
        </el-tab-pane>
        <el-tab-pane label="检查中" name="in_progress">
          <span slot="label">检查中 <el-badge :value="counts.in_progress" type="primary" /></span>
        </el-tab-pane>
        <el-tab-pane label="已完成" name="completed">
          <span slot="label">已完成 <el-badge :value="counts.completed" type="success" /></span>
        </el-tab-pane>
        <el-tab-pane label="已出报告" name="reported">
          <span slot="label">已出报告 <el-badge :value="counts.reported" type="purple" /></span>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- Search Form -->
    <div class="card search-card">
      <el-form :inline="true" :model="searchForm" @submit.native.prevent>
        <el-form-item label="检查号">
          <el-input v-model="searchForm.examId" placeholder="请输入检查号" clearable></el-input>
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="searchForm.patientName" placeholder="请输入患者姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="检查类型">
          <el-select v-model="searchForm.modality" placeholder="请选择" clearable>
            <el-option label="CT" value="CT"></el-option>
            <el-option label="MRI" value="MRI"></el-option>
            <el-option label="DR" value="DR"></el-option>
            <el-option label="US" value="US"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Exam Table -->
    <div class="card">
      <el-table :data="examList" v-loading="loading" stripe>
        <el-table-column prop="examId" label="检查号" width="140"></el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="100"></el-table-column>
        <el-table-column prop="modality" label="检查类型" width="80">
          <template slot-scope="{ row }">
            <el-tag size="small" :class="`modality-${row.modality.toLowerCase()}`">{{ row.modality }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examPart" label="检查部位" width="100"></el-table-column>
        <el-table-column prop="examReason" label="检查原因" min-width="150"></el-table-column>
        <el-table-column prop="applyDoctor" label="申请医生" width="100"></el-table-column>
        <el-table-column prop="examDate" label="检查日期" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="{ row }"><StatusTag :status="row.status" /></template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="viewExam(row)">查看</el-button>
            <el-button type="text" size="small" @click="editExam(row)" v-if="row.status === 'pending'">编辑</el-button>
            <el-button type="text" size="small" @click="uploadImage(row)" v-if="['pending', 'in_progress'].includes(row.status)">上传影像</el-button>
            <el-button type="text" size="small" @click="writeReport(row)" v-if="row.status === 'completed'">写报告</el-button>
            <el-button type="text" size="small" @click="changeStatus(row)" v-if="row.status !== 'reported'">状态</el-button>
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

    <!-- Add/Edit Exam Modal -->
    <el-dialog :visible.sync="examModalVisible" :title="modalTitle" width="650px" @close="resetForm">
      <el-form ref="examForm" :model="examForm" :rules="rules" label-width="100px">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="examForm.patientId" filterable placeholder="请选择患者" @change="onPatientSelect">
            <el-option v-for="p in patients" :key="p.patientId" :label="`${p.patientName} (${p.patientId})`" :value="p.patientId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="检查类型" prop="modality">
          <el-select v-model="examForm.modality" placeholder="请选择">
            <el-option label="CT" value="CT"></el-option>
            <el-option label="MRI" value="MRI"></el-option>
            <el-option label="DR" value="DR"></el-option>
            <el-option label="US" value="US"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="检查部位" prop="examPart">
          <el-input v-model="examForm.examPart" placeholder="请输入检查部位"></el-input>
        </el-form-item>
        <el-form-item label="检查原因" prop="examReason">
          <el-input v-model="examForm.examReason" type="textarea" placeholder="请输入检查原因"></el-input>
        </el-form-item>
        <el-form-item label="申请医生" prop="applyDoctor">
          <el-input v-model="examForm.applyDoctor" placeholder="请输入申请医生"></el-input>
        </el-form-item>
        <el-form-item label="预约日期" prop="examDate">
          <el-date-picker v-model="examForm.examDate" type="datetime" placeholder="选择日期时间" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="examForm.remark" type="textarea" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="examModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </span>
    </el-dialog>

    <!-- Status Change Dialog -->
    <el-dialog :visible.sync="statusDialogVisible" title="修改状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前状态"><StatusTag :status="currentExam.status" /></el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="newStatus" placeholder="请选择新状态">
            <el-option v-for="s in availableStatuses" :key="s.value" :label="s.label" :value="s.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getExaminationList, addExamination, updateExamination, updateExaminationStatus } from '@/api/examination'
import { getPatientList } from '@/api/patient'

export default {
  name: 'ExamList',
  data() {
    return {
      loading: false,
      submitLoading: false,
      examModalVisible: false,
      statusDialogVisible: false,
      modalTitle: '新建检查',
      isEdit: false,
      activeStatus: 'all',
      counts: { all: 89, pending: 12, in_progress: 23, completed: 35, reported: 19 },
      examList: [],
      patients: [],
      currentExam: null,
      newStatus: '',
      statusOptions: [
        { value: 'pending', label: '待检查' },
        { value: 'in_progress', label: '检查中' },
        { value: 'completed', label: '已完成' },
        { value: 'reported', label: '已出报告' }
      ],
      searchForm: {
        examId: '',
        patientName: '',
        modality: ''
      },
      examForm: {
        patientId: '',
        patientName: '',
        modality: '',
        examPart: '',
        examReason: '',
        applyDoctor: '',
        examDate: '',
        remark: ''
      },
      rules: {
        patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
        modality: [{ required: true, message: '请选择检查类型', trigger: 'change' }],
        examPart: [{ required: true, message: '请输入检查部位', trigger: 'blur' }]
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  computed: {
    availableStatuses() {
      if (!this.currentExam) return this.statusOptions
      const statusOrder = ['pending', 'in_progress', 'completed', 'reported']
      const currentIndex = statusOrder.indexOf(this.currentExam.status)
      return this.statusOptions.filter((s, i) => i > currentIndex)
    }
  },
  async created() {
    await this.loadPatients()
    await this.loadExams()
  },
  methods: {
    async loadPatients() {
      try {
        const data = await getPatientList({ pageNum: 1, pageSize: 1000 })
        this.patients = data.rows || []
      } catch {
        this.patients = [
          { patientId: 'P202603001', patientName: '王小红' },
          { patientId: 'P202603002', patientName: '王建国' },
          { patientId: 'P202603003', patientName: '李秀英' },
          { patientId: 'P202603004', patientName: '张伟' },
          { patientId: 'P202603005', patientName: '李明' }
        ]
      }
    },
    async loadExams() {
      this.loading = true
      try {
        const params = {
          ...this.searchForm,
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
        if (this.activeStatus !== 'all') {
          params.status = this.activeStatus
        }
        const data = await getExaminationList(params)
        this.examList = data.rows || []
        this.pagination.total = data.total || 0
      } catch {
        this.examList = [
          { examId: 'EX2026032605', patientName: '王建国', modality: 'CT', examPart: '胸部', examReason: '咳嗽伴胸痛2周', applyDoctor: '李医生', examDate: '2026-03-26 10:30', status: 'pending' },
          { examId: 'EX2026032604', patientName: '李秀英', modality: 'MRI', examPart: '头部', examReason: '头晕头痛', applyDoctor: '王医生', examDate: '2026-03-26 14:00', status: 'in_progress' },
          { examId: 'EX2026032603', patientName: '张伟', modality: 'DR', examPart: '四肢', examReason: '外伤检查', applyDoctor: '赵医生', examDate: '2026-03-25 09:00', status: 'completed' },
          { examId: 'EX2026032602', patientName: '李明', modality: 'US', examPart: '腹部', examReason: '常规体检', applyDoctor: '孙医生', examDate: '2026-03-25 15:30', status: 'reported' },
          { examId: 'EX2026032601', patientName: '王小红', modality: 'CT', examPart: '胸部', examReason: '发热待查', applyDoctor: '周医生', examDate: '2026-03-24 11:00', status: 'reported' }
        ]
        this.pagination.total = this.examList.length
      } finally {
        this.loading = false
      }
    },
    handleStatusChange() {
      this.pagination.pageNum = 1
      this.loadExams()
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadExams()
    },
    resetSearch() {
      this.searchForm = { examId: '', patientName: '', modality: '' }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadExams()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadExams()
    },
    showAddModal() {
      this.modalTitle = '新建检查'
      this.isEdit = false
      this.examModalVisible = true
    },
    editExam(row) {
      this.modalTitle = '编辑检查'
      this.isEdit = true
      this.examForm = { ...row }
      this.examModalVisible = true
    },
    viewExam(row) {
      this.$router.push(`/examinations?examId=${row.examId}`)
    },
    uploadImage(row) {
      this.$router.push(`/images?action=upload&examId=${row.examId}`)
    },
    writeReport(row) {
      this.$router.push(`/reports?examId=${row.examId}`)
    },
    changeStatus(row) {
      this.currentExam = row
      this.newStatus = ''
      this.statusDialogVisible = true
    },
    async submitStatus() {
      if (!this.newStatus) {
        this.$message.warning('请选择新状态')
        return
      }
      try {
        await updateExaminationStatus(this.currentExam.examId, this.newStatus)
        this.$message.success('状态修改成功')
        this.statusDialogVisible = false
        this.loadExams()
      } catch {
        this.$message.error('状态修改失败')
      }
    },
    onPatientSelect(patientId) {
      const patient = this.patients.find(p => p.patientId === patientId)
      if (patient) {
        this.examForm.patientName = patient.patientName
      }
    },
    async submitForm() {
      const valid = await this.$refs.examForm.validate().catch(() => false)
      if (!valid) return

      this.submitLoading = true
      try {
        if (this.isEdit) {
          await updateExamination(this.examForm)
          this.$message.success('修改成功')
        } else {
          await addExamination(this.examForm)
          this.$message.success('新建成功')
        }
        this.examModalVisible = false
        this.loadExams()
      } catch (error) {
        this.$message.error(this.isEdit ? '修改失败' : '新建失败')
      } finally {
        this.submitLoading = false
      }
    },
    resetForm() {
      this.$refs.examForm.resetFields()
      this.examForm = { patientId: '', patientName: '', modality: '', examPart: '', examReason: '', applyDoctor: '', examDate: '', remark: '' }
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-list {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .status-tabs-card {
    margin-bottom: 16px;
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
}
</style>
