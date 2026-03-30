<template>
  <span class="status-tag" :class="typeClass">{{ displayText }}</span>
</template>

<script>
export default {
  name: 'StatusTag',
  props: {
    status: {
      type: String,
      required: true
    }
  },
  computed: {
    typeClass() {
      const map = {
        'pending': 'status-pending',
        'in_progress': 'status-in-progress',
        'completed': 'status-completed',
        'reported': 'status-reported',
        'draft': 'status-draft',
        'normal': 'status-normal',
        'active': 'status-active'
      }
      return map[this.status] || 'status-default'
    },
    displayText() {
      const map = {
        'pending': '待检查',
        'in_progress': '检查中',
        'completed': '已完成',
        'reported': '已出报告',
        'draft': '草稿',
        'normal': '正常',
        'active': '启用',
        'inactive': '停用'
      }
      return map[this.status] || this.status
    }
  }
}
</script>

<style lang="scss" scoped>
.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid;
}

.status-pending {
  background: #fffbe6;
  color: #ad6800;
  border-color: #ffe58f;
}

.status-in-progress {
  background: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.status-completed {
  background: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}

.status-reported {
  background: #f9f0ff;
  color: #722ed1;
  border-color: #d3adf7;
}

.status-draft {
  background: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.status-normal,
.status-active {
  background: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}
</style>
