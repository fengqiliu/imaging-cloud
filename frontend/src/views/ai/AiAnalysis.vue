<template>
  <div class="ai-analysis">
    <div class="page-header">
      <h1 class="page-title">AI 解读</h1>
    </div>

    <div class="analysis-layout">
      <!-- Report Selection -->
      <div class="report-selector">
        <h3>选择报告</h3>
        <el-radio-group v-model="selectedReportId" @change="selectReport">
          <el-radio v-for="r in reportList" :key="r.reportId" :label="r.reportId" class="report-radio">
            <div class="report-item">
              <span class="exam-id">{{ r.examId }}</span>
              <span class="patient">{{ r.patientName }}</span>
              <StatusTag :status="r.status" />
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <!-- Original Report -->
      <div class="report-original" v-if="currentReport">
        <h3>原始诊断报告</h3>
        <div class="report-content">
          <div class="content-section">
            <label>临床诊断：</label>
            <p>{{ currentReport.clinicalDiagnosis }}</p>
          </div>
          <div class="content-section">
            <label>检查所见：</label>
            <p>{{ currentReport.examinationFindings }}</p>
          </div>
          <div class="content-section">
            <label>诊断意见：</label>
            <p>{{ currentReport.diagnosticOpinion }}</p>
          </div>
        </div>
      </div>

      <!-- AI Interpretation -->
      <div class="ai-result">
        <div class="ai-header">
          <h3>AI 解读结果</h3>
          <div class="ai-model">
            <span>AI 模型：</span>
            <el-select v-model="aiModel" size="mini">
              <el-option label="Claude 3.5" value="claude"></el-option>
              <el-option label="GPT-4o" value="gpt"></el-option>
              <el-option label="通义千问" value="qwen"></el-option>
            </el-select>
          </div>
        </div>
        <div class="ai-content" v-if="aiResult">
          <div class="ai-section">
            <label><i class="fas fa-language"></i> 通俗解释</label>
            <p>{{ aiResult.plainExplanation }}</p>
          </div>
          <div class="ai-section">
            <label><i class="fas fa-list-ul"></i> 关键发现</label>
            <ul>
              <li v-for="(finding, i) in aiResult.keyFindings" :key="i">{{ finding }}</li>
            </ul>
          </div>
          <div class="ai-section">
            <label><i class="fas fa-lightbulb"></i> 建议</label>
            <ul>
              <li v-for="(rec, i) in aiResult.recommendations" :key="i">{{ rec }}</li>
            </ul>
          </div>
          <div class="ai-section warning" v-if="aiResult.warnings.length">
            <label><i class="fas fa-exclamation-triangle"></i> 注意事项</label>
            <ul>
              <li v-for="(warn, i) in aiResult.warnings" :key="i">{{ warn }}</li>
            </ul>
          </div>
        </div>
        <div class="ai-placeholder" v-else>
          <i class="fas fa-robot"></i>
          <p>请选择一个报告，AI 将为您生成通俗易懂的解读</p>
        </div>
        <div class="ai-actions" v-if="currentReport">
          <el-button size="mini" @click="regenerate" :loading="loading">
            <i class="fas fa-redo"></i> 重新生成
          </el-button>
          <el-button size="mini" @click="copyResult">
            <i class="fas fa-copy"></i> 复制结果
          </el-button>
          <el-button size="mini" @click="shareResult">
            <i class="fas fa-share"></i> 分享
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiAnalysis',
  data() {
    return {
      selectedReportId: '',
      aiModel: 'claude',
      loading: false,
      reportList: [
        { reportId: 1, examId: 'EX2026032601', patientName: '王小红', status: 'completed', clinicalDiagnosis: '发热待查', examinationFindings: '双肺纹理清晰，未见明显实质性病变。心脏大小正常。', diagnosticOpinion: '胸片未见明显异常。' },
        { reportId: 2, examId: 'EX2026032603', patientName: '张伟', status: 'completed', clinicalDiagnosis: '外伤检查', examinationFindings: '左膝关节骨质未见明显骨折征象，关节间隙正常，软组织肿胀。', diagnosticOpinion: '左膝关节骨质未见明显骨折。' },
        { reportId: 3, examId: 'EX2026032604', patientName: '李秀英', status: 'completed', clinicalDiagnosis: '头晕头痛', examinationFindings: '脑实质内未见明显异常信号影。脑室系统未见扩大。', diagnosticOpinion: '颅脑MRI平扫未见明显异常。' }
      ],
      currentReport: null,
      aiResult: null
    }
  },
  methods: {
    selectReport(reportId) {
      this.currentReport = this.reportList.find(r => r.reportId === reportId)
      if (this.currentReport) {
        this.generateAIResult()
      }
    },
    async generateAIResult() {
      this.loading = true
      try {
        await new Promise(resolve => setTimeout(resolve, 1500))
        const report = this.currentReport
        this.aiResult = {
          plainExplanation: `根据这份${report.examId}的影像报告，${report.patientName}的${report.clinicalDiagnosis}检查结果显示${report.diagnosticOpinion} 医生在检查${report.examId === 'EX2026032601' ? '胸部' : report.examId === 'EX2026032603' ? '左膝关节' : '颅脑'}时，${report.examinationFindings.substring(0, 30)}...整体来看，检查结果正常或无明显异常发现。`,
          keyFindings: [
            '影像学检查未发现明显异常',
            '相关器官形态和结构正常',
            '未发现需要紧急处理的病变'
          ],
          recommendations: [
            '继续保持良好的生活习惯',
            '如仍有不适症状，建议定期复查',
            '如有其他症状，及时就医咨询'
          ],
          warnings: []
        }
      } catch {
        this.$message.error('生成失败')
      } finally {
        this.loading = false
      }
    },
    regenerate() {
      this.generateAIResult()
    },
    copyResult() {
      this.$message.success('已复制到剪贴板')
    },
    shareResult() {
      this.$message.info('分享功能开发中')
    }
  }
}
</script>

<style lang="scss" scoped>
.ai-analysis {
  .page-header {
    margin-bottom: 24px;
  }
}

.analysis-layout {
  display: grid;
  grid-template-columns: 280px 1fr 1fr;
  gap: 16px;
}

.report-selector {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  h3 {
    font-size: 14px;
    margin-bottom: 16px;
    color: #303133;
  }
}

.report-radio {
  display: block;
  margin-bottom: 12px;
}

.report-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0;

  .exam-id { font-weight: 600; font-size: 13px; }
  .patient { color: #606266; font-size: 13px; flex: 1; }
}

.report-original, .ai-result {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  h3 {
    font-size: 14px;
    margin-bottom: 16px;
    color: #303133;
  }
}

.report-content {
  .content-section {
    margin-bottom: 16px;

    label {
      font-size: 13px;
      font-weight: 500;
      color: #606266;
      display: block;
      margin-bottom: 6px;
    }

    p {
      font-size: 14px;
      line-height: 1.8;
      color: #303133;
    }
  }
}

.ai-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 { margin-bottom: 0; }
}

.ai-model {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.ai-content {
  .ai-section {
    margin-bottom: 20px;

    label {
      font-size: 13px;
      font-weight: 500;
      color: #1890ff;
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 10px;

      i { font-size: 14px; }
    }

    p {
      font-size: 14px;
      line-height: 1.8;
      color: #303133;
    }

    ul {
      list-style: none;
      padding-left: 0;

      li {
        font-size: 14px;
        line-height: 1.8;
        color: #303133;
        padding: 4px 0;
        padding-left: 20px;
        position: relative;

        &::before {
          content: '•';
          position: absolute;
          left: 6px;
          color: #1890ff;
        }
      }
    }

    &.warning label { color: #faad14; }
    &.warning li::before { color: #faad14; }
  }
}

.ai-placeholder {
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;

  i {
    font-size: 48px;
    margin-bottom: 16px;
  }
}

.ai-actions {
  display: flex;
  gap: 8px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
