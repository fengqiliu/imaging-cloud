<template>
  <div class="settings">
    <div class="page-header">
      <h1 class="page-title">系统设置</h1>
    </div>

    <div class="settings-layout">
      <!-- Basic Settings -->
      <div class="card settings-section">
        <div class="section-header">
          <h3><i class="fas fa-cog"></i> 基本设置</h3>
        </div>
        <el-form :model="basicSettings" label-width="140px" class="settings-form">
          <el-form-item label="系统名称">
            <el-input v-model="basicSettings.systemName"></el-input>
          </el-form-item>
          <el-form-item label="系统版本">
            <el-input v-model="basicSettings.version" disabled></el-input>
          </el-form-item>
          <el-form-item label="默认院区">
            <el-select v-model="basicSettings.defaultDept">
              <el-option label="总院" :value="1"></el-option>
              <el-option label="分院" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Token有效期">
            <el-select v-model="basicSettings.tokenExpire">
              <el-option label="2小时" :value="2"></el-option>
              <el-option label="24小时" :value="24"></el-option>
              <el-option label="7天" :value="168"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <!-- Storage Settings -->
      <div class="card settings-section">
        <div class="section-header">
          <h3><i class="fas fa-hdd"></i> 存储设置</h3>
        </div>
        <el-form :model="storageSettings" label-width="140px" class="settings-form">
          <el-form-item label="存储方式">
            <el-radio-group v-model="storageSettings.type">
              <el-radio label="local">本地存储</el-radio>
              <el-radio label="minio">MinIO对象存储</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="本地存储路径" v-if="storageSettings.type === 'local'">
            <el-input v-model="storageSettings.localPath" placeholder="./uploads"></el-input>
          </el-form-item>
          <el-form-item label="MinIO地址" v-if="storageSettings.type === 'minio'">
            <el-input v-model="storageSettings.minioEndpoint" placeholder="http://localhost:9000"></el-input>
          </el-form-item>
          <el-form-item label="Bucket名称" v-if="storageSettings.type === 'minio'">
            <el-input v-model="storageSettings.minioBucket"></el-input>
          </el-form-item>
          <el-form-item label="文件大小限制">
            <el-input v-model="storageSettings.maxFileSize">
              <template slot="append">MB</template>
            </el-input>
          </el-form-item>
          <el-form-item label="允许的文件格式">
            <el-checkbox-group v-model="storageSettings.allowedTypes">
              <el-checkbox label=".dcm">DICOM (.dcm)</el-checkbox>
              <el-checkbox label=".dicom">DICOM (.dicom)</el-checkbox>
              <el-checkbox label=".jpg">JPEG (.jpg)</el-checkbox>
              <el-checkbox label=".png">PNG (.png)</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>

      <!-- Save Button -->
      <div class="save-section">
        <el-button type="primary" size="large" @click="saveSettings" :loading="saving">
          保存设置
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Settings',
  data() {
    return {
      saving: false,
      basicSettings: {
        systemName: 'D-Site 云胶片管理系统',
        version: 'V1.0.0',
        defaultDept: 1,
        tokenExpire: 24
      },
      storageSettings: {
        type: 'local',
        localPath: './uploads',
        minioEndpoint: 'http://localhost:9000',
        minioBucket: 'medical-images',
        maxFileSize: 500,
        allowedTypes: ['.dcm', '.dicom', '.jpg', '.png']
      }
    }
  },
  methods: {
    async saveSettings() {
      this.saving = true
      try {
        await new Promise(resolve => setTimeout(resolve, 500))
        this.$message.success('设置已保存')
      } catch {
        this.$message.error('保存失败')
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.settings {
  .page-header { margin-bottom: 24px; }
}

.settings-layout {
  max-width: 800px;
}

.settings-section {
  margin-bottom: 24px;
}

.section-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;

  h3 {
    font-size: 16px;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0;

    i { color: #1890ff; }
  }
}

.settings-form {
  .el-form-item { margin-bottom: 20px; }
  .el-select, .el-input { width: 300px; }
}

.save-section {
  text-align: center;
}
</style>
