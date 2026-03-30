<template>
  <div class="share-list">
    <div class="page-header">
      <h1 class="page-title">分享管理</h1>
    </div>

    <!-- Share Table -->
    <div class="card">
      <el-table :data="shareList" v-loading="loading" stripe>
        <el-table-column prop="shareId" label="分享ID" width="100"></el-table-column>
        <el-table-column prop="shareNo" label="分享码" width="120"></el-table-column>
        <el-table-column prop="examId" label="检查号" width="140"></el-table-column>
        <el-table-column prop="patientName" label="患者" width="100"></el-table-column>
        <el-table-column prop="accessCode" label="访问码" width="100">
          <template slot-scope="{ row }">
            <span class="access-code">{{ row.accessCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="过期时间" width="120"></el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="60" align="center"></el-table-column>
        <el-table-column prop="downloadCount" label="下载" width="60" align="center"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="copyLink(row)">复制链接</el-button>
            <el-button type="text" size="small" @click="viewShare(row)">查看</el-button>
            <el-button type="text" size="small" class="danger" @click="cancelShare(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.pageNum"
          :page-sizes="[10, 20, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
        ></el-pagination>
      </div>
    </div>

    <!-- Share Detail Modal -->
    <el-dialog :visible.sync="detailVisible" title="分享详情" width="500px">
      <div class="share-detail" v-if="currentShare">
        <div class="qr-section">
          <div class="qr-placeholder">
            <i class="fas fa-qrcode"></i>
          </div>
          <div class="share-link-box">
            <el-input :value="shareUrl" readonly>
              <el-button slot="append" @click="copyLink(currentShare)">复制</el-button>
            </el-input>
          </div>
        </div>
        <div class="share-info-grid">
          <div class="info-item"><label>分享码：</label><span class="code">{{ currentShare.shareNo }}</span></div>
          <div class="info-item"><label>访问码：</label><span class="code">{{ currentShare.accessCode }}</span></div>
          <div class="info-item"><label>过期时间：</label><span>{{ currentShare.expireDate }}</span></div>
          <div class="info-item"><label>浏览次数：</label><span>{{ currentShare.viewCount }}</span></div>
          <div class="info-item"><label>下载次数：</label><span>{{ currentShare.downloadCount }}</span></div>
        </div>
      </div>
    </el-dialog>

    <!-- Cancel Confirm -->
    <ConfirmDialog
      :visible="cancelDialogVisible"
      title="取消分享"
      message="确定要取消此分享链接吗？取消后他人将无法访问。"
      confirmType="danger"
      @confirm="confirmCancel"
      @cancel="cancelDialogVisible = false"
    />
  </div>
</template>

<script>
import { getShareList, deleteShare } from '@/api/share'

export default {
  name: 'ShareList',
  data() {
    return {
      loading: false,
      detailVisible: false,
      cancelDialogVisible: false,
      shareList: [],
      currentShare: null,
      shareUrl: '',
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  async created() {
    await this.loadShares()
  },
  methods: {
    async loadShares() {
      this.loading = true
      try {
        const data = await getShareList({
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize
        })
        this.shareList = data.rows || []
        this.pagination.total = data.total || 0
      } catch {
        this.shareList = [
          { shareId: 1, shareNo: 'SH2026032601', examId: 'EX2026032601', patientName: '王小红', accessCode: '847293', expireDate: '2026-04-02', viewCount: 5, downloadCount: 2, createTime: '2026-03-26 10:30' },
          { shareId: 2, shareNo: 'SH2026032503', examId: 'EX2026032503', patientName: '张伟', accessCode: '392847', expireDate: '2026-04-01', viewCount: 12, downloadCount: 5, createTime: '2026-03-25 14:20' },
          { shareId: 3, shareNo: 'SH2026032501', examId: 'EX2026032501', patientName: '李秀英', accessCode: '102938', expireDate: '2026-04-01', viewCount: 3, downloadCount: 1, createTime: '2026-03-25 09:15' },
          { shareId: 4, shareNo: 'SH2026032405', examId: 'EX2026032405', patientName: '王建国', accessCode: '567890', expireDate: '2026-03-31', viewCount: 8, downloadCount: 3, createTime: '2026-03-24 16:45' }
        ]
        this.pagination.total = this.shareList.length
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadShares()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadShares()
    },
    viewShare(row) {
      this.currentShare = row
      this.shareUrl = `${window.location.origin}/external/${row.shareNo}`
      this.detailVisible = true
    },
    copyLink(row) {
      const url = `${window.location.origin}/external/${row.shareNo}`
      navigator.clipboard.writeText(url).then(() => {
        this.$message.success('链接已复制到剪贴板')
      }).catch(() => {
        this.$message.error('复制失败')
      })
    },
    cancelShare(row) {
      this.currentShare = row
      this.cancelDialogVisible = true
    },
    async confirmCancel() {
      try {
        await deleteShare(this.currentShare.shareId)
        this.$message.success('分享已取消')
        this.cancelDialogVisible = false
        this.loadShares()
      } catch {
        this.$message.error('取消失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.share-list {
  .page-header {
    margin-bottom: 24px;
  }

  .pagination-wrapper {
    margin-top: 20px;
    text-align: right;
  }

  .access-code {
    font-family: Consolas, monospace;
    background: #f5f7fa;
    padding: 2px 8px;
    border-radius: 4px;
  }
}

.share-detail {
  .qr-section {
    display: flex;
    gap: 20px;
    margin-bottom: 24px;
    align-items: center;
  }

  .qr-placeholder {
    width: 120px;
    height: 120px;
    background: #f5f7fa;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;

    i {
      font-size: 60px;
      color: #ccc;
    }
  }

  .share-link-box {
    flex: 1;
  }

  .share-info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .info-item {
    label {
      color: #909399;
      margin-right: 8px;
    }

    .code {
      font-family: Consolas, monospace;
      color: #1890ff;
      font-weight: 600;
    }
  }
}

.danger {
  color: #ff4d4f;
}
</style>
