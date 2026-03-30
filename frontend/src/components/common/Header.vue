<template>
  <header class="header">
    <div class="header-left">
      <i class="fas fa-bars toggle-btn" @click="$emit('toggle-sidebar')"></i>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentRoute">{{ currentRoute }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <div class="avatar">
            <i class="fas fa-user"></i>
          </div>
          <span class="username">{{ nickName }}</span>
          <i class="fas fa-chevron-down"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile">
            <i class="fas fa-user-circle"></i> 个人中心
          </el-dropdown-item>
          <el-dropdown-item command="settings">
            <i class="fas fa-cog"></i> 系统设置
          </el-dropdown-item>
          <el-dropdown-item divided command="logout">
            <i class="fas fa-sign-out-alt"></i> 退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </header>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'Header',
  computed: {
    ...mapState('auth', ['nickName']),
    currentRoute() {
      return this.$route.meta.title
    }
  },
  methods: {
    ...mapActions('auth', ['logout']),
    async handleCommand(command) {
      if (command === 'logout') {
        await this.logout()
        this.$router.push('/login')
      } else if (command === 'profile') {
        this.$message.info('个人中心功能开发中')
      } else if (command === 'settings') {
        this.$router.push('/system/settings')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  height: 60px;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 99;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.toggle-btn {
  font-size: 18px;
  color: #606266;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;

  &:hover {
    background: #f5f7fa;
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;

  &:hover {
    background: #f5f7fa;
  }
}

.avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  i {
    color: white;
    font-size: 14px;
  }
}

.username {
  font-size: 14px;
  color: #303133;
}
</style>
