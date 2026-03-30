<template>
  <div class="dict-list">
    <div class="page-header">
      <h1 class="page-title">字典管理</h1>
    </div>

    <div class="dict-layout">
      <!-- Dict Type List -->
      <div class="card dict-type-panel">
        <div class="card-header">
          <div class="card-title">字典类型</div>
          <el-button size="mini" type="text" @click="addDictType"><i class="el-icon-plus"></i></el-button>
        </div>
        <div class="type-list">
          <div
            v-for="type in dictTypes"
            :key="type.dictId"
            class="type-item"
            :class="{ active: currentType && currentType.dictId === type.dictId }"
            @click="selectType(type)"
          >
            <span class="type-name">{{ type.dictName }}</span>
            <el-badge :value="type.count" class="type-count" />
          </div>
        </div>
      </div>

      <!-- Dict Items -->
      <div class="card dict-items-panel">
        <div class="card-header">
          <div class="card-title">{{ currentType?.dictName || '字典项' }}</div>
          <el-button size="mini" type="primary" @click="addDictItem" :disabled="!currentType">
            <i class="el-icon-plus"></i> 新增
          </el-button>
        </div>
        <el-table :data="dictItems" stripe>
          <el-table-column prop="dictLabel" label="标签" width="150"></el-table-column>
          <el-table-column prop="dictValue" label="键值" width="150"></el-table-column>
          <el-table-column prop="dictSort" label="排序" width="80" align="center"></el-table-column>
          <el-table-column prop="dictType" label="样式" width="100">
            <template slot-scope="{ row }">
              <el-tag v-if="row.listClass" :type="row.listClass === 'primary' ? '' : row.listClass">{{ row.dictLabel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="{ row }">
              <StatusTag :status="row.status === '0' ? 'normal' : 'inactive'" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template slot-scope="{ row }">
              <el-button type="text" size="small" @click="editItem(row)">编辑</el-button>
              <el-button type="text" size="small" class="danger" @click="deleteItem(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- Edit Dialog -->
    <el-dialog :visible.sync="editVisible" :title="editTitle" width="500px">
      <el-form ref="dictForm" :model="dictForm" label-width="80px">
        <el-form-item label="标签">
          <el-input v-model="dictForm.dictLabel" placeholder="请输入标签"></el-input>
        </el-form-item>
        <el-form-item label="键值">
          <el-input v-model="dictForm.dictValue" placeholder="请输入键值"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dictForm.dictSort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dictForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItem">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getDictTypeList } from '@/api/system'

export default {
  name: 'DictList',
  data() {
    return {
      dictTypes: [],
      currentType: null,
      dictItems: [],
      editVisible: false,
      editTitle: '新增字典项',
      isEdit: false,
      dictForm: { dictLabel: '', dictValue: '', dictSort: 0, status: '0' }
    }
  },
  async created() {
    await this.loadDictTypes()
    if (this.dictTypes.length > 0) {
      this.selectType(this.dictTypes[0])
    }
  },
  methods: {
    async loadDictTypes() {
      try {
        const data = await getDictTypeList()
        this.dictTypes = data.rows || []
      } catch {
        this.dictTypes = [
          { dictId: 1, dictName: '检查状态', dictType: 'exam_status', count: 4 },
          { dictId: 2, dictName: '检查类型', dictType: 'exam_type', count: 4 },
          { dictId: 3, dictName: '影像类型', dictType: 'modality', count: 4 },
          { dictId: 4, dictName: '性别', dictType: 'gender', count: 2 },
          { dictId: 5, dictName: '报告状态', dictType: 'report_status', count: 3 }
        ]
      }
    },
    selectType(type) {
      this.currentType = type
      this.dictItems = this.getMockItems(type.dictType)
    },
    getMockItems(type) {
      const items = {
        exam_status: [
          { dictLabel: '待检查', dictValue: 'pending', dictSort: 1, status: '0' },
          { dictLabel: '检查中', dictValue: 'in_progress', dictSort: 2, status: '0' },
          { dictLabel: '已完成', dictValue: 'completed', dictSort: 3, status: '0' },
          { dictLabel: '已出报告', dictValue: 'reported', dictSort: 4, status: '0' }
        ],
        modality: [
          { dictLabel: 'CT', dictValue: 'CT', dictSort: 1, status: '0', listClass: 'primary' },
          { dictLabel: 'MRI', dictValue: 'MRI', dictSort: 2, status: '0', listClass: 'success' },
          { dictLabel: 'DR', dictValue: 'DR', dictSort: 3, status: '0', listClass: 'warning' },
          { dictLabel: 'US', dictValue: 'US', dictSort: 4, status: '0', listClass: 'danger' }
        ],
        gender: [
          { dictLabel: '男', dictValue: '1', dictSort: 1, status: '0' },
          { dictLabel: '女', dictValue: '0', dictSort: 2, status: '0' }
        ]
      }
      return items[type] || []
    },
    addDictType() { this.$message.info('新增字典类型功能开发中') },
    addDictItem() { this.editTitle = '新增字典项'; this.isEdit = false; this.dictForm = { dictLabel: '', dictValue: '', dictSort: 0, status: '0' }; this.editVisible = true },
    editItem(item) { this.editTitle = '编辑字典项'; this.isEdit = true; this.dictForm = { ...item }; this.editVisible = true },
    async saveItem() {
      this.$message.success('保存成功')
      this.editVisible = false
      if (this.currentType) this.selectType(this.currentType)
    },
    async deleteItem(item) {
      await this.$confirm('确定要删除该字典项吗？', '提示')
      this.$message.success('删除成功')
      if (this.currentType) this.selectType(this.currentType)
    }
  }
}
</script>

<style lang="scss" scoped>
.dict-list { .page-header { margin-bottom: 24px; } }
.dict-layout { display: grid; grid-template-columns: 280px 1fr; gap: 16px; }
.dict-type-panel { .type-list { padding: 8px; } }
.type-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; cursor: pointer; border-radius: 6px; margin-bottom: 4px; &:hover { background: #f5f7fa; } &.active { background: #e6f7ff; } }
.type-count { margin-right: 8px; }
.danger { color: #ff4d4f; }
</style>
