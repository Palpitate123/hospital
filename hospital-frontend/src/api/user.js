import request from '@/utils/request'

/**
 * 分页查询用户列表
 */
export function getUserPage(params) {
    return request({
        url: '/sys/user/page',
        method: 'get',
        params
    })
}

/**
 * 新增用户
 */
export function addUser(data) {
    return request({
        url: '/sys/user',
        method: 'post',
        data: data
    })
}

/**
 * 修改用户
 */
export function updateUser(data) {
    return request({
        url: '/sys/user',
        method: 'put',
        data: data
    })
}

/**
 * 物理删除用户
 */
export function deleteUser(id) {
    return request({
        url: `/sys/user/${id}`,
        method: 'delete'
    })
}
