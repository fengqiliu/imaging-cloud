<template>
  <div class="login-page">
    <div class="login-left">
      <div class="login-logo">
        <i class="fas fa-x-ray"></i>
      </div>
      <h1>D-Site 云胶片</h1>
      <p>医学影像云管理平台</p>
      <p class="slogan">让诊断更高效，让影像更有价值</p>
    </div>
    <div class="login-right">
      <div class="login-form">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p class="subtitle">请登录您的账号</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="rules" @submit.native.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              size="large"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              native-type="submit"
              style="width: 100%;"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="demo-accounts">
          <h4><i class="el-icon-info"></i> 测试账号</h4>
          <ul>
            <li>管理员: <code>admin</code> / <code>admin123</code></li>
            <li>放射科医生: <code>radiologist</code> / <code>admin123</code></li>
            <li>临床医生: <code>clinician</code> / <code>admin123</code></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: 'admin123'
      },
      rememberPassword: true,
      loading: false,
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    ...mapActions('auth', ['login']),
    async handleLogin() {
      const valid = await this.$refs.loginForm.validate().catch(() => false)
      if (!valid) return

      this.loading = true
      try {
        const data = await this.login(this.loginForm)
        this.$message.success('登录成功')
        this.$router.push('/dashboard')
      } catch (error) {
        this.$message.error(error.message || '登录失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px;
  color: white;

  h1 {
    font-size: 48px;
    margin-bottom: 16px;
    font-weight: 600;
  }

  p {
    font-size: 18px;
    opacity: 0.9;
    text-align: center;
    max-width: 400px;
  }

  .slogan {
    margin-top: 20px;
    font-size: 16px;
    opacity: 0.7;
  }
}

.login-logo {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  backdrop-filter: blur(10px);

  i {
    font-size: 50px;
    color: white;
  }
}

.login-right {
  width: 480px;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
}

.login-form {
  max-width: 320px;
  margin: 0 auto;

  h2 {
    font-size: 28px;
    color: #303133;
    margin-bottom: 10px;
  }

  .subtitle {
    color: #909399;
    margin-bottom: 40px;
  }
}

.demo-accounts {
  margin-top: 30px;
  padding: 15px;
  background: #f0f9eb;
  border-radius: 8px;
  border-left: 4px solid #52c41a;

  h4 {
    font-size: 14px;
    color: #52c41a;
    margin-bottom: 10px;
  }

  ul {
    list-style: none;
    font-size: 13px;
    color: #606266;
  }

  li {
    margin: 5px 0;
  }

  code {
    background: #e8f5e9;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: Consolas, monospace;
  }
}
</style>
