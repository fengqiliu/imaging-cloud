<template>
  <div class="image-list">
    <div class="page-header">
      <h1 class="page-title">影像管理</h1>
      <el-button type="primary" icon="el-icon-upload" @click="showUploadModal">上传影像</el-button>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue"><i class="fas fa-images"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalImages }}</div>
          <div class="stat-label">影像总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><i class="fas fa-hdd"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.storageUsed }}</div>
          <div class="stat-label">存储使用</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><i class="fas fa-layer-group"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.seriesCount }}</div>
          <div class="stat-label">序列数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red"><i class="fas fa-cloud-upload-alt"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayUploads }}</div>
          <div class="stat-label">今日上传</div>
        </div>
      </div>
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

    <!-- Image Table -->
    <div class="card">
      <el-table :data="imageList" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="缩略图" width="80">
          <template slot-scope="{ row }">
            <div class="thumbnail" @click="viewImage(row)">
              <i class="fas fa-image"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="examId" label="检查号" width="140"></el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="100"></el-table-column>
        <el-table-column prop="modality" label="类型" width="80">
          <template slot-scope="{ row }">
            <el-tag size="small" :class="`modality-${row.modality.toLowerCase()}`">{{ row.modality }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="seriesNumber" label="序列号" width="80"></el-table-column>
        <el-table-column prop="imageNumber" label="图像号" width="80"></el-table-column>
        <el-table-column prop="sopInstanceUID" label="SOP UID" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="160"></el-table-column>
        <el-table-column prop="isKey" label="关键" width="60" align="center">
          <template slot-scope="{ row }">
            <i v-if="row.isKey" class="fas fa-star" style="color: #faad14;"></i>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="viewImage(row)">查看</el-button>
            <el-button type="text" size="small" @click="setKeyImage(row)">{{ row.isKey ? '取消关键' : '设为关键' }}</el-button>
            <el-button type="text" size="small" class="danger" @click="deleteImage(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="batch-actions" v-if="selectedImages.length > 0">
        <span>已选择 {{ selectedImages.length }} 项</span>
        <el-button size="mini" type="danger" @click="batchDelete">批量删除</el-button>
      </div>

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

    <!-- Upload Modal -->
    <el-dialog :visible.sync="uploadModalVisible" title="上传影像" width="600px" @close="resetUploadForm">
      <el-form ref="uploadForm" :model="uploadForm" label-width="100px">
        <el-form-item label="检查号" prop="examId">
          <el-select v-model="uploadForm.examId" filterable placeholder="请选择或搜索检查" @change="onExamSelect">
            <el-option v-for="e in examinations" :key="e.examId" :label="`${e.examId} - ${e.patientName}`" :value="e.examId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :auto-upload="false"
            :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            accept=".dcm,.dicom"
            multiple
            drag
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将 DICOM 文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">支持 .dcm, .dicom 格式，单个文件不超过 500MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploading">开始上传</el-button>
      </span>
    </el-dialog>

    <!-- Image Preview Modal -->
    <el-dialog :visible.sync="previewVisible" title="影像预览" width="800px">
      <div class="image-preview" v-if="currentImage">
        <div class="preview-placeholder">
          <i class="fas fa-image"></i>
          <p>DICOM 图像预览</p>
          <p class="info">{{ currentImage.sopInstanceUID }}</p>
        </div>
        <div class="preview-info">
          <h4>图像信息</h4>
          <div class="info-grid">
            <div class="info-item"><label>检查号：</label><span>{{ currentImage.examId }}</span></div>
            <div class="info-item"><label>患者：</label><span>{{ currentImage.patientName }}</span></div>
            <div class="info-item"><label>类型：</label><span>{{ currentImage.modality }}</span></div>
            <div class="info-item"><label>序列号：</label><span>{{ currentImage.seriesNumber }}</span></div>
            <div class="info-item"><label>图像号：</label><span>{{ currentImage.imageNumber }}</span></div>
            <div class="info-item"><label>上传时间：</label><span>{{ currentImage.uploadTime }}</span></div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- Delete Confirm -->
    <ConfirmDialog
      :visible="deleteDialogVisible"
      title="删除影像"
      message="确定要删除选中的影像吗？此操作不可恢复。"
      confirmType="danger"
      @confirm="confirmDelete"
      @cancel="deleteDialogVisible = false"
    />
  </div>
</template>

<script>
import { getImageList, uploadImage, deleteImage, setKeyImage } from '@/api/image'
import { getExaminationList } from '@/api/examination'

export default {
  name: 'ImageList',
  data() {
    return {
      loading: false,
      uploading: false,
      uploadModalVisible: false,
      previewVisible: false,
      deleteDialogVisible: false,
      imageList: [],
      examinations: [],
      selectedImages: [],
      currentImage: null,
      fileList: [],
      stats: {
        totalImages: 12586,
        storageUsed: '256GB',
        seriesCount: 1234,
        todayUploads: 89
      },
      searchForm: {
        examId: '',
        patientName: '',
        modality: ''
      },
      uploadForm: {
        examId: ''
      },
      uploadUrl: '/api/cloudfilm/image/upload',
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  async created() {
    await this.loadExaminations()
    await this.loadImages()
  },
  methods: {
    async loadExaminations() {
      try {
        const data = await getExaminationList({ pageNum: 1, pageSize: 1000 })
        this.examinations = data.rows || []
      } catch {
        this.examinations = [
          { examId: 'EX2026032605', patientName: '王建国' },
          { examId: 'EX2026032604', patientName: '李秀英' },
          { examId: 'EX2026032603', patientName: '张伟' },
          { examId: 'EX2026032602', patientName: '李明' },
          { examId: 'EX2026032601', patientName: '王小红' }
        ]
      }
    },
    async loadImages() {
      this.loading = true
      try {
        const params = {
          ...this.searchForm,
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        }
        const data = await getImageList(params)
        this.imageList = data.rows || []
        this.pagination.total = data.total || 0
      } catch {
        this.imageList = [
          { imageId: 1, examId: 'EX2026032605', patientName: '王建国', modality: 'CT', seriesNumber: 1, imageNumber: 45, sopInstanceUID: '1.2.840.113619.2.55.3.12345', uploadTime: '2026-03-26 10:35', isKey: true },
          { imageId: 2, examId: 'EX2026032605', patientName: '王建国', modality: 'CT', seriesNumber: 2, imageNumber: 120, sopInstanceUID: '1.2.840.113619.2.55.3.12346', uploadTime: '2026-03-26 10:36', isKey: false },
          { imageId: 3, examId: 'EX2026032604', patientName: '李秀英', modality: 'MRI', seriesNumber: 1, imageNumber: 30, sopInstanceUID: '1.2.840.113619.2.55.3.12347', uploadTime: '2026-03-26 14:05', isKey: true },
          { imageId: 4, examId: 'EX2026032603', patientName: '张伟', modality: 'DR', seriesNumber: 1, imageNumber: 1, sopInstanceUID: '1.2.840.113619.2.55.3.12348', uploadTime: '2026-03-25 09:05', isKey: true },
          { imageId: 5, examId: 'EX2026032602', patientName: '李明', modality: 'US', seriesNumber: 1, imageNumber: 15, sopInstanceUID: '1.2.840.113619.2.55.3.12349', uploadTime: '2026-03-25 15:35', isKey: false },
          { imageId: 6, examId: 'EX2026032601', patientName: '王小红', modality: 'CT', seriesNumber: 1, imageNumber: 200, sopInstanceUID: '1.2.840.113619.2.55.3.12350', uploadTime: '2026-03-24 11:10', isKey: true }
        ]
        this.pagination.total = this.imageList.length
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.loadImages()
    },
    resetSearch() {
      this.searchForm = { examId: '', patientName: '', modality: '' }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadImages()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadImages()
    },
    handleSelectionChange(val) {
      this.selectedImages = val
    },
    showUploadModal() {
      this.uploadModalVisible = true
    },
    onExamSelect(examId) {
      // Auto-fill patient name if needed
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList
    },
    handleFileRemove(file, fileList) {
      this.fileList = fileList
    },
    async submitUpload() {
      if (!this.uploadForm.examId) {
        this.$message.warning('请选择检查')
        return
      }
      if (this.fileList.length === 0) {
        this.$message.warning('请选择要上传的文件')
        return
      }

      this.uploading = true
      try {
        const formData = new FormData()
        formData.append('examId', this.uploadForm.examId)
        this.fileList.forEach(file => {
          formData.append('files', file.raw)
        })
        await uploadImage(formData)
        this.$message.success('上传成功')
        this.uploadModalVisible = false
        this.loadImages()
      } catch {
        this.$message.error('上传失败')
      } finally {
        this.uploading = false
      }
    },
    resetUploadForm() {
      this.fileList = []
      this.uploadForm.examId = ''
    },
    viewImage(row) {
      this.currentImage = row
      this.previewVisible = true
    },
    async setKeyImage(row) {
      try {
        await setKeyImage(row.imageId)
        this.$message.success(row.isKey ? '已取消关键图像' : '已设为关键图像')
        this.loadImages()
      } catch {
        this.$message.error('操作失败')
      }
    },
    deleteImage(row) {
      this.selectedImages = [row]
      this.deleteDialogVisible = true
    },
    async batchDelete() {
      if (this.selectedImages.length === 0) return
      this.deleteDialogVisible = true
    },
    async confirmDelete() {
      try {
        const ids = this.selectedImages.map(i => i.imageId).join(',')
        await deleteImage(ids)
        this.$message.success('删除成功')
        this.deleteDialogVisible = false
        this.selectedImages = []
        this.loadImages()
      } catch {
        this.$message.error('删除失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.image-list {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;

  &.blue { background: #e6f7ff; color: #1890ff; }
  &.green { background: #f6ffed; color: #52c41a; }
  &.orange { background: #fff7e6; color: #fa8c16; }
  &.red { background: #fff1f0; color: #ff4d4f; }
}

.stat-info {
  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
  .stat-label {
    font-size: 12px;
    color: #909399;
  }
}

.search-card {
  margin-bottom: 16px;
  .el-form-item { margin-bottom: 0; }
}

.thumbnail {
  width: 48px;
  height: 48px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  &:hover { background: #e6f7ff; }
  i { font-size: 20px; color: #909399; }
}

.batch-actions {
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.image-preview {
  .preview-placeholder {
    height: 300px;
    background: #1a1a1a;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    margin-bottom: 20px;
    i { font-size: 64px; color: #444; }
    p { margin-top: 16px; }
    .info { font-size: 12px; color: #666; }
  }
  .preview-info {
    h4 { margin-bottom: 12px; }
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }
    .info-item {
      label { color: #909399; }
    }
  }
}

.danger { color: #ff4d4f; }
</style>
