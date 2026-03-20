const state = {
  sidebar: true,
  device: 'desktop'
}

const mutations = {
  TOGGLE_SIDEBAR: (state) => {
    state.sidebar = !state.sidebar
  },
  CLOSE_SIDEBAR: (state) => {
    state.sidebar = false
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  }
}

const actions = {
  toggleSidebar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  closeSidebar({ commit }) {
    commit('CLOSE_SIDEBAR')
  },
  toggleDevice({ commit }, device) {
    commit('TOGGLE_DEVICE', device)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
