import request from './http'

// 管理端登录
export function adminLogin(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data,
  })
}

// 管理端退出登录
export function adminLogout() {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}

// 分页获取用户列表
export function getAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params,
  })
}

// 更新用户状态（封禁 / 解封）
export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status },
  })
}

// 更新用户角色（管理员 / 普通用户）
export function updateUserRole(id, role) {
  return request({
    url: `/admin/users/${id}/role`,
    method: 'put',
    data: { role },
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete',
  })
}
