import Cookies from 'js-cookie'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/auth'

const state = {
  token: Cookies.get('Admin-Token') || '',
  userInfo: null,
  roles: []
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    Cookies.set('Admin-Token', token, { expires: 7 })
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
  },
  SET_ROLES(state, roles) {
    state.roles = roles
  },
  CLEAR_AUTH(state) {
    state.token = ''
    state.userInfo = null
    state.roles = []
    Cookies.remove('Admin-Token')
  }
}

const actions = {
  async login({ commit }, loginData) {
    try {
      const data = await loginApi(loginData)
      commit('SET_TOKEN', data.token)
      return data
    } catch (error) {
      commit('CLEAR_AUTH')
      throw error
    }
  },

  async getUserInfo({ commit, state }) {
    if (!state.token) {
      throw new Error('No token')
    }
    try {
      const data = await getUserInfo()
      commit('SET_USER_INFO', data)
      commit('SET_ROLES', data.roles || [])
      return data
    } catch (error) {
      commit('CLEAR_AUTH')
      throw error
    }
  },

  async logout({ commit }) {
    try {
      await logoutApi()
    } catch (e) {
      // Ignore logout API errors
    } finally {
      commit('CLEAR_AUTH')
    }
  },

  resetToken({ commit }) {
    commit('CLEAR_AUTH')
  }
}

const getters = {
  isAuthenticated: state => !!state.token,
  userName: state => state.userInfo?.userName || state.userInfo?.username || '',
  nickName: state => state.userInfo?.nickName || '',
  avatar: state => state.userInfo?.avatar || '',
  userRoles: state => state.roles
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
