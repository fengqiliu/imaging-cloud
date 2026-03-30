<template>
  <div class="empty-state">
    <div class="empty-icon">
      <i :class="icon || 'fa-inbox'"></i>
    </div>
    <div class="empty-text">{{ text || '暂无数据' }}</div>
    <div class="empty-action" v-if="$slots.action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    icon: {
      type: String,
      default: 'fa-inbox'
    },
    text: {
      type: String,
      default: '暂无数据'
    }
  }
}
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: #f5f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;

  i {
    font-size: 32px;
    color: #c0c4cc;
  }
}

.empty-text {
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}
</style>
