<template>
  <div class="statistics">
    <div class="page-header">
      <h1 class="page-title">运营统计</h1>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue"><i class="fas fa-users"></i></div>
        <div class="stat-info">
          <div class="stat-value">1,286</div>
          <div class="stat-label">患者总数</div>
          <div class="stat-trend up"><i class="fas fa-arrow-up"></i> 12.5%</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><i class="fas fa-clipboard-check"></i></div>
        <div class="stat-info">
          <div class="stat-value">8,456</div>
          <div class="stat-label">检查总数</div>
          <div class="stat-trend up"><i class="fas fa-arrow-up"></i> 8.3%</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><i class="fas fa-file-medical"></i></div>
        <div class="stat-info">
          <div class="stat-value">7,892</div>
          <div class="stat-label">报告总数</div>
          <div class="stat-trend up"><i class="fas fa-arrow-up"></i> 15.2%</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red"><i class="fas fa-share-alt"></i></div>
        <div class="stat-info">
          <div class="stat-value">3,456</div>
          <div class="stat-label">分享次数</div>
          <div class="stat-trend up"><i class="fas fa-arrow-up"></i> 23.1%</div>
        </div>
      </div>
    </div>

    <!-- Charts -->
    <div class="charts-grid">
      <div class="card chart-card">
        <div class="card-header">
          <div class="card-title"><i class="fas fa-chart-line"></i> 检查趋势</div>
          <el-select v-model="trendRange" size="small" style="width: 120px;">
            <el-option label="近7天" value="7"></el-option>
            <el-option label="近30天" value="30"></el-option>
            <el-option label="近3个月" value="90"></el-option>
          </el-select>
        </div>
        <div class="chart-placeholder">
          <i class="fas fa-chart-area"></i>
          <span>趋势图区域</span>
        </div>
      </div>
      <div class="card chart-card">
        <div class="card-header">
          <div class="card-title"><i class="fas fa-chart-pie"></i> 检查类型分布</div>
        </div>
        <div class="chart-placeholder">
          <i class="fas fa-chart-pie"></i>
          <span>饼图区域</span>
          <div class="legend">
            <span><i class="dot blue"></i> CT 35%</span>
            <span><i class="dot green"></i> MRI 25%</span>
            <span><i class="dot orange"></i> DR 20%</span>
            <span><i class="dot red"></i> US 20%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Doctor Ranking -->
    <div class="card">
      <div class="card-header">
        <div class="card-title"><i class="fas fa-trophy"></i> 医生报告排行</div>
      </div>
      <el-table :data="doctorRanking">
        <el-table-column type="rank" width="60" align="center">
          <template slot-scope="{ $index }">
            <span :class="{ 'top-rank': $index < 3 }">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="医生姓名" width="120"></el-table-column>
        <el-table-column prop="deptName" label="科室" width="120"></el-table-column>
        <el-table-column prop="reportCount" label="报告数" width="100" align="center"></el-table-column>
        <el-table-column prop="avgTime" label="平均出报时间" width="120" align="center"></el-table-column>
        <el-table-column label="完成率">
          <template slot-scope="{ row }">
            <el-progress :percentage="row.completionRate" :color="row.completionRate > 90 ? '#52c41a' : '#1890ff'"></el-progress>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Daily Stats Table -->
    <div class="card">
      <div class="card-header">
        <div class="card-title"><i class="fas fa-table"></i> 每日统计</div>
        <div>
          <el-button size="mini" @click="exportExcel"><i class="fas fa-file-excel"></i> 导出</el-button>
          <el-button size="mini" @click="exportPdf"><i class="fas fa-file-pdf"></i> PDF</el-button>
        </div>
      </div>
      <el-table :data="dailyStats">
        <el-table-column prop="date" label="日期" width="120"></el-table-column>
        <el-table-column prop="patients" label="新增患者" width="100" align="center"></el-table-column>
        <el-table-column prop="exams" label="检查数" width="100" align="center"></el-table-column>
        <el-table-column prop="reports" label="报告数" width="100" align="center"></el-table-column>
        <el-table-column prop="images" label="影像数" width="100" align="center"></el-table-column>
        <el-table-column prop="shares" label="分享数" width="100" align="center"></el-table-column>
        <el-table-column prop="views" label="浏览数" width="100" align="center"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Statistics',
  data() {
    return {
      trendRange: '7',
      doctorRanking: [
        { doctorName: '王医生', deptName: '放射科', reportCount: 156, avgTime: '2.5h', completionRate: 98 },
        { doctorName: '李医生', deptName: '放射科', reportCount: 142, avgTime: '2.8h', completionRate: 95 },
        { doctorName: '张医生', deptName: '放射科', reportCount: 128, avgTime: '3.0h', completionRate: 92 },
        { doctorName: '赵医生', deptName: '放射科', reportCount: 115, avgTime: '3.2h', completionRate: 90 },
        { doctorName: '周医生', deptName: '放射科', reportCount: 98, avgTime: '3.5h', completionRate: 88 }
      ],
      dailyStats: [
        { date: '2026-03-28', patients: 12, exams: 89, reports: 76, images: 456, shares: 23, views: 156 },
        { date: '2026-03-27', patients: 15, exams: 92, reports: 82, images: 512, shares: 28, views: 189 },
        { date: '2026-03-26', patients: 18, exams: 95, reports: 85, images: 534, shares: 31, views: 201 },
        { date: '2026-03-25', patients: 14, exams: 88, reports: 78, images: 489, shares: 25, views: 167 },
        { date: '2026-03-24', patients: 16, exams: 91, reports: 80, images: 502, shares: 27, views: 178 }
      ]
    }
  },
  methods: {
    exportExcel() {
      this.$message.info('导出 Excel 功能开发中')
    },
    exportPdf() {
      this.$message.info('导出 PDF 功能开发中')
    }
  }
}
</script>

<style lang="scss" scoped>
.statistics {
  .page-header { margin-bottom: 24px; }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;

  &.blue { background: #e6f7ff; color: #1890ff; }
  &.green { background: #f6ffed; color: #52c41a; }
  &.orange { background: #fff7e6; color: #fa8c16; }
  &.red { background: #fff1f0; color: #ff4d4f; }
}

.stat-info {
  .stat-value { font-size: 28px; font-weight: 600; color: #303133; }
  .stat-label { font-size: 14px; color: #909399; margin: 4px 0; }
  .stat-trend { font-size: 12px; &.up { color: #52c41a; } }
}

.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card { min-height: 300px; }

.chart-placeholder {
  height: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  margin-top: 16px;
  color: #c0c4cc;

  i { font-size: 48px; margin-bottom: 12px; }

  .legend {
    display: flex;
    gap: 16px;
    margin-top: 16px;
    font-size: 13px;
    color: #606266;

    .dot {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      margin-right: 4px;

      &.blue { background: #1890ff; }
      &.green { background: #52c41a; }
      &.orange { background: #fa8c16; }
      &.red { background: #ff4d4f; }
    }
  }
}

.top-rank { color: #faad14; font-weight: 600; }
</style>
