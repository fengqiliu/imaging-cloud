<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">欢迎回来，{{ nickName }}</h1>
      <p class="page-desc">{{ currentDate }} · {{ weatherInfo }}</p>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div class="stat-card blue">
        <div class="stat-icon"><i class="fas fa-users"></i></div>
        <div class="stat-value">{{ stats.patientCount || 0 }}</div>
        <div class="stat-label">患者总数</div>
        <div class="stat-trend up" v-if="stats.patientGrowth">
          <i class="fas fa-arrow-up"></i> {{ stats.patientGrowth }}% 较上月
        </div>
      </div>
      <div class="stat-card green">
        <div class="stat-icon"><i class="fas fa-clipboard-check"></i></div>
        <div class="stat-value">{{ stats.todayExams || 0 }}</div>
        <div class="stat-label">今日检查</div>
        <div class="stat-trend up" v-if="stats.examGrowth">
          <i class="fas fa-arrow-up"></i> {{ stats.examGrowth }}% 较昨日
        </div>
      </div>
      <div class="stat-card orange">
        <div class="stat-icon"><i class="fas fa-file-medical"></i></div>
        <div class="stat-value">{{ stats.pendingReports || 0 }}</div>
        <div class="stat-label">待出报告</div>
        <div class="stat-trend down" v-if="stats.reportTrend">
          <i class="fas fa-arrow-down"></i> {{ stats.reportTrend }}% 较昨日
        </div>
      </div>
      <div class="stat-card red">
        <div class="stat-icon"><i class="fas fa-share-alt"></i></div>
        <div class="stat-value">{{ stats.shareCount || 0 }}</div>
        <div class="stat-label">分享次数</div>
        <div class="stat-trend up" v-if="stats.shareGrowth">
          <i class="fas fa-arrow-up"></i> {{ stats.shareGrowth }}% 较上周
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="card">
      <div class="card-header">
        <div class="card-title"><i class="fas fa-bolt"></i> 快捷入口</div>
      </div>
      <div class="quick-actions">
        <div class="quick-action" @click="quickAction('/patients?action=add')">
          <div class="quick-action-icon blue"><i class="fas fa-user-plus"></i></div>
          <div class="quick-action-title">新增患者</div>
        </div>
        <div class="quick-action" @click="quickAction('/examinations?action=add')">
          <div class="quick-action-icon green"><i class="fas fa-plus-circle"></i></div>
          <div class="quick-action-title">新建检查</div>
        </div>
        <div class="quick-action" @click="quickAction('/images?action=upload')">
          <div class="quick-action-icon orange"><i class="fas fa-upload"></i></div>
          <div class="quick-action-title">上传影像</div>
        </div>
        <div class="quick-action" @click="quickAction('/reports')">
          <div class="quick-action-icon red"><i class="fas fa-edit"></i></div>
          <div class="quick-action-title">撰写报告</div>
        </div>
      </div>
    </div>

    <!-- Charts and Todos -->
    <div class="chart-grid">
      <div class="card chart-card">
        <div class="card-header">
          <div class="card-title"><i class="fas fa-chart-line"></i> 检查趋势</div>
          <el-select v-model="chartRange" size="small" style="width: 120px;">
            <el-option label="近7天" value="7" />
            <el-option label="近30天" value="30" />
            <el-option label="近3个月" value="90" />
          </el-select>
        </div>
        <div ref="examTrendChart" class="chart-container"></div>
      </div>

      <div class="card chart-card">
        <div class="card-header">
          <div class="card-title"><i class="fas fa-tasks"></i> 待办事项</div>
        </div>
        <ul class="todo-list">
          <li
            v-for="todo in todos"
            :key="todo.id"
            class="todo-item"
            :class="{ completed: todo.completed }"
          >
            <div class="todo-checkbox" @click="toggleTodo(todo)">
              <i v-if="todo.completed" class="fas fa-check"></i>
            </div>
            <div class="todo-content">
              <div class="todo-title">{{ todo.title }}</div>
              <div class="todo-meta">{{ todo.meta }}</div>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <!-- Recent Examinations -->
    <div class="card">
      <div class="card-header">
        <div class="card-title"><i class="fas fa-clock"></i> 近期检查</div>
        <el-button type="text" @click="$router.push('/examinations')">
          查看全部 <i class="el-icon-arrow-right"></i>
        </el-button>
      </div>
      <el-table :data="recentExams" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="examId" label="检查号" width="140" />
        <el-table-column prop="patientName" label="患者姓名" width="100" />
        <el-table-column prop="modality" label="检查类型" width="100">
          <template slot-scope="{ row }">
            <el-tag size="small" :type="getModalityType(row.modality)">{{ row.modality }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examPart" label="检查部位" width="100" />
        <el-table-column prop="examDate" label="检查日期" width="120" />
        <el-table-column prop="status" label="状态">
          <template slot-scope="{ row }">
            <StatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="viewExam(row)">查看</el-button>
            <el-button type="text" size="small" @click="writeReport(row)" v-if="row.status === 'completed'">写报告</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getExaminationList } from '@/api/examination'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      chartRange: '7',
      examTrendChart: null,
      stats: {
        patientCount: 1286,
        todayExams: 89,
        pendingReports: 45,
        shareCount: 234,
        patientGrowth: 12.5,
        examGrowth: 8.3,
        reportTrend: 5.2,
        shareGrowth: 23.1
      },
      todos: [
        { id: 1, title: '审核王小红CT报告', meta: '检查号: EX2026032601 · 10:30前', completed: false },
        { id: 2, title: '上传张伟MRI影像', meta: '检查号: EX2026032503 · 今天', completed: false },
        { id: 3, title: '完成李明DR检查阅片', meta: '检查号: EX2026032602 · 已完成', completed: true }
      ],
      recentExams: [],
      loading: false
    }
  },
  computed: {
    ...mapState('auth', ['nickName']),
    currentDate() {
      return new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    weatherInfo() {
      return '晴 · 适宜工作'
    }
  },
  async created() {
    await this.loadRecentExams()
  },
  mounted() {
    this.initExamTrendChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    if (this.examTrendChart) {
      this.examTrendChart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  watch: {
    chartRange() {
      this.updateExamTrendChart()
    }
  },
  methods: {
    initExamTrendChart() {
      if (!this.$refs.examTrendChart) return

      this.examTrendChart = echarts.init(this.$refs.examTrendChart)
      this.updateExamTrendChart()
    },
    updateExamTrendChart() {
      if (!this.examTrendChart) return

      const chartData = this.getChartData()

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        legend: {
          data: ['检查数量', '增长率'],
          top: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: chartData.dates,
            axisPointer: {
              type: 'shadow'
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '检查数量',
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '增长率',
            axisLabel: {
              formatter: '{value}%'
            }
          }
        ],
        series: [
          {
            name: '检查数量',
            type: 'bar',
            data: chartData.examCounts,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#2378f7' },
                  { offset: 0.7, color: '#2378f7' },
                  { offset: 1, color: '#83bff6' }
                ])
              }
            }
          },
          {
            name: '增长率',
            type: 'line',
            yAxisIndex: 1,
            data: chartData.growthRates,
            smooth: true,
            itemStyle: {
              color: '#52c41a'
            },
            lineStyle: {
              width: 3
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
                { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
              ])
            }
          }
        ]
      }

      this.examTrendChart.setOption(option)
    },
    getChartData() {
      const days = parseInt(this.chartRange)
      const dates = []
      const examCounts = []
      const growthRates = []

      for (let i = days - 1; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        dates.push(`${date.getMonth() + 1}/${date.getDate()}`)

        // 模拟数据：基础值 + 随机波动
        const baseCount = 80
        const randomFactor = Math.random() * 40 - 20
        const count = Math.round(baseCount + randomFactor + (days - i) * 2)
        examCounts.push(count)

        // 计算增长率
        if (i === days - 1) {
          growthRates.push(0)
        } else {
          const prevCount = examCounts[examCounts.length - 2]
          const growth = ((count - prevCount) / prevCount * 100).toFixed(1)
          growthRates.push(parseFloat(growth))
        }
      }

      return { dates, examCounts, growthRates }
    },
    handleResize() {
      if (this.examTrendChart) {
        this.examTrendChart.resize()
      }
    },
    async loadRecentExams() {
      this.loading = true
      try {
        const data = await getExaminationList({ pageNum: 1, pageSize: 5 })
        this.recentExams = data.rows || []
      } catch (error) {
        // Use mock data if API fails
        this.recentExams = [
          { examId: 'EX2026032605', patientName: '王建国', modality: 'CT', examPart: '胸部', examDate: '2026-03-26', status: 'pending' },
          { examId: 'EX2026032604', patientName: '李秀英', modality: 'MRI', examPart: '头部', examDate: '2026-03-26', status: 'in_progress' },
          { examId: 'EX2026032603', patientName: '张伟', modality: 'DR', examPart: '四肢', examDate: '2026-03-25', status: 'completed' },
          { examId: 'EX2026032602', patientName: '李明', modality: 'US', examPart: '腹部', examDate: '2026-03-25', status: 'reported' },
          { examId: 'EX2026032601', patientName: '王小红', modality: 'CT', examPart: '胸部', examDate: '2026-03-24', status: 'reported' }
        ]
      } finally {
        this.loading = false
      }
    },
    getModalityType(modality) {
      const map = { 'CT': '', 'MRI': 'success', 'DR': 'warning', 'US': 'danger' }
      return map[modality] || 'info'
    },
    toggleTodo(todo) {
      todo.completed = !todo.completed
    },
    viewExam(row) {
      this.$router.push(`/examinations?examId=${row.examId}`)
    },
    writeReport(row) {
      this.$router.push(`/reports?examId=${row.examId}`)
    },
    quickAction(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  .page-desc {
    color: #909399;
    font-size: 14px;
  }
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
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
  }

  &.blue::before { background: #1890ff; }
  &.green::before { background: #52c41a; }
  &.orange::before { background: #faad14; }
  &.red::before { background: #ff4d4f; }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 16px;

  .blue & { background: #e6f7ff; color: #1890ff; }
  .green & { background: #f6ffed; color: #52c41a; }
  .orange & { background: #fffbe6; color: #faad14; }
  .red & { background: #fff1f0; color: #ff4d4f; }
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 8px;

  &.up { color: #52c41a; }
  &.down { color: #ff4d4f; }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-action {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;

  &:hover {
    border-color: #1890ff;
    background: #fff;
  }
}

.quick-action-icon {
  width: 48px;
  height: 48px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  font-size: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &.blue { color: #1890ff; }
  &.green { color: #52c41a; }
  &.orange { color: #faad14; }
  &.red { color: #ff4d4f; }
}

.quick-action-title {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.chart-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  min-height: 300px;
}

.chart-container {
  width: 100%;
  height: 280px;
  margin-top: 16px;
}

.chart-area {
  height: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  margin-top: 16px;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    color: #dcdfe6;
  }

  span {
    font-size: 14px;
  }

  p {
    font-size: 12px;
    color: #c0c4cc;
    margin-top: 8px;
  }
}

.todo-list {
  list-style: none;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;

  &:last-child { border-bottom: none; }

  &.completed {
    .todo-checkbox {
      background: #52c41a;
      border-color: #52c41a;
    }

    .todo-content {
      text-decoration: line-through;
      color: #909399;
    }
  }
}

.todo-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid #dcdfe6;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
  margin-top: 2px;
  display: flex;
  align-items: center;
  justify-content: center;

  i {
    font-size: 12px;
    color: white;
  }
}

.todo-content {
  flex: 1;
}

.todo-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.todo-meta {
  font-size: 12px;
  color: #909399;
}
</style>
