<template>
  <div class="main-layout">
    <Sidebar :collapsed="sidebarCollapsed" />
    <div class="main-content" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <Header @toggle-sidebar="toggleSidebar" />
      <div class="content-wrapper">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import Sidebar from '@/components/common/Sidebar.vue'
import Header from '@/components/common/Header.vue'

export default {
  name: 'MainLayout',
  components: { Sidebar, Header },
  computed: {
    ...mapState('app', ['sidebarCollapsed'])
  },
  methods: {
    ...mapMutations('app', ['TOGGLE_SIDEBAR']),
    toggleSidebar() {
      this.TOGGLE_SIDEBAR()
    }
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  margin-left: 220px;
  min-height: 100vh;
  transition: margin-left 0.3s;

  &.sidebar-collapsed {
    margin-left: 64px;
  }
}

.content-wrapper {
  padding: 24px;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa;
}
</style>
