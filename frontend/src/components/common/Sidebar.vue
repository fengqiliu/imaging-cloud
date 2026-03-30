<template>
  <aside class="sidebar" :class="{ collapsed }">
    <div class="sidebar-header">
      <div class="sidebar-logo">
        <i class="fas fa-x-ray"></i>
      </div>
      <span class="sidebar-title" v-if="!collapsed">D-Site</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      background-color="#001529"
      text-color="rgba(255,255,255,0.7)"
      active-text-color="#ffffff"
      :router="true"
    >
      <el-menu-item index="/dashboard">
        <i class="fas fa-th-large"></i>
        <span slot="title">仪表盘</span>
      </el-menu-item>

      <div class="menu-section">云胶片业务</div>

      <el-menu-item index="/patients">
        <i class="fas fa-users"></i>
        <span slot="title">患者管理</span>
      </el-menu-item>

      <el-menu-item index="/examinations">
        <i class="fas fa-clipboard-list"></i>
        <span slot="title">检查管理</span>
        <el-badge v-if="pendingExamCount > 0" :value="pendingExamCount" class="badge" />
      </el-menu-item>

      <el-menu-item index="/images">
        <i class="fas fa-images"></i>
        <span slot="title">影像管理</span>
      </el-menu-item>

      <el-menu-item index="/viewer">
        <i class="fas fa-eye"></i>
        <span slot="title">DICOM阅片</span>
      </el-menu-item>

      <el-menu-item index="/reports">
        <i class="fas fa-file-medical"></i>
        <span slot="title">诊断报告</span>
      </el-menu-item>

      <el-menu-item index="/ai">
        <i class="fas fa-robot"></i>
        <span slot="title">AI解读</span>
        <span class="new-badge">NEW</span>
      </el-menu-item>

      <el-menu-item index="/shares">
        <i class="fas fa-share-alt"></i>
        <span slot="title">分享管理</span>
      </el-menu-item>

      <el-menu-item index="/statistics">
        <i class="fas fa-chart-bar"></i>
        <span slot="title">运营统计</span>
      </el-menu-item>

      <div class="menu-section">系统管理</div>

      <el-menu-item index="/system/users">
        <i class="fas fa-user-cog"></i>
        <span slot="title">用户管理</span>
      </el-menu-item>

      <el-menu-item index="/system/roles">
        <i class="fas fa-theater-masks"></i>
        <span slot="title">角色管理</span>
      </el-menu-item>

      <el-menu-item index="/system/depts">
        <i class="fas fa-hospital"></i>
        <span slot="title">院区管理</span>
      </el-menu-item>

      <el-menu-item index="/system/dict">
        <i class="fas fa-book"></i>
        <span slot="title">字典管理</span>
      </el-menu-item>

      <el-menu-item index="/system/logs">
        <i class="fas fa-history"></i>
        <span slot="title">日志管理</span>
      </el-menu-item>

      <el-menu-item index="/system/settings">
        <i class="fas fa-cog"></i>
        <span slot="title">系统设置</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script>
export default {
  name: 'Sidebar',
  props: {
    collapsed: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      pendingExamCount: 5
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  background: #001529;
  position: fixed;
  height: 100vh;
  overflow-y: auto;
  z-index: 100;
  transition: width 0.3s;

  &.collapsed {
    width: 64px;
  }
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.sidebar-logo {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;

  i {
    color: white;
    font-size: 20px;
  }
}

.sidebar-title {
  color: white;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.menu-section {
  padding: 10px 20px 5px;
  color: rgba(255,255,255,0.4);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.new-badge {
  background: #722ed1;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
}

.badge {
  margin-left: auto;
}
</style>
