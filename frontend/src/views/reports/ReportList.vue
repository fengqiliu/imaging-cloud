<template>
  <div class="report-list">
    <div class="page-header">
      <h1 class="page-title">诊断报告</h1>
    </div>

    <!-- Status Tabs -->
    <div class="card status-tabs-card">
      <el-tabs v-model="activeStatus" @tab-click="handleStatusChange">
        <el-tab-pane label="全部" name="all">
          <span slot="label">全部 <el-badge :value="counts.all" /></span>
        </el-tab-pane>
        <el-tab-pane label="待写" name="pending">
          <span slot="label">待写 <el-badge :value="counts.pending" type="warning" /></span>
        </el-tab-pane>
        <el-tab-pane label="草稿" name="draft">
          <span slot="label">草稿 <el-badge :value="counts.draft" type="info" /></span>
        </el-tab-pane>
        <el-tab-pane label="已完成" name="completed">
          <span slot="label">已完成 <el-badge :value="counts.completed" type="success" /></span>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="report-layout">
      <!-- Report List -->
      <div class="report-sidebar">
        <!-- Search -->
        <div class="search-box">
          <el-input v-model="searchForm.keyword" placeholder="搜索检查号或患者姓名" clearable @change="handleSearch">
            <i slot="prefix" class="el-icon-search"></i>
          </el-input>
        </div>

        <!-- Report Items -->
        <div class="report-items">
          <div
            v-for="report in reportList"
            :key="report.reportId"
            class="report-item"
            :class="{ active: currentReport && currentReport.reportId === report.reportId }"
            @click="selectReport(report)"
          >
            <div class="report-item-header">
              <span class="exam-id">{{ report.examId }}</span>
              <StatusTag :status="report.status" />
            </div>
            <div class="report-item-body">
              <span class="patient-name">{{ report.patientName }}</span>
              <span class="modality">{{ report.modality }} · {{ report.examPart }}</span>
            </div>
            <div class="report-item-footer">
              <span class="doctor">{{ report.createDoctor }}</span>
              <span class="date">{{ report.createTime }}</span>
            </div>
          </div>
          <EmptyState v-if="reportList.length === 0 && !loading" text="暂无报告" />
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            small
            @current-change="handlePageChange"
            :current-page="pagination.pageNum"
            :page-size="pagination.pageSize"
            layout="prev, pager, next"
            :total="pagination.total"
          ></el-pagination>
        </div>
      </div>

      <!-- Report Editor -->
      <div class="report-editor">
        <template v-if="currentReport">
          <div class="editor-header">
            <div class="report-info">
              <h3>检查报告</h3>
              <span class="exam-info">{{ currentReport.examId }} · {{ currentReport.patientName }} · {{ currentReport.modality }} {{ currentReport.examPart }}</span>
            </div>
            <div class="editor-actions">
              <el-button v-if="currentReport.status === 'draft'" size="mini" @click="saveDraft" :loading="saving">保存草稿</el-button>
              <el-button v-if="['pending', 'draft'].includes(currentReport.status)" type="primary" size="mini" @click="submitReport">提交报告</el-button>
              <el-button size="mini" @click="exportPdf">导出PDF</el-button>
            </div>
          </div>

          <div class="editor-content">
            <!-- Patient Info -->
            <div class="info-section">
              <div class="info-row">
                <div class="info-item"><label>患者姓名：</label><span>{{ currentReport.patientName }}</span></div>
                <div class="info-item"><label>性别：</label><span>{{ currentReport.gender }}</span></div>
                <div class="info-item"><label>年龄：</label><span>{{ currentReport.age }}</span></div>
                <div class="info-item"><label>检查日期：</label><span>{{ currentReport.examDate }}</span></div>
              </div>
            </div>

            <!-- Quick Templates -->
            <div class="template-section">
              <span class="section-label">快速模板：</span>
              <el-button-group size="mini">
                <el-button @click="applyTemplate('chest')">胸部CT</el-button>
                <el-button @click="applyTemplate('abdomen')">腹部CT</el-button>
                <el-button @click="applyTemplate('brain')">头颅MRI</el-button>
              </el-button-group>
            </div>

            <!-- Report Content -->
            <div class="content-section">
              <div class="content-item">
                <label>临床诊断：</label>
                <el-input type="textarea" v-model="currentReport.clinicalDiagnosis" :rows="2" placeholder="请输入临床诊断"></el-input>
              </div>
              <div class="content-item">
                <label>检查所见：</label>
                <el-input type="textarea" v-model="currentReport.examinationFindings" :rows="6" placeholder="请输入检查所见"></el-input>
              </div>
              <div class="content-item">
                <label>诊断意见：</label>
                <el-input type="textarea" v-model="currentReport.diagnosticOpinion" :rows="4" placeholder="请输入诊断意见"></el-input>
              </div>
            </div>

            <!-- Sign Info -->
            <div class="sign-section">
              <div class="sign-row">
                <div class="sign-item">
                  <label>报告医生：</label>
                  <span>{{ currentReport.createDoctor }}</span>
                </div>
                <div class="sign-item">
                  <label>报告日期：</label>
                  <span>{{ currentReport.createTime }}</span>
                </div>
                <div class="sign-item" v-if="currentReport.reviewDoctor">
                  <label>审核医生：</label>
                  <span>{{ currentReport.reviewDoctor }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>
        <EmptyState v-else icon="fa-file-medical" text="请选择一个报告进行编辑" />
      </div>
    </div>

    <!-- Submit Confirm Dialog -->
    <el-dialog :visible.sync="submitDialogVisible" title="提交报告" width="400px">
      <p>确定要提交此报告吗？提交后将无法修改。</p>
      <span slot="footer" class="dialog-footer">
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubmit">确定提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getReportList, getReport, updateReport } from '@/api/report'

export default {
  name: 'ReportList',
  data() {
    return {
      loading: false,
      saving: false,
      activeStatus: 'all',
      counts: { all: 89, pending: 12, draft: 5, completed: 72 },
      reportList: [],
      currentReport: null,
      searchForm: {
        keyword: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 20,
        total: 0
      },
      submitDialogVisible: false,
      templates: {
        chest: {
          clinicalDiagnosis: '咳嗽待查',
          examinationFindings: '胸廓对称。双肺野纹理清晰，走行自然。双肺门结构正常。纵隔居中，心影不大。双膈光整，肋膈角锐利。\n余肺野未见明显异常密度影。',
          diagnosticOpinion: '胸片所见心肺未见明显异常。'
        },
        abdomen: {
          clinicalDiagnosis: '腹痛待查',
          examinationFindings: '肝脏大小、形态正常，肝裂不宽，肝表面光滑，肝实质回声均匀，肝内管道结构清晰。\n胆囊形态、大小正常，壁不厚，内未见明显异常回声。\n脾脏大小、形态正常，回声均匀。\n胰腺显示部分，胰头、胰体大小、形态正常，回声均匀。\n双肾大小、形态正常，皮髓质分界清。',
          diagnosticOpinion: '肝胆脾胰双肾未见明显异常。'
        },
        brain: {
          clinicalDiagnosis: '头痛头晕待查',
          examinationFindings: '颅内各层扫描显示：脑实质内未见明显异常信号影。脑室系统未见扩大，中线结构居中。\n小脑及脑干信号正常。\n双侧大脑半球对称。',
          diagnosticOpinion: '颅脑MRI平扫未见明显异常。'
        }
      }
    }
  },
  async created() {
    await this.loadReports()
  },
  methods: {
    async loadReports() {
      this.loading = true
      try {
        const params = {
          keyword: this.searchForm.keyword,
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
        if (this.activeStatus !== 'all') {
          params.status = this.activeStatus
        }
        const data = await getReportList(params)
        this.reportList = data.rows || []
        this.pagination.total = data.total || 0
      } catch {
        this.reportList = [
          { reportId: 1, examId: 'EX2026032601', patientName: '王小红', gender: '女', age: '45岁', modality: 'CT', examPart: '胸部', examDate: '2026-03-24', clinicalDiagnosis: '发热待查', examinationFindings: '', diagnosticOpinion: '', status: 'completed', createDoctor: '李医生', createTime: '2026-03-25 10:30', reviewDoctor: '张主任' },
          { reportId: 2, examId: 'EX2026032602', patientName: '李明', gender: '男', age: '51岁', modality: 'US', examPart: '腹部', examDate: '2026-03-25', clinicalDiagnosis: '常规体检', examinationFindings: '', diagnosticOpinion: '', status: 'completed', createDoctor: '王医生', createTime: '2026-03-25 16:00', reviewDoctor: '' },
          { reportId: 3, examId: 'EX2026032603', patientName: '张伟', gender: '男', age: '38岁', modality: 'DR', examPart: '四肢', examDate: '2026-03-25', clinicalDiagnosis: '外伤检查', examinationFindings: '左膝关节骨质未见明显骨折征象，关节间隙正常，软组织肿胀。', diagnosticOpinion: '左膝关节骨质未见明显骨折。', status: 'draft', createDoctor: '赵医生', createTime: '2026-03-25 09:30', reviewDoctor: '' },
          { reportId: 4, examId: 'EX2026032604', patientName: '李秀英', gender: '女', age: '56岁', modality: 'MRI', examPart: '头部', examDate: '2026-03-26', clinicalDiagnosis: '头晕头痛', examinationFindings: '', diagnosticOpinion: '', status: 'pending', createDoctor: '孙医生', createTime: '2026-03-26 14:00', reviewDoctor: '' },
          { reportId: 5, examId: 'EX2026032605', patientName: '王建国', gender: '男', age: '61岁', modality: 'CT', examPart: '胸部', examDate: '2026-03-26', clinicalDiagnosis: '咳嗽伴胸痛2周', examinationFindings: '', diagnosticOpinion: '', status: 'pending', createDoctor: '周医生', createTime: '2026-03-26 10:30', reviewDoctor: '' }
        ]
        this.pagination.total = this.reportList.length
      } finally {
        this.loading = false
      }
    },
    handleStatusChange() {
      this.pagination.pageNum = 1
      this.loadReports()
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadReports()
    },
    handlePageChange(val) {
      this.pagination.pageNum = val
      this.loadReports()
    },
    async selectReport(report) {
      try {
        const data = await getReport(report.reportId)
        this.currentReport = { ...data }
      } catch {
        this.currentReport = { ...report }
      }
    },
    applyTemplate(type) {
      if (!this.currentReport) return
      const template = this.templates[type]
      if (template) {
        this.currentReport.clinicalDiagnosis = template.clinicalDiagnosis
        this.currentReport.examinationFindings = template.examinationFindings
        this.currentReport.diagnosticOpinion = template.diagnosticOpinion
      }
    },
    async saveDraft() {
      if (!this.currentReport) return
      this.saving = true
      try {
        await updateReport(this.currentReport)
        this.$message.success('草稿已保存')
      } catch {
        this.$message.error('保存失败')
      } finally {
        this.saving = false
      }
    },
    submitReport() {
      this.submitDialogVisible = true
    },
    async confirmSubmit() {
      if (!this.currentReport) return
      this.submitDialogVisible = false
      try {
        await updateReport({ ...this.currentReport, status: 'completed' })
        this.$message.success('报告已提交')
        this.currentReport.status = 'completed'
        this.loadReports()
      } catch {
        this.$message.error('提交失败')
      }
    },
    exportPdf() {
      this.$message.info('PDF导出功能开发中')
    }
  }
}
</script>

<style lang="scss" scoped>
.report-list {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 16px;
}

.status-tabs-card {
  margin-bottom: 16px;
}

.report-layout {
  display: flex;
  gap: 16px;
  flex: 1;
  overflow: hidden;
}

.report-sidebar {
  width: 360px;
  background: white;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-box {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
}

.report-items {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.report-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 8px;
  border: 1px solid transparent;
  transition: all 0.2s;

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #e6f7ff;
    border-color: #1890ff;
  }
}

.report-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;

  .exam-id {
    font-weight: 600;
    font-size: 13px;
  }
}

.report-item-body {
  margin-bottom: 8px;

  .patient-name {
    font-size: 14px;
    color: #303133;
    margin-right: 8px;
  }

  .modality {
    font-size: 12px;
    color: #909399;
  }
}

.report-item-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #c0c4cc;
}

.pagination-wrapper {
  padding: 8px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

.report-editor {
  flex: 1;
  background: white;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.editor-header {
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 16px;
    margin-bottom: 4px;
  }

  .exam-info {
    font-size: 13px;
    color: #909399;
  }
}

.editor-actions {
  display: flex;
  gap: 8px;
}

.editor-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.info-item {
  label {
    color: #909399;
    margin-right: 8px;
  }
}

.template-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;

  .section-label {
    font-size: 14px;
    color: #606266;
  }
}

.content-section {
  margin-bottom: 20px;
}

.content-item {
  margin-bottom: 16px;

  label {
    font-size: 14px;
    color: #303133;
    font-weight: 500;
    display: block;
    margin-bottom: 8px;
  }
}

.sign-section {
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.sign-row {
  display: flex;
  gap: 40px;
}

.sign-item {
  label {
    color: #909399;
    margin-right: 8px;
  }
}
</style>
