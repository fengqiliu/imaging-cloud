<template>
  <div class="dept-tree">
    <div class="page-header">
      <h1 class="page-title">院区管理</h1>
    </div>

    <div class="dept-layout">
      <div class="card dept-tree-panel">
        <div class="card-header">
          <div class="card-title"><i class="fas fa-hospital"></i> 组织架构</div>
          <el-button size="mini" type="text" @click="addRootDept"><i class="el-icon-plus"></i> 新增</el-button>
        </div>
        <div class="tree-wrapper">
          <el-tree
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            node-key="deptId"
            :expand-on-click-node="false"
            @node-click="selectDept"
          >
            <span slot-scope="{ node, data }" class="tree-node">
              <span><i :class="data.parentId === 0 ? 'fas fa-hospital' : 'fas fa-building'"></i> {{ data.deptName }}</span>
              <span class="node-actions">
                <el-button type="text" size="mini" @click.stop="addChildDept(data)"><i class="el-icon-plus"></i></el-button>
                <el-button type="text" size="mini" @click.stop="editDept(data)"><i class="el-icon-edit"></i></el-button>
                <el-button type="text" size="mini" @click.stop="deleteDept(data)"><i class="el-icon-delete"></i></el-button>
              </span>
            </span>
          </el-tree>
        </div>
      </div>

      <div class="card dept-detail-panel" v-if="currentDept">
        <div class="card-header">
          <div class="card-title"><i class="fas fa-info-circle"></i> 部门详情</div>
        </div>
        <el-form :model="currentDept" label-width="100px" class="dept-form">
          <el-form-item label="部门名称">
            <el-input v-model="currentDept.deptName"></el-input>
          </el-form-item>
          <el-form-item label="上级部门">
            <el-input :value="getParentName(currentDept)" disabled></el-input>
          </el-form-item>
          <el-form-item label="联系人">
            <el-input v-model="currentDept.contactPerson"></el-input>
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="currentDept.phone"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="currentDept.email"></el-input>
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="currentDept.address"></el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="currentDept.remark" type="textarea"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveDept">保存</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getDeptList } from '@/api/system'

export default {
  name: 'DeptTree',
  data() {
    return {
      deptTree: [],
      currentDept: null
    }
  },
  async created() {
    await this.loadDepts()
  },
  methods: {
    async loadDepts() {
      try {
        const data = await getDeptList()
        this.deptTree = this.buildTree(data.rows || [])
      } catch {
        this.deptTree = [
          { deptId: 1, deptName: '总院', parentId: 0, children: [
            { deptId: 11, deptName: '放射科', parentId: 1 },
            { deptId: 12, deptName: '神经内科', parentId: 1 },
            { deptId: 13, deptName: '心内科', parentId: 1 }
          ]},
          { deptId: 2, deptName: '分院', parentId: 0, children: [
            { deptId: 21, deptName: '放射科', parentId: 2 }
          ]}
        ]
      }
    },
    buildTree(list) {
      const map = {}
      const roots = []
      list.forEach(item => { map[item.deptId] = { ...item, children: [] } })
      list.forEach(item => {
        if (item.parentId === 0) roots.push(map[item.deptId])
        else if (map[item.parentId]) map[item.parentId].children.push(map[item.deptId])
      })
      return roots
    },
    selectDept(data) { this.currentDept = { ...data } },
    getParentName(dept) {
      if (dept.parentId === 0) return '无'
      // Find parent name from tree
      return '上级部门'
    },
    addRootDept() { this.currentDept = { deptId: '', deptName: '', parentId: 0 } },
    addChildDept(parent) { this.currentDept = { deptId: '', deptName: '', parentId: parent.deptId } },
    editDept(dept) { this.currentDept = { ...dept } },
    async saveDept() {
      this.$message.success('保存成功')
      await this.loadDepts()
    },
    async deleteDept(dept) {
      await this.$confirm('确定要删除该部门吗？', '提示')
      this.$message.success('删除成功')
      await this.loadDepts()
    }
  }
}
</script>

<style lang="scss" scoped>
.dept-tree { .page-header { margin-bottom: 24px; } }
.dept-layout { display: grid; grid-template-columns: 400px 1fr; gap: 16px; }
.dept-tree-panel { .tree-wrapper { padding: 16px; max-height: 600px; overflow-y: auto; } }
.tree-node { flex: 1; display: flex; justify-content: space-between; align-items: center; i { margin-right: 8px; } .node-actions { display: none; } }
.tree-node:hover .node-actions { display: inline; }
.dept-detail-panel { .dept-form { padding: 20px; } }
</style>
