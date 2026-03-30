import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'normalize.css/normalize.css'
import '@/assets/styles/common.scss'

import MainLayout from '@/layout/MainLayout.vue'

Vue.use(ElementUI, {
  size: 'default',
  zIndex: 3000
})

Vue.config.productionTip = false

// Global components registration
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

Vue.component('StatusTag', StatusTag)
Vue.component('EmptyState', EmptyState)
Vue.component('ConfirmDialog', ConfirmDialog)

new Vue({
  router,
  store,
  render: h => h(App),
  mounted() {
    // Check authentication status on app mount
    if (this.$store.getters['auth/isAuthenticated']) {
      this.$store.dispatch('auth/getUserInfo').catch(() => {
        // Token invalid, redirect to login
        this.$router.push('/login')
      })
    }
  }
}).$mount('#app')
