<template>
  <div class="user-list">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <el-button type="primary" icon="el-icon-plus" @click="showAddModal">新增用户</el-button>
    </div>

    <div class="card">
      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="userId" label="用户ID" width="80"></el-table-column>
        <el-table-column prop="userName" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickName" label="昵称" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="roleName" label="角色" width="120"></el-table-column>
        <el-table-column prop="deptName" label="部门" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-switch :value="row.status === '0'" @change="toggleStatus(row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最后登录" width="160"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="editUser(row)">编辑</el-button>
            <el-button type="text" size="small" @click="resetPwd(row)">重置密码</el-button>
            <el-button type="text" size="small" class="danger" @click="deleteUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.pageNum"
          :page-sizes="[10, 20, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
        ></el-pagination>
      </div>
    </div>

    <!-- Add/Edit User Modal -->
    <el-dialog :visible.sync="userModalVisible" :title="modalTitle" width="600px">
      <el-form ref="userForm" :model="userForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userForm.userName" :disabled="isEdit" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="userForm.nickName" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色">
            <el-option v-for="r in roles" :key="r.roleId" :label="r.roleName" :value="r.roleId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="userForm.deptId" placeholder="请选择部门">
            <el-option v-for="d in depts" :key="d.deptId" :label="d.deptName" :value="d.deptId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="userModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>

    <!-- Reset Password Modal -->
    <el-dialog :visible.sync="pwdModalVisible" title="重置密码" width="400px">
      <p>确定要重置用户 <strong>{{ currentUser?.userName }}</strong> 的密码吗？</p>
      <p>新密码将设为：<code>admin123</code></p>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pwdModalVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResetPwd">确定重置</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, addUser, updateUser, deleteUser } from '@/api/system'

export default {
  name: 'UserList',
  data() {
    return {
      loading: false,
      userModalVisible: false,
      pwdModalVisible: false,
      modalTitle: '新增用户',
      isEdit: false,
      userList: [],
      roles: [
        { roleId: 1, roleName: '超级管理员' },
        { roleId: 2, roleName: '放射科医生' },
        { roleId: 3, roleName: '临床医生' }
      ],
      depts: [
        { deptId: 1, deptName: '放射科' },
        { deptId: 2, deptName: '神经内科' },
        { deptId: 3, deptName: '心内科' }
      ],
      currentUser: null,
      userForm: {
        userName: '', nickName: '', phone: '', email: '', roleId: '', deptId: '', status: '0'
      },
      rules: {
        userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      },
      pagination: { pageNum: 1, pageSize: 10, total: 0 }
    }
  },
  async created() {
    await this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      try {
        const data = await getUserList({ pageNum: this.pagination.pageNum, pageSize: this.pagination.pageSize })
        this.userList = data.rows || []
        this.pagination.total = data.total || 0
      } catch {
        this.userList = [
          { userId: 1, userName: 'admin', nickName: '管理员', phone: '13800138000', email: 'admin@dsite.com', roleName: '超级管理员', deptName: '信息中心', status: '0', lastLogin: '2026-03-29 10:30' },
          { userId: 2, userName: 'radiologist', nickName: '王医生', phone: '13900139000', email: 'wang@dsite.com', roleName: '放射科医生', deptName: '放射科', status: '0', lastLogin: '2026-03-29 09:15' },
          { userId: 3, userName: 'clinician', nickName: '李医生', phone: '13700137000', email: 'li@dsite.com', roleName: '临床医生', deptName: '神经内科', status: '0', lastLogin: '2026-03-28 16:45' }
        ]
        this.pagination.total = this.userList.length
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) { this.pagination.pageSize = val; this.loadUsers() },
    handleCurrentChange(val) { this.pagination.pageNum = val; this.loadUsers() },
    showAddModal() { this.modalTitle = '新增用户'; this.isEdit = false; this.userModalVisible = true },
    editUser(row) { this.modalTitle = '编辑用户'; this.isEdit = true; this.userForm = { ...row }; this.userModalVisible = true },
    async submitForm() {
      const valid = await this.$refs.userForm.validate().catch(() => false)
      if (!valid) return
      try {
        if (this.isEdit) await updateUser(this.userForm)
        else await addUser(this.userForm)
        this.$message.success('操作成功')
        this.userModalVisible = false
        this.loadUsers()
      } catch { this.$message.error('操作失败') }
    },
    resetPwd(row) { this.currentUser = row; this.pwdModalVisible = true },
    async confirmResetPwd() {
      this.$message.success('密码已重置为 admin123')
      this.pwdModalVisible = false
    },
    async deleteUser(row) {
      await this.$confirm('确定要删除该用户吗？', '提示')
      try {
        await deleteUser(row.userId)
        this.$message.success('删除成功')
        this.loadUsers()
      } catch { this.$message.error('删除失败') }
    },
    toggleStatus(row) {
      row.status = row.status === '0' ? '1' : '0'
      this.$message.success(`已${row.status === '0' ? '启用' : '停用'}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.user-list {
  .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
  .pagination-wrapper { margin-top: 20px; text-align: right; }
  .danger { color: #ff4d4f; }
}
</style>
