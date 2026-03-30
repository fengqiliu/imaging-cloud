<template>
  <div class="dicom-viewer">
    <div class="viewer-toolbar">
      <div class="toolbar-left">
        <el-button-group>
          <el-button size="mini" :class="{ active: currentTool === 'pan' }" @click="currentTool = 'pan'"><i class="fas fa-hand-paper"></i></el-button>
          <el-button size="mini" :class="{ active: currentTool === 'zoom' }" @click="currentTool = 'zoom'"><i class="fas fa-search"></i></el-button>
          <el-button size="mini" :class="{ active: currentTool === 'window' }" @click="currentTool = 'window'"><i class="fas fa-sun"></i></el-button>
          <el-button size="mini" :class="{ active: currentTool === 'rotate' }" @click="currentTool = 'rotate'"><i class="fas fa-redo"></i></el-button>
        </el-button-group>
        <el-divider direction="vertical"></el-divider>
        <el-button size="mini" @click="resetView"><i class="fas fa-sync"></i> 重置</el-button>
        <el-button size="mini"><i class="fas fa-ruler"></i> 测量</el-button>
        <el-button size="mini"><i class="fas fa-comment"></i> 标注</el-button>
        <el-button size="mini"><i class="fas fa-camera"></i> 截图</el-button>
        <el-button size="mini"><i class="fas fa-play"></i> 播放</el-button>
      </div>
      <div class="toolbar-right">
        <el-button size="mini" type="primary" @click="writeReport"><i class="fas fa-edit"></i> 写报告</el-button>
        <el-button size="mini" @click="shareImage"><i class="fas fa-share"></i> 分享</el-button>
      </div>
    </div>

    <div class="viewer-container">
      <!-- Series List -->
      <div class="series-panel">
        <div class="series-header">
          <h4>序列列表</h4>
        </div>
        <div class="series-list">
          <div
            v-for="series in seriesList"
            :key="series.seriesInstanceUID"
            class="series-item"
            :class="{ active: currentSeries && currentSeries.seriesInstanceUID === series.seriesInstanceUID }"
            @click="selectSeries(series)"
          >
            <div class="series-thumb">
              <i class="fas fa-images"></i>
            </div>
            <div class="series-info">
              <div class="series-desc">{{ series.seriesDescription }}</div>
              <div class="series-meta">{{ series.instanceCount }} 幅</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Viewport -->
      <div class="viewport-main">
        <div class="viewport">
          <div class="dicom-image" ref="viewport">
            <div class="image-placeholder">
              <i class="fas fa-x-ray"></i>
              <p>DICOM 图像显示区域</p>
              <p class="hint">请从左侧选择序列或从检查列表进入</p>
            </div>
          </div>
        </div>
        <div class="viewport-info">
          <span>患者：{{ patientName || '-' }}</span>
          <span>检查号：{{ examId || '-' }}</span>
          <span>序列：{{ currentSeriesIndex }}/{{ seriesList.length }}</span>
          <span>图像：{{ currentImageIndex }}/{{ currentInstanceCount }}</span>
          <span>类型：{{ currentModality || '-' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DicomViewer',
  data() {
    return {
      currentTool: 'pan',
      currentSeries: null,
      currentSeriesIndex: 1,
      currentImageIndex: 1,
      currentInstanceCount: 0,
      patientName: '',
      examId: '',
      currentModality: '',
      seriesList: [
        { seriesInstanceUID: '1.2.3.4.5.1', seriesDescription: '胸部增强-动脉期', instanceCount: 120 },
        { seriesInstanceUID: '1.2.3.4.5.2', seriesDescription: '胸部增强-静脉期', instanceCount: 120 },
        { seriesInstanceUID: '1.2.3.4.5.3', seriesDescription: '胸部平扫', instanceCount: 150 }
      ]
    }
  },
  created() {
    this.examId = this.$route.params.examId || this.$route.query.examId || ''
    if (this.examId) {
      this.loadStudy()
    }
  },
  methods: {
    async loadStudy() {
      // Load study series data
      this.seriesList = [
        { seriesInstanceUID: '1.2.3.4.5.1', seriesDescription: '胸部增强-动脉期', instanceCount: 120 },
        { seriesInstanceUID: '1.2.3.4.5.2', seriesDescription: '胸部增强-静脉期', instanceCount: 120 },
        { seriesInstanceUID: '1.2.3.4.5.3', seriesDescription: '胸部平扫', instanceCount: 150 }
      ]
      this.currentInstanceCount = this.seriesList[0]?.instanceCount || 0
    },
    selectSeries(series) {
      this.currentSeries = series
      this.currentSeriesIndex = this.seriesList.indexOf(series) + 1
      this.currentImageIndex = 1
      this.currentInstanceCount = series.instanceCount
    },
    resetView() {
      this.$message.info('重置视图')
    },
    writeReport() {
      this.$router.push(`/reports?examId=${this.examId}`)
    },
    shareImage() {
      this.$router.push(`/shares?examId=${this.examId}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.dicom-viewer {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background: #1a1a1a;
  border-radius: 8px;
  overflow: hidden;
}

.viewer-toolbar {
  height: 50px;
  background: #252525;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;

  .el-button {
    background: #3a3a3a;
    border-color: #4a4a4a;
    color: #ccc;

    &:hover, &.active {
      background: #1890ff;
      border-color: #1890ff;
      color: white;
    }
  }

  .el-divider {
    background: #4a4a4a;
  }
}

.toolbar-left, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.viewer-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.series-panel {
  width: 220px;
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
  overflow-y: auto;
  height: calc(100% - 50px);
}

.series-item {
  display: flex;
  gap: 12px;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 4px;

  &:hover {
    background: #333;
  }

  &.active {
    background: #1890ff;
  }
}

.series-thumb {
  width: 48px;
  height: 48px;
  background: #1a1a1a;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;

  i {
    color: #666;
    font-size: 20px;
  }
}

.series-info {
  flex: 1;

  .series-desc {
    color: #ccc;
    font-size: 12px;
    margin-bottom: 4px;
  }

  .series-meta {
    color: #888;
    font-size: 11px;
  }
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
  padding: 20px;
}

.dicom-image {
  max-width: 100%;
  max-height: 100%;
}

.image-placeholder {
  width: 500px;
  height: 400px;
  background: #2a2a2a;
  border: 2px dashed #444;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;

  i {
    font-size: 80px;
    margin-bottom: 20px;
  }

  p {
    margin: 8px 0;
  }

  .hint {
    font-size: 12px;
    color: #555;
  }
}

.viewport-info {
  height: 36px;
  background: #252525;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 30px;
  font-size: 12px;
  color: #888;

  span {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}
</style>
