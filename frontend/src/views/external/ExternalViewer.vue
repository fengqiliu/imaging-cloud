<template>
  <div class="external-viewer">
    <!-- Access Code Verification -->
    <div v-if="!verified" class="access-page">
      <div class="access-card">
        <div class="access-header">
          <div class="logo">
            <i class="fas fa-x-ray"></i>
          </div>
          <h1>D-Site 云胶片</h1>
          <p>您正在查看外部分享的医学影像</p>
        </div>

        <div class="exam-preview" v-if="shareInfo">
          <h3>检查信息</h3>
          <div class="preview-grid">
            <div class="preview-item"><label>检查号：</label><span>{{ shareInfo.examId }}</span></div>
            <div class="preview-item"><label>患者：</label><span>{{ shareInfo.patientName }}</span></div>
            <div class="preview-item"><label>类型：</label><span>{{ shareInfo.modality }}</span></div>
            <div class="preview-item"><label>检查日期：</label><span>{{ shareInfo.examDate }}</span></div>
            <div class="preview-item"><label>影像数量：</label><span>{{ shareInfo.imageCount }} 幅</span></div>
          </div>
        </div>

        <el-form v-if="shareInfo && !shareInfo.expired" @submit.native.prevent="verifyCode">
          <div class="code-input-wrapper">
            <label>请输入访问码</label>
            <el-input
              v-model="accessCode"
              placeholder="请输入6位访问码"
              maxlength="6"
              :class="{ error: verifyError }"
              @input="verifyError = false"
            ></el-input>
            <p v-if="verifyError" class="error-hint">访问码错误，请重新输入</p>
          </div>
          <el-button type="primary" size="large" :loading="verifying" @click="verifyCode" style="width: 100%;">
            验证访问
          </el-button>
        </el-form>

        <div v-else-if="!shareInfo" class="loading-state">
          <i class="el-icon-loading"></i>
          <p>正在加载...</p>
        </div>

        <div v-else-if="shareInfo.expired" class="expired-state">
          <i class="fas fa-exclamation-triangle"></i>
          <h3>链接已过期</h3>
          <p>此分享链接已过期，无法继续访问</p>
        </div>
      </div>
    </div>

    <!-- DICOM Viewer -->
    <div v-else class="viewer-wrapper">
      <div class="viewer-toolbar">
        <div class="toolbar-left">
          <div class="patient-info">
            <span class="patient-name">{{ shareInfo.patientName }}</span>
            <span class="exam-id">{{ shareInfo.examId }}</span>
          </div>
        </div>
        <div class="toolbar-right">
          <el-button size="mini" @click="logout"><i class="fas fa-sign-out-alt"></i> 退出</el-button>
        </div>
      </div>

      <div class="viewer-container">
        <div class="series-panel">
          <div class="series-header"><h4>序列</h4></div>
          <div class="series-list">
            <div
              v-for="series in seriesList"
              :key="series.seriesInstanceUID"
              class="series-item"
              :class="{ active: currentSeries === series }"
              @click="currentSeries = series"
            >
              <div class="series-thumb"><i class="fas fa-images"></i></div>
              <div class="series-info">
                <div class="series-desc">{{ series.description }}</div>
                <div class="series-meta">{{ series.count }} 幅</div>
              </div>
            </div>
          </div>
        </div>

        <div class="viewport-main">
          <div class="viewport">
            <div class="image-placeholder">
              <i class="fas fa-x-ray"></i>
              <p>DICOM 图像</p>
            </div>
          </div>
          <div class="viewport-info">
            <span>序列：1/{{ seriesList.length }}</span>
            <span>图像：1/{{ currentSeries?.count || 0 }}</span>
            <span>类型：{{ shareInfo.modality }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getShareInfo, verifyShareAccess } from '@/api/share'

export default {
  name: 'ExternalViewer',
  data() {
    return {
      verified: false,
      verifying: false,
      verifyError: false,
      shareNo: '',
      accessCode: '',
      shareInfo: null,
      seriesList: [],
      currentSeries: null
    }
  },
  async created() {
    this.shareNo = this.$route.params.shareNo || this.$route.query.shareNo || ''
    if (this.shareNo) {
      await this.loadShareInfo()
    }
  },
  methods: {
    async loadShareInfo() {
      try {
        const data = await getShareInfo(this.shareNo)
        this.shareInfo = data
        this.seriesList = data.seriesList || [
          { seriesInstanceUID: '1', description: '序列1', count: 45 },
          { seriesInstanceUID: '2', description: '序列2', count: 60 }
        ]
        this.currentSeries = this.seriesList[0]
      } catch {
        this.$message.error('加载失败')
      }
    },
    async verifyCode() {
      if (!this.accessCode || this.accessCode.length !== 6) {
        this.$message.warning('请输入6位访问码')
        return
      }

      this.verifying = true
      try {
        await verifyShareAccess({ shareNo: this.shareNo, accessCode: this.accessCode })
        this.verified = true
        this.$message.success('验证成功')
      } catch {
        this.verifyError = true
      } finally {
        this.verifying = false
      }
    },
    logout() {
      this.verified = false
      this.accessCode = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.external-viewer {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.access-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.access-card {
  background: white;
  border-radius: 16px;
  padding: 48px;
  width: 100%;
  max-width: 440px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.access-header {
  text-align: center;
  margin-bottom: 32px;

  .logo {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #1890ff, #096dd9);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;

    i {
      font-size: 40px;
      color: white;
    }
  }

  h1 {
    font-size: 24px;
    color: #303133;
    margin-bottom: 8px;
  }

  p {
    color: #909399;
    font-size: 14px;
  }
}

.exam-preview {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;

  h3 {
    font-size: 14px;
    color: #606266;
    margin-bottom: 12px;
  }

  .preview-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .preview-item {
    label {
      color: #909399;
      margin-right: 4px;
    }
  }
}

.code-input-wrapper {
  margin-bottom: 20px;

  label {
    display: block;
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;
  }

  .el-input {
    input {
      text-align: center;
      font-size: 20px;
      letter-spacing: 8px;
    }

    &.error input {
      border-color: #ff4d4f;
    }
  }

  .error-hint {
    color: #ff4d4f;
    font-size: 12px;
    margin-top: 8px;
  }
}

.loading-state, .expired-state {
  text-align: center;
  padding: 40px 0;

  i {
    font-size: 48px;
    color: #909399;
    margin-bottom: 16px;
  }

  h3 {
    color: #606266;
    margin-bottom: 8px;
  }

  p {
    color: #909399;
    font-size: 14px;
  }
}

.expired-state i {
  color: #faad14;
}

.viewer-wrapper {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #1a1a1a;
}

.viewer-toolbar {
  height: 50px;
  background: #252525;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.patient-info {
  display: flex;
  align-items: center;
  gap: 16px;

  .patient-name {
    color: white;
    font-size: 14px;
  }

  .exam-id {
    color: #888;
    font-size: 12px;
  }
}

.viewer-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.series-panel {
  width: 200px;
  background: #252525;
  border-right: 1px solid #333;
}

.series-header {
  padding: 12px 16px;
  border-bottom: 1px solid #333;

  h4 {
    color: #ccc;
    font-size: 14px;
    margin: 0;
  }
}

.series-list {
  padding: 8px;
}

.series-item {
  display: flex;
  gap: 10px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 4px;

  &:hover { background: #333; }
  &.active { background: #1890ff; }
}

.series-thumb {
  width: 40px;
  height: 40px;
  background: #1a1a1a;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;

  i { color: #666; font-size: 16px; }
}

.series-info {
  .series-desc { color: #ccc; font-size: 12px; }
  .series-meta { color: #888; font-size: 11px; }
}

.viewport-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.viewport {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-placeholder {
  width: 400px;
  height: 300px;
  background: #2a2a2a;
  border: 2px dashed #444;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;

  i { font-size: 60px; margin-bottom: 16px; }
}

.viewport-info {
  height: 32px;
  background: #252525;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 30px;
  font-size: 12px;
  color: #888;
}
</style>
