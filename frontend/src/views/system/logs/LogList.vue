<template>
  <div class="log-list">
    <div class="page-header">
      <h1 class="page-title">日志管理</h1>
    </div>

    <!-- Tabs -->
    <div class="card tabs-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="操作日志" name="oper"></el-tab-pane>
        <el-tab-pane label="登录日志" name="login"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- Search -->
    <div class="card search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="操作人">
          <el-input v-model="searchForm.operName" placeholder="请输入" clearable></el-input>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择" clearable>
            <el-option label="新增" value="1"></el-option>
            <el-option label="修改" value="2"></el-option>
            <el-option label="删除" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="正常" value="0"></el-option>
            <el-option label="异常" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Log Table -->
    <div class="card">
      <div class="table-actions">
        <el-button size="mini" @click="exportLogs"><i class="fas fa-file-excel"></i> 导出</el-button>
        <el-button size="mini" type="danger" @click="cleanLogs"><i class="fas fa-trash-alt"></i> 清空</el-button>
      </div>
      <el-table :data="logList" v-loading="loading" stripe :row-class-name="getRowClass">
        <el-table-column prop="operTime" label="操作时间" width="160"></el-table-column>
        <el-table-column prop="operName" label="操作人" width="100"></el-table-column>
        <el-table-column prop="businessType" label="操作类型" width="100">
          <template slot-scope="{ row }">{{ getTypeName(row.businessType) }}</template>
        </el-table-column>
        <el-table-column prop="title" label="操作模块" width="120"></el-table-column>
        <el-table-column prop="operContent" label="操作内容" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="ipaddr" label="IP地址" width="140"></el-table-column>
        <el-table-column prop="responseTime" label="响应时间" width="100" align="center">
          <template slot-scope="{ row }">{{ row.responseTime }}ms</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.status === '0'" type="success" size="small">正常</el-tag>
            <el-tag v-else type="danger" size="small">异常</el-tag>
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
  </div>
</template>

<script>
import { getOperLogList, cleanOperLog } from '@/api/system'

export default {
  name: 'LogList',
  data() {
    return {
      loading: false,
      activeTab: 'oper',
      logList: [],
      searchForm: { operName: '', businessType: '', status: '' },
      pagination: { pageNum: 1, pageSize: 10, total: 0 }
    }
  },
  async created() {
    await this.loadLogs()
  },
  methods: {
    async loadLogs() {
      this.loading = true
      try {
        const data = await getOperLogList({ ...this.searchForm, pageNum: this.pagination.pageNum, pageSize: this.pagination.pageSize })
        this.logList = data.rows || []
        this.pagination.total = data.total || 0
      } catch {
        this.logList = [
          { operId: 1, operTime: '2026-03-29 10:30:15', operName: 'admin', businessType: '1', title: '患者管理', operContent: '新增患者信息', ipaddr: '192.168.1.100', responseTime: 125, status: '0' },
          { operId: 2, operTime: '2026-03-29 10:25:10', operName: 'admin', businessType: '2', title: '检查管理', operContent: '修改检查状态', ipaddr: '192.168.1.100', responseTime: 89, status: '0' },
          { operId: 3, operTime: '2026-03-29 10:20:05', operName: 'radiologist', businessType: '2', title: '报告管理', operContent: '编辑诊断报告', ipaddr: '192.168.1.101', responseTime: 156, status: '0' },
          { operId: 4, operTime: '2026-03-29 09:15:30', operName: 'admin', businessType: '3', title: '用户管理', operContent: '删除用户', ipaddr: '192.168.1.100', responseTime: 45, status: '1' },
          { operId: 5, operTime: '2026-03-28 16:45:20', operName: 'clinician', businessType: '1', title: '检查管理', operContent: '创建检查订单', ipaddr: '192.168.1.102', responseTime: 234, status: '0' }
        ]
        this.pagination.total = this.logList.length
      } finally { this.loading = false }
    },
    handleTabChange() { this.pagination.pageNum = 1; this.loadLogs() },
    handleSearch() { this.pagination.pageNum = 1; this.loadLogs() },
    resetSearch() { this.searchForm = { operName: '', businessType: '', status: '' }; this.handleSearch() },
    handleSizeChange(val) { this.pagination.pageSize = val; this.loadLogs() },
    handleCurrentChange(val) { this.pagination.pageNum = val; this.loadLogs() },
    getTypeName(type) { const map = { '1': '新增', '2': '修改', '3': '删除', '4': '查询' }; return map[type] || type },
    getRowClass({ row }) { return row.status === '1' ? 'error-row' : '' },
    exportLogs() { this.$message.info('导出功能开发中') },
    async cleanLogs() {
      await this.$confirm('确定要清空所有日志吗？此操作不可恢复。', '警告')
      try {
        await cleanOperLog()
        this.$message.success('日志已清空')
        this.loadLogs()
      } catch { this.$message.error('清空失败') }
    }
  }
}
</script>

<style lang="scss" scoped>
.log-list {
  .page-header { margin-bottom: 24px; }
  .tabs-card { margin-bottom: 16px; }
  .search-card { margin-bottom: 16px; .el-form-item { margin-bottom: 0; } }
  .table-actions { margin-bottom: 16px; display: flex; gap: 8px; }
  .pagination-wrapper { margin-top: 20px; text-align: right; }
}
</style>
