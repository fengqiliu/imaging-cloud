import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/external/:shareNo?',
    name: 'ExternalViewer',
    component: () => import('@/views/external/ExternalViewer.vue'),
    meta: { title: '外部查看', public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'fa-th-large' }
      },
      {
        path: 'patients',
        name: 'Patients',
        component: () => import('@/views/patients/PatientList.vue'),
        meta: { title: '患者管理', icon: 'fa-users' }
      },
      {
        path: 'examinations',
        name: 'Examinations',
        component: () => import('@/views/examinations/ExamList.vue'),
        meta: { title: '检查管理', icon: 'fa-clipboard-list' }
      },
      {
        path: 'images',
        name: 'Images',
        component: () => import('@/views/images/ImageList.vue'),
        meta: { title: '影像管理', icon: 'fa-images' }
      },
      {
        path: 'viewer/:examId?',
        name: 'DicomViewer',
        component: () => import('@/views/viewer/DicomViewer.vue'),
        meta: { title: 'DICOM阅片', icon: 'fa-eye' }
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('@/views/reports/ReportList.vue'),
        meta: { title: '诊断报告', icon: 'fa-file-medical' }
      },
      {
        path: 'ai',
        name: 'AiAnalysis',
        component: () => import('@/views/ai/AiAnalysis.vue'),
        meta: { title: 'AI解读', icon: 'fa-robot' }
      },
      {
        path: 'shares',
        name: 'Shares',
        component: () => import('@/views/share/ShareList.vue'),
        meta: { title: '分享管理', icon: 'fa-share-alt' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/Statistics.vue'),
        meta: { title: '运营统计', icon: 'fa-chart-bar' }
      },
      // System management
      {
        path: 'system/users',
        name: 'Users',
        component: () => import('@/views/system/users/UserList.vue'),
        meta: { title: '用户管理', icon: 'fa-user-cog' }
      },
      {
        path: 'system/roles',
        name: 'Roles',
        component: () => import('@/views/system/roles/RoleList.vue'),
        meta: { title: '角色管理', icon: 'fa-theater-masks' }
      },
      {
        path: 'system/depts',
        name: 'Departments',
        component: () => import('@/views/system/depts/DeptTree.vue'),
        meta: { title: '院区管理', icon: 'fa-hospital' }
      },
      {
        path: 'system/dict',
        name: 'Dictionary',
        component: () => import('@/views/system/dict/DictList.vue'),
        meta: { title: '字典管理', icon: 'fa-book' }
      },
      {
        path: 'system/logs',
        name: 'Logs',
        component: () => import('@/views/system/logs/LogList.vue'),
        meta: { title: '日志管理', icon: 'fa-history' }
      },
      {
        path: 'system/settings',
        name: 'Settings',
        component: () => import('@/views/system/settings/Settings.vue'),
        meta: { title: '系统设置', icon: 'fa-cog' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL || '/',
  routes
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  NProgress.start()

  const isPublic = to.matched.some(record => record.meta.public)
  const isAuthenticated = store.getters['auth/isAuthenticated']

  if (!isPublic && !isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

router.afterEach((to) => {
  NProgress.done()
  document.title = to.meta.title ? `${to.meta.title} - D-Site 云胶片` : 'D-Site 云胶片'
})

export default router
