const state = {
  sidebarCollapsed: false,
  device: 'desktop'
}

const mutations = {
  TOGGLE_SIDEBAR(state) {
    state.sidebarCollapsed = !state.sidebarCollapsed
  },
  SET_SIDEBAR(state, collapsed) {
    state.sidebarCollapsed = collapsed
  }
}

const actions = {
  toggleSidebar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
