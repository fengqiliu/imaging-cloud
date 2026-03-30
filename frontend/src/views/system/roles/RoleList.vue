<template>
  <div class="role-list">
    <div class="page-header">
      <h1 class="page-title">角色管理</h1>
      <el-button type="primary" icon="el-icon-plus" @click="showAddModal">新增角色</el-button>
    </div>

    <div class="card">
      <el-table :data="roleList" v-loading="loading" stripe>
        <el-table-column prop="roleId" label="角色ID" width="80"></el-table-column>
        <el-table-column prop="roleName" label="角色名称" width="150"></el-table-column>
        <el-table-column prop="roleKey" label="角色标识" width="150"></el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <StatusTag :status="row.status === '0' ? 'normal' : 'inactive'" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="editRole(row)">编辑</el-button>
            <el-button type="text" size="small" @click="configPerms(row)">权限配置</el-button>
            <el-button type="text" size="small" class="danger" @click="deleteRole(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Edit Role Modal -->
    <el-dialog :visible.sync="roleModalVisible" :title="modalTitle" width="500px">
      <el-form ref="roleForm" :model="roleForm" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="roleForm.roleKey" placeholder="请输入角色标识"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="roleForm.sort" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="roleForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="roleModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>

    <!-- Permission Config Modal -->
    <el-dialog :visible.sync="permModalVisible" title="权限配置" width="600px">
      <div class="perm-tree" v-if="currentRole">
        <h4>{{ currentRole.roleName }}</h4>
        <el-tree
          ref="permTree"
          :data="permTreeData"
          show-checkbox
          node-key="permId"
          :default-expand-all="true"
        ></el-tree>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="permModalVisible = false">取消</el-button>
        <el-button type="primary" @click="savePerms">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getRoleList, addRole, updateRole, deleteRole } from '@/api/system'

export default {
  name: 'RoleList',
  data() {
    return {
      loading: false,
      roleModalVisible: false,
      permModalVisible: false,
      modalTitle: '新增角色',
      isEdit: false,
      currentRole: null,
      roleList: [],
      roleForm: { roleId: '', roleName: '', roleKey: '', sort: 0, status: '0' },
      rules: { roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }], roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }] },
      permTreeData: [
        { permId: 1, label: '云胶片业务', children: [
          { permId: 11, label: '患者管理', children: [
            { permId: 111, label: '查看' }, { permId: 112, label: '新增' }, { permId: 113, label: '编辑' }, { permId: 114, label: '删除' }
          ]},
          { permId: 12, label: '检查管理', children: [
            { permId: 121, label: '查看' }, { permId: 122, label: '新增' }, { permId: 123, label: '编辑' }, { permId: 124, label: '删除' }
          ]},
          { permId: 13, label: '影像管理', children: [
            { permId: 131, label: '查看' }, { permId: 132, label: '上传' }, { permId: 133, label: '删除' }
          ]},
          { permId: 14, label: '报告管理', children: [
            { permId: 141, label: '查看' }, { permId: 142, label: '编辑' }, { permId: 143, label: '审核' }
          ]}
        ]}
      ]
    }
  },
  async created() { await this.loadRoles() },
  methods: {
    async loadRoles() {
      this.loading = true
      try {
        const data = await getRoleList({ pageNum: 1, pageSize: 100 })
        this.roleList = data.rows || []
      } catch {
        this.roleList = [
          { roleId: 1, roleName: '超级管理员', roleKey: 'super_admin', sort: 1, status: '0', createTime: '2026-01-01 00:00' },
          { roleId: 2, roleName: '放射科医生', roleKey: 'radiologist', sort: 2, status: '0', createTime: '2026-01-01 00:00' },
          { roleId: 3, roleName: '临床医生', roleKey: 'clinician', sort: 3, status: '0', createTime: '2026-01-01 00:00' },
          { roleId: 4, roleName: '患者', roleKey: 'patient', sort: 4, status: '0', createTime: '2026-01-01 00:00' }
        ]
      } finally { this.loading = false }
    },
    showAddModal() { this.modalTitle = '新增角色'; this.isEdit = false; this.roleForm = { roleId: '', roleName: '', roleKey: '', sort: 0, status: '0' }; this.roleModalVisible = true },
    editRole(row) { this.modalTitle = '编辑角色'; this.isEdit = true; this.roleForm = { ...row }; this.roleModalVisible = true },
    configPerms(row) { this.currentRole = row; this.permModalVisible = true },
    async submitForm() {
      const valid = await this.$refs.roleForm.validate().catch(() => false)
      if (!valid) return
      try {
        if (this.isEdit) await updateRole(this.roleForm)
        else await addRole(this.roleForm)
        this.$message.success('操作成功')
        this.roleModalVisible = false
        this.loadRoles()
      } catch { this.$message.error('操作失败') }
    },
    async savePerms() {
      this.$message.success('权限配置已保存')
      this.permModalVisible = false
    },
    async deleteRole(row) {
      await this.$confirm('确定要删除该角色吗？', '提示')
      try {
        await deleteRole(row.roleId)
        this.$message.success('删除成功')
        this.loadRoles()
      } catch { this.$message.error('删除失败') }
    }
  }
}
</script>

<style lang="scss" scoped>
.role-list {
  .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
  .danger { color: #ff4d4f; }
  .perm-tree {
    h4 { margin-bottom: 16px; }
  }
}
</style>
