import { login, getUserInfo, logout } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: getToken() || '',
  userId: null,
  username: '',
  nickName: '',
  roleCode: '',
  roleName: ''
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
    setToken(token)
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userId = userInfo.userId
    state.username = userInfo.username
    state.nickName = userInfo.nickName
    state.roleCode = userInfo.roleCode
    state.roleName = userInfo.roleName
  },
  CLEAR_USER: (state) => {
    state.token = ''
    state.userId = null
    state.username = ''
    state.nickName = ''
    state.roleCode = ''
    state.roleName = ''
    removeToken()
  }
}

const actions = {
  login({ commit }, loginForm) {
    return new Promise((resolve, reject) => {
      login(loginForm)
        .then(response => {
          const data = response.data
          commit('SET_TOKEN', data.token)
          commit('SET_USER_INFO', data)
          resolve()
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  getUserInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then(response => {
          const data = response.data
          commit('SET_USER_INFO', data)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  logout({ commit }) {
    return new Promise((resolve) => {
      logout().finally(() => {
        commit('CLEAR_USER')
        resolve()
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
