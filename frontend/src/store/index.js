import Vue from 'vue'
import Vuex from 'vuex'
import auth from './modules/auth'
import app from './modules/app'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    auth,
    app
  },
  getters: {
    sidebarCollapsed: state => state.app.sidebarCollapsed,
    device: state => state.app.device
  }
})
