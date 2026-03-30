<template>
  <el-dialog
    :visible.sync="visible"
    :title="title"
    width="400px"
    :close-on-click-modal="false"
  >
    <div class="confirm-content">
      <i :class="iconClass"></i>
      <span>{{ message }}</span>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button :type="confirmType" @click="handleConfirm">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ConfirmDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '提示'
    },
    message: {
      type: String,
      default: '确定要执行此操作吗？'
    },
    confirmType: {
      type: String,
      default: 'primary'
    }
  },
  computed: {
    iconClass() {
      const iconMap = {
        primary: 'fas fa-question-circle',
        danger: 'fas fa-exclamation-circle',
        warning: 'fas fa-exclamation-triangle'
      }
      return iconMap[this.confirmType] || 'fas fa-question-circle'
    }
  },
  methods: {
    handleConfirm() {
      this.$emit('confirm')
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style lang="scss" scoped>
.confirm-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;

  i {
    font-size: 24px;
    color: #1890ff;
  }
}
</style>
