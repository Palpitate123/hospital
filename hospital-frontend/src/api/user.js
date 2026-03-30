import request from '@/utils/request'

// 分页查询用户列表
export function getUserPage(params) {
    return request({
        url: '/sys/user/page',
        method: 'get',
        params
    })
}

// 修改用户状态
export function updateUserStatus(id, status) {
    return request({
        url: `/sys/user/status/${id}/${status}`,
        method: 'put'
    })
}
