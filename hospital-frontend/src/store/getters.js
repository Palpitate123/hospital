const getters = {
  token: state => state.user.token,
  userId: state => state.user.userId,
  username: state => state.user.username,
  nickName: state => state.user.nickName,
  roleCode: state => state.user.roleCode,
  roleName: state => state.user.roleName
}

export default getters
